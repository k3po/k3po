/*
 * Copyright 2014, Kaazing Corporation. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kaazing.k3po.driver;

import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static org.jboss.netty.channel.Channels.pipeline;
import static org.jboss.netty.channel.Channels.pipelineFactory;
import static org.jboss.netty.util.CharsetUtil.UTF_8;
import static org.kaazing.k3po.driver.netty.bootstrap.BootstrapFactory.newBootstrapFactory;
import static org.kaazing.k3po.driver.netty.channel.ChannelAddressFactory.newChannelAddressFactory;
import static org.kaazing.k3po.lang.RegionInfo.newSequential;

import java.io.ByteArrayInputStream;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ChildChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.ChannelGroupFutureListener;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.local.DefaultLocalClientChannelFactory;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.kaazing.k3po.driver.behavior.Configuration;
import org.kaazing.k3po.driver.behavior.ScriptProgress;
import org.kaazing.k3po.driver.behavior.ScriptProgressException;
import org.kaazing.k3po.driver.behavior.handler.CompletionHandler;
import org.kaazing.k3po.driver.behavior.parser.Parser;
import org.kaazing.k3po.driver.behavior.visitor.GenerateConfigurationVisitor;
import org.kaazing.k3po.driver.netty.bootstrap.BootstrapFactory;
import org.kaazing.k3po.driver.netty.bootstrap.ClientBootstrap;
import org.kaazing.k3po.driver.netty.bootstrap.ServerBootstrap;
import org.kaazing.k3po.driver.netty.channel.ChannelAddressFactory;
import org.kaazing.k3po.driver.netty.channel.CompositeChannelFuture;
import org.kaazing.k3po.lang.RegionInfo;
import org.kaazing.k3po.lang.ast.AstScriptNode;
import org.kaazing.k3po.lang.parser.ScriptParser;

public class Robot {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(Robot.class);

    private final List<ChannelFuture> bindFutures = new ArrayList<>();
    private final List<ChannelFuture> connectFutures = new ArrayList<>();

    private final Channel channel = new DefaultLocalClientChannelFactory().newChannel(pipeline(new SimpleChannelHandler()));
    private final ChannelFuture startedFuture = Channels.future(channel);
    private final ChannelFuture abortedFuture = Channels.future(channel);
    private final ChannelFuture finishedFuture = Channels.future(channel);

    private final DefaultChannelGroup serverChannels = new DefaultChannelGroup();
    private final DefaultChannelGroup clientChannels = new DefaultChannelGroup();

    private Configuration configuration;
    private ChannelFuture preparedFuture;
    private volatile boolean destroyed;

    private final ChannelAddressFactory addressFactory;
    private final BootstrapFactory bootstrapFactory;
    private final boolean createdBootstrapFactory;

    private ScriptProgress progress;

    private final ChannelHandler closeOnExceptionHandler = new CloseOnExceptionHandler();

    // tests
    public Robot() {
        this(newChannelAddressFactory());
    }

    private Robot(ChannelAddressFactory addressFactory) {
        this(addressFactory,
             newBootstrapFactory(Collections.<Class<?>, Object>singletonMap(ChannelAddressFactory.class, addressFactory)), true);
    }

    public Robot(ChannelAddressFactory addressFactory, BootstrapFactory bootstrapFactory) {
        this(addressFactory, bootstrapFactory, false);
    }

    private Robot(
            ChannelAddressFactory addressFactory,
            BootstrapFactory bootstrapFactory,
            boolean createdBootstrapFactory) {

        this.addressFactory = addressFactory;
        this.bootstrapFactory = bootstrapFactory;
        this.createdBootstrapFactory = createdBootstrapFactory;

        ChannelFutureListener stopConfigurationListener = createStopConfigurationListener();
        this.abortedFuture.addListener(stopConfigurationListener);
        this.finishedFuture.addListener(stopConfigurationListener);
    }

    public ChannelFuture getPreparedFuture() {
        return preparedFuture;
    }

    public ChannelFuture getStartedFuture() {
        return startedFuture;
    }

    public ChannelFuture prepare(String expectedScript) throws Exception {

        if (preparedFuture != null) {
            throw new IllegalStateException("Script already prepared");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Expected script:\n" + expectedScript);
        }

        final ScriptParser parser = new Parser();
        AstScriptNode scriptAST = parser.parse(new ByteArrayInputStream(expectedScript.getBytes(UTF_8)));

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Parsed script:\n" + scriptAST);
        }

        RegionInfo scriptInfo = scriptAST.getRegionInfo();
        progress = new ScriptProgress(scriptInfo, expectedScript);

        final GenerateConfigurationVisitor visitor = new GenerateConfigurationVisitor(bootstrapFactory, addressFactory);
        configuration = scriptAST.accept(visitor, new GenerateConfigurationVisitor.State());

        preparedFuture = prepareConfiguration();

        return preparedFuture;
    }

    public ChannelFuture prepareAndStart(String script) throws Exception {
        ChannelFuture preparedFuture = prepare(script);
        preparedFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                start();
            }
        });
        return startedFuture;
    }

    public ChannelFuture start() throws Exception {

        if (preparedFuture == null || !preparedFuture.isSuccess()) {
            throw new IllegalStateException("Script has not been prepared or is still preparing");
        } else if (startedFuture.isDone()) {
            throw new IllegalStateException("Script has already been started");
        }

        // ensure prepare has completed before start can progress
        preparedFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                startConfiguration();
                startedFuture.setSuccess();
            }
        });

        return startedFuture;
    }

    public ChannelFuture abort() {

        abortedFuture.setSuccess();

        return finishedFuture;
    }

    public ChannelFuture finish() {

        return finishedFuture;
    }

    public String getObservedScript() {
        return (progress != null) ? progress.getObservedScript() : null;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public boolean destroy() {

        if (destroyed) {
            return true;
        }

        abort();

        if (createdBootstrapFactory) {
            try {
                bootstrapFactory.shutdown();
                bootstrapFactory.releaseExternalResources();
            }
            catch (Exception e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Caught exception releasing resources", e);
                }
                return false;
            }
        }

        return destroyed = true;
    }

    private ChannelFuture prepareConfiguration() {

        List<ChannelFuture> completionFutures = new ArrayList<>();
        ChannelFutureListener streamCompletionListener = createStreamCompletionListener();
        for (ChannelPipeline pipeline : configuration.getClientAndServerPipelines()) {
            CompletionHandler completionHandler = pipeline.get(CompletionHandler.class);
            ChannelFuture completionFuture = completionHandler.getHandlerFuture();
            completionFutures.add(completionFuture);
            completionFuture.addListener(streamCompletionListener);
        }

        ChannelFuture executionFuture = new CompositeChannelFuture<>(channel, completionFutures);
        ChannelFutureListener executionListener = createScriptCompletionListener();
        executionFuture.addListener(executionListener);

        return prepareServers();
    }

    private ChannelFuture prepareServers() {

        /* Accept's ... Robot acting as a server */
        for (ServerBootstrap server : configuration.getServerBootstraps()) {

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Binding to address " + server.getOption("localAddress"));
            }

            /* Keep track of the client channels */
            server.setParentHandler(new SimpleChannelHandler() {
                @Override
                public void childChannelOpen(ChannelHandlerContext ctx, ChildChannelStateEvent e) throws Exception {
                    clientChannels.add(e.getChildChannel());
                }

                @Override
                public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
                    Channel channel = ctx.getChannel();
                    channel.close();
                }
            });


            // Bind Asynchronously
            ChannelFuture bindFuture = server.bindAsync();

            // Add to out serverChannel Group
            serverChannels.add(bindFuture.getChannel());

            // Add to our list of bindFutures so we can cancel them later on a possible abort
            bindFutures.add(bindFuture);

            // Listen for the bindFuture.
            RegionInfo regionInfo = (RegionInfo) server.getOption("regionInfo");
            bindFuture.addListener(createBindCompleteListener(regionInfo));
        }

        // What should prepared mean ... server channels have all completed binding or just that they started.
        // Initially I was thinking that it should be when they are done. But I'm not so sure.
        // In that case what should happen if a subset of the binds fail and a subset succeed? What should happen if they all
        // fail? I think in either case the robot should generate an observed script with the exception events in place of
        // the accept lines. This is why I choose to return a successful future rather than some composite of the bind
        // futures.
        return Channels.succeededFuture(channel);
    }

    private void startConfiguration() {
        /* Connect to any clients */
        for (ClientBootstrap client : configuration.getClientBootstraps()) {

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("[id:           ] connect " + client.getOption("remoteAddress"));
            }

            final RegionInfo regionInfo = (RegionInfo) client.getOption("regionInfo");
            ChannelFuture connectFuture = client.connect();
            connectFutures.add(connectFuture);
            clientChannels.add(connectFuture.getChannel());
            connectFuture.addListener(createConnectCompleteListener(regionInfo));
        }
    }

    private void stopConfiguration() {

        if (configuration == null) {
            // abort received but script not prepared, therefore entire script failed
            if (progress == null) {
                progress = new ScriptProgress(newSequential(0, 0), "");
            }
            RegionInfo scriptInfo = progress.getScriptInfo();
            progress.addScriptFailure(scriptInfo);
        }
        else {
            // stopping the configuration will implicitly trigger the script complete listener
            // to handle incomplete script that is being aborted by canceling the finish future

            // clear out the pipelines for new connections to avoid impacting the observed script
            for (ServerBootstrap server : configuration.getServerBootstraps()) {
                server.setPipelineFactory(pipelineFactory(pipeline(closeOnExceptionHandler)));
            }
            for (ClientBootstrap bootstrap : configuration.getClientBootstraps()) {
                bootstrap.setPipelineFactory(pipelineFactory(pipeline(closeOnExceptionHandler)));
            }

            // remove each handler from the configuration pipelines
            // this will trigger failures for any handlers on a pipeline for an incomplete stream
            // including pipelines not yet associated with any channel
            for (ChannelPipeline pipeline : configuration.getClientAndServerPipelines()) {
                stopStream(pipeline);
            }

            // cancel any pending binds and connects
            for (ChannelFuture bindFuture : bindFutures) {
                bindFuture.cancel();
            }

            for (ChannelFuture connectFuture : connectFutures) {
                connectFuture.cancel();
            }

            // close server and client channels
            final ChannelGroupFuture closeFuture = serverChannels.close();
            closeFuture.addListener(new ChannelGroupFutureListener() {
                @Override
                public void operationComplete(final ChannelGroupFuture future) {
                    clientChannels.close();
                }
            });
        }
    }

    private void stopStream(final ChannelPipeline pipeline) {
        if (pipeline.isAttached()) {

            // avoid race between pipeline clean up and channel events on same pipeline
            // by executing the pipeline clean up on the I/O worker thread
            pipeline.execute(new Runnable() {
                @Override
                public void run() {
                    stopStreamAligned(pipeline);
                }

            });
        }
        else {
            // no race if not attached
            stopStreamAligned(pipeline);
        }
    }

    private void stopStreamAligned(final ChannelPipeline pipeline) {
        for (ChannelHandler handler : pipeline.toMap().values()) {
            // note: removing this handler can trigger script completion
            //       which in turn can re-attempt to stop this pipeline
            pipeline.remove(handler);
        }

        // non-empty pipeline required to avoid warnings
        if (pipeline.getContext(closeOnExceptionHandler) == null) {
            pipeline.addLast("closeOnException", closeOnExceptionHandler);
        }
    }

    private ChannelFutureListener createBindCompleteListener(final RegionInfo regionInfo) {
        return new ChannelFutureListener() {
            @Override
            public void operationComplete(final ChannelFuture bindFuture) throws Exception {

                if (bindFuture.isSuccess()) {
                    if (LOGGER.isDebugEnabled()) {
                        Channel boundChannel = bindFuture.getChannel();
                        SocketAddress localAddress = boundChannel.getLocalAddress();
                        LOGGER.debug("Successfully bound to " + localAddress);
                    }
                } else {
                    Throwable cause = bindFuture.getCause();
                    String message = format("accept failed: %s", cause.getMessage());
                    progress.addScriptFailure(regionInfo, message);

                    // fail each pipeline that required this bind to succeed
                    List<ChannelPipeline> acceptedPipelines = configuration.getServerPipelines(regionInfo);
                    for (ChannelPipeline acceptedPipeline : acceptedPipelines) {
                        stopStream(acceptedPipeline);
                    }
                }

            }
        };
    }

    private ChannelFutureListener createConnectCompleteListener(final RegionInfo regionInfo) {
        return new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture connectFuture) throws Exception {
                if (connectFuture.isCancelled()) {
                    progress.addScriptFailure(regionInfo, "connect failed");
                }
                else if (!connectFuture.isSuccess()) {
                    Throwable cause = connectFuture.getCause();
                    String message = format("connect failed: %s", cause.getMessage());
                    progress.addScriptFailure(regionInfo, message);
                }
            }
        };
    }

    private ChannelFutureListener createStreamCompletionListener() {
        return new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture completionFuture) throws Exception {
                if (!completionFuture.isSuccess()) {
                    Throwable cause = completionFuture.getCause();
                    if (cause instanceof ScriptProgressException) {
                        ScriptProgressException exception = (ScriptProgressException) cause;
                        progress.addScriptFailure(exception.getRegionInfo(), exception.getMessage());
                    }
                    else {
                        LOGGER.warn("Unexpected exception", cause);
                    }
                }
            }
        };
    }

    private ChannelFutureListener createScriptCompletionListener() {
        ChannelFutureListener executionListener = new ChannelFutureListener() {

            @Override
            public void operationComplete(final ChannelFuture future) throws Exception {

                if (LOGGER.isDebugEnabled()) {
                    // detect observed script
                    String observedScript = progress.getObservedScript();
                    LOGGER.debug("Observed script:\n" + observedScript);
                }

                if (abortedFuture.isDone()) {
                    // abort complete, trigger finished future
                    finishedFuture.setSuccess();
                }
                else {
                    // execution complete, trigger finished future
                    finishedFuture.setSuccess();
                }
            }

        };

        return executionListener;
    }

    private ChannelFutureListener createStopConfigurationListener() {
        return new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                stopConfiguration();
            }
        };
    }

    @Sharable
    private static final class CloseOnExceptionHandler extends SimpleChannelHandler {

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
            // avoid stack overflow when exception happens on close
            if (TRUE != ctx.getAttachment()) {
                ctx.setAttachment(TRUE);
                // close channel and avoid warning logged by default exceptionCaught implementation
                Channel channel = ctx.getChannel();
                channel.close();
            }
            else {
                // log exception during close
                super.exceptionCaught(ctx, e);
            }
        }
    }

}
