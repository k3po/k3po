/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.behavior.handler.codec;

import static org.kaazing.robot.behavior.handler.codec.HttpUtils.END_OF_HTTP_MESSAGE_BUFFER;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

public class SimpleHttpChannelUpstreamHandler extends SimpleChannelUpstreamHandler implements
        HttpChannelUpstreamHandler {

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        Object message = e.getMessage();
        if (message instanceof HttpRequest) {
            httpRequestReceived(ctx, e, (HttpRequest) message);
        } else if (message instanceof HttpResponse) {
            httpResponseReceived(ctx, e, (HttpResponse) message);
        } else if (message instanceof HttpChunk) {
            httpChunkReceived(ctx, e, (HttpChunk) message);
        } else if (message instanceof ChannelBuffer) {
            if (message == END_OF_HTTP_MESSAGE_BUFFER) {
                httpEndOfContentReceived(ctx, e);
            } else {
                httpContentReceived(ctx, e, (ChannelBuffer) message);
            }
        } else {
            super.messageReceived(ctx, e);
        }
    }

    @Override
    public void httpRequestReceived(ChannelHandlerContext ctx, MessageEvent e, HttpRequest httpRequest)
            throws Exception {
        super.messageReceived(ctx, e);
    }

    @Override
    public void httpResponseReceived(ChannelHandlerContext ctx, MessageEvent e, HttpResponse httpResponse)
            throws Exception {
        super.messageReceived(ctx, e);
    }

    @Override
    public void httpChunkReceived(ChannelHandlerContext ctx, MessageEvent e, HttpChunk chunk) throws Exception {
        super.messageReceived(ctx, e);
    }

    @Override
    public void httpContentReceived(ChannelHandlerContext ctx, MessageEvent e, ChannelBuffer httpContent)
            throws Exception {
        super.messageReceived(ctx, e);
    }

    @Override
    public void httpEndOfContentReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        super.messageReceived(ctx, e);
    }

}
