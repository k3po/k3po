/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.control.handler;

import static org.jboss.netty.channel.Channels.fireMessageReceived;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import com.kaazing.robot.control.PrepareMessage;
import com.kaazing.robot.control.StartMessage;

public class ControlDecoderCompatibility extends ControlUpstreamHandler {

    @Override
    public void prepareReceived(final ChannelHandlerContext ctx, MessageEvent evt) throws Exception {
        PrepareMessage prepare = (PrepareMessage) evt.getMessage();
        switch (prepare.getCompatibilityKind()) {
        case PREPARE:
            super.prepareReceived(ctx, evt);
            break;
        case START:
            // propagate PREPARE transformed from START
            super.prepareReceived(ctx, evt);

            // inject implicit START message
            StartMessage start = new StartMessage();
            start.setScriptName(prepare.getScriptName());
            fireMessageReceived(ctx, start);

            break;
        }
    }

}
