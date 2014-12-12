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

package org.kaazing.k3po.driver.control.handler;

import static java.lang.String.format;
import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.jboss.netty.buffer.ChannelBuffers.dynamicBuffer;
import static org.jboss.netty.buffer.ChannelBuffers.wrappedBuffer;
import static org.jboss.netty.util.CharsetUtil.UTF_8;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.kaazing.k3po.driver.control.ControlMessage;
import org.kaazing.k3po.driver.control.ErrorMessage;
import org.kaazing.k3po.driver.control.FinishedMessage;
import org.kaazing.k3po.driver.control.PreparedMessage;
import org.kaazing.k3po.driver.control.StartedMessage;
import org.kaazing.k3po.driver.control.ControlMessage.Kind;

public class ControlEncoder extends OneToOneEncoder {

    private static final byte LF = (byte) 0x0a;

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object message) throws Exception {

        if (message instanceof ControlMessage) {
            ControlMessage controlMessage = (ControlMessage) message;

            switch (controlMessage.getKind()) {
            case PREPARED:
                return encodePreparedMessage(ctx, channel, (PreparedMessage) controlMessage);
            case STARTED:
                return encodeStartedMessage(ctx, channel, (StartedMessage) controlMessage);
            case ERROR:
                return encodeErrorMessage(ctx, channel, (ErrorMessage) controlMessage);
            case FINISHED:
                return encodeFinishedMessage(ctx, channel, (FinishedMessage) controlMessage);
            default:
                break;
            }
        }

        // unknown message
        return message;
    }

    private Object encodePreparedMessage(ChannelHandlerContext ctx, Channel channel, PreparedMessage preparedMessage) {

        Kind kind = preparedMessage.getKind();
        String script = preparedMessage.getScript();

        ChannelBuffer buf = dynamicBuffer(channel.getConfig().getBufferFactory());
        encodeInitial(kind, buf);
        return encodeContent(script, buf);
    }

    private Object encodeStartedMessage(ChannelHandlerContext ctx, Channel channel, StartedMessage startedMessage) {

        Kind kind = startedMessage.getKind();

        ChannelBuffer buf = dynamicBuffer(channel.getConfig().getBufferFactory());
        encodeInitial(kind, buf);
        return encodeNoContent(buf);
    }

    private Object encodeErrorMessage(ChannelHandlerContext ctx, Channel channel, ErrorMessage errorMessage) {
        Kind kind = errorMessage.getKind();
        String summary = errorMessage.getSummary();
        String description = errorMessage.getDescription();

        ChannelBuffer buf = dynamicBuffer(channel.getConfig().getBufferFactory());
        encodeInitial(kind, buf);
        encodeHeader("summary", summary, buf);
        return encodeContent(description, buf);
    }

    private Object encodeFinishedMessage(ChannelHandlerContext ctx, Channel channel, FinishedMessage finishedMessage) {
        Kind kind = finishedMessage.getKind();
        String script = finishedMessage.getScript();

        ChannelBuffer buf = dynamicBuffer(channel.getConfig().getBufferFactory());
        encodeInitial(kind, buf);
        return encodeContent(script, buf);
    }

    private static void encodeInitial(Kind kind, ChannelBuffer buf) {
        buf.writeBytes(copiedBuffer(kind.toString(), UTF_8));
        buf.writeByte(LF);
    }

    private static void encodeHeader(String bufName, Object bufValue, ChannelBuffer buf) {
        if (bufValue != null) {
            buf.writeBytes(copiedBuffer(format("%s:%s", bufName, bufValue), UTF_8));
            buf.writeByte(LF);
        }
    }

    private static ChannelBuffer encodeNoContent(ChannelBuffer buf) {
        buf.writeByte(LF);
        return buf;
    }

    private static ChannelBuffer encodeContent(String content, ChannelBuffer buf) {
        if (content == null) {
            // note: missing content not same as empty content
            return encodeNoContent(buf);
        }
        else {
            ChannelBuffer contentBuf = copiedBuffer(content, UTF_8);
            encodeHeader("content-length", contentBuf.readableBytes(), buf);
            buf.writeByte(LF);
            return wrappedBuffer(buf, contentBuf);
        }

    }

}
