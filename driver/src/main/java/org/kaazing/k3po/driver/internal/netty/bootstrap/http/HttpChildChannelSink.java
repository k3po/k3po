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

import static java.util.Objects.requireNonNull;
import static org.jboss.netty.buffer.ChannelBuffers.EMPTY_BUFFER;
import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.jboss.netty.channel.Channels.fireChannelClosed;
import static org.jboss.netty.channel.Channels.fireChannelDisconnected;
import static org.jboss.netty.channel.Channels.fireChannelUnbound;
import static org.jboss.netty.channel.Channels.future;
import static org.jboss.netty.handler.codec.http.HttpHeaders.getContentLength;
import static org.jboss.netty.handler.codec.http.HttpHeaders.isContentLengthSet;
import static org.jboss.netty.handler.codec.http.HttpHeaders.isTransferEncodingChunked;
import static org.jboss.netty.handler.codec.http.HttpHeaders.setContentLength;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.SWITCHING_PROTOCOLS;
import static org.kaazing.k3po.driver.internal.channel.Channels.chainFutures;
import static org.kaazing.k3po.driver.internal.channel.Channels.chainWriteCompletes;
import static org.kaazing.k3po.driver.internal.netty.bootstrap.http.HttpChildChannel.HttpWriteState.CONTENT_BUFFERED;
import static org.kaazing.k3po.driver.internal.netty.bootstrap.http.HttpChildChannel.HttpWriteState.CONTENT_CHUNKED;
import static org.kaazing.k3po.driver.internal.netty.bootstrap.http.HttpChildChannel.HttpWriteState.CONTENT_CLOSE;
import static org.kaazing.k3po.driver.internal.netty.bootstrap.http.HttpChildChannel.HttpWriteState.CONTENT_CLOSING;
import static org.kaazing.k3po.driver.internal.netty.bootstrap.http.HttpChildChannel.HttpWriteState.CONTENT_COMPLETE;
import static org.kaazing.k3po.driver.internal.netty.bootstrap.http.HttpChildChannel.HttpWriteState.UPGRADED;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.DefaultHttpChunk;
import org.jboss.netty.handler.codec.http.DefaultHttpChunkTrailer;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpChunkTrailer;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpHeaders.Names;
import org.jboss.netty.handler.codec.http.HttpHeaders.Values;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.kaazing.k3po.driver.internal.netty.bootstrap.channel.AbstractChannelSink;
import org.kaazing.k3po.driver.internal.netty.channel.FlushEvent;
import org.kaazing.k3po.driver.internal.netty.channel.ShutdownOutputEvent;
import org.kaazing.k3po.driver.internal.netty.channel.WriteAbortEvent;

public class HttpChildChannelSink extends AbstractChannelSink {

    private final Channel transport;
    private HttpResponse httpBufferedResponse;

    public HttpChildChannelSink(Channel transport) {
        this.transport = requireNonNull(transport);
    }

    @Override
    public ChannelFuture execute(ChannelPipeline httpPipeline, Runnable task) {
        ChannelPipeline pipeline = transport.getPipeline();
        ChannelFuture future = pipeline.execute(task);
        Channel httpChannel = pipeline.getChannel();
        ChannelFuture httpFuture = future(httpChannel);
        chainFutures(future, httpFuture);
        return httpFuture;
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

        switch (httpChildChannel.writeState()) {
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
                httpChildChannel.writeState(UPGRADED);
                chainWriteCompletes(future, httpFuture, httpReadableBytes);
            }
            else if (isContentLengthSet(httpResponse) && httpReadableBytes == getContentLength(httpResponse)) {
                httpResponse.setContent(httpContent);
                ChannelFuture future = transport.write(httpResponse);
                httpChildChannel.writeState(CONTENT_COMPLETE);
                chainWriteCompletes(future, httpFuture, httpReadableBytes);
            }
            else if (isTransferEncodingChunked(httpResponse)) {
                httpResponse.setChunked(true);
                transport.write(httpResponse);
                httpChildChannel.writeState(CONTENT_CHUNKED);

                HttpChunk httpChunk = new DefaultHttpChunk(httpContent);
                ChannelFuture future = transport.write(httpChunk);
                chainWriteCompletes(future, httpFuture, httpReadableBytes);
            }
            else if (httpResponse.headers().getAll(Names.CONNECTION).contains(Values.CLOSE)) {
                httpResponse.setContent(httpContent);
                ChannelFuture future = transport.write(httpResponse);
                httpChildChannel.writeState(CONTENT_CLOSE);
                chainWriteCompletes(future, httpFuture, httpReadableBytes);
            }
            else if (httpChildConfig.getMaximumBufferedContentLength() >= httpReadableBytes) {
                // automatically calculate content-length
                httpResponse.setContent(httpContent);
                httpBufferedResponse = httpResponse;
                httpChildChannel.writeState(CONTENT_BUFFERED);
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
        case CONTENT_CLOSING:
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
    protected void abortOutputRequested(ChannelPipeline pipeline, final WriteAbortEvent evt) throws Exception {
        HttpChildChannel channel = (HttpChildChannel) evt.getChannel();
        switch (channel.writeState())
        {
        case CONTENT_BUFFERED:
            ChannelFuture flushFuture = Channels.future(channel);
            flushRequested(channel, flushFuture);
            flushFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    ChannelFuture disconnect = transport.disconnect();
                    chainFutures(disconnect, evt.getFuture());
                }
            });
            break;
        default:
            ChannelFuture disconnect = transport.disconnect();
            chainFutures(disconnect, evt.getFuture());
            break;
        }
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

