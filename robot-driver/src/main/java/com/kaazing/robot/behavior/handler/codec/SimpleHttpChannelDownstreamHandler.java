/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.behavior.handler.codec;

import static com.kaazing.robot.behavior.handler.codec.HttpUtils.END_OF_HTTP_MESSAGE_BUFFER;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelDownstreamHandler;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;


public class SimpleHttpChannelDownstreamHandler extends SimpleChannelDownstreamHandler implements
        HttpChannelDownstreamHandler {

    @Override
    public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        Object message = e.getMessage();
        if (message instanceof HttpRequest) {
            writeHttpRequest(ctx, e, (HttpRequest) message);
        } else if (message instanceof HttpResponse) {
            writeHttpResponse(ctx, e, (HttpResponse) message);
        } else if (message instanceof HttpChunk) {
            writeHttpChunk(ctx, e, (HttpChunk) message);
        } else if (message instanceof ChannelBuffer) {
            if (message == END_OF_HTTP_MESSAGE_BUFFER) {
                writeHttpEndOfContent(ctx, e);
            } else {
                writeHttpContent(ctx, e, (ChannelBuffer) message);
            }
        } else {
            super.writeRequested(ctx, e);
        }
    }

    @Override
    public void writeHttpRequest(ChannelHandlerContext ctx, MessageEvent e, HttpRequest httpRequest) throws Exception {
        super.writeRequested(ctx, e);
    }

    @Override
    public void writeHttpResponse(ChannelHandlerContext ctx, MessageEvent e, HttpResponse httpResponse)
            throws Exception {
        super.writeRequested(ctx, e);
    }

    @Override
    public void writeHttpChunk(ChannelHandlerContext ctx, MessageEvent e, HttpChunk chunk) throws Exception {
        super.writeRequested(ctx, e);
    }

    @Override
    public void writeHttpContent(ChannelHandlerContext ctx, MessageEvent e, ChannelBuffer httpContent) throws Exception {
        super.writeRequested(ctx, e);
    }

    @Override
    public void writeHttpEndOfContent(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        super.writeRequested(ctx, e);
    }
}
