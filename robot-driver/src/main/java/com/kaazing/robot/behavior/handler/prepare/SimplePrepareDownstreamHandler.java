/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.behavior.handler.prepare;

import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.SimpleChannelDownstreamHandler;

public abstract class SimplePrepareDownstreamHandler extends SimpleChannelDownstreamHandler {

    @Override
    public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent evt) throws Exception {

        if (evt instanceof PreparationEvent) {
            prepareComplete(ctx, (PreparationEvent) evt);
        }
        else {
            super.handleDownstream(ctx, evt);
        }
    }

    public void prepareComplete(ChannelHandlerContext ctx, PreparationEvent evt) {
        ctx.sendDownstream(evt);
    }
}
