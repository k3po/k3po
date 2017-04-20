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

import static java.util.concurrent.Executors.newCachedThreadPool;
import static org.jboss.netty.channel.Channels.pipeline;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

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
import org.kaazing.k3po.driver.internal.control.handler.ControlDecoder;
import org.kaazing.k3po.driver.internal.control.handler.ControlEncoder;
import org.kaazing.k3po.driver.internal.control.handler.ControlServerHandler;
import org.kaazing.k3po.driver.internal.netty.bootstrap.BootstrapFactory;
import org.kaazing.k3po.driver.internal.netty.bootstrap.ServerBootstrap;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddressFactory;

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

    private AtomicReference<Robot> activeRobotRef  = new AtomicReference<Robot>(null);

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
        sharedWorkerPool = new ShareableWorkerPool<>(workerPool);
        clientChannelFactory = new NioClientSocketChannelFactory(clientBossPool, sharedWorkerPool);
        serverChannelFactory = new NioServerSocketChannelFactory(serverBossPool, sharedWorkerPool);

        Map<Class<?>, Object> injectables = new HashMap<>();
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

                ControlServerHandler controller = new ControlServerHandler(activeRobotRef);
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
