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
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.SWITCHING_PROTOCOLS;
import static org.kaazing.robot.driver.channel.Channels.chainFutures;
import static org.kaazing.robot.driver.channel.Channels.chainWriteCompletes;
import static org.kaazing.robot.driver.netty.bootstrap.http.HttpChildChannel.HttpState.CONTENT_CHUNKED;
import static org.kaazing.robot.driver.netty.bootstrap.http.HttpChildChannel.HttpState.CONTENT_CLOSE;
import static org.kaazing.robot.driver.netty.bootstrap.http.HttpChildChannel.HttpState.CONTENT_COMPLETE;
import static org.kaazing.robot.driver.netty.bootstrap.http.HttpChildChannel.HttpState.CONTENT_LENGTH;
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
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.kaazing.robot.driver.netty.channel.http.HttpContentCompleteEvent;

public class HttpChildChannelSink extends AbstractHttpChannelSink {

    private final Channel transport;
    private HttpResponse httpBufferedResponse;
    private ChannelFuture httpBufferedFuture;

    public HttpChildChannelSink(Channel transport) {
        this.transport = transport;
    }

    @Override
    protected void setInterestOpsRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
    }

    @Override
    protected void writeRequested(ChannelPipeline pipeline, MessageEvent e) throws Exception {

        HttpChildChannel httpChildChannel = (HttpChildChannel) pipeline.getChannel();
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

            if (httpResponse.getStatus() == SWITCHING_PROTOCOLS) {
                httpResponse.setContent(EMPTY_BUFFER);
                transport.write(httpResponse);
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
                httpBufferedFuture = httpFuture;
                httpChildChannel.state(CONTENT_LENGTH);
            }
            else {
                throw new IllegalStateException("Missing Content-Length, Transfer-Encoding: chunked, or Connection: close");
            }
            break;
        case CONTENT_LENGTH:
            ChannelBuffer httpBufferedContent = httpBufferedResponse.getContent();
            int httpBufferedBytes = httpBufferedContent.readableBytes();
            if (httpChildConfig.getMaximumBufferedContentLength() >= httpBufferedBytes + httpReadableBytes) {
                httpBufferedResponse.setContent(copiedBuffer(httpBufferedContent, httpContent));
                chainFutures(httpBufferedFuture, httpFuture);
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
    protected void contentCompleteRequested(ChannelPipeline pipeline, HttpContentCompleteEvent e) throws Exception {
        // TODO: similar semantics to close
    }

    @Override
    protected void closeRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
        HttpChildChannel httpChildChannel = (HttpChildChannel) pipeline.getChannel();
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

            httpResponse.setContent(EMPTY_BUFFER);
            ChannelFuture httpFuture = evt.getFuture();
            ChannelFuture future = transport.write(httpResponse);

            if (httpResponse.getStatus() == SWITCHING_PROTOCOLS) {
                httpChildChannel.state(UPGRADED);
                future = transport.close();
                chainFutures(future, httpFuture);
            }
            else if (httpResponse.headers().getAll(Names.CONNECTION).contains(Values.CLOSE)) {
                httpChildChannel.state(CONTENT_CLOSE);
                future = transport.close();
                chainFutures(future, httpFuture);
            }
            else if (httpResponse.getStatus() == OK) {
                httpResponse.headers().set(Names.CONTENT_LENGTH, 0);
                httpChildChannel.state(CONTENT_COMPLETE);
                chainFutures(future, httpFuture);
            }
            else if (httpResponse.getStatus() == NO_CONTENT) {
                httpChildChannel.state(CONTENT_COMPLETE);
                chainFutures(future, httpFuture);
            }
            break;
        }
        case CONTENT_LENGTH: {
            HttpResponse httpBufferedResponse = this.httpBufferedResponse;
            ChannelFuture httpBufferedFuture = this.httpBufferedFuture;
            this.httpBufferedResponse = null;
            this.httpBufferedFuture = null;

            ChannelFuture httpFuture = evt.getFuture();
            chainFutures(httpBufferedFuture, httpFuture);

            ChannelBuffer httpBufferedContent = httpBufferedResponse.getContent();
            int httpReadableBytes = httpBufferedContent.readableBytes();
            setContentLength(httpBufferedResponse, httpReadableBytes);
            ChannelFuture future = transport.write(httpBufferedResponse);
            httpChildChannel.state(CONTENT_COMPLETE);
            chainWriteCompletes(future, httpBufferedFuture, httpReadableBytes);
            break;
        }
        case CONTENT_CHUNKED: {
            ChannelFuture httpFuture = evt.getFuture();
            HttpChunk httpChunk = DefaultHttpChunk.LAST_CHUNK;
            ChannelFuture future = transport.write(httpChunk);
            httpChildChannel.state(CONTENT_COMPLETE);
            chainFutures(future, httpFuture);
            break;
        }
        case CONTENT_CLOSE: {
            ChannelFuture httpFuture = evt.getFuture();
            ChannelFuture future = transport.close();
            httpChildChannel.state(CONTENT_COMPLETE);
            chainFutures(future, httpFuture);
            break;
        }
        case CONTENT_COMPLETE:
        default:
            break;
        }

        if (httpChildChannel.setClosed()) {
            fireChannelDisconnected(httpChildChannel);
            fireChannelUnbound(httpChildChannel);
            fireChannelClosed(httpChildChannel);
        }
    }
}
