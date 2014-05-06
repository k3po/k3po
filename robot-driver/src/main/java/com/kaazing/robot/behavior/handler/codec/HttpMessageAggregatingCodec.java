/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.behavior.handler.codec;

import static com.kaazing.robot.behavior.handler.codec.HttpUtils.removeHttpFiltersFromPipeline;
import static org.jboss.netty.buffer.ChannelBuffers.wrappedBuffer;
import static org.jboss.netty.channel.Channels.write;
import static org.jboss.netty.handler.codec.http.HttpHeaders.setContentLength;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.DefaultHttpChunk;
import org.jboss.netty.handler.codec.http.DefaultHttpChunkTrailer;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

public class HttpMessageAggregatingCodec extends SimpleHttpChannelDownstreamHandler {

    HttpMessage aggregatedHttpMessage;

    @Override
    public void writeHttpRequest(ChannelHandlerContext ctx, MessageEvent e, HttpRequest httpRequest) throws Exception {
        if (httpRequest.isChunked()) {
            write(ctx, e.getFuture(), httpRequest);
        } else {
            aggregatedHttpMessage = httpRequest;
        }
    }

    @Override
    public void writeHttpResponse(ChannelHandlerContext ctx, MessageEvent e, HttpResponse httpResponse)
            throws Exception {
        if (httpResponse.isChunked()) {
            write(ctx, e.getFuture(), httpResponse);
            removeHttpFiltersFromPipeline(ctx.getPipeline());
        } else {
            aggregatedHttpMessage = httpResponse;
        }
    }

    @Override
    public void writeHttpChunk(ChannelHandlerContext ctx, MessageEvent e, HttpChunk chunk) throws Exception {
        write(ctx, e.getFuture(), chunk);
    }

    @Override
    public void writeHttpContent(ChannelHandlerContext ctx, MessageEvent e, ChannelBuffer httpContent) throws Exception {
        if (aggregatedHttpMessage.isChunked()) {
            HttpChunk chunk = new DefaultHttpChunk(httpContent);
            write(ctx, e.getFuture(), chunk);
        } else {
            ChannelBuffer priorContent = aggregatedHttpMessage.getContent();
            if (priorContent == null || priorContent == ChannelBuffers.EMPTY_BUFFER) {
                aggregatedHttpMessage.setContent(httpContent);
            } else {
                ChannelBuffer[] holdingBuffer = {priorContent, httpContent};
                aggregatedHttpMessage.setContent(wrappedBuffer(holdingBuffer));
            }
            e.getFuture().setSuccess();
        }
    }

    @Override
    public void writeHttpEndOfContent(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if (aggregatedHttpMessage.isChunked()) {
            HttpChunk chunk = new DefaultHttpChunkTrailer();
            write(ctx, e.getFuture(), chunk);
        } else {
            if (aggregatedHttpMessage.getHeader("Content-Length") != null) {
                ChannelBuffer content = aggregatedHttpMessage.getContent();
                setContentLength(aggregatedHttpMessage, content.capacity());
            }
            write(ctx, e.getFuture(), aggregatedHttpMessage);
            if (aggregatedHttpMessage instanceof HttpResponse && !aggregatedHttpMessage.isChunked()) {
                removeHttpFiltersFromPipeline(ctx.getPipeline());
            }
        }
    }

}
