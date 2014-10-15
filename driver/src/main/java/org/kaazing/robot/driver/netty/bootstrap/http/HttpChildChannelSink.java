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

package org.kaazing.robot.driver.netty.bootstrap.http;

import static org.jboss.netty.buffer.ChannelBuffers.EMPTY_BUFFER;
import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.jboss.netty.channel.Channels.fireChannelClosed;
import static org.jboss.netty.channel.Channels.fireChannelDisconnected;
import static org.jboss.netty.channel.Channels.fireChannelUnbound;
import static org.jboss.netty.handler.codec.http.HttpHeaders.getContentLength;
import static org.jboss.netty.handler.codec.http.HttpHeaders.isContentLengthSet;
import static org.jboss.netty.handler.codec.http.HttpHeaders.isTransferEncodingChunked;
import static org.jboss.netty.handler.codec.http.HttpHeaders.setContentLength;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.SWITCHING_PROTOCOLS;
import static org.kaazing.robot.driver.channel.Channels.chainFutures;
import static org.kaazing.robot.driver.channel.Channels.chainWriteCompletes;
import static org.kaazing.robot.driver.netty.bootstrap.http.HttpChildChannel.HttpState.CONTENT_BUFFERED;
import static org.kaazing.robot.driver.netty.bootstrap.http.HttpChildChannel.HttpState.CONTENT_CHUNKED;
import static org.kaazing.robot.driver.netty.bootstrap.http.HttpChildChannel.HttpState.CONTENT_CLOSE;
import static org.kaazing.robot.driver.netty.bootstrap.http.HttpChildChannel.HttpState.CONTENT_COMPLETE;
import static org.kaazing.robot.driver.netty.bootstrap.http.HttpChildChannel.HttpState.UPGRADED;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.DefaultHttpChunk;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpHeaders.Names;
import org.jboss.netty.handler.codec.http.HttpHeaders.Values;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.kaazing.robot.driver.netty.bootstrap.channel.AbstractChannelSink;
import org.kaazing.robot.driver.netty.channel.FlushEvent;
import org.kaazing.robot.driver.netty.channel.ShutdownOutputEvent;

public class HttpChildChannelSink extends AbstractChannelSink {

    private final Channel transport;
    private HttpResponse httpBufferedResponse;

    public HttpChildChannelSink(Channel transport) {
        this.transport = transport;
    }

