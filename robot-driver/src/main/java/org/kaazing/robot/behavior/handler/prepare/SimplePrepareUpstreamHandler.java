/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.behavior.handler.prepare;

import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public abstract class SimplePrepareUpstreamHandler extends SimpleChannelUpstreamHandler {

    @Override
    public final void handleUpstream(ChannelHandlerContext ctx, ChannelEvent evt) throws Exception {

        if (evt instanceof PreparationEvent) {
            prepareRequested(ctx, (PreparationEvent) evt);
        }
        else {
            handleUpstream0(ctx, evt);
        }
    }

    public void prepareRequested(ChannelHandlerContext ctx, PreparationEvent evt) {
        ctx.sendUpstream(evt);
    }

    protected void handleUpstream0(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {

        super.handleUpstream(ctx, e);
    }

}
