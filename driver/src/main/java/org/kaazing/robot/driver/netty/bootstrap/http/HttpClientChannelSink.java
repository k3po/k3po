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

import static java.lang.String.format;
import static org.jboss.netty.buffer.ChannelBuffers.EMPTY_BUFFER;
import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.jboss.netty.channel.Channels.fireChannelBound;
import static org.jboss.netty.channel.Channels.fireChannelClosed;
import static org.jboss.netty.channel.Channels.fireChannelConnected;
import static org.jboss.netty.channel.Channels.fireChannelDisconnected;
import static org.jboss.netty.channel.Channels.fireChannelUnbound;
import static org.jboss.netty.handler.codec.http.HttpHeaders.getContentLength;
import static org.jboss.netty.handler.codec.http.HttpHeaders.isContentLengthSet;
import static org.jboss.netty.handler.codec.http.HttpHeaders.isTransferEncodingChunked;
import static org.jboss.netty.handler.codec.http.HttpHeaders.setContentLength;
import static org.kaazing.robot.driver.channel.Channels.chainFutures;
import static org.kaazing.robot.driver.channel.Channels.chainWriteCompletes;
import static org.kaazing.robot.driver.netty.bootstrap.http.HttpClientChannel.HttpState.CONTENT_CHUNKED;
import static org.kaazing.robot.driver.netty.bootstrap.http.HttpClientChannel.HttpState.CONTENT_COMPLETE;
import static org.kaazing.robot.driver.netty.bootstrap.http.HttpClientChannel.HttpState.CONTENT_LENGTH;

import java.net.URI;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.DefaultHttpChunk;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpHeaders.Names;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.kaazing.robot.driver.netty.bootstrap.BootstrapFactory;
import org.kaazing.robot.driver.netty.channel.ChannelAddress;
import org.kaazing.robot.driver.netty.channel.http.HttpContentCompleteEvent;

public class HttpClientChannelSink extends AbstractHttpChannelSink {

    private final ChannelPipelineFactory pipelineFactory;
    private final BootstrapFactory bootstrapFactory;

    private Channel transport;
    private HttpRequest httpBufferedRequest;
    private ChannelFuture httpBufferedFuture;

    public HttpClientChannelSink(BootstrapFactory bootstrapFactory, ChannelPipelineFactory pipelineFactory) {
        this.bootstrapFactory = bootstrapFactory;
        this.pipelineFactory = pipelineFactory;
    }

