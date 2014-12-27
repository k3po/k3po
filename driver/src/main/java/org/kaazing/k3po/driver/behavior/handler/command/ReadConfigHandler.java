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

package org.kaazing.k3po.driver.behavior.handler.command;

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
import org.kaazing.k3po.driver.behavior.handler.codec.ConfigDecoder;
import org.kaazing.k3po.driver.behavior.handler.prepare.PreparationEvent;

//
// Reading the configuration requires the channel to be readable as an indication that
// any handshaking is complete (such as HTTP request -> response) such that the timing
// is appropriate to determine characteristics of the completed handshake (such as
// HTTP response status code)
//
public class ReadConfigHandler extends AbstractCommandHandler {

    private final List<ConfigDecoder> decoders;
    private ChannelFuture readableFuture;

    public ReadConfigHandler(ConfigDecoder decoder) {
        this(singletonList(decoder));
    }

    public ReadConfigHandler(List<ConfigDecoder> decoders) {
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
            for (ConfigDecoder decoder : decoders) {
                decoder.decode(channel);
            }
            getHandlerFuture().setSuccess();
        }
        catch (Exception e) {
            getHandlerFuture().setFailure(e);
        }
    }

    @Override
    public String toString() {
        return format("read config %s", decoders);
    }

}
