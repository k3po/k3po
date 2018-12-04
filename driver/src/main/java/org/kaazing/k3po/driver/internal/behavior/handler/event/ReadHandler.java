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
package org.kaazing.k3po.driver.internal.behavior.handler.event;

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
import org.jboss.netty.channel.ChannelConfig;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.UpstreamMessageEvent;
import org.kaazing.k3po.driver.internal.behavior.ScriptProgressException;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.Masker;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.MessageDecoder;

public class ReadHandler extends AbstractEventHandler {

    private final List<MessageDecoder> decoders;
    private final Masker unmasker;

    private final List<MessageDecoder> consumedDecoders;

    public ReadHandler(List<MessageDecoder> decoders, Masker unmasker) {
        super(of(ChannelEventKind.MESSAGE));
        if (decoders == null) {
            throw new NullPointerException("decoders");
        } else if (decoders.size() == 0) {
            throw new IllegalArgumentException("must have at least one decoder");
        }
        this.decoders = decoders;
        this.unmasker = unmasker;
        this.consumedDecoders = new ArrayList<>(decoders);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        messageReceived(ctx, e, false);
    }

    @Override
    protected StringBuilder describe(StringBuilder sb) {
        sb.append("read ");
        for (MessageDecoder decoder : decoders) {
            sb.append(decoder).append(' ');
        }
        sb.setLength(sb.length() - 1);
        return sb;
    }

    @Override
    protected void handleUnexpectedEvent(ChannelHandlerContext ctx, ChannelEvent evt) {
        Channel channel = evt.getChannel();
        MessageEvent msg = new UpstreamMessageEvent(channel, copiedBuffer("", UTF_8), channel.getRemoteAddress());
        // We create a message with an empty string. We need make sure our decoders get the decoder Last call.
        messageReceived(ctx, msg, true);

        // If the above caused a completion we are done. Otherwise we still need to handle the unexpected event
        if (!getHandlerFuture().isDone()) {
            super.handleUnexpectedEvent(ctx, evt);
        }
    }

    private void messageReceived(ChannelHandlerContext ctx, MessageEvent e, boolean isLast) {

        final Channel channel = ctx.getChannel();
        final ChannelConfig config = channel.getConfig();
        final boolean messageAligned = isMessageAligned(config);

        ChannelBuffer buf = (ChannelBuffer) e.getMessage();
        final int writerIndex = buf.writerIndex();
        buf.resetWriterIndex();
        final int markedWriterIndex = buf.writerIndex();
        buf.writerIndex(writerIndex);

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
                handlerFuture.setFailure(exception);
                return;
            } catch (Exception mme) {
                // TODO: We will eventually have to create an AstRead node containing what we actually saw. This will come
                // later.
                handlerFuture.setFailure(mme);
                return;
            }

            if (buf == null) {
                // need more data to complete the decode, must not detect message boundary when message oriented
                if (messageAligned && markedWriterIndex != 0)
                {
                    handlerFuture.setFailure(new ScriptProgressException(getRegionInfo(), "invalid message boundary"));
                }
                return;
            }

            // remove the decoder because it is done
            iterator.remove();
        }

        // if we get through the list of decoders without an exception we are done
        if (messageAligned)
        {
            if (buf.readable() || markedWriterIndex != writerIndex)
            {
                handlerFuture.setFailure(new ScriptProgressException(getRegionInfo(), "invalid message boundary"));
            }
            else
            {
                handlerFuture.setSuccess();
            }
        }
        else
        {
            handlerFuture.setSuccess();

            // propagate remaining data for next handler(s) when not message-oriented
            if (buf.readable()) {
                buf = unmasker.undoMask(buf);
                fireMessageReceived(ctx, buf, channel.getRemoteAddress());
            }
        }
    }

    protected boolean isMessageAligned(
        ChannelConfig config)
    {
        return (config instanceof org.kaazing.k3po.driver.internal.netty.bootstrap.channel.ChannelConfig) &&
                "message".equals(((org.kaazing.k3po.driver.internal.netty.bootstrap.channel.ChannelConfig)config).getAlignment());
    }
}
