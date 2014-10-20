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
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ChildChannelStateEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.ChannelGroupFutureListener;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.kaazing.robot.driver.netty.bootstrap.ServerBootstrap;
import org.kaazing.robot.driver.netty.bootstrap.channel.AbstractServerChannelSink;
import org.kaazing.robot.driver.netty.channel.ChannelAddress;

public class HttpServerChannelSink extends AbstractServerChannelSink<HttpServerChannel> {

    private final ConcurrentNavigableMap<URI, HttpServerChannel> httpBindings;
    private final ConcurrentMap<URI, HttpTransport> httpTransportsByLocation; // TODO: use address for location stack
    private final ChannelPipelineFactory pipelineFactory;

    public HttpServerChannelSink() {
        this(new ConcurrentSkipListMap<URI, HttpServerChannel>());
    }

    private HttpServerChannelSink(ConcurrentNavigableMap<URI, HttpServerChannel> httpBindings) {
        this.pipelineFactory = new HttpChildChannelPipelineFactory(httpBindings);
        this.httpBindings = httpBindings;
        this.httpTransportsByLocation = new ConcurrentHashMap<URI, HttpTransport>();
    }

    @Override
    protected void bindRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
        final HttpServerChannel httpBindChannel = (HttpServerChannel) evt.getChannel();
        final ChannelFuture httpBindFuture = evt.getFuture();
        final ChannelAddress httpLocalAddress = (ChannelAddress) evt.getValue();
        URI httpLocation = httpLocalAddress.getLocation();

        HttpServerChannel httpBoundChannel = httpBindings.putIfAbsent(httpLocation, httpBindChannel);
        if (httpBoundChannel != null) {
            httpBindFuture.setFailure(new ChannelException(format("Duplicate bind failed: %s", httpLocation)));
        }

        ChannelAddress address = httpLocalAddress.getTransport();
        URI location = address.getLocation();
        HttpTransport httpTransport = httpTransportsByLocation.get(location);
        if (httpTransport == null) {
            String schemeName = address.getLocation().getScheme();
            String httpSchemeName = httpLocalAddress.getLocation().getScheme();

            ServerBootstrap bootstrap = bootstrapFactory.newServerBootstrap(schemeName);
            bootstrap.setParentHandler(createParentHandler(httpBindChannel));
            bootstrap.setPipelineFactory(pipelineFactory);
            bootstrap.setOption(format("%s.nextProtocol", schemeName), httpSchemeName);

            // bind transport
            ChannelFuture bindFuture = bootstrap.bindAsync(address);
            HttpTransport newHttpTransport = new HttpTransport(bindFuture, 1);
            httpTransport = httpTransportsByLocation.putIfAbsent(location, newHttpTransport);
            if (httpTransport == null) {
                httpTransport = newHttpTransport;
            }
        }
        else {
            httpTransport.count.incrementAndGet();
        }

        if (httpTransport.future.isDone()) {
            handleHttpTransportBindComplete(httpBindChannel, httpBindFuture, httpLocalAddress, httpTransport.future);
        }
        else {
            httpTransport.future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    handleHttpTransportBindComplete(httpBindChannel, httpBindFuture, httpLocalAddress, future);
                }
            });
        }
    }

    @Override
    protected void unbindRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
        final HttpServerChannel httpUnbindChannel = (HttpServerChannel) evt.getChannel();
        final ChannelFuture httpUnbindFuture = evt.getFuture();
        ChannelAddress httpLocalAddress = httpUnbindChannel.getLocalAddress();
        URI httpLocation = httpLocalAddress.getLocation();

        if (!httpBindings.remove(httpLocation, httpUnbindChannel)) {
            httpUnbindFuture.setFailure(new ChannelException("Channel not bound").fillInStackTrace());
            return;
        }

        ChannelAddress address = httpLocalAddress.getTransport();
        URI location = address.getLocation();
        HttpTransport httpTransport = httpTransportsByLocation.get(location);
        assert httpTransport != null;

        if (httpTransport.count.decrementAndGet() == 0) {
            // ensure only zero count is removed
            HttpTransport oldHttpTransport = new HttpTransport(httpTransport.future);
            if (httpTransportsByLocation.remove(location, oldHttpTransport)) {
                // unbind transport
                Channel transport = httpUnbindChannel.getTransport();
                ChannelFuture unbindFuture = transport.unbind();
                if (unbindFuture.isDone()) {
                    handleHttpTransportUnbindComplete(httpUnbindChannel, httpUnbindFuture, unbindFuture);
                }
                else {
                    unbindFuture.addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture unbindFuture) throws Exception {
                            handleHttpTransportUnbindComplete(httpUnbindChannel, httpUnbindFuture, unbindFuture);
                        }
                    });
                }
            }
        }
        else {
            fireChannelUnbound(httpUnbindChannel);
            httpUnbindFuture.setSuccess();
        }
    }

    @Override
    protected void closeRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
        final HttpServerChannel httpCloseChannel = (HttpServerChannel) evt.getChannel();
        final ChannelFuture httpCloseFuture = evt.getFuture();
        boolean wasBound = httpCloseChannel.isBound();
        if (httpCloseChannel.setClosed()) {
            if (wasBound) {
                unbindRequested(pipeline, evt);
            }

            Channel transport = httpCloseChannel.getTransport();
            if (transport != null) {
                ChannelFuture closeFuture = transport.close();
                if (closeFuture.isDone()) {
                    handleHttpTransportCloseComplete(httpCloseChannel, httpCloseFuture, closeFuture);
                }
                else {
                    closeFuture.addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture closeFuture) throws Exception {
                            handleHttpTransportCloseComplete(httpCloseChannel, httpCloseFuture, closeFuture);
                        }
                    });
                }
            }
        }
    }

    private ChannelHandler createParentHandler(HttpServerChannel channel) {
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

    private static void handleHttpTransportBindComplete(
            HttpServerChannel httpBindChannel,
            ChannelFuture httpBindFuture,
            ChannelAddress httpLocalAddress,
            ChannelFuture bindFuture) {

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

    private static void handleHttpTransportUnbindComplete(
            HttpServerChannel httpUnbindChannel,
            ChannelFuture httpUnbindFuture,
            ChannelFuture unbindFuture) {

        if (unbindFuture.isSuccess()) {
            fireChannelUnbound(httpUnbindChannel);
            httpUnbindFuture.setSuccess();
        }
        else {
            httpUnbindFuture.setFailure(unbindFuture.getCause());
        }
    }

    private static void handleHttpTransportCloseComplete(
            HttpServerChannel httpCloseChannel,
            ChannelFuture httpCloseFuture,
            ChannelFuture closeFuture) {

        if (closeFuture.isSuccess()) {
            fireChannelClosed(httpCloseChannel);
            httpCloseFuture.setSuccess();
        }
        else {
            httpCloseFuture.setFailure(closeFuture.getCause());
        }
    }

    private static final class HttpTransport {
        final ChannelFuture future;
        final AtomicInteger count;

        HttpTransport(ChannelFuture future) {
            this(future, 0);
        }

        HttpTransport(ChannelFuture future, int count) {
            this.future = future;
            this.count = new AtomicInteger(count);
        }

        @Override
        public int hashCode() {
            return Objects.hash(future, count);
        }

        @Override
        public boolean equals(Object obj) {
            HttpTransport that = (HttpTransport) obj;
            return Objects.equals(this.future, that.future) &&
                    this.count.get() == that.count.get();
        }

        @Override
        public String toString() {
            return format("[future=@%d, count=%d]", Objects.hashCode(future), count.get());
        }
    }
}
