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
package org.kaazing.k3po.driver.internal.netty.bootstrap.bbosh;

import static java.lang.String.format;
import static org.jboss.netty.channel.Channels.fireChannelBound;
import static org.jboss.netty.channel.Channels.fireChannelClosed;
import static org.jboss.netty.channel.Channels.fireChannelUnbound;

import java.net.URI;
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
import org.kaazing.k3po.driver.internal.netty.bootstrap.BootstrapFactory;
import org.kaazing.k3po.driver.internal.netty.bootstrap.ServerBootstrap;
import org.kaazing.k3po.driver.internal.netty.bootstrap.channel.AbstractServerChannelSink;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddressFactory;

public class BBoshServerChannelSink extends AbstractServerChannelSink<BBoshServerChannel> {

    private final BBoshHandshakeChildChannelPipelineFactory pipelineFactory;
    private final ConcurrentNavigableMap<URI, BBoshServerChannel> bboshBindings;

    public BBoshServerChannelSink() {
        this(new ConcurrentSkipListMap<URI, BBoshServerChannel>());
    }

    private BBoshServerChannelSink(ConcurrentNavigableMap<URI, BBoshServerChannel> bboshBindings) {
        this.pipelineFactory = new BBoshHandshakeChildChannelPipelineFactory(bboshBindings);
        this.bboshBindings = bboshBindings;
    }

    public void setAddressFactory(ChannelAddressFactory addressFactory) {
        pipelineFactory.setAddressFactory(addressFactory);
    }

    @Override
    public void setBootstrapFactory(BootstrapFactory bootstrapFactory) {
        super.setBootstrapFactory(bootstrapFactory);
        pipelineFactory.setBootstrapFactory(bootstrapFactory);
    }

    @Override
    protected void bindRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {

        final BBoshServerChannel bboshBindChannel = (BBoshServerChannel) evt.getChannel();
        final ChannelFuture bboshBindFuture = evt.getFuture();
        final ChannelAddress bboshLocalAddress = (ChannelAddress) evt.getValue();
        URI bboshLocation = bboshLocalAddress.getLocation();

        BBoshServerChannel bboshBoundChannel = bboshBindings.putIfAbsent(bboshLocation, bboshBindChannel);
        if (bboshBoundChannel != null) {
            bboshBindFuture.setFailure(new ChannelException(format("Duplicate bind failed: %s", bboshLocation)));
        }

        ChannelAddress address = bboshLocalAddress.getTransport();
        String schemeName = address.getLocation().getScheme();
        String bboshSchemeName = bboshLocalAddress.getLocation().getScheme();

        ServerBootstrap bootstrap = bootstrapFactory.newServerBootstrap(schemeName);
        bootstrap.setParentHandler(createParentHandler(bboshBindChannel));
        bootstrap.setPipelineFactory(pipelineFactory);
        bootstrap.setOption(format("%s.nextProtocol", schemeName), bboshSchemeName);

        // bind transport
        ChannelFuture bindFuture = bootstrap.bindAsync(address);
        if (bindFuture.isDone()) {
            handleBBoshTransportBindComplete(bboshBindChannel, bboshBindFuture, bboshLocalAddress, bindFuture);
        }
        else {
            bindFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture bindFuture) throws Exception {
                    handleBBoshTransportBindComplete(bboshBindChannel, bboshBindFuture, bboshLocalAddress, bindFuture);
                }
            });
        }
    }

    @Override
    protected void unbindRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
        final BBoshServerChannel bboshUnbindChannel = (BBoshServerChannel) evt.getChannel();
        final ChannelFuture bboshUnbindFuture = evt.getFuture();
        ChannelAddress bboshLocalAddress = bboshUnbindChannel.getLocalAddress();
        URI bboshLocation = bboshLocalAddress.getLocation();

        if (!bboshBindings.remove(bboshLocation, bboshUnbindChannel)) {
            bboshUnbindFuture.setFailure(new ChannelException("Channel not bound"));
            return;
        }

        Channel transport = bboshUnbindChannel.getTransport();
        ChannelFuture unbindFuture = transport.unbind();
        if (unbindFuture.isDone()) {
            handleBBoshTransportUnbindComplete(bboshUnbindChannel, bboshUnbindFuture, unbindFuture);
        }
        else {
            unbindFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture unbindFuture) throws Exception {
                    handleBBoshTransportUnbindComplete(bboshUnbindChannel, bboshUnbindFuture, unbindFuture);
                }
            });
        }
    }

    @Override
    protected void closeRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
        final BBoshServerChannel bboshCloseChannel = (BBoshServerChannel) evt.getChannel();
        final ChannelFuture bboshCloseFuture = evt.getFuture();
        boolean wasBound = bboshCloseChannel.isBound();
        if (bboshCloseChannel.setClosed()) {
            if (wasBound) {
                unbindRequested(pipeline, evt);
            }

            Channel transport = bboshCloseChannel.getTransport();
            if (transport != null) {
                ChannelFuture closeFuture = transport.close();
                if (closeFuture.isDone()) {
                    handleBBoshTransportCloseComplete(bboshCloseChannel, bboshCloseFuture, closeFuture);
                }
                else {
                    closeFuture.addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture closeFuture) throws Exception {
                            handleBBoshTransportCloseComplete(bboshCloseChannel, bboshCloseFuture, closeFuture);
                        }
                    });
                }
            }
        }
    }

    private ChannelHandler createParentHandler(BBoshServerChannel channel) {
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

    private static void handleBBoshTransportBindComplete(
            final BBoshServerChannel bboshBindChannel,
            final ChannelFuture bboshBindFuture,
            final ChannelAddress bboshLocalAddress, ChannelFuture future) {

        if (future.isSuccess()) {
            bboshBindChannel.setTransport(future.getChannel());
            bboshBindChannel.setLocalAddress(bboshLocalAddress);
            bboshBindChannel.setBound();

            fireChannelBound(bboshBindChannel, bboshBindChannel.getLocalAddress());
            bboshBindFuture.setSuccess();
        }
        else {
            bboshBindFuture.setFailure(future.getCause());
        }
    }


    private static void handleBBoshTransportUnbindComplete(
            final BBoshServerChannel bboshUnbindChannel,
            final ChannelFuture bboshUnbindFuture, ChannelFuture future) {

        if (future.isSuccess()) {
            fireChannelUnbound(bboshUnbindChannel);
            bboshUnbindFuture.setSuccess();
        }
        else {
            bboshUnbindFuture.setFailure(future.getCause());
        }
    }

    private static void handleBBoshTransportCloseComplete(
            BBoshServerChannel bboshCloseChannel,
            ChannelFuture bboshCloseFuture,
            ChannelFuture closeFuture) {

        if (closeFuture.isSuccess()) {
            fireChannelClosed(bboshCloseChannel);
            bboshCloseFuture.setSuccess();
        }
        else {
            bboshCloseFuture.setFailure(closeFuture.getCause());
        }
    }

}
