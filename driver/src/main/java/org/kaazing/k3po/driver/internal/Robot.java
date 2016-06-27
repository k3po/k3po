/**
 * Copyright 2007-2015, Kaazing Corporation. All rights reserved.
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
package org.kaazing.k3po.driver.internal;

import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static org.jboss.netty.channel.Channels.pipeline;
import static org.jboss.netty.channel.Channels.pipelineFactory;
import static org.jboss.netty.util.CharsetUtil.UTF_8;
import static org.kaazing.k3po.driver.internal.netty.bootstrap.BootstrapFactory.newBootstrapFactory;
import static org.kaazing.k3po.driver.internal.netty.channel.ChannelAddressFactory.newChannelAddressFactory;
import static org.kaazing.k3po.lang.internal.RegionInfo.newSequential;

import java.io.ByteArrayInputStream;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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
import org.kaazing.k3po.driver.internal.behavior.Barrier;
import org.kaazing.k3po.driver.internal.behavior.Configuration;
import org.kaazing.k3po.driver.internal.behavior.ScriptProgress;
import org.kaazing.k3po.driver.internal.behavior.ScriptProgressException;
import org.kaazing.k3po.driver.internal.behavior.handler.CompletionHandler;
import org.kaazing.k3po.driver.internal.behavior.parser.Parser;
import org.kaazing.k3po.driver.internal.behavior.parser.ScriptValidator;
import org.kaazing.k3po.driver.internal.behavior.visitor.GenerateConfigurationVisitor;
import org.kaazing.k3po.driver.internal.netty.bootstrap.BootstrapFactory;
import org.kaazing.k3po.driver.internal.netty.bootstrap.ClientBootstrap;
import org.kaazing.k3po.driver.internal.netty.bootstrap.ServerBootstrap;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddressFactory;
import org.kaazing.k3po.driver.internal.netty.channel.CompositeChannelFuture;
import org.kaazing.k3po.driver.internal.resolver.ClientBootstrapResolver;
import org.kaazing.k3po.driver.internal.resolver.ServerBootstrapResolver;
import org.kaazing.k3po.lang.internal.RegionInfo;
import org.kaazing.k3po.lang.internal.ast.AstScriptNode;
import org.kaazing.k3po.lang.internal.parser.ScriptParser;

public class Robot {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(Robot.class);

    private final List<ChannelFuture> bindFutures = new ArrayList<>();
    private final List<ChannelFuture> connectFutures = new ArrayList<>();

    private final Channel channel = new DefaultLocalClientChannelFactory().newChannel(pipeline(new SimpleChannelHandler()));

    private final ChannelFuture startedFuture = Channels.future(channel);
    private final ChannelFuture abortedFuture = Channels.future(channel);
    private final ChannelFuture finishedFuture = Channels.future(channel);
    private final ChannelFuture disposedFuture = Channels.future(channel);

    private final DefaultChannelGroup serverChannels = new DefaultChannelGroup();
    private final DefaultChannelGroup clientChannels = new DefaultChannelGroup();

    private Configuration configuration;
    private ChannelFuture preparedFuture;

    private final ChannelAddressFactory addressFactory;
    private final BootstrapFactory bootstrapFactory;

    private ScriptProgress progress;

    private final ChannelHandler closeOnExceptionHandler = new CloseOnExceptionHandler();

    private final ConcurrentMap<String, Barrier> barriersByName = new ConcurrentHashMap<>();

    public Robot() {
        this.addressFactory = newChannelAddressFactory();
        this.bootstrapFactory =
                newBootstrapFactory(Collections.<Class<?>, Object>singletonMap(ChannelAddressFactory.class, addressFactory));

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

        final ScriptValidator validator = new ScriptValidator();
        validator.validate(scriptAST);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Parsed script:\n" + scriptAST);
        }

        RegionInfo scriptInfo = scriptAST.getRegionInfo();
        progress = new ScriptProgress(scriptInfo, expectedScript);

        final GenerateConfigurationVisitor visitor = new GenerateConfigurationVisitor(bootstrapFactory, addressFactory);
        configuration = scriptAST.accept(visitor, new GenerateConfigurationVisitor.State(barriersByName));

        preparedFuture = prepareConfiguration();

        return preparedFuture;
    }

    public ChannelFuture start() {

        if (preparedFuture == null || !preparedFuture.isDone()) {
            throw new IllegalStateException("Script has not been prepared or is still preparing");
        } else if (startedFuture.isDone()) {
            throw new IllegalStateException("Script has already been started");
        }

        // ensure prepare has completed before start can progress
        preparedFuture.addListener(future -> {
            try {
                startConfiguration();
                startedFuture.setSuccess();
            } catch (Exception ex) {
                startedFuture.setFailure(ex);
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

    public ChannelFuture dispose() {
        if (preparedFuture == null) {
            // no need to clean up if never started
            disposedFuture.setSuccess();
        } else if (!disposedFuture.isDone()) {
            ChannelFuture future = abort();
            future.addListener(new ChannelFutureListener() {

                @Override
                public void operationComplete(ChannelFuture future) throws Exception {

                    // avoid I/O deadlock checker
                    new Thread(new Runnable() {
                        public void run() {
                            // close server and client channels
                            // final ChannelGroupFuture closeFuture =
                            serverChannels.close().addListener(new ChannelGroupFutureListener() {

                                @Override
                                public void operationComplete(ChannelGroupFuture future) throws Exception {
                                    try {
                                        clientChannels.close();
                                        bootstrapFactory.shutdown();
                                        bootstrapFactory.releaseExternalResources();

                                        for (AutoCloseable resource : configuration.getResources()) {
                                            try {
                                                resource.close();
                                            } catch (Exception e) {
                                                // ignore
                                            }
                                        }
                                    } catch (Exception e) {
                                        if (LOGGER.isDebugEnabled()) {
                                            LOGGER.error("Caught exception releasing resources", e);
                                        }
                                    } finally {
                                        // always succeed, as there is no command to send on wire saying failed.
                                        disposedFuture.setSuccess();
                                    }

                                }
                            });
                        }
                    }).start();
                }
            });
        }
        return disposedFuture;
    }

    private ChannelFuture prepareConfiguration() throws Exception {

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

    private ChannelFuture prepareServers() throws Exception {

        /* Accept's ... Robot acting as a server */
        for (ServerBootstrapResolver serverResolver : configuration.getServerResolvers()) {
            ServerBootstrap server = serverResolver.resolve();
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
                    ctx.getChannel().close();
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
            bindFuture.addListener(createBindCompleteListener(regionInfo, serverResolver.getNotifyBarrier()));
        }

        return new CompositeChannelFuture<>(channel, bindFutures);
    }

    private void startConfiguration() throws Exception {
        /* Connect to any clients */
        for (final ClientBootstrapResolver clientResolver : configuration.getClientResolvers()) {
            Barrier awaitBarrier = clientResolver.getAwaitBarrier();
            if (awaitBarrier != null) {
                awaitBarrier.getFuture().addListener(future -> connectClient(clientResolver));
            } else {
                connectClient(clientResolver);
            }
        }
    }

    private void connectClient(ClientBootstrapResolver clientResolver) throws Exception {
        final RegionInfo regionInfo = clientResolver.getRegionInfo();
        ClientBootstrap client = clientResolver.resolve();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("[id:           ] connect " + client.getOption("remoteAddress"));
        }

        ChannelFuture connectFuture = client.connect();
        connectFutures.add(connectFuture);
        clientChannels.add(connectFuture.getChannel());
        connectFuture.addListener(createConnectCompleteListener(regionInfo));
    }

    private void stopConfiguration() throws Exception {

        if (configuration == null) {
            // abort received but script not prepared, therefore entire script failed
            if (progress == null) {
                progress = new ScriptProgress(newSequential(0, 0), "");
            }
            RegionInfo scriptInfo = progress.getScriptInfo();
            progress.addScriptFailure(scriptInfo);
        } else {
            // stopping the configuration will implicitly trigger the script complete listener
            // to handle incomplete script that is being aborted by canceling the finish future

            // clear out the pipelines for new connections to avoid impacting the observed script
            for (ServerBootstrapResolver serverResolver : configuration.getServerResolvers()) {
                try {
                    ServerBootstrap server = serverResolver.resolve();
                    server.setPipelineFactory(pipelineFactory(pipeline(closeOnExceptionHandler)));
                } catch (RuntimeException e) {
                    LOGGER.warn("Exception caught while trying to stop server pipelies", e);
                }
            }
            for (ClientBootstrapResolver clientResolver : configuration.getClientResolvers()) {
                try {
                    ClientBootstrap client = clientResolver.resolve();
                    client.setPipelineFactory(pipelineFactory(pipeline(closeOnExceptionHandler)));
                } catch (RuntimeException e) {
                    LOGGER.warn("Exception caught while trying to stop client pipelies", e);
                }
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
                if (connectFuture.cancel()) {
                    LOGGER.debug("Cancelled connect future: " + connectFuture.getChannel().getRemoteAddress());
                }
            }
        }
    }

    private void stopStream(final ChannelPipeline pipeline) {
        if (pipeline.isAttached()) {

            // avoid race between pipeline clean up and channel events on same pipeline
            // by executing the pipeline clean up on the I/O worker thread
            pipeline.execute(() -> stopStreamAligned(pipeline));
        } else {
            // no race if not attached
            stopStreamAligned(pipeline);
        }
    }

    private void stopStreamAligned(final ChannelPipeline pipeline) {

        LOGGER.debug("Stopping pipeline");

        for (ChannelHandler handler : pipeline.toMap().values()) {

            if (LOGGER.isDebugEnabled()) {
                Channel channel = pipeline.getChannel();
                int id = (channel != null) ? channel.getId() : 0;
                LOGGER.debug(format("[id: 0x%08x] %s", id, handler));
            }

            // note: removing this handler can trigger script completion
            // which in turn can re-attempt to stop this pipeline
            pipeline.remove(handler);
        }

        // non-empty pipeline required to avoid warnings
        if (pipeline.getContext(closeOnExceptionHandler) == null) {
            pipeline.addLast("closeOnException", closeOnExceptionHandler);
        }
    }

    private ChannelFutureListener createBindCompleteListener(final RegionInfo regionInfo, final Barrier notifyBarrier) {
        return bindFuture -> {

            Channel boundChannel = bindFuture.getChannel();
            SocketAddress localAddress = boundChannel.getLocalAddress();
            if (bindFuture.isSuccess()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Successfully bound to " + localAddress);
                }

                if (notifyBarrier != null) {
                    ChannelFuture barrierFuture = notifyBarrier.getFuture();
                    barrierFuture.setSuccess();
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

        };
    }

    private ChannelFutureListener createConnectCompleteListener(final RegionInfo regionInfo) {
        return connectFuture -> {
            if (connectFuture.isCancelled()) {
                // This is more that the connect never really fired, as in the case of a barrier, or the the connect
                // is still in process here, so an empty line annotates that it did not do a connect, an actual
                // connect
                // failure should fail the future
                progress.addScriptFailure(regionInfo, "");
            } else if (!connectFuture.isSuccess()) {
                Throwable cause = connectFuture.getCause();
                String message = format("connect failed: %s", cause.getMessage());
                progress.addScriptFailure(regionInfo, message);
            }
        };
    }

    private ChannelFutureListener createStreamCompletionListener() {
        return completionFuture -> {
            if (!completionFuture.isSuccess()) {
                Throwable cause = completionFuture.getCause();
                if (cause instanceof ScriptProgressException) {
                    ScriptProgressException exception = (ScriptProgressException) cause;
                    progress.addScriptFailure(exception.getRegionInfo(), exception.getMessage());
                } else {
                    LOGGER.warn("Unexpected exception", cause);
                }
            }
        };
    }

    private ChannelFutureListener createScriptCompletionListener() {
        return future -> {

            if (LOGGER.isDebugEnabled()) {
                // detect observed script
                String observedScript = progress.getObservedScript();
                LOGGER.debug("Observed script:\n" + observedScript);
            }

            if (abortedFuture.isDone()) {
                // abort complete, trigger finished future
                finishedFuture.setSuccess();
            } else {
                // execution complete, trigger finished future
                finishedFuture.setSuccess();
            }
        };
    }

    private ChannelFutureListener createStopConfigurationListener() {
        return future -> stopConfiguration();
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
            } else {
                // log exception during close
                super.exceptionCaught(ctx, e);
            }
        }

        @Override
        public String toString() {
            return "close-on-exception";
        }
    }

    public Map<String, Barrier> getBarriersByName() {
        return barriersByName;
    }

    public void notifyBarrier(String barrierName) throws Exception {
        final Barrier barrier = barriersByName.get(barrierName);
        if (barrier == null) {
            throw new Exception("Can not notify a barrier that does not exist in the script: " + barrierName);
        }
        barrier.getFuture().setSuccess();
    }

    public ChannelFuture awaitBarrier(String barrierName) throws Exception {
        final Barrier barrier = barriersByName.get(barrierName);
        if (barrier == null) {
            throw new Exception("Can not notify a barrier that does not exist in the script: " + barrierName);
        }
        return barrier.getFuture();
    }
    

    // ONLY used for testing, TODO, remove and use TestSpecification instead
    ChannelFuture prepareAndStart(String script) throws Exception {
        prepare(script).addListener(future -> start());
        return startedFuture;
    }

}