    private void closeRequested(final HttpChildChannel httpChildChannel, ChannelFuture httpFuture) {
        if (!httpChildChannel.isOpen()) {
            httpFuture.setSuccess();
            return;
        }

        ChannelFuture httpCloseFuture = httpChildChannel.getCloseFuture();
        if (httpFuture != httpCloseFuture) {
            chainFutures(httpCloseFuture, httpFuture);
        }

        ChannelFuture httpFlushed = future(httpChildChannel);
        flushRequested(httpChildChannel, httpFlushed);

        switch (httpChildChannel.writeState()) {
        case UPGRADED:
        case CONTENT_CLOSE:
            httpChildChannel.writeState(CONTENT_CLOSING);
            if (transport.isOpen()) {
                // setClosed() chained asynchronously after transport.close() completes
                transport.close();
            }
            else if (httpChildChannel.setWriteClosed()) {
                fireChannelDisconnected(httpChildChannel);
                fireChannelUnbound(httpChildChannel);
                fireChannelClosed(httpChildChannel);
            }
            break;
        case CONTENT_CHUNKED:
            HttpChunkTrailer trailingChunk = new DefaultHttpChunkTrailer();
            trailingChunk.trailingHeaders().add(httpChildChannel.getConfig().getWriteTrailers());
            ChannelFuture future = transport.write(trailingChunk);
            httpChildChannel.writeState(CONTENT_COMPLETE);
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (httpChildChannel.setWriteClosed()) {
                        fireChannelDisconnected(httpChildChannel);
                        fireChannelUnbound(httpChildChannel);
                        fireChannelClosed(httpChildChannel);
                    }
                }
            });
            break;
        case CONTENT_COMPLETE:
            if (httpChildChannel.setWriteClosed()) {
                fireChannelDisconnected(httpChildChannel);
                fireChannelUnbound(httpChildChannel);
                fireChannelClosed(httpChildChannel);
            }
            break;
        default:
            throw new IllegalStateException("Unexpected state after closeRequested: " + httpChildChannel.writeState());
        }
    }

    private void flushRequested(HttpChildChannel httpChildChannel, ChannelFuture httpFuture) {
        switch (httpChildChannel.writeState()) {
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
                httpChildChannel.writeState(UPGRADED);
                ChannelPipeline pipeline = transport.getPipeline();
                pipeline.remove(HttpRequestDecoder.class);
                pipeline.remove(HttpResponseEncoder.class);
                chainFutures(future, httpFuture);
            }
            else if (isTransferEncodingChunked(httpResponse)) {
                httpResponse.setChunked(true);
                ChannelFuture future = transport.write(httpResponse);
                httpChildChannel.writeState(CONTENT_CHUNKED);
                chainFutures(future, httpFuture);
            }
            else if (httpResponse.headers().getAll(Names.CONNECTION).contains(Values.CLOSE)) {
                ChannelFuture future = transport.write(httpResponse);
                httpChildChannel.writeState(CONTENT_CLOSE);
                chainFutures(future, httpFuture);
            }
            else {
                // see RFC-7320 section 3.3 regarding content-length
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
                httpChildChannel.writeState(CONTENT_COMPLETE);
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
            httpChildChannel.writeState(CONTENT_COMPLETE);
            chainWriteCompletes(future, httpFuture, httpReadableBytes);
            break;
        }
        case CONTENT_CHUNKED:
        case CONTENT_CLOSE:
        case CONTENT_CLOSING:
        case CONTENT_COMPLETE:
        case UPGRADED:
            httpFuture.setSuccess();
            break;
        default:
            break;
        }
    }
}
