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

public class BoundHandler extends AbstractEventHandler {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(BoundHandler.class);

    public BoundHandler() {
        super(of(ChannelEventKind.BOUND));
    }

    @Override
    public void channelBound(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {

        LOGGER.info("channel bound");
        ChannelFuture handlerFuture = getHandlerFuture();
        assert handlerFuture != null;
        handlerFuture.setSuccess();
    }
}
