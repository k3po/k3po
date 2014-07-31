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
import static org.jboss.netty.util.CharsetUtil.UTF_8;

import java.util.regex.Matcher;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.kaazing.robot.driver.control.ControlMessage;
import org.kaazing.robot.driver.control.ErrorMessage;
import org.kaazing.robot.driver.control.FinishedMessage;
import org.kaazing.robot.driver.control.ControlMessage.Kind;

public class HttpControlResponseEncoder extends OneToOneEncoder {

    private static final byte LF = (byte) 0x0a;
    private static final byte LEFT_CURLY_BRACKET = (byte) 0x7b;
    private static final byte RIGHT_CURLY_BRACKET = (byte) 0x7d;
    private static final byte COMMA = (byte) 0x2c;

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        if (msg instanceof ControlMessage) {
            ControlMessage controlMessage = (ControlMessage) msg;
            ChannelBuffer header;

            switch (controlMessage.getKind()) {
            case PREPARED:
                header = (ChannelBuffer) encodeMessageWithoutContent(ctx, channel, controlMessage);
                return createHttpResponse(header);
            case STARTED:
                header = (ChannelBuffer) encodeMessageWithoutContent(ctx, channel, controlMessage);
                return createHttpResponse(header);
            case ERROR:
                header = (ChannelBuffer) encodeErrorMessage(ctx, channel, (ErrorMessage) controlMessage);
                return createHttpResponse(header);
            case FINISHED:
                header = (ChannelBuffer) encodeFinishedMessage(ctx, channel, (FinishedMessage) controlMessage);
                return createHttpResponse(header);
            default:
                break;
            }
        }

        // unknown message
        return msg;
    }

    private Object createHttpResponse(ChannelBuffer header) throws Exception {
        DefaultHttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.headers().add(HttpHeaders.Names.CONTENT_TYPE, "text/html");
        response.headers().add(HttpHeaders.Names.CONTENT_LENGTH, String.format("%d", header.readableBytes()));
        response.setContent(header);
        return response;
    }

    private Object encodeMessageWithoutContent(ChannelHandlerContext ctx, Channel channel, ControlMessage message) {
        Kind kind = message.getKind();
        String name = message.getName();

        ChannelBuffer header = dynamicBuffer(channel.getConfig().getBufferFactory());
        encodeInitial(kind, header);
        encodeNameHeader(name, header, false);
        return encodeNoContent(header);
    }

    private Object encodeMessageBeginning(Channel channel, ControlMessage message) {
        Kind kind = message.getKind();
        String name = message.getName();

        ChannelBuffer header = dynamicBuffer(channel.getConfig().getBufferFactory());
        encodeInitial(kind, header);
        encodeNameHeader(name, header, true);

        return header;
    }

    private Object encodeErrorMessage(ChannelHandlerContext ctx, Channel channel, ErrorMessage errorMessage) {
        String summary = errorMessage.getSummary();
        String description = errorMessage.getDescription();

        ChannelBuffer header = (ChannelBuffer) encodeMessageBeginning(channel, errorMessage);
        encodeContent("summary", summary, header, true);
        encodeContent("content", description, header, false);
        header.writeByte(RIGHT_CURLY_BRACKET);

        return header;
    }

    private Object encodeFinishedMessage(ChannelHandlerContext ctx, Channel channel, FinishedMessage finishedMessage) {
        String expectedScript = finishedMessage.getExpectedScript().replaceAll("\n", Matcher.quoteReplacement("\\n"));
        String observedScript = finishedMessage.getObservedScript().replaceAll("\n", Matcher.quoteReplacement("\\n"));

        ChannelBuffer header = (ChannelBuffer) encodeMessageBeginning(channel, finishedMessage);
        encodeContent("expected_script", expectedScript, header, true);
        encodeContent("observed_script", observedScript, header, false);
        header.writeByte(RIGHT_CURLY_BRACKET);

        return header;
    }

    private static void encodeInitial(Kind kind, ChannelBuffer header) {
        header.writeByte(LEFT_CURLY_BRACKET);
        header.writeByte(LF);
        header.writeBytes(copiedBuffer(format("    \"kind\": \"%s\",", kind.toString()), UTF_8));
        header.writeByte(LF);
    }

    private static void encodeNameHeader(String name, ChannelBuffer header, boolean comma) {
        if (name != null) {
            header.writeBytes(copiedBuffer(format("    \"name\": \"%s\"", name), UTF_8));
        } else {
            header.writeBytes(copiedBuffer("    \"name\": null", UTF_8));
        }
        if (comma) {
            header.writeByte(COMMA);
        }
        header.writeByte(LF);
    }

    private static Object encodeNoContent(ChannelBuffer header) {
        header.writeByte(RIGHT_CURLY_BRACKET);
        return header;
    }

    private static void encodeContent(String contentDescription, String contentAsString, ChannelBuffer header, boolean comma) {
        if (contentAsString != null) {
            header.writeBytes(copiedBuffer(format("    \"%s\": \"%s\"", contentDescription, contentAsString), UTF_8));
        } else {
            header.writeBytes(copiedBuffer(format("    \"%s\": null", contentDescription, contentAsString), UTF_8));
        }
        if (comma) {
            header.writeByte(COMMA);
        }
        header.writeByte(LF);
    }
}
