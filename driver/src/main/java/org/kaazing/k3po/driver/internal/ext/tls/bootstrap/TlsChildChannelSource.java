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
package org.kaazing.k3po.driver.internal.ext.tls.bootstrap;

import static org.jboss.netty.channel.Channels.fireChannelBound;
import static org.jboss.netty.channel.Channels.fireChannelClosed;
import static org.jboss.netty.channel.Channels.fireChannelConnected;
import static org.jboss.netty.channel.Channels.fireChannelDisconnected;
import static org.jboss.netty.channel.Channels.fireChannelOpen;
import static org.jboss.netty.channel.Channels.fireChannelUnbound;
import static org.jboss.netty.channel.Channels.fireExceptionCaught;
import static org.jboss.netty.channel.Channels.fireMessageReceived;
import static org.kaazing.k3po.driver.internal.channel.Channels.remoteAddress;
import static org.kaazing.k3po.driver.internal.netty.channel.Channels.fireInputAborted;
import static org.kaazing.k3po.driver.internal.netty.channel.Channels.fireInputShutdown;
import static org.kaazing.k3po.driver.internal.netty.channel.Channels.fireOutputAborted;
import static org.kaazing.k3po.driver.internal.netty.channel.Channels.fireOutputShutdown;

import java.net.URI;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Objects;

import javax.net.ssl.ExtendedSSLSession;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLEngine;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.ssl.SslHandler;
import org.kaazing.k3po.driver.internal.netty.bootstrap.channel.ChannelConfig;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;
import org.kaazing.k3po.driver.internal.netty.channel.ReadAbortEvent;
import org.kaazing.k3po.driver.internal.netty.channel.ShutdownInputEvent;
import org.kaazing.k3po.driver.internal.netty.channel.ShutdownOutputEvent;
import org.kaazing.k3po.driver.internal.netty.channel.SimpleChannelHandler;
import org.kaazing.k3po.driver.internal.netty.channel.WriteAbortEvent;

public class TlsChildChannelSource extends SimpleChannelHandler {

    private final NavigableMap<ChannelAddress, TlsServerChannel> tlsBindings;

    private volatile TlsChildChannel tlsChildChannel;

    public TlsChildChannelSource(NavigableMap<ChannelAddress, TlsServerChannel> tlsBindings) {

        this.tlsBindings = tlsBindings;
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        Channel transport = ctx.getChannel();

        ChannelPipeline pipeline = ctx.getPipeline();
        SslHandler handler = pipeline.get(SslHandler.class);
        SSLEngine tlsEngine = handler.getEngine();
        ExtendedSSLSession tlsSession = (ExtendedSSLSession) tlsEngine.getSession();
        List<SNIServerName> sniServerNames = tlsSession.getRequestedServerNames();

        Entry<ChannelAddress, TlsServerChannel> tlsBinding = null;

        if (sniServerNames.size() > 0)
        {
            SNIHostName sniHostName = (SNIHostName) sniServerNames.get(0);
            String serverName = sniHostName.getAsciiName();

            URI tlsLocation = URI.create(String.format("tls://%s", serverName));

            // channel's local address is resolved address so get the bind address from
            // server channel's attachment
            ChannelAddress transportCandidate = (ChannelAddress) ctx.getChannel().getParent().getAttachment();
            ChannelAddress candidate = new ChannelAddress(tlsLocation, transportCandidate);

            tlsBinding = tlsBindings.floorEntry(candidate);
        }

        if (tlsBinding == null) {
            transport.close();
        }
        else
        {
            TlsServerChannel parent = tlsBinding.getValue();
            ChannelFactory factory = parent.getFactory();
            ChannelConfig parentConfig = parent.getConfig();
            ChannelPipelineFactory childPipelineFactory = parentConfig.getPipelineFactory();
            ChannelPipeline childPipeline = childPipelineFactory.getPipeline();
            ChannelAddress tlsLocalAddress = parent.getLocalAddress();
            URI tlsLocation = tlsLocalAddress.getLocation();

            ChannelAddress remoteAddress = remoteAddress(transport);
            ChannelAddress tlsRemoteAddress = new ChannelAddress(tlsLocation, remoteAddress, true);

            TlsChildChannelSink sink = new TlsChildChannelSink(transport);
            TlsChildChannel tlsChildChannel = new TlsChildChannel(parent, factory, childPipeline, sink);
            TlsChannelConfig tlsChildConfig = tlsChildChannel.getConfig();
            tlsChildConfig.setParameters(tlsEngine.getSSLParameters());

            ChannelFuture tlsCloseFuture = handler.getSSLEngineInboundCloseFuture();
            tlsCloseFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {

                    if (tlsChildChannel.setReadClosed()) {
                        fireInputShutdown(tlsChildChannel);
                        fireChannelDisconnected(tlsChildChannel);
                        fireChannelUnbound(tlsChildChannel);
                        fireChannelClosed(tlsChildChannel);
                    }
                    else
                    {
                        fireInputShutdown(tlsChildChannel);
                    }
                }
            });

            transport.getConfig().setBufferFactory(tlsChildConfig.getBufferFactory());
            this.tlsChildChannel = tlsChildChannel;

