/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.behavior.handler.event;

import static java.util.EnumSet.of;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

public class OpenedHandler extends AbstractEventHandler {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(OpenedHandler.class);

    public OpenedHandler() {
        super(of(ChannelEventKind.OPEN));
    }

    @Override
    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {

        LOGGER.info("channel opened");
        ChannelFuture handlerFuture = getHandlerFuture();
        assert handlerFuture != null;
        handlerFuture.setSuccess();
    }
}
