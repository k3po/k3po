/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.behavior.handler.event;

import static com.kaazing.robot.behavior.handler.event.AbstractEventHandler.ChannelEventKind.CHILD_CLOSED;
import static java.util.EnumSet.of;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChildChannelStateEvent;

public class ChildClosedHandler extends AbstractServerEventHandler {

    public ChildClosedHandler() {
        super(of(CHILD_CLOSED));
    }

    @Override
    public void childChannelClosed(ChannelHandlerContext ctx, ChildChannelStateEvent e) throws Exception {

        ChannelFuture handlerFuture = getHandlerFuture();
        assert handlerFuture != null;
        handlerFuture.setSuccess();
    }
}
