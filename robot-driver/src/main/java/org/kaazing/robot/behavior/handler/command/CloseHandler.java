/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.behavior.handler.command;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

public class CloseHandler extends AbstractCommandHandler {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(CloseHandler.class);

    @Override
    protected void invokeCommand(ChannelHandlerContext ctx) throws Exception {

        LOGGER.debug("closing channel");
        ChannelFuture handlerFuture = getHandlerFuture();
        Channels.close(ctx, handlerFuture);
    }

}
