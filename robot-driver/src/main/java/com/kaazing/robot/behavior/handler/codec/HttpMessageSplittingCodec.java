/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.behavior.handler.codec;

import static com.kaazing.robot.behavior.handler.codec.HttpUtils.END_OF_HTTP_MESSAGE_BUFFER;
import static com.kaazing.robot.behavior.handler.codec.HttpUtils.isOneOOneResponseMessage;
import static com.kaazing.robot.behavior.handler.codec.HttpUtils.removeHttpFiltersFromPipeline;
import static org.jboss.netty.buffer.ChannelBuffers.EMPTY_BUFFER;
import static org.jboss.netty.channel.Channels.fireMessageReceived;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

public class HttpMessageSplittingCodec extends SimpleHttpChannelUpstreamHandler {

    @Override
    public void httpRequestReceived(ChannelHandlerContext ctx, MessageEvent e, HttpRequest httpRequest)
            throws Exception {
        if (httpRequest.isChunked()) {
            handleHttpMessageThatIsChunked(ctx, e, httpRequest);
        } else {
            handleBasicHttpMessage(ctx, e, httpRequest);
        }
    }

    @Override
    public void httpResponseReceived(ChannelHandlerContext ctx, MessageEvent e, HttpResponse httpResponse)
            throws Exception {
        if (isOneOOneResponseMessage(httpResponse)) {
            // if 101, remove codec after forwarding
            fireMessageReceived(ctx, httpResponse);
            removeHttpFiltersFromPipeline(ctx.getPipeline());
            fireMessageReceived(ctx, END_OF_HTTP_MESSAGE_BUFFER);
        } else if (httpResponse.isChunked()) {
            // if it is chunked don't send closing frame and don't remove codec
            handleHttpMessageThatIsChunked(ctx, e, httpResponse);
        } else {
            handleBasicHttpMessage(ctx, e, httpResponse);
            removeHttpFiltersFromPipeline(ctx.getPipeline());
        }
    }

    private void handleBasicHttpMessage(ChannelHandlerContext ctx, MessageEvent e, HttpMessage httpMessage)
            throws Exception {
        ChannelBuffer content = httpMessage.getContent();
        if (content != null) {
            // if content, split it out
            fireMessageReceived(ctx, httpMessage);
            fireMessageReceived(ctx, content);
            fireMessageReceived(ctx, END_OF_HTTP_MESSAGE_BUFFER);
        } else {
            // if no content, forward message
            fireMessageReceived(ctx, httpMessage);
            fireMessageReceived(ctx, END_OF_HTTP_MESSAGE_BUFFER);
        }
    }

    private void handleHttpMessageThatIsChunked(ChannelHandlerContext ctx, MessageEvent e, HttpMessage httpMessage)
            throws Exception {
        fireMessageReceived(ctx, httpMessage);
    }

    @Override
    public void httpChunkReceived(ChannelHandlerContext ctx, MessageEvent e, HttpChunk chunk) throws Exception {
        ChannelBuffer content = chunk.getContent();
        // Send chunk data
        if (content != null && content != EMPTY_BUFFER) {
            fireMessageReceived(ctx, content);
        }
        // Send Empty buffer to indicate end of http iff chunking is done
        if (chunk.isLast()) {
            fireMessageReceived(ctx, END_OF_HTTP_MESSAGE_BUFFER);
        }
    }

}
