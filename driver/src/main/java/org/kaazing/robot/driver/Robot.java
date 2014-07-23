/*
 * Copyright (c) 2014 "Kaazing Corporation," (www.kaazing.com)
 *
 * This file is part of Robot.
 *
 * Robot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.kaazing.robot.driver;

import static org.jboss.netty.channel.Channels.pipeline;
import static org.jboss.netty.channel.Channels.pipelineFactory;
import static org.jboss.netty.util.CharsetUtil.UTF_8;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ChildChannelStateEvent;
import org.jboss.netty.channel.DefaultChannelFuture;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.ChannelGroupFutureListener;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.local.DefaultLocalClientChannelFactory;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

import org.kaazing.robot.driver.netty.bootstrap.ClientBootstrap;
import org.kaazing.robot.driver.netty.bootstrap.ServerBootstrap;
import org.kaazing.robot.driver.behavior.Configuration;
import org.kaazing.robot.driver.behavior.PlayBackScript;
import org.kaazing.robot.driver.behavior.RobotCompletionFuture;
import org.kaazing.robot.driver.behavior.RobotCompletionFutureImpl;
import org.kaazing.robot.driver.behavior.handler.CompletionHandler;
import org.kaazing.robot.driver.behavior.parser.Parser;
import org.kaazing.robot.driver.behavior.visitor.GatherStreamsLocationVisitor;
import org.kaazing.robot.driver.behavior.visitor.GenerateConfigurationVisitor;
import org.kaazing.robot.lang.LocationInfo;
import org.kaazing.robot.lang.ast.AstScriptNode;
import org.kaazing.robot.lang.parser.ScriptParser;
import org.kaazing.robot.driver.netty.channel.CompositeChannelFuture;

public class Robot {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(Robot.class);

    /*
     * A list of completion futures that will indicate that the script is completed. Each stream except for a AcceptNode has
     * a completion handler. The completion handler's handlerFuture is the complete future
     */
    private final List<ChannelFuture> completionFutures = new ArrayList<ChannelFuture>();
    private final List<LocationInfo> progressInfos = new ArrayList<LocationInfo>();
    private final Map<LocationInfo, Object> serverLocations = new HashMap<LocationInfo, Object>();
    private final List<ChannelFuture> bindFutures = new ArrayList<ChannelFuture>();
    private final List<ChannelFuture> connectFutures = new ArrayList<ChannelFuture>();

    private final Channel channel = new DefaultLocalClientChannelFactory().newChannel(pipeline(new SimpleChannelHandler()));
    private final ChannelFuture startedFuture = Channels.future(channel);
    private final RobotCompletionFutureImpl finishedFuture = new RobotCompletionFutureImpl(channel, true);
    private final DefaultChannelGroup serverChannels = new DefaultChannelGroup();
    private final DefaultChannelGroup clientChannels = new DefaultChannelGroup();
    private final Map<LocationInfo, Throwable> failedCauses = new HashMap<LocationInfo, Throwable>();

    private String expectedScript;
    private Configuration configuration;
    private AstScriptNode scriptAST;
    private ChannelFuture preparedFuture;
    private volatile boolean destroyed;

    public Robot() {
        listenForFinishedFuture();
    }

    public RobotCompletionFuture getScriptCompleteFuture() {
        return finishedFuture;
    }

    public ChannelFuture prepare(String script) throws Exception {

        if (preparedFuture != null) {
            throw new IllegalStateException("Script already prepared");
        }

        this.expectedScript = script;

        final boolean debugLogEnabled = LOGGER.isDebugEnabled();

        final ScriptParser parser = new Parser();
        scriptAST = parser.parse(new ByteArrayInputStream(expectedScript.getBytes(UTF_8)));

        if (debugLogEnabled) {
            LOGGER.debug("script parsed");
        }

        final GenerateConfigurationVisitor visitor = new GenerateConfigurationVisitor();
        configuration = scriptAST.accept(visitor, new GenerateConfigurationVisitor.State());

        if (debugLogEnabled) {
            LOGGER.debug("configuration created");
        }

        preparedFuture = bindServers();

        /* Iterate over the set of completion handlers. */
        for (final CompletionHandler h : configuration.getCompletionHandlers()) {
            if (debugLogEnabled) {
                LOGGER.debug("Adding listener for a completion future");
            }
            /* Add the completion future */
            final ChannelFuture f = h.getHandlerFuture();
            completionFutures.add(f);

            /*
             * Listen for each completion future and grab its location info.
             * This is the last command or event (not implicit) that
             * succeeds
             */
            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(final ChannelFuture future) throws Exception {
                    LocationInfo location = h.getProgressInfo();
                    if (debugLogEnabled) {
                        LOGGER.debug("Completion future done. Location info is " + location);
                    }
                    /*
                     * An accept or connect that never connected will have a
                     * null location. Don't include these.
                     */
                    if (location != null) {
                        progressInfos.add(location);
                    }

                    Throwable cause = future.getCause();
                    if (cause != null) {
                        if (debugLogEnabled) {
                            LOGGER.error("channel failed with cause: ", cause);
                        } else {
                            LOGGER.error("channel failed with cause: " + cause);
                        }
                        failedCauses.put(h.getStreamStartLocation(), cause);
                    }
                }
            });
        }

        // We start listening before start because one can abort before start.
        listenForScriptCompletion();

        return preparedFuture;
    }

    public ChannelFuture prepareAndStart(String script) throws Exception {
        ChannelFuture prepareFuture = prepare(script);
        prepareFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(final ChannelFuture future) throws Exception {
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

        final boolean infoLogEnabled = LOGGER.isInfoEnabled();

        /* Connect to any clients */
        for (final ClientBootstrap client : configuration.getClientBootstraps()) {

            if (infoLogEnabled) {
                LOGGER.debug("Connecting to remote address " + client.getOption("remoteAddress"));
            }

            ChannelFuture connectFuture = client.connect();
            connectFutures.add(connectFuture);
            clientChannels.add(connectFuture.getChannel());
        }

        /*
         * If we have no completion futures it means that there was an error or
         * otherwise we have the null script
         */
        if (completionFutures.isEmpty() && !scriptAST.toString().equals("")) {
            throw new RobotException("No Completion Futures exists");
        }

        startedFuture.setSuccess();
        return startedFuture;
    }

    public RobotCompletionFuture abort() {

        if (!finishedFuture.isDone()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Aborting script");
            }
            finishedFuture.cancel();
        }

        return finishedFuture;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public boolean destroy() {

        if (destroyed) {
            return true;
        }

        abort();

        try {
            releaseExternalResources();
        } catch (Exception e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Caught exception releasing resources", e);
            }
            return false;
        }

        return destroyed = true;
    }

    private void listenForScriptCompletion() {
        /*
         * OK. Now listen for the set of all completion futures so that we can
         * tell the client when we are done
         */
        ChannelFuture executionFuture = new CompositeChannelFuture<ChannelFuture>(channel, completionFutures);

        executionFuture.addListener(new ChannelFutureListener() {

            @Override
            public void operationComplete(final ChannelFuture future) throws Exception {

                final boolean debugLogEnabled = LOGGER.isDebugEnabled();
                final String finishedStatus = future.isSuccess() ? "SUCCESS" : "FAILED";

                LOGGER.debug("script completion futures finished with status: " + finishedStatus);

                if (debugLogEnabled) {
                    StringBuilder sb = new StringBuilder();
                    for (LocationInfo progressInfo : progressInfos) {
                        sb.append(progressInfo + ",");
                    }
                    LOGGER.debug("ProgressInfos at script completion: " + sb);
                }

                /*
                 * We need to map our progressInfos to streams so that we can create the observed script. After running the
                 * GatherStreamsLocationVisitor our results are in state.results.
                 */
                final GatherStreamsLocationVisitor.State state = new GatherStreamsLocationVisitor.State(progressInfos,
                        serverLocations);

                scriptAST.accept(new GatherStreamsLocationVisitor(), state);

                // Create observed Script
                PlayBackScript o = new PlayBackScript(expectedScript, state.results, failedCauses);
                String observedScript = o.createPlayBackScript();

                detachAllPipelines();

                // Cancel any pending binds and connects
                for (ChannelFuture f : bindFutures) {
                    f.cancel();
                }
                for (ChannelFuture f : connectFutures) {
                    f.cancel();
                }

                // Close server and client channels
                closeChannels();

                if (debugLogEnabled) {
                    LOGGER.debug("Observed:\n" + observedScript);
                }

                if (finishedFuture.isDone()) {
                    finishedFuture.setObservedScript(observedScript);
                } else {
                    finishedFuture.setSuccess(observedScript);
                }
            }

        });
    }

    // If we are canceling we have to cancel the script execution.
    // And then ... in all cases we need to release external resources.
    private void listenForFinishedFuture() {
        finishedFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(final ChannelFuture future) throws Exception {

                if (future.isCancelled()) {
                    if (configuration != null) {

                        for (final CompletionHandler h : configuration.getCompletionHandlers()) {

                            // Cancel the completion handler
                            ChannelFuture cancelFuture = h.cancel();

                            boolean isDefaultChannelFuture = cancelFuture instanceof DefaultChannelFuture;

                            // Edge case. Normally the cancel occurs immediately. Only when the pipeline is not
                            // prepared (preperation event) does it not.
                            // Since we are aborting we don't care that we are blocking in the io thread.
                            if (isDefaultChannelFuture) {
                                DefaultChannelFuture.setUseDeadLockChecker(false);
                            }

                            boolean isCancelled = cancelFuture.awaitUninterruptibly(500);

                            if (isDefaultChannelFuture) {
                                DefaultChannelFuture.setUseDeadLockChecker(true);
                            }

                            if (!isCancelled) {
                                // Force the completion handler future to success if it does not cancel in half a second
                                h.getHandlerFuture().setSuccess();
                            }
                        }
                    } else {
                        // Then we just get the empty script
                        LOGGER.debug("Abort received but script not prepared");
                        finishedFuture.setObservedScript("");
                    }
                }
            }
        });
    }

    private void releaseExternalResources() {
        if (configuration != null) {
            for (final ServerBootstrap server : configuration.getServerBootstraps()) {
                server.releaseExternalResources();
            }
            for (final ClientBootstrap client : configuration.getClientBootstraps()) {
                client.releaseExternalResources();
            }
        }
    }

    private void detachAllPipelines() {

        // We need some kind of handler to avoid warnings.
        ChannelHandler finalHandler = new SimpleChannelHandler() {
            @Override
            public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
                super.handleDownstream(ctx, e);
            }

            @Override
            public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
                super.handleUpstream(ctx, e);
            }

        };

        /*
         * Set the pipelines to empty just in case some one trys to connect before we close the server channel Or just in
         * case one of the client bootstraps havent connected yet ... dont think that is poosible
         */
        for (ServerBootstrap bootstrap : configuration.getServerBootstraps()) {
            bootstrap.setPipelineFactory(pipelineFactory(pipeline(finalHandler)));
        }
        for (ClientBootstrap bootstrap : configuration.getClientBootstraps()) {
            bootstrap.setPipelineFactory(pipelineFactory(pipeline(finalHandler)));
        }
        /*
         * Remove all the handlers from any existing channels. The problem we are solving here is that when a script is
         * aborted we set the pipeline future of the completion handler to success. However, this does not cause earlier
         * pipeline futures to succeed. As such if there are any barriers. A subsequent close will end up getting queued and
         * will never end. Another option would be when be to cancel the pipeline future. And make that cancel on a composite
         * cancel all its containing futures. However, it does not seem right to do that for cancel, but not setSuccess and
         * setFailure. But maybe we can do that too. But ... removing all the handlers seems cleaner anyway. Why have events
         * flowing through the system do to us closing the channels when the script is already considered complete.
         */
        for (Channel c : clientChannels) {
            ChannelPipeline pipeline = c.getPipeline();
            for (ChannelHandler handler : pipeline.toMap().values()) {
                pipeline.remove(handler);
            }
            pipeline.addLast("SCRIPTDONEHANDLER", finalHandler);
        }
    }

    private void closeChannels() {
        final ChannelGroupFuture closeFuture = serverChannels.close();
        closeFuture.addListener(new ChannelGroupFutureListener() {
            @Override
            public void operationComplete(final ChannelGroupFuture future) {
                clientChannels.close();
            }
        });
    }

    private ChannelFuture bindServers() {

        /* Accept's ... Robot acting as a server */
        for (final ServerBootstrap server : configuration.getServerBootstraps()) {

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Binding to address " + server.getOption("localAddress"));
            }

            final LocationInfo location = (LocationInfo) server.getOption("locationInfo");

            assert !serverLocations.containsKey(location) : "There is already a location " + location
                    + " for this server " + server.getOption("localAddress");

            /* Keep track of the client channels */
            server.setParentHandler(new SimpleChannelHandler() {
                @Override
                public void childChannelOpen(ChannelHandlerContext ctx, ChildChannelStateEvent e) throws Exception {
                    clientChannels.add(e.getChildChannel());
                }
            });


            // Bind Asynchronously
            ChannelFuture bindFuture = server.bindAsync();

            // Add to out serverChannel Group
            serverChannels.add(bindFuture.getChannel());

            // Add to our list of bindFutures so we can cancel them later on a possible abort
            bindFutures.add(bindFuture);

            // Listen for the bindFuture.
            bindFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(final ChannelFuture future) throws Exception {

                    final boolean isDebugEnabled = LOGGER.isDebugEnabled();

                    if (future.isSuccess()) {
                        if (isDebugEnabled) {
                            LOGGER.debug("Successfully bound to " + server.getOption("localAddress"));
                        }
                        // Add to our list of serverLocations ... which contain the locationInfo's of successfully bound
                        // server channels
                        serverLocations.put((LocationInfo) server.getOption("locationInfo"), null);

                    } else {
                        Throwable cause = future.getCause();
                        String errMsg = "Bind to " + server.getOption("localAddress") + " failed.";

                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.error(errMsg, cause);
                        } else {
                            LOGGER.error(errMsg + "Due to " + cause);
                        }
                        /*
                         * Grab the set of completion handlers for the server. This is the set of completion futures for the
                         * Accept stream.
                         */
                        @SuppressWarnings("unchecked")
                        final Collection<ChannelFuture> serverCompletionFutures =
                                (Collection<ChannelFuture>) server.getOption("completionFutures");

                        /* Set all the futures to fail. If we couldn't bind */
                        for (ChannelFuture f : serverCompletionFutures) {
                            f.setFailure(cause);
                        }
                    }

                }
            });
        }
        // What should prepared mean ... server channels have all completed binding or just that they started.
        // Initially I was thinking that it should be when they are done. But I'm not so sure.
        // In that case what should happen if a subset of the binds fail and a subset succeed? What should happen if they all
        // fail? I think in either case the robot should generate an observed script with the exception events in place of
        // the accept lines. This is why I choose to return a successful future rather than some composite of the bind
        // futures.
        return Channels.succeededFuture(channel);
    }

}
