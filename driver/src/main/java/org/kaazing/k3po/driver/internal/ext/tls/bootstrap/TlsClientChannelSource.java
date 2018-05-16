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

import static org.jboss.netty.channel.Channels.fireChannelClosed;
import static org.jboss.netty.channel.Channels.fireChannelDisconnected;
import static org.jboss.netty.channel.Channels.fireChannelUnbound;
import static org.jboss.netty.channel.Channels.fireExceptionCaught;
import static org.jboss.netty.channel.Channels.fireMessageReceived;
import static org.kaazing.k3po.driver.internal.netty.channel.Channels.fireInputAborted;
import static org.kaazing.k3po.driver.internal.netty.channel.Channels.fireInputShutdown;
import static org.kaazing.k3po.driver.internal.netty.channel.Channels.fireOutputAborted;
import static org.kaazing.k3po.driver.internal.netty.channel.Channels.fireOutputShutdown;

import javax.net.ssl.SSLEngine;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.ssl.SslHandler;
import org.kaazing.k3po.driver.internal.netty.channel.ReadAbortEvent;
import org.kaazing.k3po.driver.internal.netty.channel.ShutdownInputEvent;
import org.kaazing.k3po.driver.internal.netty.channel.ShutdownOutputEvent;
import org.kaazing.k3po.driver.internal.netty.channel.SimpleChannelHandler;
import org.kaazing.k3po.driver.internal.netty.channel.WriteAbortEvent;

public class TlsClientChannelSource extends SimpleChannelHandler {

    volatile TlsClientChannel tlsClientChannel;

    public void setTlsChannel(TlsClientChannel tlsClientChannel) {
        assert this.tlsClientChannel == null;
        this.tlsClientChannel = tlsClientChannel;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception
    {
        ChannelBuffer message = (ChannelBuffer) e.getMessage();
        if (message.readable()) {
            fireMessageReceived(tlsClientChannel, message);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        if (tlsClientChannel != null) {
            TlsClientChannel tlsClientChannel = this.tlsClientChannel;
            this.tlsClientChannel = null;
            if (tlsClientChannel.setReadClosed()) {
                fireExceptionCaught(tlsClientChannel, e.getCause());
                fireChannelClosed(tlsClientChannel);
            }
        }

        Channel channel = ctx.getChannel();
        channel.close();
    }

    @Override
    public void inputAborted(ChannelHandlerContext ctx, ReadAbortEvent e) {
        TlsClientChannel tlsClientChannel = this.tlsClientChannel;
        if (tlsClientChannel != null) {
            if (tlsClientChannel.setReadAborted()) {
                if (tlsClientChannel.setReadClosed()) {
                    fireInputAborted(tlsClientChannel);
                    fireChannelDisconnected(tlsClientChannel);
                    fireChannelUnbound(tlsClientChannel);
                    fireChannelClosed(tlsClientChannel);
                }
                else {
                    fireInputAborted(tlsClientChannel);
                }
            }
        }
    }

    @Override
    public void outputAborted(ChannelHandlerContext ctx, WriteAbortEvent e) {
        TlsClientChannel tlsClientChannel = this.tlsClientChannel;
        if (tlsClientChannel != null) {
            if (tlsClientChannel.setWriteAborted()) {
                if (tlsClientChannel.setWriteClosed()) {
                    fireOutputAborted(tlsClientChannel);
                    fireChannelDisconnected(tlsClientChannel);
                    fireChannelUnbound(tlsClientChannel);
                    fireChannelClosed(tlsClientChannel);
                }
                else {
                    fireOutputAborted(tlsClientChannel);
                }
            }
        }
    }

    @Override
    public void inputShutdown(ChannelHandlerContext ctx, ShutdownInputEvent e) {
        TlsClientChannel tlsClientChannel = this.tlsClientChannel;
        if (tlsClientChannel != null) {
            if (tlsClientChannel.setReadClosed()) {
                fireInputShutdown(tlsClientChannel);
                fireChannelDisconnected(tlsClientChannel);
                fireChannelUnbound(tlsClientChannel);
                fireChannelClosed(tlsClientChannel);
            }
            else {
                fireInputShutdown(tlsClientChannel);
            }
        }
    }

    @Override
    public void outputShutdown(ChannelHandlerContext ctx, ShutdownOutputEvent e) {
        TlsClientChannel tlsClientChannel = this.tlsClientChannel;
        if (tlsClientChannel != null) {
            if (tlsClientChannel.setWriteClosed()) {
                fireOutputShutdown(tlsClientChannel);
                fireChannelDisconnected(tlsClientChannel);
                fireChannelUnbound(tlsClientChannel);
                fireChannelClosed(tlsClientChannel);
            }
            else {
                fireOutputShutdown(tlsClientChannel);
            }
        }
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        TlsClientChannel tlsClientChannel = this.tlsClientChannel;
        if (tlsClientChannel != null) {
            SslHandler tlsHandler = ctx.getPipeline().get(SslHandler.class);
            SSLEngine tlsEngine = tlsHandler.getEngine();
            if (!tlsEngine.isInboundDone()) {
                if (tlsClientChannel.setReadAborted()) {
                    fireInputAborted(tlsClientChannel);
                }
            }
        }
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        TlsClientChannel tlsClientChannel = this.tlsClientChannel;
        if (tlsClientChannel != null) {

            this.tlsClientChannel = null;

            if (tlsClientChannel.setReadClosed()) {
                fireChannelDisconnected(tlsClientChannel);
                fireChannelUnbound(tlsClientChannel);
                fireChannelClosed(tlsClientChannel);
            }
        }
    }
}
