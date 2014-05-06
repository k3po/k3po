/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.behavior.handler.command;

import static org.jboss.netty.buffer.ChannelBuffers.wrappedBuffer;

import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

import com.kaazing.robot.behavior.handler.codec.MessageEncoder;

public class WriteHandler extends AbstractCommandHandler {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(WriteHandler.class);

    private final List<MessageEncoder> encoders;

    public WriteHandler(List<MessageEncoder> encoders) {
        if (encoders == null) {
            throw new NullPointerException("encoders");
        } else if (encoders.size() == 0) {
            throw new IllegalArgumentException("must have at least one encoder");
        }
        this.encoders = encoders;
    }

    @Override
    protected void invokeCommand(ChannelHandlerContext ctx) throws Exception {
        ChannelBuffer[] buffers = new ChannelBuffer[encoders.size()];
        int idx = 0;
        for (MessageEncoder encoder : encoders) {
            buffers[idx] = encoder.encode();
            idx++;
        }
        LOGGER.info("Invoking write command");
        Channels.write(ctx, getHandlerFuture(), wrappedBuffer(buffers));
    }

}
