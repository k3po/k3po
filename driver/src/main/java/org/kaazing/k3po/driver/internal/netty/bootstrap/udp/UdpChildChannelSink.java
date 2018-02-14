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

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.socket.nio.NioDatagramChannel;
import org.kaazing.k3po.driver.internal.netty.bootstrap.channel.AbstractChannelSink;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;

import java.net.SocketAddress;

import static org.jboss.netty.channel.Channels.fireChannelClosed;
import static org.jboss.netty.channel.Channels.fireChannelDisconnected;
import static org.jboss.netty.channel.Channels.fireChannelUnbound;
import static org.kaazing.k3po.driver.internal.channel.Channels.toInetSocketAddress;

class UdpChildChannelSink extends AbstractChannelSink {

    private final NioDatagramChannel serverChannel;
    private final UdpChildChannelSource childChannelSource;

    UdpChildChannelSink(UdpChildChannelSource childChannelSource) {
        this.childChannelSource = childChannelSource;
        this.serverChannel = childChannelSource.serverChannel.getTransport();
    }

    protected void writeRequested(ChannelPipeline pipeline, MessageEvent e) throws Exception {
        assert e.getChannel() instanceof UdpChildChannel;
        assert e.getRemoteAddress() != null;

        SocketAddress toAddress = toInetSocketAddress((ChannelAddress) e.getChannel().getRemoteAddress());

        serverChannel.write(e.getMessage(), toAddress);
        e.getFuture().setSuccess();
    }

    @Override
    protected void closeRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
        final UdpChildChannel channel = (UdpChildChannel) evt.getChannel();

        if (channel.isConnected()) {
            childChannelSource.closeChildChannel(channel);
        }

        if (channel.setClosed())
        {
            fireChannelDisconnected(channel);
            fireChannelUnbound(channel);
            fireChannelClosed(channel);
        }

        evt.getFuture().setSuccess();
    }

}
