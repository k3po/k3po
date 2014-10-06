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

import static org.jboss.netty.channel.Channels.fireChannelClosed;
import static org.jboss.netty.channel.Channels.fireChannelDisconnected;
import static org.jboss.netty.channel.Channels.fireChannelUnbound;
import static org.jboss.netty.channel.Channels.fireMessageReceived;
import static org.kaazing.robot.driver.netty.channel.http.HttpChannels.fireHttpContentComplete;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpResponse;


public class HttpClientChannelSource extends HttpChannelHandler {

    private HttpClientChannel httpClientChannel;

    public void setHttpChannel(HttpClientChannel httpClientChannel) {
        assert this.httpClientChannel == null;
        this.httpClientChannel = httpClientChannel;
    }

    @Override
    protected void httpMessageReceived(ChannelHandlerContext ctx, MessageEvent e, HttpResponse message) throws Exception {
        HttpChannelConfig httpChildConfig = httpClientChannel.getConfig();
        httpChildConfig.setStatus(message.getStatus());
        httpChildConfig.setVersion(message.getProtocolVersion());
        httpChildConfig.setReadHeaders(message.headers());

        ChannelBuffer content = message.getContent();
        if (content.readable()) {
            fireMessageReceived(httpClientChannel, content);
        }

        if (!message.isChunked()) {
            HttpClientChannel httpClientChannel = this.httpClientChannel;
            this.httpClientChannel = null;
            fireHttpContentComplete(httpClientChannel);

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

    @Override
    protected void httpMessageReceived(ChannelHandlerContext ctx, MessageEvent e, HttpChunk httpMessage) throws Exception {
        ChannelBuffer content = httpMessage.getContent();
        if (content.readable()) {
            fireMessageReceived(httpClientChannel, content);
        }

        boolean last = httpMessage.isLast();
        if (last) {
            HttpClientChannel httpClientChannel = this.httpClientChannel;
            this.httpClientChannel = null;
            fireHttpContentComplete(httpClientChannel);

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

}