            detectWriteTransportClosed(transport, tlsChildChannel);

            fireChannelOpen(tlsChildChannel);

            tlsChildChannel.setLocalAddress(tlsLocalAddress);
            tlsChildChannel.setBound();
            fireChannelBound(tlsChildChannel, tlsLocalAddress);

            tlsChildChannel.setRemoteAddress(tlsRemoteAddress);
            tlsChildChannel.setConnected();
            fireChannelConnected(tlsChildChannel, tlsRemoteAddress);
        }
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception
    {
        ChannelBuffer message = (ChannelBuffer) e.getMessage();
        if (message.readable()) {
            fireMessageReceived(tlsChildChannel, message);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        TlsChildChannel tlsChildChannel = this.tlsChildChannel;
        if (tlsChildChannel != null) {

            this.tlsChildChannel = null;

            if (tlsChildChannel.setReadClosed() || tlsChildChannel.setWriteClosed()) {
                fireExceptionCaught(tlsChildChannel, e.getCause());
                fireChannelClosed(tlsChildChannel);
            }
        }

        Channel channel = ctx.getChannel();
        channel.close();
    }

    @Override
    public void inputAborted(ChannelHandlerContext ctx, ReadAbortEvent e) {
        TlsChildChannel tlsChildChannel = this.tlsChildChannel;
        if (tlsChildChannel != null) {
            if (tlsChildChannel.setReadAborted()) {
                if (tlsChildChannel.setReadClosed()) {
                    fireInputAborted(tlsChildChannel);
                    fireChannelDisconnected(tlsChildChannel);
                    fireChannelUnbound(tlsChildChannel);
                    fireChannelClosed(tlsChildChannel);
                }
                else {
                    fireInputAborted(tlsChildChannel);
                }
            }
        }
    }

    @Override
    public void outputAborted(ChannelHandlerContext ctx, WriteAbortEvent e) {
        TlsChildChannel tlsChildChannel = this.tlsChildChannel;
        if (tlsChildChannel != null) {
            if (tlsChildChannel.setWriteAborted()) {
                if (tlsChildChannel.setWriteClosed()) {
                    fireOutputAborted(tlsChildChannel);
                    fireChannelDisconnected(tlsChildChannel);
                    fireChannelUnbound(tlsChildChannel);
                    fireChannelClosed(tlsChildChannel);
                }
                else {
                    fireOutputAborted(tlsChildChannel);
                }
            }
        }
    }

    @Override
    public void inputShutdown(ChannelHandlerContext ctx, ShutdownInputEvent e) {
        TlsChildChannel tlsChildChannel = this.tlsChildChannel;
        if (tlsChildChannel != null) {
            if (tlsChildChannel.setReadClosed()) {
                fireInputShutdown(tlsChildChannel);
                fireChannelDisconnected(tlsChildChannel);
                fireChannelUnbound(tlsChildChannel);
                fireChannelClosed(tlsChildChannel);
            }
            else {
                fireInputShutdown(tlsChildChannel);
            }
        }
    }

    @Override
    public void outputShutdown(ChannelHandlerContext ctx, ShutdownOutputEvent e) {
        TlsChildChannel tlsChildChannel = this.tlsChildChannel;
        if (tlsChildChannel != null) {
            if (tlsChildChannel.setWriteClosed()) {
                fireOutputShutdown(tlsChildChannel);
                fireChannelDisconnected(tlsChildChannel);
                fireChannelUnbound(tlsChildChannel);
                fireChannelClosed(tlsChildChannel);
            }
            else {
                fireOutputShutdown(tlsChildChannel);
            }
        }
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        TlsChildChannel tlsChildChannel = this.tlsChildChannel;
        if (tlsChildChannel != null) {
            SslHandler tlsHandler = ctx.getPipeline().get(SslHandler.class);
            SSLEngine tlsEngine = tlsHandler.getEngine();
            if (!tlsEngine.isInboundDone()) {
                if (tlsChildChannel.setReadAborted()) {
                    fireInputAborted(tlsChildChannel);
                }
            }
        }

        super.channelDisconnected(ctx, e);
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        TlsChildChannel tlsChildChannel = this.tlsChildChannel;
        if (tlsChildChannel != null) {

            this.tlsChildChannel = null;

            if (tlsChildChannel.setReadClosed()) {
                fireChannelDisconnected(tlsChildChannel);
                fireChannelUnbound(tlsChildChannel);
                fireChannelClosed(tlsChildChannel);
            }
        }
    }

    private void detectWriteTransportClosed(Channel transport, TlsChildChannel tlsChildChannel) {

        Objects.requireNonNull(tlsChildChannel);

        final ChannelFutureListener closeListener = new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (tlsChildChannel.setWriteClosed()) {
                    fireChannelDisconnected(tlsChildChannel);
                    fireChannelUnbound(tlsChildChannel);
                    fireChannelClosed(tlsChildChannel);
                }
            }
        };

        transport.getCloseFuture().addListener(closeListener);

        tlsChildChannel.getCloseFuture().addListener(new ChannelFutureListener()
        {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                transport.getCloseFuture().removeListener(closeListener);
            }
        });
    }
}
