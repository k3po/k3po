/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.control.handler;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import com.kaazing.robot.control.PreparedMessage;

public class ControlEncoderCompatibility extends ControlDownstreamHandler {

    @Override
    public void writePreparedRequested(ChannelHandlerContext ctx, MessageEvent e)
            throws Exception {
        PreparedMessage prepared = (PreparedMessage) e.getMessage();
        switch (prepared.getCompatibilityKind()) {
        case PREPARE:
            super.writePreparedRequested(ctx, e);
            break;
        default:
            // skip (implicit)
            break;
        }
    }
}
