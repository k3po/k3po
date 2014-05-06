/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.behavior.handler.command;

import static org.jboss.netty.channel.Channels.bind;

import java.net.SocketAddress;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

public class BindHandler extends AbstractCommandHandler {

    private final SocketAddress localAddress;

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(BindHandler.class);

    public BindHandler(SocketAddress localAddress) {
        this.localAddress = localAddress;
    }

    @Override
    protected void invokeCommand(ChannelHandlerContext ctx) throws Exception {

        LOGGER.info("binding channel");
        ChannelFuture handlerFuture = getHandlerFuture();
        bind(ctx, handlerFuture, localAddress);
    }

}
