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
import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.jboss.netty.channel.Channels.fireChannelBound;
import static org.jboss.netty.channel.Channels.fireChannelClosed;
import static org.jboss.netty.channel.Channels.fireChannelConnected;
import static org.jboss.netty.channel.Channels.fireChannelDisconnected;
import static org.jboss.netty.channel.Channels.fireChannelUnbound;
import static org.jboss.netty.channel.Channels.future;
import static org.jboss.netty.handler.codec.http.HttpHeaders.getContentLength;
import static org.jboss.netty.handler.codec.http.HttpHeaders.isContentLengthSet;
import static org.jboss.netty.handler.codec.http.HttpHeaders.isTransferEncodingChunked;
import static org.jboss.netty.handler.codec.http.HttpHeaders.setContentLength;
import static org.kaazing.k3po.driver.internal.channel.Channels.chainFutures;
import static org.kaazing.k3po.driver.internal.channel.Channels.chainWriteCompletes;
import static org.kaazing.k3po.driver.internal.netty.bootstrap.http.HttpClientChannel.HttpWriteState.CONTENT_BUFFERED;
import static org.kaazing.k3po.driver.internal.netty.bootstrap.http.HttpClientChannel.HttpWriteState.CONTENT_CHUNKED;
import static org.kaazing.k3po.driver.internal.netty.bootstrap.http.HttpClientChannel.HttpWriteState.CONTENT_COMPLETE;
import static org.kaazing.k3po.driver.internal.netty.bootstrap.http.HttpClientChannel.HttpWriteState.CONTENT_STREAMED;
import static org.kaazing.k3po.driver.internal.netty.bootstrap.http.HttpClientChannel.HttpWriteState.UPGRADEABLE;
import static org.kaazing.k3po.driver.internal.netty.bootstrap.http.HttpRequestForm.ABSOLUTE_FORM;
import static org.kaazing.k3po.driver.internal.netty.bootstrap.http.HttpRequestForm.ORIGIN_FORM;

import java.net.URI;
import java.net.URISyntaxException;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.DefaultHttpChunk;
import org.jboss.netty.handler.codec.http.DefaultHttpChunkTrailer;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpHeaders.Names;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.http.QueryStringEncoder;
import org.kaazing.k3po.driver.internal.netty.bootstrap.BootstrapFactory;
import org.kaazing.k3po.driver.internal.netty.bootstrap.channel.AbstractChannelSink;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;
import org.kaazing.k3po.driver.internal.netty.channel.FlushEvent;
import org.kaazing.k3po.driver.internal.netty.channel.ShutdownOutputEvent;
import org.kaazing.k3po.driver.internal.netty.channel.WriteAbortEvent;

public class HttpClientChannelSink extends AbstractChannelSink {

    private final ChannelPipelineFactory pipelineFactory;
    private final BootstrapFactory bootstrapFactory;

    private Channel transport;
    private HttpRequest httpBufferedRequest;

    public HttpClientChannelSink(BootstrapFactory bootstrapFactory, ChannelPipelineFactory pipelineFactory) {
        this.bootstrapFactory = bootstrapFactory;
        this.pipelineFactory = pipelineFactory;
    }

    @Override
    public ChannelFuture execute(ChannelPipeline httpPipeline, Runnable task) {

        if (transport != null) {
            ChannelPipeline pipeline = transport.getPipeline();
            ChannelFuture future = pipeline.execute(task);
            Channel httpChannel = pipeline.getChannel();
            ChannelFuture httpFuture = future(httpChannel);
            chainFutures(future, httpFuture);
            return httpFuture;
        }

        return super.execute(httpPipeline, task);
    }

