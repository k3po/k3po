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
package org.kaazing.k3po.driver.internal.behavior.handler.command;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;
import static org.jboss.netty.channel.Channel.OP_READ;
import static org.jboss.netty.channel.Channels.future;

import java.util.List;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.ChannelDecoder;

//
// Reading the configuration requires the channel to be readable as an indication that
// any handshaking is complete (such as HTTP request -> response) such that the timing
// is appropriate to determine characteristics of the completed handshake (such as
// HTTP response status code)
//
public class ReadConfigHandler extends AbstractCommandHandler {

    private final List<ChannelDecoder> decoders;
    private ChannelFuture readableFuture;

    public ReadConfigHandler(ChannelDecoder decoder) {
        this(singletonList(decoder));
    }

    public ReadConfigHandler(List<ChannelDecoder> decoders) {
        requireNonNull(decoders, "decoders");
        if (decoders.size() == 0) {
            throw new IllegalArgumentException("must have at least one decoder");
        }
        this.decoders = decoders;
        this.readableFuture = future(null);
    }

    @Override
    public void channelInterestChanged(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        Channel channel = ctx.getChannel();
        int interestOps = channel.getInterestOps();
        if ((interestOps & OP_READ) != 0) {
            readableFuture.setSuccess();
        }
    }

    @Override
    protected void invokeCommand(final ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.getChannel();

        if (channel.isReadable()) {
            invokeCommandWhenReadable(ctx);
        }
        else {
            readableFuture.addListener(new ChannelFutureListener() {

                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        invokeCommandWhenReadable(ctx);
                    }
                }
            });
        }

    }

    private void invokeCommandWhenReadable(ChannelHandlerContext ctx) {
        Channel channel = ctx.getChannel();

        try {
            for (ChannelDecoder decoder : decoders) {
                decoder.decode(channel);
            }
            getHandlerFuture().setSuccess();
        }
        catch (Exception e) {
            getHandlerFuture().setFailure(e);
        }
    }

    @Override
    protected StringBuilder describe(StringBuilder sb) {
        return sb.append(format("read config %s", decoders));
    }

}
