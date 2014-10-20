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

package org.kaazing.robot.driver.netty.bootstrap.bbosh;

import static org.jboss.netty.channel.Channels.fireChannelClosed;
import static org.jboss.netty.channel.Channels.fireChannelDisconnected;
import static org.jboss.netty.channel.Channels.fireChannelUnbound;
import static org.jboss.netty.channel.Channels.fireMessageReceived;
import static org.kaazing.robot.driver.netty.bootstrap.bbosh.BBoshHttpHeaders.getIntHeader;
import static org.kaazing.robot.driver.netty.channel.Channels.shutdownOutput;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.kaazing.robot.driver.netty.bootstrap.bbosh.BBoshHttpHeaders.Names;
import org.kaazing.robot.driver.netty.bootstrap.http.HttpChannelConfig;
import org.kaazing.robot.driver.netty.bootstrap.http.HttpChildChannel;
import org.kaazing.robot.driver.netty.channel.ShutdownInputEvent;
import org.kaazing.robot.driver.netty.channel.SimpleChannelHandler;

public class BBoshPollingChildChannelSource extends SimpleChannelHandler {

    private static final ChannelFutureListener READ_RESUMER = new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            Channel channel = future.getChannel();
            channel.setReadable(true);
        }
    };

    private final BBoshChildChannel bboshChannel;
    private final BBoshPollingChildChannelSink bboshChannelSink;

    public BBoshPollingChildChannelSource(BBoshChildChannel bboshChannel) {
        this.bboshChannel = bboshChannel;
        this.bboshChannelSink = (BBoshPollingChildChannelSink) bboshChannel.getPipeline().getSink();
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        HttpChildChannel httpChannel = (HttpChildChannel) ctx.getChannel();
        HttpChannelConfig httpConfig = httpChannel.getConfig();
        HttpHeaders httpHeaders = httpConfig.getReadHeaders();

        int sequenceNo = getIntHeader(httpHeaders, Names.X_SEQUENCE_NO);
        ChannelFuture attachFuture = bboshChannelSink.attach(sequenceNo, httpChannel);
        if (!attachFuture.isDone()) {
            httpChannel.setReadable(false);
            attachFuture.addListener(READ_RESUMER);
        }

    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        Object message = e.getMessage();
        fireMessageReceived(bboshChannel, message);
    }

    @Override
    public void inputShutdown(ChannelHandlerContext ctx, ShutdownInputEvent e) {
        HttpChildChannel httpChannel = (HttpChildChannel) ctx.getChannel();
        HttpChannelConfig httpConfig = httpChannel.getConfig();
        HttpMethod httpMethod = httpConfig.getMethod();
        if (HttpMethod.DELETE.getName().equalsIgnoreCase(httpMethod.getName())) {
            shutdownOutput(httpChannel);
        }
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        HttpChildChannel httpChannel = (HttpChildChannel) ctx.getChannel();
        HttpChannelConfig httpConfig = httpChannel.getConfig();
        HttpMethod httpMethod = httpConfig.getMethod();

        bboshChannelSink.detach(httpChannel);
        if (HttpMethod.DELETE.getName().equalsIgnoreCase(httpMethod.getName())) {
            if (bboshChannel.setClosed()) {
                fireChannelDisconnected(bboshChannel);
                fireChannelUnbound(bboshChannel);
                fireChannelClosed(bboshChannel);
            }
        }
        else {
            // TODO: start timer for reconnect (close BBoshChannel on timeout, triggers cleanup of child channel handler)
        }
    }

}
