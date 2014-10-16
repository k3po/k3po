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
import static org.jboss.netty.channel.Channels.fireChannelClosed;
import static org.jboss.netty.channel.Channels.fireChannelConnected;
import static org.jboss.netty.channel.Channels.fireChannelDisconnected;
import static org.jboss.netty.channel.Channels.fireChannelOpen;
import static org.jboss.netty.channel.Channels.fireChannelUnbound;
import static org.jboss.netty.channel.Channels.fireExceptionCaught;
import static org.jboss.netty.channel.Channels.fireMessageReceived;
import static org.jboss.netty.channel.Channels.future;
import static org.jboss.netty.channel.Channels.write;
import static org.jboss.netty.handler.codec.http.HttpHeaders.getHost;
import static org.jboss.netty.handler.codec.http.HttpHeaders.isContentLengthSet;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.SWITCHING_PROTOCOLS;
import static org.kaazing.robot.driver.channel.Channels.remoteAddress;
import static org.kaazing.robot.driver.netty.channel.Channels.fireInputShutdown;

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
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.jboss.netty.handler.codec.http.QueryStringEncoder;
import org.kaazing.robot.driver.netty.bootstrap.http.HttpChildChannel.HttpReadState;
import org.kaazing.robot.driver.netty.channel.ChannelAddress;

public class HttpChildChannelSource extends HttpChannelHandler {

    private final NavigableMap<URI, HttpServerChannel> httpBindings;

    private volatile HttpChildChannel httpChildChannel;

    public HttpChildChannelSource(NavigableMap<URI, HttpServerChannel> httpBindings) {
        this.httpBindings = httpBindings;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        if (httpChildChannel != null) {
            HttpChildChannel httpChildChannel = this.httpChildChannel;
            this.httpChildChannel = null;

            if (httpChildChannel.setReadClosed() || httpChildChannel.setWriteClosed()) {
                fireExceptionCaught(httpChildChannel, e.getCause());
                fireChannelClosed(httpChildChannel);
            }
        }

        Channel channel = ctx.getChannel();
        channel.close();
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        HttpChildChannel httpChildChannel = this.httpChildChannel;
        if (httpChildChannel != null) {

            this.httpChildChannel = null;

            switch (httpChildChannel.readState()) {
            case UPGRADED:
                if (httpChildChannel.setReadClosed()) {
                    fireChannelDisconnected(httpChildChannel);
                    fireChannelUnbound(httpChildChannel);
                    fireChannelClosed(httpChildChannel);
                }
                break;
            case CONTENT_COMPLETE:
                break;
            default:
                ChannelException exception = new ChannelException("Channel closed unexpectedly");
                exception.fillInStackTrace();
                fireExceptionCaught(httpChildChannel, exception);
                break;
            }

            switch (httpChildChannel.writeState()) {
            case UPGRADED:
            case CONTENT_CLOSE:
                if (httpChildChannel.setWriteClosed()) {
                    fireChannelDisconnected(httpChildChannel);
                    fireChannelUnbound(httpChildChannel);
                    fireChannelClosed(httpChildChannel);
                }
                break;
            case CONTENT_COMPLETE:
                break;
            default:
                ChannelException exception = new ChannelException("Channel closed unexpectedly");
                exception.fillInStackTrace();
                fireExceptionCaught(httpChildChannel, exception);
                break;
            }
        }
    }

    @Override
    protected void httpMessageReceived(ChannelHandlerContext ctx, MessageEvent e, HttpRequest httpRequest) throws Exception {

        String host = getHost(httpRequest);
        String uri = httpRequest.getUri();
        URI httpLocation = URI.create(format("http://%s%s", host, uri));

        Entry<URI, HttpServerChannel> httpBinding = (host != null) ? httpBindings.floorEntry(httpLocation) : null;

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

        HttpChildChannelSink sink = new HttpChildChannelSink(transport);
        HttpChildChannel httpChildChannel = new HttpChildChannel(parent, factory, pipeline, sink);
        HttpChannelConfig httpChildConfig = httpChildChannel.getConfig();
        httpChildConfig.setMethod(httpRequest.getMethod());
        httpChildConfig.setVersion(httpRequest.getProtocolVersion());
        httpChildConfig.getReadHeaders().set(httpRequest.headers());
        httpChildConfig.setReadQuery(new QueryStringDecoder(httpRequest.getUri()));
        httpChildConfig.setWriteQuery(new QueryStringEncoder(httpRequest.getUri()));
        httpChildConfig.setStatus(HttpResponseStatus.OK);

        this.httpChildChannel = httpChildChannel;

        // update read state before firing channel events
        if (httpRequest.isChunked()) {
            httpChildChannel.readState(HttpReadState.CONTENT_CHUNKED);
        }
        else if (isContentLengthSet(httpRequest)) {
            httpChildChannel.readState(HttpReadState.CONTENT_COMPLETE);
        }
        else if (httpRequest.getMethod() == HttpMethod.GET ||
                httpRequest.getMethod() == HttpMethod.HEAD) {
            // no content by default for these HTTP methods
            httpChildChannel.readState(HttpReadState.CONTENT_COMPLETE);
        }

        fireChannelOpen(httpChildChannel);

        httpChildChannel.setLocalAddress(httpLocalAddress);
        httpChildChannel.setBound();
        fireChannelBound(httpChildChannel, httpLocalAddress);

        httpChildChannel.setRemoteAddress(httpRemoteAddress);
        httpChildChannel.setConnected();
        fireChannelConnected(httpChildChannel, httpRemoteAddress);

        ChannelBuffer content = httpRequest.getContent();
        if (content.readable()) {
            fireMessageReceived(httpChildChannel, content);
        }

        // note: status may be set in reaction to one of the above events, such as CONNECTED
        //       so defer status code check until this point
        if (httpChildConfig.getStatus().getCode() == SWITCHING_PROTOCOLS.getCode()) {
            httpChildChannel.readState(HttpReadState.UPGRADED);
        }

        switch (httpChildChannel.readState()) {
        case CONTENT_COMPLETE:
            fireInputShutdown(httpChildChannel);
            this.httpChildChannel = null;
            if (httpChildChannel.setReadClosed()) {
                fireChannelDisconnected(httpChildChannel);
                fireChannelUnbound(httpChildChannel);
                fireChannelClosed(httpChildChannel);
            }
            break;
        default:
            break;
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
            HttpChildChannel httpChildChannel = this.httpChildChannel;
            httpChildChannel.readState(HttpReadState.CONTENT_COMPLETE);
            this.httpChildChannel = null;
            fireInputShutdown(httpChildChannel);
            if (httpChildChannel.setReadClosed()) {
                fireChannelDisconnected(httpChildChannel);
                fireChannelUnbound(httpChildChannel);
                fireChannelClosed(httpChildChannel);
            }
        }
    }

    @Override
    protected void httpMessageReceived(ChannelHandlerContext ctx, MessageEvent e, ChannelBuffer message) throws Exception {
        if (message.readable()) {
            // after 101 switching protocols
            fireMessageReceived(httpChildChannel, message);
        }
    }

}
