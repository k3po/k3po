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

package org.kaazing.robot.behavior.handler.codec;

import static org.jboss.netty.buffer.ChannelBuffers.wrappedBuffer;
import static org.jboss.netty.channel.Channels.write;
import static org.jboss.netty.handler.codec.http.HttpHeaders.setContentLength;
import static org.kaazing.robot.behavior.handler.codec.HttpUtils.removeHttpFiltersFromPipeline;

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
