/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.control.handler;

import static java.lang.String.format;
import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.jboss.netty.buffer.ChannelBuffers.dynamicBuffer;
import static org.jboss.netty.buffer.ChannelBuffers.wrappedBuffer;
import static org.jboss.netty.util.CharsetUtil.UTF_8;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import com.kaazing.robot.control.ControlMessage;
import com.kaazing.robot.control.ControlMessage.Kind;
import com.kaazing.robot.control.ErrorMessage;
import com.kaazing.robot.control.FinishedMessage;
import com.kaazing.robot.control.PreparedMessage;
import com.kaazing.robot.control.StartedMessage;

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
        String observedScript = finishedMessage.getObservedScript();

        ChannelBuffer header = dynamicBuffer(channel.getConfig().getBufferFactory());
        encodeInitial(kind, header);
        encodeNameHeader(scriptName, header);
        return encodeContent(observedScript, header);
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
        }
        else {
            header.writeBytes(copiedBuffer("content-length:0", UTF_8));
            header.writeByte(LF);
            header.writeByte(LF);

            return header;
        }
    }

}
