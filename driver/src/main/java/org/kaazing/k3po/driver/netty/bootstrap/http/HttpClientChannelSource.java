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

package org.kaazing.k3po.driver.netty.bootstrap.http;

import static java.lang.String.format;
import static org.jboss.netty.channel.Channels.fireChannelClosed;
import static org.jboss.netty.channel.Channels.fireChannelDisconnected;
import static org.jboss.netty.channel.Channels.fireChannelInterestChanged;
import static org.jboss.netty.channel.Channels.fireChannelUnbound;
import static org.jboss.netty.channel.Channels.fireExceptionCaught;
import static org.jboss.netty.channel.Channels.fireMessageReceived;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.SWITCHING_PROTOCOLS;
import static org.kaazing.k3po.driver.netty.channel.Channels.fireInputShutdown;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpRequestEncoder;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;


public class HttpClientChannelSource extends HttpChannelHandler {

    private HttpClientChannel httpClientChannel;

    public void setHttpChannel(HttpClientChannel httpClientChannel) {
        assert this.httpClientChannel == null;
        this.httpClientChannel = httpClientChannel;
    }

    @Override
    protected void httpMessageReceived(ChannelHandlerContext ctx, MessageEvent e, HttpResponse httpResponse) throws Exception {
        HttpChannelConfig httpChildConfig = httpClientChannel.getConfig();
        httpChildConfig.setStatus(httpResponse.getStatus());
        httpChildConfig.setVersion(httpResponse.getProtocolVersion());
        httpChildConfig.getReadHeaders().set(httpResponse.headers());

        if (httpResponse.getStatus().getCode() == SWITCHING_PROTOCOLS.getCode()) {
            Channel transport = ctx.getChannel();
            ChannelPipeline pipeline = transport.getPipeline();
            pipeline.remove(HttpRequestEncoder.class);

            boolean readable = httpClientChannel.isReadable();
            if (!readable) {
                httpClientChannel.setReadable(true);
                fireChannelInterestChanged(httpClientChannel);
            }

            // propagate any remaining bytes out of replaying decoder (after channel is marked as readable)
            ChannelHandlerContext httpDecoderCtx = pipeline.getContext(HttpResponseDecoder.class);
            HttpResponseDecoder httpDecoder = (HttpResponseDecoder) httpDecoderCtx.getHandler();
            httpDecoder.replace(format("%s.noop", httpDecoderCtx.getName()), NOOP_HANDLER);
        }
        else {
            ChannelBuffer content = httpResponse.getContent();
            boolean readable = httpClientChannel.isReadable();
            if (!readable) {
                httpClientChannel.setReadable(true);
                fireChannelInterestChanged(httpClientChannel);
            }

            if (content.readable()) {
                fireMessageReceived(httpClientChannel, content);
            }

            if (!httpResponse.isChunked()) {
                HttpClientChannel httpClientChannel = this.httpClientChannel;
                this.httpClientChannel = null;
                fireInputShutdown(httpClientChannel);

                boolean wasConnected = httpClientChannel.isConnected();
                boolean wasBound = httpClientChannel.isBound();
                if (httpClientChannel.setClosed()) {
                    if (wasConnected) {
                        fireChannelDisconnected(httpClientChannel);
                    }
                    if (wasBound) {
                        fireChannelUnbound(httpClientChannel);
                    }
                    fireChannelClosed(httpClientChannel);
                }
            }
        }
    }

    @Override
    protected void httpMessageReceived(ChannelHandlerContext ctx, MessageEvent e, HttpChunk httpChunk) throws Exception {
        ChannelBuffer content = httpChunk.getContent();
        if (content.readable()) {
            fireMessageReceived(httpClientChannel, content);
        }

        boolean last = httpChunk.isLast();
        if (last) {
            HttpClientChannel httpClientChannel = this.httpClientChannel;
            this.httpClientChannel = null;
            fireInputShutdown(httpClientChannel);

            if (httpClientChannel.setClosed()) {
                fireChannelClosed(httpClientChannel);
            }
        }
    }

    @Override
    protected void httpMessageReceived(ChannelHandlerContext ctx, MessageEvent e, ChannelBuffer message) throws Exception {
        if (message.readable()) {
            // after 101 switching protocols
            fireMessageReceived(httpClientChannel, message);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        if (httpClientChannel != null) {
            HttpClientChannel httpClientChannel = this.httpClientChannel;
            this.httpClientChannel = null;
            if (httpClientChannel.setClosed()) {
                fireExceptionCaught(httpClientChannel, e.getCause());
                fireChannelClosed(httpClientChannel);
            }
        }

        Channel channel = ctx.getChannel();
        channel.close();
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        if (httpClientChannel != null) {
            HttpChannelConfig httpClientConfig = httpClientChannel.getConfig();
            HttpResponseStatus httpStatus = httpClientConfig.getStatus();
            int httpStatusCode = (httpStatus != null) ? httpStatus.getCode() : 0;
            if (httpStatusCode == SWITCHING_PROTOCOLS.getCode()) {
                if (httpClientChannel.setClosed()) {
                    fireChannelDisconnected(httpClientChannel);
                    fireChannelUnbound(httpClientChannel);
                    fireChannelClosed(httpClientChannel);
                }
            }
            else {
                ChannelException exception = new ChannelException("transport closed unexpectedly");
                exception.fillInStackTrace();
                fireExceptionCaught(httpClientChannel, exception);
            }
        }
    }

    @Sharable
    private static class NoopChannelHandler extends SimpleChannelHandler {
    }

    private static final ChannelHandler NOOP_HANDLER = new NoopChannelHandler();
}
