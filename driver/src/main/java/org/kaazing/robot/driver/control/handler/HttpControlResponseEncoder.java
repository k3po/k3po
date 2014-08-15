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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.kaazing.robot.driver.control.BadRequestMessage;
import org.kaazing.robot.driver.control.ControlMessage;
import org.kaazing.robot.driver.control.ErrorMessage;
import org.kaazing.robot.driver.control.FinishedMessage;
import org.kaazing.robot.driver.control.ControlMessage.Kind;

public class HttpControlResponseEncoder extends OneToOneEncoder {

    private static final byte LF = (byte) 0x0a;
    private static final byte LEFT_CURLY_BRACKET = (byte) 0x7b;
    private static final byte RIGHT_CURLY_BRACKET = (byte) 0x7d;
    private static final byte COMMA = (byte) 0x2c;
    private static final long TIME_LIMIT_MILLIS = 500;

    private Map<String, Object> scriptResultCache = new HashMap<String, Object>();
    private Date lastResultRequestTime;
    private String lastResultRequestName;
    private final Runnable clearLastRequestEntry = new Runnable() {
        public void run() {
            if (lastResultRequestName != null) {
                scriptResultCache.remove(lastResultRequestName);
            }
        }
    };
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        if (msg instanceof ControlMessage) {
            ControlMessage controlMessage = (ControlMessage) msg;

            switch (controlMessage.getKind()) {
            case PREPARED:
                return createHttpResponse((ChannelBuffer) encodeMessageWithoutContent(ctx, channel, controlMessage));
            case STARTED:
                return createHttpResponse((ChannelBuffer) encodeMessageWithoutContent(ctx, channel, controlMessage));
            case ERROR:
                return createHttpResponse((ChannelBuffer) encodeErrorMessage(ctx, channel,
                        (ErrorMessage) controlMessage));
            case FINISHED:
                ChannelBuffer header = (ChannelBuffer) encodeFinishedMessage(ctx, channel,
                        (FinishedMessage) controlMessage);
                scriptResultCache.put(controlMessage.getName(), createHttpResponse(header));
                return ChannelBuffers.EMPTY_BUFFER;
            case RESULT_REQUEST:
                if (scriptResultCache.containsKey(controlMessage.getName())
                        && (lastResultRequestName == null || System.currentTimeMillis()
                                - lastResultRequestTime.getTime() <= TIME_LIMIT_MILLIS)) {
                    lastResultRequestName = controlMessage.getName();
                    lastResultRequestTime = new Date();
                    scheduler.schedule(clearLastRequestEntry, TIME_LIMIT_MILLIS, TimeUnit.MILLISECONDS);
                    return scriptResultCache.get(controlMessage.getName());
                } else if (lastResultRequestName != null && lastResultRequestName.equals(controlMessage.getName())) {
                    BadRequestMessage badRequest = new BadRequestMessage();
                    badRequest.setName(controlMessage.getName());
                    badRequest.setContent("Invalid Request. No results for requested script.");
                    return encodeBadRequestMessage(ctx, channel, badRequest);
                } else {
                    ErrorMessage errorMessage = new ErrorMessage();
                    errorMessage.setName(controlMessage.getName());
                    errorMessage.setDescription("Script execution is not complete. Try again later");
                    errorMessage.setSummary("Early Request");
                    return createHttpResponse((ChannelBuffer) encodeErrorMessage(ctx, channel, errorMessage));
                }
            case BAD_REQUEST:
                return encodeBadRequestMessage(ctx, channel, (BadRequestMessage) controlMessage);
            case CLEAR_CACHE:
                scriptResultCache.clear();
                lastResultRequestTime = null;
                lastResultRequestName = null;
                return ChannelBuffers.EMPTY_BUFFER;
            default:
                break;
            }
        }

        // unknown message
        return msg;
    }

    private Object encodeBadRequestMessage(ChannelHandlerContext ctx,
                                           Channel channel,
                                           BadRequestMessage badRequestMessage) {
        String contentString = badRequestMessage.getContent();

        ChannelBuffer header = (ChannelBuffer) encodeMessageBeginning(channel, badRequestMessage);
        encodeContent("content", contentString, header, false);
        header.writeByte(RIGHT_CURLY_BRACKET);

        DefaultHttpResponse badRequest = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST);
        badRequest.headers().add(HttpHeaders.Names.CONTENT_TYPE, "text/html");
        badRequest.headers().add(HttpHeaders.Names.CONTENT_LENGTH, String.format("%d", header.readableBytes()));
        badRequest.setContent(header);

        return badRequest;
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
        String expectedScript = escapeJSONSpecialCharacters(finishedMessage.getExpectedScript());
        String observedScript = escapeJSONSpecialCharacters(finishedMessage.getObservedScript());

        ChannelBuffer header = (ChannelBuffer) encodeMessageBeginning(channel, finishedMessage);
        encodeContent("expected_script", expectedScript, header, true);
        encodeContent("observed_script", observedScript, header, false);
        header.writeByte(RIGHT_CURLY_BRACKET);

        return header;
    }

    private static String escapeJSONSpecialCharacters(String toEscape) {
        if (toEscape == null || toEscape.length() == 0) {
            return "\"\"";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < toEscape.length(); i++) {
            char current = toEscape.charAt(i);
            switch (current) {
            case '\"':
                sb.append("\\\"");
                break;
            case '\\':
                sb.append("\\\\");
                break;
            case '\b':
                sb.append("\\b");
                break;
            case '\f':
                sb.append("\\f");
                break;
            case '\n':
                sb.append("\\n");
                break;
            case '\r':
                sb.append("\\r");
                break;
            case '\t':
                sb.append("\\t");
                break;
            default:
                sb.append(current);
                break;
            }
        }
        return sb.toString();
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

    private static void encodeContent(String contentDescription,
                                      String contentAsString,
                                      ChannelBuffer header,
                                      boolean comma) {
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
