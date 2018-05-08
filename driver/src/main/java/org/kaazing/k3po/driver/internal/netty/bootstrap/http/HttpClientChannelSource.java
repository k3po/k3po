/**
 * Copyright 2007-2015, Kaazing Corporation. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaazing.k3po.driver.internal.netty.bootstrap.http;

import static java.lang.String.format;
import static org.jboss.netty.channel.Channels.fireChannelClosed;
import static org.jboss.netty.channel.Channels.fireChannelDisconnected;
import static org.jboss.netty.channel.Channels.fireChannelInterestChanged;
import static org.jboss.netty.channel.Channels.fireChannelUnbound;
import static org.jboss.netty.channel.Channels.fireExceptionCaught;
import static org.jboss.netty.channel.Channels.fireMessageReceived;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.SWITCHING_PROTOCOLS;
import static org.kaazing.k3po.driver.internal.netty.channel.Channels.fireInputAborted;
import static org.kaazing.k3po.driver.internal.netty.channel.Channels.fireInputShutdown;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpChunkTrailer;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequestEncoder;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.kaazing.k3po.driver.internal.netty.channel.ShutdownInputEvent;


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
            httpClientChannel.readState(HttpClientChannel.HttpReadState.UPGRADED);

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
            if (httpChunk instanceof HttpChunkTrailer) {
                HttpHeaders trailingHeaders = ((HttpChunkTrailer) httpChunk).trailingHeaders();
                httpClientChannel.getConfig().getReadTrailers().set(trailingHeaders);
            }
            this.httpClientChannel = null;
            fireInputShutdown(httpClientChannel);

            if (httpClientChannel.setClosed()) {
                fireChannelDisconnected(httpClientChannel);
                fireChannelUnbound(httpClientChannel);
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

        HttpClientChannel httpClientChannel = this.httpClientChannel;
        if (httpClientChannel != null) {

            this.httpClientChannel = null;

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
                fireInputAborted(httpClientChannel);
            }
        }
    }

    @Override
    public void inputShutdown(ChannelHandlerContext ctx, ShutdownInputEvent e) {
        HttpClientChannel httpClientChannel = this.httpClientChannel;

        if (httpClientChannel != null) {
            if (httpClientChannel.readState() == HttpClientChannel.HttpReadState.UPGRADED)
            {
                fireInputShutdown(httpClientChannel);
            }
        }
    }

    @Sharable
    private static class NoopChannelHandler extends SimpleChannelHandler {
    }

    private static final ChannelHandler NOOP_HANDLER = new NoopChannelHandler();
}
