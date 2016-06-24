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

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelConfig;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelSink;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.DefaultChannelConfig;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.socket.nio.NioDatagramChannel;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.Timer;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;
import org.kaazing.k3po.driver.internal.netty.channel.SimpleChannelHandler;
import org.kaazing.k3po.driver.internal.netty.channel.udp.UdpChannelAddress;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static org.jboss.netty.channel.Channels.fireChannelBound;
import static org.jboss.netty.channel.Channels.fireChannelClosed;
import static org.jboss.netty.channel.Channels.fireChannelConnected;
import static org.jboss.netty.channel.Channels.fireChannelOpen;
import static org.jboss.netty.channel.Channels.fireExceptionCaught;
import static org.jboss.netty.channel.Channels.fireMessageReceived;
import static org.kaazing.k3po.driver.internal.channel.Channels.channelAddress;
import static org.kaazing.k3po.driver.internal.channel.Channels.toInetSocketAddress;

/*
 * UdpServerChannelSink sets up NioDatagramChannel with this handler as the pipeline
 *
 * This handler creates a channel (UdpChildChannel) for each remote address
 * Netty doesn't have this feature yet (https://github.com/netty/netty/issues/344)
 */
class UdpChildChannelSource extends SimpleChannelHandler {

    // remote address --> child channel
    private final Map<SocketAddress, UdpChildChannel> childChannels = new ConcurrentHashMap<>();

    final UdpServerChannel serverChannel;
    private final Timer timer;

    UdpChildChannelSource(UdpServerChannel serverChannel, Timer timer) {
        this.serverChannel = serverChannel;
        this.timer = timer;
    }

    void closeChildChannel(UdpChildChannel childChannel) {
        InetSocketAddress socketAddress = toInetSocketAddress(childChannel.getRemoteAddress());
        childChannels.remove(socketAddress);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        childChannels.forEach(((socketAddress, udpChildChannel) -> {
            fireExceptionCaught(udpChildChannel, e.getCause());
            fireChannelClosed(udpChildChannel);
        }));
        childChannels.clear();

        Channel channel = ctx.getChannel();
        channel.close();
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        childChannels.forEach(((socketAddress, udpChildChannel) -> udpChildChannel.close()));

        e.getFuture().setSuccess();
        childChannels.clear();
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        SocketAddress remoteAddress = e.getRemoteAddress();
        NioDatagramChannel datagramChannel = (NioDatagramChannel) e.getChannel();
        UdpChannelAddress localAddress = serverChannel.getLocalAddress();
        long timeout = localAddress.timeout();

        UdpChildChannel udpChildChannel = childChannels.computeIfAbsent(remoteAddress, x -> {
            ChannelPipelineFactory pipelineFactory = serverChannel.getConfig().getPipelineFactory();
            ChannelPipeline pipeline;
            try {
                 pipeline = pipelineFactory.getPipeline();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

            if (timeout != 0) {
                IdleStateHandler idleStateHandler = new IdleStateHandler(timer, 0, 0, timeout, TimeUnit.MILLISECONDS);
                pipeline.addFirst("idleHandler", new UdpIdleHandler());
                pipeline.addFirst("idleStateHandler", idleStateHandler);
            }

            ChannelConfig config = new DefaultChannelConfig();
            ChannelSink sink = new UdpChildChannelSink(this);

            UdpChildChannel childChannel = new UdpChildChannel(serverChannel, null, pipeline, sink, config);
            fireChannelOpen(childChannel);

            childChannel.setLocalAddress(serverChannel.getLocalAddress());
            childChannel.setBound();
            fireChannelBound(childChannel, e.getRemoteAddress());

            ChannelAddress address = channelAddress(datagramChannel, e.getRemoteAddress());
            childChannel.setRemoteAddress(address);
            childChannel.setConnected();
            fireChannelConnected(childChannel, e.getRemoteAddress());

            return childChannel;
        });

        fireMessageReceived(udpChildChannel, e.getMessage());
    }

}
