/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot;

import static org.jboss.netty.channel.Channels.pipeline;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeoutException;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChildChannelStateEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.handler.logging.LoggingHandler;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

import org.kaazing.netty.bootstrap.BootstrapFactory;
import org.kaazing.netty.bootstrap.ServerBootstrap;
import org.kaazing.netty.channel.ChannelAddress;
import org.kaazing.netty.channel.ChannelAddressFactory;
import org.kaazing.robot.control.handler.ControlDecoder;
import org.kaazing.robot.control.handler.ControlDecoderCompatibility;
import org.kaazing.robot.control.handler.ControlEncoder;
import org.kaazing.robot.control.handler.ControlEncoderCompatibility;
import org.kaazing.robot.control.handler.ControlServerHandler;
import org.kaazing.robot.lang.parser.ScriptParserImpl;
import org.kaazing.robot.netty.bootstrap.SingletonBootstrapFactory;

public class RobotServer {

    private final ChannelGroup channelGroup;
    private final List<ControlServerHandler> controlHandlers;
    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(RobotServer.class);

    private ServerBootstrap server;
    private URI acceptURI;
    private Channel serverChannel;

    private boolean verbose;

    public RobotServer() {
        channelGroup = new DefaultChannelGroup("robot-server");
        controlHandlers = new CopyOnWriteArrayList<ControlServerHandler>();
    }

    public void setAccept(URI acceptURI) {
        this.acceptURI = acceptURI;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public void start() throws Exception {
        start(ScriptParserImpl.EARLIEST_SUPPORTED_FORMAT);
    }

    public void start(final String format) throws Exception {

        if (acceptURI == null) {
            throw new NullPointerException("acceptURI");
        }

        Map<String, Object> options = new HashMap<String, Object>();
        // TODO: options.put("tcp.transport", "socks://...");

        ChannelAddressFactory addressFactory = ChannelAddressFactory.newChannelAddressFactory();
        ChannelAddress localAddress = addressFactory.newChannelAddress(acceptURI, options);

        BootstrapFactory factory = SingletonBootstrapFactory.getInstance();
        server = factory.newServerBootstrap(acceptURI.getScheme());

        server.setPipelineFactory(new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() throws Exception {

                ChannelPipeline pipeline = pipeline();

                ChannelHandler decoder = new ControlDecoder();
                pipeline.addLast("control.decoder", decoder);

                ChannelHandler decoderCompatibility = new ControlDecoderCompatibility();
                pipeline.addLast("control.decoder.compatibility", decoderCompatibility);

                ChannelHandler encoder = new ControlEncoder();
                pipeline.addLast("control.encoder", encoder);

                ChannelHandler encoderCompatibility = new ControlEncoderCompatibility();
                pipeline.addLast("control.encoder.compatibility", encoderCompatibility);

                if (verbose) {
                    ChannelHandler logging = new LoggingHandler("robot.server", false);
                    pipeline.addLast("control.logging", logging);
                }

                ChannelHandler controller = new ControlServerHandler(format);
                pipeline.addLast("control.handler", controller);

                return pipeline;
            }
        });

        /* Keep track of all open channels */
        server.setParentHandler(new SimpleChannelHandler() {

            @Override
            public void childChannelOpen(ChannelHandlerContext ctx, ChildChannelStateEvent e) throws Exception {
                LOGGER.debug("Control Channel Opened");
                Channel childChannel = e.getChildChannel();
                channelGroup.add(childChannel);
                final ControlServerHandler controller =
                        (ControlServerHandler) childChannel.getPipeline().getContext("control.handler").getHandler();
                // Add the controller to our list
                controlHandlers.add(controller);


                 // And remove it when the channel is closed.
                controller.getChannelClosedFuture().addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        controlHandlers.remove(controller);
                    }
                });
            }

        });

        serverChannel = server.bind(localAddress);
    }

    public void stop() throws TimeoutException {
        boolean isDebugEnabled = LOGGER.isDebugEnabled();
        if (serverChannel != null) {

            serverChannel.close().awaitUninterruptibly(2000);

            if (isDebugEnabled) {
                LOGGER.debug("Server control channel closed.");
            }
        }

        channelGroup.close().awaitUninterruptibly(2000);

        if (isDebugEnabled) {
            LOGGER.debug("Control channels closed.");
        }

        // Note it is important that we wait for the control handler to process the channelClosed
        // event, otherwise there will be a race between it and releasing resources.
        for (ControlServerHandler controller : controlHandlers) {
            controller.getChannelClosedFuture().awaitUninterruptibly(2000);
            // controller.completeShutDown(2000);
        }


        if (server != null) {
            LOGGER.info("Releasing external resources");
            server.releaseExternalResources();
            if (isDebugEnabled) {
                LOGGER.debug("External resources released.");
            }
        }
    }

    public void join() throws InterruptedException {
        if (serverChannel != null) {
            serverChannel.getCloseFuture().await();
        }
    }
}
