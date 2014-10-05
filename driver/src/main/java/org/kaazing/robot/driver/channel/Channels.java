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

package org.kaazing.robot.driver.channel;

import static java.lang.String.format;
import static org.jboss.netty.channel.Channels.fireWriteComplete;
import static org.jboss.netty.channel.Channels.future;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.socket.nio.NioDatagramChannel;
import org.kaazing.robot.driver.behavior.handler.prepare.UpstreamPreparationEvent;
import org.kaazing.robot.driver.netty.channel.ChannelAddress;

public final class Channels {

    public static ChannelFuture prepare(Channel channel) {
        ChannelPipeline pipeline = channel.getPipeline();
        ChannelFuture future = future(channel);
        pipeline.sendUpstream(new UpstreamPreparationEvent(channel, future));
        return future;
    }

    public static ChannelAddress localAddress(Channel channel) {
        SocketAddress localAddress = channel.getLocalAddress();
        return channelAddress(channel, localAddress);
    }

    public static ChannelAddress remoteAddress(Channel channel) {
        SocketAddress remoteAddress = channel.getRemoteAddress();
        return channelAddress(channel, remoteAddress);
    }

    private static ChannelAddress channelAddress(Channel channel, SocketAddress address) {
        if (address instanceof ChannelAddress) {
            return (ChannelAddress) address;
        }
        else if (address instanceof InetSocketAddress) {
            String scheme = (channel instanceof NioDatagramChannel) ? "udp" : "tcp";
            InetSocketAddress inetAddress = (InetSocketAddress) address;
            String hostname = inetAddress.getHostString();
            int port = inetAddress.getPort();
            URI location = URI.create(format("%s://%s:%d", scheme, hostname, port));
            return new ChannelAddress(location);
        }
        else {
            throw new IllegalArgumentException();
        }
    }

    private Channels() {
        // utility class, no instances
    }

    public static void chainFutures(ChannelFuture sourceFuture, ChannelFuture targetFuture) {
        if (sourceFuture.isDone()) {
            if (sourceFuture.isSuccess()) {
                targetFuture.setSuccess();
            }
            else {
                targetFuture.setFailure(sourceFuture.getCause());
            }
        }
    }

    public static void chainWriteCompletes(ChannelFuture sourceFuture, ChannelFuture targetFuture, long amountWritten) {
        if (sourceFuture.isDone()) {
            if (sourceFuture.isSuccess()) {
                fireWriteComplete(targetFuture.getChannel(), amountWritten);
                targetFuture.setSuccess();
            }
            else {
                targetFuture.setFailure(sourceFuture.getCause());
            }
        }
    }
}
