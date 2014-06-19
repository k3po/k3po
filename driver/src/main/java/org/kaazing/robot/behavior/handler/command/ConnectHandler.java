/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.behavior.handler.command;

import java.net.SocketAddress;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;

public class ConnectHandler extends AbstractCommandHandler {

    private final SocketAddress remoteAddress;

    public ConnectHandler(SocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    @Override
    protected void invokeCommand(ChannelHandlerContext ctx) throws Exception {

        ChannelFuture handlerFuture = getHandlerFuture();
        Channels.connect(ctx, handlerFuture, remoteAddress);
    }

}
