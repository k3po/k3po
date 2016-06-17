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
package org.kaazing.k3po.driver.internal.netty.bootstrap.udp;

import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.socket.nio.NioDatagramChannel;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;
import org.jboss.netty.channel.socket.nio.NioDatagramWorkerPool;
import org.kaazing.k3po.driver.internal.netty.bootstrap.channel.AbstractServerChannelSink;
import org.kaazing.k3po.driver.internal.netty.bootstrap.channel.AbstractChannelSink;
import org.kaazing.k3po.driver.internal.netty.bootstrap.http.HttpServerChannel;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.jboss.netty.channel.Channels.fireChannelClosed;
import static org.jboss.netty.channel.Channels.fireChannelDisconnected;
import static org.jboss.netty.channel.Channels.fireChannelUnbound;
import static org.jboss.netty.channel.Channels.pipeline;

public class UdpChildChannelSink extends AbstractChannelSink {

    private final NioDatagramChannel serverChannel;

    public UdpChildChannelSink(NioDatagramChannel serverChannel) {
        this.serverChannel = serverChannel;
    }

    @Override
    protected void bindRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
        ChannelFuture bindFuture = evt.getFuture();
        bindFuture.setSuccess();
    }

    protected void writeRequested(ChannelPipeline pipeline, MessageEvent e) throws Exception {
        assert e.getChannel() instanceof UdpChildChannel;
        assert e.getRemoteAddress() != null;

        SocketAddress toAddress = toInetSocketAddress((ChannelAddress) e.getChannel().getRemoteAddress());

        serverChannel.write(e.getMessage(), toAddress);
        e.getFuture().setSuccess();
    }

    @Override
    protected void unbindRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
        evt.getFuture().setSuccess();
    }

    @Override
    protected void closeRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
        fireChannelDisconnected(evt.getChannel());
        fireChannelUnbound(evt.getChannel());
        fireChannelClosed(evt.getChannel());

        evt.getFuture().setSuccess();
    }

    private static InetSocketAddress toInetSocketAddress(ChannelAddress channelAddress) {
        if (channelAddress == null) {
            return null;
        }
        URI location = channelAddress.getLocation();
        String hostname = location.getHost();
        int port = location.getPort();
        return new InetSocketAddress(hostname, port);
    }

}
