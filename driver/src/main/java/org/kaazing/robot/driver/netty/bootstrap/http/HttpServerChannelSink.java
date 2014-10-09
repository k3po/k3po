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

package org.kaazing.robot.driver.netty.bootstrap.http;

import static java.lang.String.format;
import static org.jboss.netty.channel.Channels.fireChannelBound;
import static org.jboss.netty.channel.Channels.fireChannelClosed;
import static org.jboss.netty.channel.Channels.fireChannelUnbound;

import java.net.URI;
import java.util.Objects;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ChildChannelStateEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.ChannelGroupFutureListener;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.kaazing.robot.driver.netty.bootstrap.ServerBootstrap;
import org.kaazing.robot.driver.netty.bootstrap.channel.AbstractServerChannelSink;
import org.kaazing.robot.driver.netty.channel.ChannelAddress;

public class HttpServerChannelSink extends AbstractServerChannelSink<HttpServerChannel> {

    private final ConcurrentNavigableMap<URI, HttpServerChannel> httpBindings;

    public HttpServerChannelSink() {
        this(new ConcurrentSkipListMap<URI, HttpServerChannel>());
    }

    private HttpServerChannelSink(ConcurrentNavigableMap<URI, HttpServerChannel> httpBindings) {
        super(new HttpChildChannelPipelineFactory(httpBindings));
        this.httpBindings = httpBindings;
    }

    @Override
    protected void bindRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
        final HttpServerChannel httpBindChannel = (HttpServerChannel) evt.getChannel();
        final ChannelFuture httpBindFuture = evt.getFuture();
        final ChannelAddress httpLocalAddress = (ChannelAddress) evt.getValue();
        URI httpLocation = httpLocalAddress.getLocation();

        HttpServerChannel httpBoundChannel = httpBindings.putIfAbsent(httpLocation, httpBindChannel);
        if (httpBoundChannel == null) {
            httpBoundChannel = httpBindChannel;
        }
        final HttpServerChannel httpBoundChannel0 = httpBoundChannel;

        if (!Objects.equals(httpBindChannel.getConfig(), httpBoundChannel.getConfig()) ||
                !Objects.equals(httpBindChannel.getTransport(), httpBoundChannel.getTransport())) {
            httpBindFuture.setFailure(new ChannelException(format("Duplicate bind failed: %s", httpLocation)));
        }
        else {
            if (httpBoundChannel.getBindCount().getAndIncrement() == 0) {
                ChannelAddress address = httpLocalAddress.getTransport();
                String schemeName = address.getLocation().getScheme();
                String httpSchemeName = httpLocalAddress.getLocation().getScheme();

                ServerBootstrap bootstrap = bootstrapFactory.newServerBootstrap(schemeName);
                bootstrap.setParentHandler(createParentHandler(httpBindChannel));
                bootstrap.setPipelineFactory(pipelineFactory);
                bootstrap.setOption(format("%s.nextProtocol", schemeName), httpSchemeName);

                ChannelFuture bindFuture = bootstrap.bindAsync(address);
                bindFuture.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture bindFuture) throws Exception {
                        if (bindFuture.isSuccess()) {
                            httpBindChannel.setTransport(bindFuture.getChannel());
                            httpBindChannel.setLocalAddress(httpLocalAddress);
                            httpBindChannel.setBound();

                            fireChannelBound(httpBindChannel, httpBindChannel.getLocalAddress());
                            httpBindFuture.setSuccess();
                        }
                        else {
                            httpBindFuture.setFailure(bindFuture.getCause());
                        }
                    }
                });
            }
            else {
                httpBindChannel.setTransport(httpBoundChannel0.getTransport());
                httpBindChannel.setLocalAddress(httpBoundChannel0.getLocalAddress());
                httpBindChannel.setBound();

                fireChannelBound(httpBindChannel, httpBindChannel.getLocalAddress());
                httpBindFuture.setSuccess();
            }
        }
    }

    @Override
    protected void unbindRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
        final HttpServerChannel unbindHttpChannel = (HttpServerChannel) evt.getChannel();
        final ChannelFuture unbindHttpFuture = evt.getFuture();
        ChannelAddress httpLocalAddress = unbindHttpChannel.getLocalAddress();
        URI httpLocation = httpLocalAddress.getLocation();

        HttpServerChannel boundHttpChannel = httpBindings.get(httpLocation);
        assert Objects.equals(unbindHttpChannel.getConfig(), boundHttpChannel.getConfig());

        if (boundHttpChannel.getBindCount().decrementAndGet() == 0) {
            httpBindings.remove(httpLocation, boundHttpChannel);
            Channel transport = boundHttpChannel.getTransport();
            transport.unbind().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        fireChannelUnbound(unbindHttpChannel);
                        unbindHttpFuture.setSuccess();
                    }
                    else {
                        unbindHttpFuture.setFailure(future.getCause());
                    }
                }
            });
        }
        else {
            fireChannelUnbound(unbindHttpChannel);
            evt.getFuture().setSuccess();
        }
    }

    @Override
    protected void closeRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
        final HttpServerChannel closeHttpChannel = (HttpServerChannel) evt.getChannel();
        final ChannelFuture closeHttpFuture = evt.getFuture();
        boolean wasBound = closeHttpChannel.isBound();
        if (closeHttpChannel.setClosed()) {
            if (wasBound) {
                unbindRequested(pipeline, evt);
            }

            Channel transport = closeHttpChannel.getTransport();
            if (transport != null) {
                transport.close().addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (future.isSuccess()) {
                            fireChannelClosed(closeHttpChannel);
                            closeHttpFuture.setSuccess();
                        }
                        else {
                            closeHttpFuture.setFailure(future.getCause());
                        }
                    }
                });
            }
        }
    }

    protected ChannelHandler createParentHandler(HttpServerChannel channel) {
        return new SimpleChannelHandler() {

            private final ChannelGroup childChannels = new DefaultChannelGroup();

            @Override
            public void childChannelOpen(ChannelHandlerContext ctx, ChildChannelStateEvent e) throws Exception {
                childChannels.add(e.getChildChannel());
                super.childChannelOpen(ctx, e);
            }

            @Override
            public void childChannelClosed(ChannelHandlerContext ctx, ChildChannelStateEvent e) throws Exception {
                childChannels.remove(e.getChildChannel());
                super.childChannelClosed(ctx, e);
            }

            @Override
            public void closeRequested(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
                // close up any transports previously in use for HTTP
                childChannels.close().addListener(new ChannelGroupFutureListener() {
                    @Override
                    public void operationComplete(ChannelGroupFuture future) throws Exception {
                        ctx.sendDownstream(e);
                    }
                });
            }

        };
    }

}
