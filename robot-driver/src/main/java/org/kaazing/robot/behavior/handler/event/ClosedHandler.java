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

// TODO: handle stream completion externally via event future
public class ClosedHandler extends AbstractEventHandler {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(ClosedHandler.class);

    public ClosedHandler() {
        super(of(ChannelEventKind.CLOSED));
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {

        LOGGER.info("channel closed");
        ChannelFuture handlerFuture = getHandlerFuture();
        assert handlerFuture != null;
        handlerFuture.setSuccess();
    }
}
