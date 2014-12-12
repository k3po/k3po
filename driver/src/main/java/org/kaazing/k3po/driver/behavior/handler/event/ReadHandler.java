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

package org.kaazing.k3po.driver.behavior.handler.event;

import static java.lang.String.format;
import static java.util.EnumSet.of;
import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.jboss.netty.channel.Channels.fireMessageReceived;
import static org.jboss.netty.util.CharsetUtil.UTF_8;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.el.ELException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.UpstreamMessageEvent;
import org.kaazing.k3po.driver.behavior.ScriptProgressException;
import org.kaazing.k3po.driver.behavior.handler.codec.MaskingDecoder;
import org.kaazing.k3po.driver.behavior.handler.codec.MessageDecoder;

public class ReadHandler extends AbstractEventHandler {

    private final List<MessageDecoder> decoders;
    private final MaskingDecoder unmasker;

    private final List<MessageDecoder> consumedDecoders;

    public ReadHandler(List<MessageDecoder> decoders, MaskingDecoder unmasker) {
        super(of(ChannelEventKind.MESSAGE));
        if (decoders == null) {
            throw new NullPointerException("decoders");
        } else if (decoders.size() == 0) {
            throw new IllegalArgumentException("must have at least one decoder");
        }
        this.decoders = decoders;
        this.unmasker = unmasker;
        this.consumedDecoders = new ArrayList<MessageDecoder>(decoders);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        messageReceived(ctx, e, false);
    }

    @Override
    public String toString() {
        return format("read %s", decoders);
    }

    @Override
    protected void handleUnexpectedEvent(ChannelHandlerContext ctx, ChannelEvent evt) throws Exception {
        Channel channel = evt.getChannel();
        MessageEvent msg = new UpstreamMessageEvent(channel, copiedBuffer("", UTF_8), channel.getRemoteAddress());
        // We create a message with an empty string. We need make sure our decoders get the decoder Last call.
        messageReceived(ctx, msg, true);

        // If the above caused a completion we are done. Otherwise we still need to handle the unexpected event
        if (!getHandlerFuture().isDone()) {
            super.handleUnexpectedEvent(ctx, evt);
        }
    }

    private void messageReceived(ChannelHandlerContext ctx, MessageEvent e, boolean isLast) throws Exception {

        ChannelBuffer buf = (ChannelBuffer) e.getMessage();
        // first unmask the bytes (if mask read option is specified)
        buf = unmasker.applyMask(buf);

        ChannelFuture handlerFuture = getHandlerFuture();
        assert handlerFuture != null;

        Iterator<MessageDecoder> iterator = consumedDecoders.iterator();
        while (iterator.hasNext()) {
            MessageDecoder decoder = iterator.next();

            try {
                if (isLast) {
                    buf = decoder.decodeLast(buf);
                } else {
                    buf = decoder.decode(buf);
                }
            } catch (ELException ele) {
                ScriptProgressException exception = new ScriptProgressException(getRegionInfo(), ele.getMessage());
                exception.initCause(ele);
                exception.fillInStackTrace();
                handlerFuture.setFailure(exception);
                return;
            } catch (Exception mme) {
                // TODO: We will eventually have to create an AstRead node containing what we actually saw. This will come
                // later.
                handlerFuture.setFailure(mme);
                return;
            }
            if (buf == null) {
                // Need more data to complete the decode
                return;
            }
            // Remove the decoder because it is done
            iterator.remove();
        }

        // If we get through the list of decoders without an exception we are done.
        handlerFuture.setSuccess();

        // Propagate remaining data for next handler(s)
        if (buf.readable()) {
            buf = unmasker.undoMask(buf);
            fireMessageReceived(ctx, buf, ctx.getChannel().getRemoteAddress());
        }
    }


}
