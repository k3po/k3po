/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.behavior.handler.command;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

public class DisconnectHandler extends AbstractCommandHandler {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(DisconnectHandler.class);

    @Override
    protected void invokeCommand(ChannelHandlerContext ctx) throws Exception {

        LOGGER.info("disconnecting channel");
        ChannelFuture handlerFuture = getHandlerFuture();
        Channels.disconnect(ctx, handlerFuture);
    }

}
