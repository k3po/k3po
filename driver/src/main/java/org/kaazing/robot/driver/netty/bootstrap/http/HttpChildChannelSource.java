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
import static org.jboss.netty.channel.Channels.fireChannelBound;
import static org.jboss.netty.channel.Channels.fireChannelConnected;
import static org.jboss.netty.channel.Channels.fireChannelOpen;
import static org.jboss.netty.channel.Channels.fireExceptionCaught;
import static org.jboss.netty.channel.Channels.fireMessageReceived;
import static org.jboss.netty.channel.Channels.future;
import static org.jboss.netty.channel.Channels.write;
import static org.jboss.netty.handler.codec.http.HttpHeaders.getHost;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static org.kaazing.robot.driver.channel.Channels.remoteAddress;

import java.net.URI;
import java.util.Map.Entry;
import java.util.NavigableMap;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelConfig;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.kaazing.robot.driver.netty.channel.ChannelAddress;

public class HttpChildChannelSource extends HttpChannelHandler {

    private final NavigableMap<URI, HttpServerChannel> httpBindings;

    // TODO: support multiple pipelined HTTP requests, up to a maximum
    private volatile HttpChildChannel httpChildChannel;

    public HttpChildChannelSource(NavigableMap<URI, HttpServerChannel> httpBindings) {
        this.httpBindings = httpBindings;
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        HttpChildChannel httpChildChannel = this.httpChildChannel;
        if (httpChildChannel != null) {
            ChannelException exception = new ChannelException("Channel closed unexpectedly");
            exception.fillInStackTrace();
            this.httpChildChannel = null;
            fireExceptionCaught(httpChildChannel, exception);
        }
    }

    @Override
    protected void httpMessageReceived(ChannelHandlerContext ctx, MessageEvent e, HttpRequest httpRequest) throws Exception {

        String host = getHost(httpRequest);
        String uri = httpRequest.getUri();
        URI httpLocation = URI.create(format("http://%s%s", host, uri));
        Entry<URI, HttpServerChannel> httpBinding = httpBindings.floorEntry(httpLocation);

        if (httpBinding == null) {
            HttpResponse httpResponse = new DefaultHttpResponse(httpRequest.getProtocolVersion(), NOT_FOUND);
            ChannelFuture future = future(ctx.getChannel());
            write(ctx, future, httpResponse);
            return;
        }

        HttpServerChannel parent = httpBinding.getValue();
        ChannelFactory factory = parent.getFactory();
        ChannelConfig config = parent.getConfig();
        ChannelPipelineFactory pipelineFactory = config.getPipelineFactory();
        ChannelPipeline pipeline = pipelineFactory.getPipeline();
        ChannelAddress httpLocalAddress = parent.getLocalAddress();

        Channel transport = ctx.getChannel();
        ChannelAddress remoteAddress = remoteAddress(transport);
        ChannelAddress httpRemoteAddress = new ChannelAddress(httpLocation, remoteAddress, true);

        HttpChildChannelSink sink = new HttpChildChannelSink(ctx);
        HttpChildChannel httpChildChannel = new HttpChildChannel(parent, factory, pipeline, sink);
        HttpChannelConfig httpChildConfig = httpChildChannel.getConfig();
        httpChildConfig.setMethod(httpRequest.getMethod());
        httpChildConfig.setVersion(httpRequest.getProtocolVersion());
        httpChildConfig.setReadHeaders(httpRequest.headers());
        httpChildConfig.setStatus(HttpResponseStatus.OK);

        if (httpRequest.isChunked()) {
            this.httpChildChannel = httpChildChannel;
        }

        fireChannelOpen(httpChildChannel);
        fireChannelBound(httpChildChannel, httpLocalAddress);
        fireChannelConnected(httpChildChannel, httpRemoteAddress);

        ChannelBuffer content = httpRequest.getContent();
        if (content.readable()) {
            fireMessageReceived(httpChildChannel, content);
        }

        if (!httpRequest.isChunked()) {
            // TODO: fire channel event for end-of-content?
        }
    }

    @Override
    protected void httpMessageReceived(ChannelHandlerContext ctx, MessageEvent e, HttpChunk httpMessage) throws Exception {
        ChannelBuffer content = httpMessage.getContent();
        if (content.readable()) {
            fireMessageReceived(httpChildChannel, content);
        }

        boolean last = httpMessage.isLast();
        if (last) {
            // TODO: fire channel event for end-of-content?
            this.httpChildChannel = null;
        }
    }

}