    @Override
    protected void setInterestOpsRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
        ChannelFuture httpFuture = evt.getFuture();
        HttpClientChannel httpClientChannel = (HttpClientChannel) evt.getChannel();
        httpClientChannel.setInterestOpsNow((int) evt.getValue());
        httpFuture.setSuccess();
    }

    @Override
    protected void bindRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
        ChannelFuture httpBindFuture = evt.getFuture();
        HttpClientChannel httpConnectChannel = (HttpClientChannel) evt.getChannel();
        ChannelAddress httpLocalAddress = (ChannelAddress) evt.getValue();
        httpConnectChannel.setLocalAddress(httpLocalAddress);
        httpConnectChannel.setBound();

        fireChannelBound(httpConnectChannel, httpLocalAddress);
        httpBindFuture.setSuccess();
    }

    @Override
    protected void connectRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
        final HttpClientChannel httpConnectChannel = (HttpClientChannel) evt.getChannel();
        final HttpChannelConfig httpConnectConfig = httpConnectChannel.getConfig();
        final ChannelFuture httpConnectFuture = evt.getFuture();
        final ChannelAddress httpRemoteAddress = (ChannelAddress) evt.getValue();
        ChannelAddress address = httpRemoteAddress.getTransport();
        String schemeName = address.getLocation().getScheme();
        String httpSchemeName = httpRemoteAddress.getLocation().getScheme();

        ClientBootstrap bootstrap = bootstrapFactory.newClientBootstrap(schemeName);
        bootstrap.setPipelineFactory(pipelineFactory);
        bootstrap.setOptions(httpConnectConfig.getTransportOptions());
        bootstrap.setOption(format("%s.nextProtocol", schemeName), httpSchemeName);

        // TODO: reuse connections with keep-alive
        ChannelFuture connectFuture = bootstrap.connect(address);
        connectFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture connectFuture) throws Exception {
                if (connectFuture.isSuccess()) {
                    transport = connectFuture.getChannel();
                    transport.getConfig().setBufferFactory(httpConnectConfig.getBufferFactory());

                    ChannelPipeline pipeline = transport.getPipeline();
                    ChannelHandlerContext ctx = pipeline.getContext(HttpClientChannelSource.class);
                    HttpClientChannelSource channelSource = (HttpClientChannelSource) ctx.getHandler();

                    if (!httpConnectChannel.isBound()) {
                        ChannelAddress httpLocalAddress = httpRemoteAddress;
                        httpConnectChannel.setLocalAddress(httpLocalAddress);
                        httpConnectChannel.setBound();
                        fireChannelBound(httpConnectChannel, httpLocalAddress);
                    }

                    channelSource.setHttpChannel(httpConnectChannel);
                    httpConnectChannel.setRemoteAddress(httpRemoteAddress);
                    httpConnectChannel.setConnected();

                    httpConnectFuture.setSuccess();
                    fireChannelConnected(httpConnectChannel, httpRemoteAddress);
                } else {
                    httpConnectFuture.setFailure(connectFuture.getCause());
                }
            }
        });
    }

    @Override
    protected void writeRequested(ChannelPipeline pipeline, MessageEvent e) throws Exception {

        HttpClientChannel httpClientChannel = (HttpClientChannel) pipeline.getChannel();
        HttpChannelConfig httpClientConfig = httpClientChannel.getConfig();
        ChannelFuture httpFuture = e.getFuture();
        ChannelBuffer httpContent = (ChannelBuffer) e.getMessage();
        int httpReadableBytes = httpContent.readableBytes();

        switch (httpClientChannel.writeState()) {
        case REQUEST:
            HttpVersion version = httpClientConfig.getVersion();
            HttpMethod method = httpClientConfig.getMethod();
            HttpHeaders headers = httpClientConfig.getWriteHeaders();
            String targetURI = getTargetURI(httpClientChannel);
            HttpRequest httpRequest = new DefaultHttpRequest(version, method, targetURI);
            HttpHeaders httpRequestHeaders = httpRequest.headers();

            if (httpClientConfig.hasWriteHeaders()) {
                httpRequestHeaders.add(headers);
            }

            if (isContentLengthSet(httpRequest)) {
                httpRequest.setContent(httpContent);
                ChannelFuture future = transport.write(httpRequest);
                if (httpReadableBytes == getContentLength(httpRequest)) {
                    httpClientChannel.writeState(CONTENT_COMPLETE);
                } else {
                    httpClientChannel.writeState(CONTENT_STREAMED);
                }
                chainWriteCompletes(future, httpFuture, httpReadableBytes);
            } else if (isTransferEncodingChunked(httpRequest)) {
                httpRequest.setChunked(true);
                transport.write(httpRequest);
                httpClientChannel.writeState(CONTENT_CHUNKED);

                HttpChunk httpChunk = new DefaultHttpChunk(httpContent);
                ChannelFuture future = transport.write(httpChunk);
                chainWriteCompletes(future, httpFuture, httpReadableBytes);
            } else if (httpRequest.headers().contains(Names.UPGRADE)) {
                httpRequest.setContent(httpContent);
                ChannelFuture future = transport.write(httpRequest);
                httpClientChannel.writeState(UPGRADEABLE);
                chainWriteCompletes(future, httpFuture, httpReadableBytes);
            } else if (httpClientConfig.getMaximumBufferedContentLength() >= httpReadableBytes) {
                // automatically calculate content-length
                httpRequest.setContent(httpContent);
                httpBufferedRequest = httpRequest;
                httpClientChannel.writeState(CONTENT_BUFFERED);
                httpFuture.setSuccess();
            } else {
                throw new IllegalStateException("Missing Upgrade, Content-Length, Transfer-Encoding: chunked");
            }
            break;
        case CONTENT_BUFFERED:
            ChannelBuffer httpBufferedContent = httpBufferedRequest.getContent();
            int httpBufferedBytes = httpBufferedContent.readableBytes();
            if (httpClientConfig.getMaximumBufferedContentLength() >= httpBufferedBytes + httpReadableBytes) {
                httpBufferedRequest.setContent(copiedBuffer(httpBufferedContent, httpContent));
                httpFuture.setSuccess();
            } else {
                throw new IllegalStateException("Exceeded maximum buffered content to calculate content length");
            }
            break;
        case CONTENT_CHUNKED: {
            HttpChunk httpChunk = new DefaultHttpChunk(httpContent);
            ChannelFuture future = transport.write(httpChunk);
            chainWriteCompletes(future, httpFuture, httpReadableBytes);
            break;
        }
        case CONTENT_STREAMED: {
            // TODO: verify content size does not exceed Content-Length value
            HttpChunk httpChunk = new DefaultHttpChunk(httpContent);
            ChannelFuture future = transport.write(httpChunk);
            chainWriteCompletes(future, httpFuture, httpReadableBytes);
            break;
        }
        case UPGRADEABLE: {
            ChannelFuture future = transport.write(httpContent);
            chainWriteCompletes(future, httpFuture, httpReadableBytes);
            break;
        }
        case CONTENT_COMPLETE:
            throw new IllegalStateException("attempted write after request content complete");
        }
    }

    @Override
    protected void shutdownOutputRequested(ChannelPipeline pipeline, ShutdownOutputEvent evt) throws Exception {
        HttpClientChannel httpClientChannel = (HttpClientChannel) pipeline.getChannel();
        ChannelFuture httpFuture = evt.getFuture();

        shutdownOutputRequested(httpClientChannel, httpFuture);
    }

    @Override
    protected void flushRequested(ChannelPipeline pipeline, FlushEvent evt) throws Exception {
        HttpClientChannel httpClientChannel = (HttpClientChannel) pipeline.getChannel();
        ChannelFuture httpFuture = evt.getFuture();
        flushRequested(httpClientChannel, httpFuture);
    }

    @Override
    protected void abortOutputRequested(ChannelPipeline pipeline, final WriteAbortEvent evt) throws Exception {
        HttpClientChannel channel = (HttpClientChannel) pipeline.getChannel();
        ChannelFuture flushFuture = Channels.future(channel);
        flushRequested(channel, flushFuture);
        flushFuture.addListener(new ChannelFutureListener() {

            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                ChannelFuture disconnect = transport.disconnect();
                chainFutures(disconnect, evt.getFuture());
            }
        });
    };

    @Override
    protected void closeRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
        HttpClientChannel httpClientChannel = (HttpClientChannel) pipeline.getChannel();
        ChannelFuture httpFuture = evt.getFuture();
        httpFuture.setSuccess();

        switch (httpClientChannel.writeState()) {
        case UPGRADEABLE:
            transport.close();
            break;
        default:
            ChannelFuture inputShutdown = future(httpClientChannel);
            shutdownOutputRequested(httpClientChannel, inputShutdown);
            break;
        }

        // TODO: extends states to model request and response status separately
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

    private void shutdownOutputRequested(HttpClientChannel httpClientChannel, ChannelFuture httpFuture) throws Exception {
        switch (httpClientChannel.writeState()) {
        case CONTENT_CHUNKED:
            DefaultHttpChunkTrailer trailingChunk = new DefaultHttpChunkTrailer();
            HttpHeaders writeTrailers = httpClientChannel.getConfig().getWriteTrailers();
            trailingChunk.trailingHeaders().add(writeTrailers);
            ChannelFuture future = transport.write(trailingChunk);
            httpClientChannel.writeState(CONTENT_COMPLETE);
            chainFutures(future, httpFuture);
            break;
        default:
            flushRequested(httpClientChannel, httpFuture);
            break;
        }
    }

    private void flushRequested(HttpClientChannel httpClientChannel, ChannelFuture httpFuture) throws Exception {
        switch (httpClientChannel.writeState()) {
        case REQUEST: {
            HttpChannelConfig httpClientConfig = httpClientChannel.getConfig();
            HttpVersion version = httpClientConfig.getVersion();
            HttpMethod method = httpClientConfig.getMethod();
            HttpHeaders headers = httpClientConfig.getWriteHeaders();

            String targetURI = getTargetURI(httpClientChannel);
            HttpRequest httpRequest = new DefaultHttpRequest(version, method, targetURI);
            HttpHeaders httpRequestHeaders = httpRequest.headers();

            if (httpClientConfig.hasWriteHeaders()) {
                httpRequestHeaders.add(headers);
            }

            if (isContentLengthSet(httpRequest)) {
                ChannelFuture future = transport.write(httpRequest);
                if (getContentLength(httpRequest) == 0) {
                    httpClientChannel.writeState(CONTENT_COMPLETE);
                } else {
                    httpClientChannel.writeState(CONTENT_STREAMED);
                }
                chainFutures(future, httpFuture);
            } else if (isTransferEncodingChunked(httpRequest)) {
                httpRequest.setChunked(true);
                ChannelFuture future = transport.write(httpRequest);
                httpClientChannel.writeState(CONTENT_CHUNKED);
                chainFutures(future, httpFuture);
            } else if (httpRequestHeaders.contains(Names.UPGRADE)) {
                ChannelFuture future = transport.write(httpRequest);
                httpClientChannel.writeState(UPGRADEABLE);
                chainFutures(future, httpFuture);
            } else if ("GET".equalsIgnoreCase(method.getName()) || "HEAD".equalsIgnoreCase(method.getName())) {

                // no content and no content-length
                ChannelFuture future = transport.write(httpRequest);
                httpClientChannel.writeState(CONTENT_COMPLETE);
                chainFutures(future, httpFuture);
            } else if (httpClientConfig.getMaximumBufferedContentLength() > 0) {
                // no content and content-length: 0
                setContentLength(httpRequest, 0);
                ChannelFuture future = transport.write(httpRequest);
                httpClientChannel.writeState(CONTENT_COMPLETE);
                chainFutures(future, httpFuture);
            } else {
                throw new IllegalStateException("Missing Upgrade, Content-Length, or Transfer-Encoding: chunked");
            }
            break;
        }
        case CONTENT_BUFFERED: {
            HttpRequest httpBufferedRequest = this.httpBufferedRequest;
            this.httpBufferedRequest = null;
            if (httpBufferedRequest != null) {
                ChannelBuffer httpBufferedContent = httpBufferedRequest.getContent();
                int httpReadableBytes = httpBufferedContent.readableBytes();
                setContentLength(httpBufferedRequest, httpReadableBytes);
                ChannelFuture future = transport.write(httpBufferedRequest);
                httpClientChannel.writeState(CONTENT_COMPLETE);
                chainWriteCompletes(future, httpFuture, httpReadableBytes);
            } else {
                throw new IllegalStateException("No buffered content");
            }
            break;
        }
        case UPGRADEABLE:
        case CONTENT_COMPLETE:
            httpFuture.setSuccess();
            break;
        default:
            break;
        }
    }

    private static String getTargetURI(HttpClientChannel httpClientChannel) throws URISyntaxException {

        HttpChannelConfig httpClientConfig = httpClientChannel.getConfig();
        HttpRequestForm requestForm = httpClientConfig.getRequestForm();
        if (requestForm == null) {
            // See RFC-7230, section 5.3.1 origin-form and section 5.3.2 absolute-form
            // default to origin-form when Host header present, otherwise absolute-form
            if (httpClientConfig.hasWriteHeaders() && httpClientConfig.getWriteHeaders().contains(Names.HOST)) {
                requestForm = ORIGIN_FORM;
            } else {
                requestForm = ABSOLUTE_FORM;
            }
        }

        QueryStringEncoder query = httpClientConfig.getWriteQuery();
        ChannelAddress httpRemoteAddress = httpClientChannel.getRemoteAddress();
        URI httpRemoteURI = query != null ? query.toUri() : httpRemoteAddress.getLocation();

        switch (requestForm) {
        case ORIGIN_FORM:
            String requestPath = httpRemoteURI.getPath();
            String requestQuery = httpRemoteURI.getQuery();
            return (requestQuery != null) ? format("%s?%s", requestPath, requestQuery) : requestPath;
        case ABSOLUTE_FORM:
        default:
            return httpRemoteURI.toString();
        }
    }
}
