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
package org.kaazing.k3po.driver.internal.channel;

import static java.lang.String.format;
import static org.jboss.netty.channel.Channels.fireWriteComplete;
import static org.jboss.netty.channel.Channels.future;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.socket.nio.NioDatagramChannel;
import org.kaazing.k3po.driver.internal.behavior.handler.prepare.UpstreamPreparationEvent;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;

public final class Channels {

    public static ChannelFuture prepare(Channel channel) {
        ChannelPipeline pipeline = channel.getPipeline();
        ChannelFuture future = future(channel);
        pipeline.sendUpstream(new UpstreamPreparationEvent(channel, future));
        return future;
    }

    public static InetSocketAddress toInetSocketAddress(final SocketAddress localAddress) {
        if (localAddress instanceof ChannelAddress) {
            return toInetSocketAddress((ChannelAddress) localAddress);
        } else {
            return (InetSocketAddress) localAddress;
        }
    }

    public static InetSocketAddress toInetSocketAddress(ChannelAddress channelAddress) {
        if (channelAddress == null) {
            return null;
        }
        URI location = channelAddress.getLocation();
        String hostname = location.getHost();
        int port = location.getPort();
        return new InetSocketAddress(hostname, port);
    }

    public static ChannelAddress localAddress(Channel channel) {
        SocketAddress localAddress = channel.getLocalAddress();
        return channelAddress(channel, localAddress);
    }

    public static ChannelAddress remoteAddress(Channel channel) {
        SocketAddress remoteAddress = channel.getRemoteAddress();
        return channelAddress(channel, remoteAddress);
    }

    public static ChannelAddress channelAddress(Channel channel, SocketAddress address) {
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

    public static void chainFutures(ChannelFuture sourceFuture, final ChannelFuture targetFuture) {
        if (sourceFuture.isDone()) {
            if (sourceFuture.isSuccess()) {
                targetFuture.setSuccess();
            }
            else {
                targetFuture.setFailure(sourceFuture.getCause());
            }
        }
        else {
            sourceFuture.addListener(new ChannelFutureListener() {

                @Override
                public void operationComplete(ChannelFuture sourceFuture) throws Exception {
                    if (sourceFuture.isSuccess()) {
                        targetFuture.setSuccess();
                    }
                    else {
                        targetFuture.setFailure(sourceFuture.getCause());
                    }
                }
            });
        }
    }

    public static void chainWriteCompletes(
            ChannelFuture sourceFuture,
            final ChannelFuture targetFuture,
            final long amountWritten) {

        if (sourceFuture.isDone()) {
            if (sourceFuture.isSuccess()) {
                fireWriteComplete(targetFuture.getChannel(), amountWritten);
                targetFuture.setSuccess();
            }
            else {
                targetFuture.setFailure(sourceFuture.getCause());
            }
        }
        else {
            sourceFuture.addListener(new ChannelFutureListener() {

                @Override
                public void operationComplete(ChannelFuture sourceFuture) throws Exception {
                    if (sourceFuture.isSuccess()) {
                        fireWriteComplete(targetFuture.getChannel(), amountWritten);
                        targetFuture.setSuccess();
                    }
                    else {
                        targetFuture.setFailure(sourceFuture.getCause());
                    }
                }
            });
        }
    }
}
