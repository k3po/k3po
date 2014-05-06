/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.behavior.handler;

import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;

public class FailureHandler extends ExecutionHandler {

    @Override
    protected void handleUpstream1(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {

        ChannelFuture handlerFuture = getHandlerFuture();
        assert handlerFuture != null;
        handlerFuture.setFailure(new ChannelException("Unexpected accepted stream").fillInStackTrace());

        super.handleUpstream1(ctx, e);
    }

}
