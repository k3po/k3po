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
package org.kaazing.k3po.driver.internal.netty.bootstrap.bbosh;

import static org.jboss.netty.channel.Channels.fireChannelClosed;
import static org.jboss.netty.channel.Channels.fireChannelDisconnected;
import static org.jboss.netty.channel.Channels.fireChannelUnbound;
import static org.jboss.netty.channel.Channels.fireMessageReceived;
import static org.kaazing.k3po.driver.internal.netty.bootstrap.bbosh.BBoshHttpHeaders.getIntHeader;
import static org.kaazing.k3po.driver.internal.netty.channel.Channels.shutdownOutput;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.kaazing.k3po.driver.internal.netty.bootstrap.bbosh.BBoshHttpHeaders.Names;
import org.kaazing.k3po.driver.internal.netty.bootstrap.http.HttpChannelConfig;
import org.kaazing.k3po.driver.internal.netty.bootstrap.http.HttpChildChannel;
import org.kaazing.k3po.driver.internal.netty.channel.ShutdownInputEvent;
import org.kaazing.k3po.driver.internal.netty.channel.SimpleChannelHandler;

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
