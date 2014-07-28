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
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.kaazing.robot.driver.control.handler.ControlServerHandler;
import org.kaazing.robot.driver.control.handler.HttpControlRequestDecoder;
import org.kaazing.robot.driver.control.handler.HttpControlResponseEncoder;
import org.kaazing.robot.driver.netty.bootstrap.BootstrapFactory;
import org.kaazing.robot.driver.netty.bootstrap.ServerBootstrap;
import org.kaazing.robot.driver.netty.bootstrap.SingletonBootstrapFactory;
import org.kaazing.robot.driver.netty.channel.ChannelAddress;
import org.kaazing.robot.driver.netty.channel.ChannelAddressFactory;

public class HttpControlledRobotServer implements RobotServer {

    private final ChannelGroup channelGroup;
    private final List<ControlServerHandler> controlHandlers;
    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(HttpControlledRobotServer.class);

    private ServerBootstrap server;
    private final URI acceptURI;
    private Channel serverChannel;

    protected HttpControlledRobotServer(URI acceptURI) {
        this.acceptURI = acceptURI;
        channelGroup = new DefaultChannelGroup("http-robot-server");
        controlHandlers = new CopyOnWriteArrayList<ControlServerHandler>();
    }

    @Override
    public void start() throws Exception {
        if (acceptURI == null) {
            throw new NullPointerException("acceptURI");
        }

        Map<String, Object> options = new HashMap<String, Object>();
        ChannelAddressFactory addressFactory = ChannelAddressFactory.newChannelAddressFactory();
        // Use tcp and layer http on top until nuklei is ready
        ChannelAddress localAddress = addressFactory.newChannelAddress(acceptURI, options);

        BootstrapFactory factory = SingletonBootstrapFactory.getInstance();
        server = factory.newServerBootstrap(acceptURI.getScheme());

        server.setPipelineFactory(new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = pipeline();

                ChannelHandler httpRequestDecoder = new HttpRequestDecoder();
                pipeline.addLast("http.request.decoder", httpRequestDecoder);

                ChannelHandler httpResponseEncoder = new HttpResponseEncoder();
                pipeline.addLast("http.response.encoder", httpResponseEncoder);

                ChannelHandler encoder = new HttpControlResponseEncoder();
                pipeline.addLast("http.control.response.encoder", encoder);

                ChannelHandler decoder = new HttpControlRequestDecoder();
                pipeline.addLast("http.control.request.decoder", decoder);

                ChannelHandler controller = new ControlServerHandler();
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
                final ControlServerHandler controller = (ControlServerHandler) childChannel.getPipeline()
                        .getContext("control.handler").getHandler();
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

    @Override
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
            LOGGER.debug("Releasing external resources");
            server.releaseExternalResources();
            if (isDebugEnabled) {
                LOGGER.debug("External resources released.");
            }
        }
    }

    @Override
    public void join() throws InterruptedException {
        if (serverChannel != null) {
            serverChannel.getCloseFuture().await();
        }
    }

}
