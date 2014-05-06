/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.control.handler;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelDownstreamHandler;

import com.kaazing.robot.control.ControlMessage;

public class ControlDownstreamHandler extends SimpleChannelDownstreamHandler {

    @Override
    public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        ControlMessage message = (ControlMessage) e.getMessage();

        switch (message.getKind()) {
        case PREPARED:
            writePreparedRequested(ctx, e);
            break;
        case STARTED:
            writeStartedRequested(ctx, e);
            break;
        case FINISHED:
            writeFinishedRequested(ctx, e);
            break;
            case ERROR:
                writeErrorRequested(ctx, e);
                break;
        default:
            throw new IllegalArgumentException(String.format("Unexpected control message: %s", message.getKind()));
        }

    }

    public void writePreparedRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        super.writeRequested(ctx, e);
    }

    public void writeStartedRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        super.writeRequested(ctx, e);
    }

    public void writeFinishedRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        super.writeRequested(ctx, e);
    }

    public void writeErrorRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        super.writeRequested(ctx, e);
    }

}