    @Override
    protected void setInterestOpsRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
    }

    @Override
    protected void writeRequested(ChannelPipeline httpPipeline, MessageEvent e) throws Exception {

        HttpChildChannel httpChildChannel = (HttpChildChannel) httpPipeline.getChannel();
        HttpChannelConfig httpChildConfig = httpChildChannel.getConfig();
        ChannelFuture httpFuture = e.getFuture();
        ChannelBuffer httpContent = (ChannelBuffer) e.getMessage();
        int httpReadableBytes = httpContent.readableBytes();

        switch (httpChildChannel.state()) {
        case RESPONSE:
            HttpVersion version = httpChildConfig.getVersion();
            HttpResponseStatus status = httpChildConfig.getStatus();
            HttpHeaders headers = httpChildConfig.getWriteHeaders();

            HttpResponse httpResponse = new DefaultHttpResponse(version, status);
            if (headers != null) {
                httpResponse.headers().add(headers);
            }

            if (httpResponse.getStatus().getCode() == SWITCHING_PROTOCOLS.getCode()) {
                httpResponse.setContent(EMPTY_BUFFER);
                ChannelPipeline pipeline = transport.getPipeline();
                pipeline.remove(HttpRequestDecoder.class);
                transport.write(httpResponse);
                pipeline.remove(HttpResponseEncoder.class);
                ChannelFuture future = transport.write(httpContent);
                httpChildChannel.state(UPGRADED);
                chainWriteCompletes(future, httpFuture, httpReadableBytes);
            }
            else if (isContentLengthSet(httpResponse) && httpReadableBytes == getContentLength(httpResponse)) {
                httpResponse.setContent(httpContent);
                ChannelFuture future = transport.write(httpResponse);
                httpChildChannel.state(CONTENT_COMPLETE);
                chainWriteCompletes(future, httpFuture, httpReadableBytes);
            }
            else if (isTransferEncodingChunked(httpResponse)) {
                httpResponse.setChunked(true);
                transport.write(httpResponse);
                httpChildChannel.state(CONTENT_CHUNKED);

                HttpChunk httpChunk = new DefaultHttpChunk(httpContent);
                ChannelFuture future = transport.write(httpChunk);
                chainWriteCompletes(future, httpFuture, httpReadableBytes);
            }
            else if (httpResponse.headers().getAll(Names.CONNECTION).contains(Values.CLOSE)) {
                httpResponse.setContent(httpContent);
                ChannelFuture future = transport.write(httpResponse);
                httpChildChannel.state(CONTENT_CLOSE);
                chainWriteCompletes(future, httpFuture, httpReadableBytes);
            }
            else if (httpChildConfig.getMaximumBufferedContentLength() >= httpReadableBytes) {
                // automatically calculate content-length
                httpResponse.setContent(httpContent);
                httpBufferedResponse = httpResponse;
                httpChildChannel.state(CONTENT_BUFFERED);
                httpFuture.setSuccess();
            }
            else {
                throw new IllegalStateException("Missing Content-Length, Transfer-Encoding: chunked, or Connection: close");
            }
            break;
        case CONTENT_BUFFERED:
            ChannelBuffer httpBufferedContent = httpBufferedResponse.getContent();
            int httpBufferedBytes = httpBufferedContent.readableBytes();
            if (httpChildConfig.getMaximumBufferedContentLength() >= httpBufferedBytes + httpReadableBytes) {
                httpBufferedResponse.setContent(copiedBuffer(httpBufferedContent, httpContent));
                httpFuture.setSuccess();
            }
            else {
                throw new IllegalStateException("Exceeded maximum buffered content to calculate content length");
            }
            break;
        case CONTENT_CHUNKED:
        case CONTENT_CLOSE: {
            HttpChunk httpChunk = new DefaultHttpChunk(httpContent);
            ChannelFuture future = transport.write(httpChunk);
            chainWriteCompletes(future, httpFuture, httpReadableBytes);
            break;
        }
        case UPGRADED: {
            ChannelFuture future = transport.write(httpContent);
            chainWriteCompletes(future, httpFuture, httpReadableBytes);
            break;
        }
        case CONTENT_COMPLETE:
            throw new IllegalStateException();
        }
    }

    @Override
    protected void flushRequested(ChannelPipeline pipeline, FlushEvent evt) throws Exception {
        HttpChildChannel httpChildChannel = (HttpChildChannel) pipeline.getChannel();
        ChannelFuture httpFuture = evt.getFuture();
        flushRequested(httpChildChannel, httpFuture);
    }

    @Override
    protected void shutdownOutputRequested(ChannelPipeline pipeline, ShutdownOutputEvent evt) throws Exception {
        HttpChildChannel httpChildChannel = (HttpChildChannel) pipeline.getChannel();
        ChannelFuture httpFuture = evt.getFuture();
        // TODO: shutdown response output is identical to close semantics (if request fully read already)
        closeRequested(httpChildChannel, httpFuture);
    }

    @Override
    protected void closeRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
        HttpChildChannel httpChildChannel = (HttpChildChannel) pipeline.getChannel();
        ChannelFuture httpFuture = evt.getFuture();
        closeRequested(httpChildChannel, httpFuture);
    }

    private void closeRequested(HttpChildChannel httpChildChannel, ChannelFuture httpFuture) {
        if (!httpChildChannel.isOpen()) {
            httpFuture.setSuccess();
            return;
        }

        flushRequested(httpChildChannel, httpFuture);

        switch (httpChildChannel.state()) {
        case CONTENT_CLOSE:
        case UPGRADED:
            // setClosed() chained asynchronously after transport.close() completes
            transport.close();
            break;
        case CONTENT_COMPLETE:
            if (httpChildChannel.setClosed()) {
                fireChannelDisconnected(httpChildChannel);
                fireChannelUnbound(httpChildChannel);
                fireChannelClosed(httpChildChannel);
            }
            break;
        default:
            throw new IllegalStateException("Unexpected state after flushRequested: " + httpChildChannel.state());
        }
    }

    private void flushRequested(HttpChildChannel httpChildChannel, ChannelFuture httpFuture) {
        switch (httpChildChannel.state()) {
        case RESPONSE: {
            HttpChannelConfig httpChildConfig = httpChildChannel.getConfig();
            HttpVersion version = httpChildConfig.getVersion();
            HttpResponseStatus status = httpChildConfig.getStatus();
            HttpHeaders headers = httpChildConfig.getWriteHeaders();

            HttpResponse httpResponse = new DefaultHttpResponse(version, status);
            if (headers != null) {
                httpResponse.headers().add(headers);
            }

            HttpResponseStatus httpStatus = httpResponse.getStatus();
            int httpStatusCode = (httpStatus != null) ? httpStatus.getCode() : 0;
            if (httpStatusCode == SWITCHING_PROTOCOLS.getCode()) {
                ChannelFuture future = transport.write(httpResponse);
                httpChildChannel.state(UPGRADED);
                ChannelPipeline pipeline = transport.getPipeline();
                pipeline.remove(HttpRequestDecoder.class);
                pipeline.remove(HttpResponseEncoder.class);
                chainFutures(future, httpFuture);
            }
            else if (httpResponse.headers().getAll(Names.CONNECTION).contains(Values.CLOSE)) {
                ChannelFuture future = transport.write(httpResponse);
                httpChildChannel.state(CONTENT_CLOSE);
                chainFutures(future, httpFuture);
            }
            else {
                if (httpStatusCode >= 200 && httpChildConfig.getMaximumBufferedContentLength() > 0) {
                    switch (httpStatusCode) {
                    case 204: // NO_CONTENT
                    case 205: // RESET_CONTENT
                    case 304: // NOT_MODIFIED
                        break;
                    default:
                        setContentLength(httpResponse, 0);
                        break;
                    }
                }

                ChannelFuture future = transport.write(httpResponse);
                httpChildChannel.state(CONTENT_COMPLETE);
                chainFutures(future, httpFuture);
            }
            break;
        }
        case CONTENT_BUFFERED: {
            HttpResponse httpBufferedResponse = this.httpBufferedResponse;
            this.httpBufferedResponse = null;

            ChannelBuffer httpBufferedContent = httpBufferedResponse.getContent();
            int httpReadableBytes = httpBufferedContent.readableBytes();
            setContentLength(httpBufferedResponse, httpReadableBytes);
            ChannelFuture future = transport.write(httpBufferedResponse);
            httpChildChannel.state(CONTENT_COMPLETE);
            chainWriteCompletes(future, httpFuture, httpReadableBytes);
            break;
        }
        case CONTENT_CHUNKED: {
            HttpChunk httpChunk = DefaultHttpChunk.LAST_CHUNK;
            ChannelFuture future = transport.write(httpChunk);
            httpChildChannel.state(CONTENT_COMPLETE);
            chainFutures(future, httpFuture);
            break;
        }
        case CONTENT_CLOSE: {
            ChannelFuture future = transport.close();
            httpChildChannel.state(CONTENT_COMPLETE);
            chainFutures(future, httpFuture);
            break;
        }
        case UPGRADED:
        case CONTENT_COMPLETE:
            httpFuture.setSuccess();
            break;
        default:
            break;
        }
    }
}
