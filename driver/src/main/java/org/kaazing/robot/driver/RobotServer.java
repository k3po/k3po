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

import static java.util.concurrent.Executors.newCachedThreadPool;
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
import org.jboss.netty.channel.socket.nio.NioClientBossPool;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerBossPool;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioWorker;
import org.jboss.netty.channel.socket.nio.NioWorkerPool;
import org.jboss.netty.channel.socket.nio.ShareableWorkerPool;
import org.jboss.netty.handler.logging.LoggingHandler;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.kaazing.robot.driver.control.handler.ControlDecoder;
import org.kaazing.robot.driver.control.handler.ControlEncoder;
import org.kaazing.robot.driver.control.handler.ControlServerHandler;
import org.kaazing.robot.driver.netty.bootstrap.BootstrapFactory;
import org.kaazing.robot.driver.netty.bootstrap.ServerBootstrap;
import org.kaazing.robot.driver.netty.channel.ChannelAddress;
import org.kaazing.robot.driver.netty.channel.ChannelAddressFactory;

public class RobotServer {

    private final ChannelGroup channelGroup;
    private final List<ControlServerHandler> controlHandlers;
    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(RobotServer.class);

    private BootstrapFactory bootstrapFactory;
    private final URI controlURI;
    private Channel serverChannel;
    private final boolean verbose;
    private final ClassLoader scriptLoader;

    private ShareableWorkerPool<NioWorker> sharedWorkerPool;
    private NioClientSocketChannelFactory clientChannelFactory;
    private NioServerSocketChannelFactory serverChannelFactory;

    public RobotServer(URI controlURI, boolean verbose, ClassLoader scriptLoader) {
        this.controlURI = controlURI;
        this.verbose = verbose;
        this.scriptLoader = scriptLoader;
        this.channelGroup = new DefaultChannelGroup("robot-server");
        this.controlHandlers = new CopyOnWriteArrayList<>();
    }

    public void start() throws Exception {
        if (controlURI == null) {
            throw new NullPointerException("controlURI");
        }

        Map<String, Object> options = new HashMap<>();
        // TODO: options.put("tcp.transport", "socks://...");

        final ChannelAddressFactory addressFactory = ChannelAddressFactory.newChannelAddressFactory();
        ChannelAddress localAddress = addressFactory.newChannelAddress(controlURI, options);

        NioClientBossPool clientBossPool = new NioClientBossPool(newCachedThreadPool(), 1);
        NioServerBossPool serverBossPool = new NioServerBossPool(newCachedThreadPool(), 1);
        NioWorkerPool workerPool = new NioWorkerPool(newCachedThreadPool(), 1);
        sharedWorkerPool = new ShareableWorkerPool<NioWorker>(workerPool);
        clientChannelFactory = new NioClientSocketChannelFactory(clientBossPool, sharedWorkerPool);
        serverChannelFactory = new NioServerSocketChannelFactory(serverBossPool, sharedWorkerPool);

        Map<Class<?>, Object> injectables = new HashMap<Class<?>, Object>();
        injectables.put(ChannelAddressFactory.class, addressFactory);
        injectables.put(NioClientSocketChannelFactory.class, clientChannelFactory);
        injectables.put(NioServerSocketChannelFactory.class, serverChannelFactory);

        bootstrapFactory = BootstrapFactory.newBootstrapFactory(injectables);

        String transportName = controlURI.getScheme();
        ServerBootstrap server = bootstrapFactory.newServerBootstrap(transportName);

        server.setPipelineFactory(new ChannelPipelineFactory() {

            @Override
            public ChannelPipeline getPipeline() throws Exception {

                ChannelPipeline pipeline = pipeline();

                ChannelHandler decoder = new ControlDecoder();
                pipeline.addLast("control.decoder", decoder);

                ChannelHandler encoder = new ControlEncoder();
                pipeline.addLast("control.encoder", encoder);

                if (verbose) {
                    ChannelHandler logging = new LoggingHandler("robot.server", false);
                    pipeline.addLast("control.logging", logging);
                }

                ControlServerHandler controller = new ControlServerHandler();
                controller.setAddressFactory(addressFactory);
                controller.setBootstrapFactory(bootstrapFactory);
                controller.setScriptLoader(scriptLoader);
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

        if (clientChannelFactory != null) {
            LOGGER.debug("Releasing tcp client channel factory");
            clientChannelFactory.shutdown();
            clientChannelFactory.releaseExternalResources();
            LOGGER.debug("Released tcp client channel factory");
        }

        if (serverChannelFactory != null) {
            LOGGER.debug("Releasing tcp server channel factory");
            serverChannelFactory.shutdown();
            serverChannelFactory.releaseExternalResources();
            LOGGER.debug("Released tcp server channel factory");
        }

        if (sharedWorkerPool != null) {
            LOGGER.debug("Destroying shared worker pool");
            sharedWorkerPool.destroy();
            LOGGER.debug("Destroyed shared worker pool.");
        }

        if (bootstrapFactory != null) {
            LOGGER.debug("Releasing external resources");
            bootstrapFactory.releaseExternalResources();
            LOGGER.debug("External resources released.");
        }
    }

    public void join() throws InterruptedException {
        if (serverChannel != null) {
            serverChannel.getCloseFuture().await();
        }
    }
}