    @Override
    protected void setInterestOpsRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
    }

    @Override
    protected void bindRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
        ChannelFuture httpBindFuture = evt.getFuture();
        HttpClientChannel httpConnectChannel = (HttpClientChannel) evt.getChannel();
        ChannelAddress httpLocalAddress = (ChannelAddress) evt.getValue();
        httpConnectChannel.setRemoteAddress(httpLocalAddress);
        httpConnectChannel.setBound();

        fireChannelBound(httpConnectChannel, httpConnectChannel.getLocalAddress());
        httpBindFuture.setSuccess();
    }

    @Override
    protected void connectRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
        final HttpClientChannel httpConnectChannel = (HttpClientChannel) evt.getChannel();
        final ChannelFuture httpConnectFuture = evt.getFuture();
        final ChannelAddress httpRemoteAddress = (ChannelAddress) evt.getValue();
        ChannelAddress address = httpRemoteAddress.getTransport();
        String schemeName = address.getLocation().getScheme();
        String httpSchemeName = httpRemoteAddress.getLocation().getScheme();

        ClientBootstrap bootstrap = bootstrapFactory.newClientBootstrap(schemeName);
        bootstrap.setPipelineFactory(pipelineFactory);
        bootstrap.setOption(format("%s.nextProtocol", schemeName), httpSchemeName);

        // TODO: reuse connections with keep-alive
        ChannelFuture connectFuture = bootstrap.connect(address);
        connectFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture connectFuture) throws Exception {
                if (connectFuture.isSuccess()) {
                    transport = connectFuture.getChannel();

                    ChannelPipeline pipeline = transport.getPipeline();
                    ChannelHandlerContext ctx = pipeline.getContext(HttpClientChannelSource.class);
                    HttpClientChannelSource channelSource = (HttpClientChannelSource) ctx.getHandler();

                    if (!httpConnectChannel.isBound()) {
                        ChannelAddress httpLocalAddress = httpRemoteAddress;
                        fireChannelBound(httpConnectChannel, httpLocalAddress);
                    }

                    channelSource.setHttpChannel(httpConnectChannel);
                    httpConnectChannel.setRemoteAddress(httpRemoteAddress);
                    httpConnectChannel.setConnected();

                    fireChannelConnected(httpConnectChannel, httpRemoteAddress);
                    httpConnectFuture.setSuccess();
                }
                else {
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

        switch (httpClientChannel.state()) {
        case REQUEST:
            HttpVersion version = httpClientConfig.getVersion();
            HttpMethod method = httpClientConfig.getMethod();
            HttpHeaders headers = httpClientConfig.getWriteHeaders();
            ChannelAddress httpRemoteAddress = httpClientChannel.getRemoteAddress();
            URI httpRemoteURI = httpRemoteAddress.getLocation();

            String uri = httpRemoteURI.getPath();
            String authority = httpRemoteURI.getAuthority();

            HttpRequest httpRequest = new DefaultHttpRequest(version, method, uri);
            httpRequest.headers().set(Names.HOST, authority);
            if (headers != null) {
                httpRequest.headers().add(headers);
            }

            if (isContentLengthSet(httpRequest) && httpReadableBytes == getContentLength(httpRequest)) {
                httpRequest.setContent(httpContent);
                ChannelFuture future = transport.write(httpRequest);
                httpClientChannel.state(CONTENT_COMPLETE);
                chainWriteCompletes(future, httpFuture, httpReadableBytes);
            }
            else if (isTransferEncodingChunked(httpRequest)) {
                httpRequest.setChunked(true);
                transport.write(httpRequest);
                httpClientChannel.state(CONTENT_CHUNKED);

                HttpChunk httpChunk = new DefaultHttpChunk(httpContent);
                ChannelFuture future = transport.write(httpChunk);
                chainWriteCompletes(future, httpFuture, httpReadableBytes);
            }
            else if (httpClientConfig.getMaximumBufferedContentLength() >= httpReadableBytes) {
                // automatically calculate content-length
                httpRequest.setContent(httpContent);
                httpBufferedRequest = httpRequest;
                httpBufferedFuture = httpFuture;
                httpClientChannel.state(CONTENT_LENGTH);
            }
            else {
                throw new IllegalStateException("Missing Content-Length, Transfer-Encoding: chunked, or Connection: close");
            }
            break;
        case CONTENT_LENGTH:
            ChannelBuffer httpBufferedContent = httpBufferedRequest.getContent();
            int httpBufferedBytes = httpBufferedContent.readableBytes();
            if (httpClientConfig.getMaximumBufferedContentLength() >= httpBufferedBytes + httpReadableBytes) {
                httpBufferedRequest.setContent(copiedBuffer(httpBufferedContent, httpContent));
                chainFutures(httpBufferedFuture, httpFuture);
            }
            else {
                throw new IllegalStateException("Exceeded maximum buffered content to calculate content length");
            }
            break;
        case CONTENT_CHUNKED: {
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
    protected void contentCompleteRequested(ChannelPipeline pipeline, HttpContentCompleteEvent evt) throws Exception {
        handleContentCompleteRequested(pipeline, evt);
    }

    @Override
    protected void closeRequested(ChannelPipeline pipeline, ChannelStateEvent evt) throws Exception {
        HttpClientChannel httpClientChannel = (HttpClientChannel) pipeline.getChannel();
        switch (httpClientChannel.state()) {
        case CONTENT_COMPLETE:
            break;
        default:
            handleContentCompleteRequested(pipeline, evt);
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

    private void handleContentCompleteRequested(ChannelPipeline pipeline, ChannelEvent evt) throws Exception {
        HttpClientChannel httpClientChannel = (HttpClientChannel) pipeline.getChannel();
        switch (httpClientChannel.state()) {
        case REQUEST: {
            HttpChannelConfig httpClientConfig = httpClientChannel.getConfig();
            HttpVersion version = httpClientConfig.getVersion();
            HttpMethod method = httpClientConfig.getMethod();
            HttpHeaders headers = httpClientConfig.getWriteHeaders();
            ChannelAddress httpRemoteAddress = httpClientChannel.getRemoteAddress();
            URI httpRemoteURI = httpRemoteAddress.getLocation();

            String uri = httpRemoteURI.getPath();
            String authority = httpRemoteURI.getAuthority();

            HttpRequest httpRequest = new DefaultHttpRequest(version, method, uri);
            httpRequest.headers().set(Names.HOST, authority);
            if (headers != null) {
                httpRequest.headers().add(headers);
            }

            httpRequest.setContent(EMPTY_BUFFER);
            ChannelFuture httpFuture = evt.getFuture();
            ChannelFuture future = transport.write(httpRequest);
            httpClientChannel.state(CONTENT_COMPLETE);
            chainFutures(future, httpFuture);
            break;
        }
        case CONTENT_LENGTH: {
            HttpRequest httpBufferedRequest = this.httpBufferedRequest;
            ChannelFuture httpBufferedFuture = this.httpBufferedFuture;
            this.httpBufferedRequest = null;
            this.httpBufferedFuture = null;

            ChannelFuture httpFuture = evt.getFuture();
            chainFutures(httpBufferedFuture, httpFuture);

            ChannelBuffer httpBufferedContent = httpBufferedRequest.getContent();
            int httpReadableBytes = httpBufferedContent.readableBytes();
            setContentLength(httpBufferedRequest, httpReadableBytes);
            ChannelFuture future = transport.write(httpBufferedRequest);
            httpClientChannel.state(CONTENT_COMPLETE);
            chainWriteCompletes(future, httpBufferedFuture, httpReadableBytes);
            break;
        }
        case CONTENT_CHUNKED: {
            ChannelFuture httpFuture = evt.getFuture();
            HttpChunk httpChunk = DefaultHttpChunk.LAST_CHUNK;
            ChannelFuture future = transport.write(httpChunk);
            httpClientChannel.state(CONTENT_COMPLETE);
            chainFutures(future, httpFuture);
            break;
        }
        case CONTENT_COMPLETE:
        default:
            break;
        }
    }
}
