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

package org.kaazing.robot.driver.control.handler;

import static java.lang.String.format;
import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.jboss.netty.buffer.ChannelBuffers.dynamicBuffer;
import static org.jboss.netty.buffer.ChannelBuffers.wrappedBuffer;
import static org.jboss.netty.util.CharsetUtil.UTF_8;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.kaazing.robot.driver.control.ControlMessage;
import org.kaazing.robot.driver.control.ControlMessage.Kind;
import org.kaazing.robot.driver.control.ErrorMessage;
import org.kaazing.robot.driver.control.FinishedMessage;
import org.kaazing.robot.driver.control.PreparedMessage;
import org.kaazing.robot.driver.control.StartedMessage;

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
        String scriptName = preparedMessage.getScriptName();

        ChannelBuffer header = dynamicBuffer(channel.getConfig().getBufferFactory());
        encodeInitial(kind, header);
        encodeNameHeader(scriptName, header);
        return encodeNoContent(header);
    }

    private Object encodeStartedMessage(ChannelHandlerContext ctx, Channel channel, StartedMessage startedMessage) {

        Kind kind = startedMessage.getKind();
        String scriptName = startedMessage.getScriptName();

        ChannelBuffer header = dynamicBuffer(channel.getConfig().getBufferFactory());
        encodeInitial(kind, header);
        encodeNameHeader(scriptName, header);
        return encodeNoContent(header);
    }

    private Object encodeErrorMessage(ChannelHandlerContext ctx, Channel channel, ErrorMessage errorMessage) {

        Kind kind = errorMessage.getKind();
        String scriptName = errorMessage.getScriptName();
        String summary = errorMessage.getSummary();
        String description = errorMessage.getDescription();

        ChannelBuffer header = dynamicBuffer(channel.getConfig().getBufferFactory());
        encodeInitial(kind, header);
        encodeNameHeader(scriptName, header);
        encodeHeader("summary", summary, header);
        return encodeContent(description, header);
    }

    private Object encodeFinishedMessage(ChannelHandlerContext ctx, Channel channel, FinishedMessage finishedMessage) {
        Kind kind = finishedMessage.getKind();
        String scriptName = finishedMessage.getScriptName();
        String expectedScript = finishedMessage.getExpectedScript();
        String observedScript = finishedMessage.getObservedScript();

        ChannelBuffer header = dynamicBuffer(channel.getConfig().getBufferFactory());
        encodeInitial(kind, header);
        encodeNameHeader(scriptName, header);
        Object encoded = encodeContent(expectedScript, header);

        ChannelBuffer content = dynamicBuffer(channel.getConfig().getBufferFactory());
        Object encodedEnd = encodeContent(observedScript, content);

        if (encoded instanceof ChannelBuffer && encodedEnd instanceof ChannelBuffer) {
            return wrappedBuffer((ChannelBuffer) encoded, (ChannelBuffer) encodedEnd);
        } else {
            throw new IllegalStateException("Expected Objects returned from encodeContent to be of type ChannelBuffer");
        }
    }

    private static void encodeInitial(Kind kind, ChannelBuffer header) {
        header.writeBytes(copiedBuffer(kind.toString(), UTF_8));
        header.writeByte(LF);
    }

    private static void encodeNameHeader(String scriptName, ChannelBuffer header) {
        if (scriptName != null) {
            header.writeBytes(copiedBuffer(format("name:%s", scriptName), UTF_8));
            header.writeByte(LF);
        }
    }

    private static void encodeHeader(String headerName, Object headerValue, ChannelBuffer header) {
        if (headerValue != null) {
            header.writeBytes(copiedBuffer(format("%s:%s", headerName, headerValue), UTF_8));
            header.writeByte(LF);
        }
    }

    private static Object encodeNoContent(ChannelBuffer header) {
        header.writeByte(LF);
        return header;
    }

    private static Object encodeContent(String contentAsString, ChannelBuffer header) {
        if (contentAsString != null) {
            ChannelBuffer content = copiedBuffer(contentAsString, UTF_8);
            encodeHeader("content-length", content.readableBytes(), header);
            header.writeByte(LF);

            return wrappedBuffer(header, content);
        } else {
            header.writeBytes(copiedBuffer("content-length:0", UTF_8));
            header.writeByte(LF);
            header.writeByte(LF);

            return header;
        }
    }

}
