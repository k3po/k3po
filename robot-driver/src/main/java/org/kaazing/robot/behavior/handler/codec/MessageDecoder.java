/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.behavior.handler.codec;

import static org.jboss.netty.buffer.ChannelBuffers.EMPTY_BUFFER;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;

public abstract class MessageDecoder {
    private ChannelBuffer cumulation;

    public ChannelBuffer decodeLast(ChannelBuffer buffer) throws Exception {
        return decode0(buffer, true);
    }

    // Returns the ChannelBuffer that should be passed on to the next
    // handler the pipeline, or null if more data are needed by the
    // decoder.
    public ChannelBuffer decode(ChannelBuffer buffer) throws Exception {
        return decode0(buffer, false);
    }

    private ChannelBuffer decode0(ChannelBuffer buffer, boolean isLast) throws Exception {
        // If we don't have a cumulation buffer yet, create it
        if (cumulation == null) {
            cumulation = ChannelBuffers.dynamicBuffer();
        }

        // Write the input bytes in the cumulation buffer
        cumulation.writeBytes(buffer);

        Object decoded;
        if (isLast) {
            decoded = decodeBufferLast(cumulation);
        } else {
            decoded = decodeBuffer(cumulation);
        }

        if (decoded == null) {
            // Not enough data yet, keeping accumulating more
            return null;
        }

        ChannelBuffer remaining = EMPTY_BUFFER;

        if (cumulation.readable()) {
            // The decoder did not consume all of our accumulated bytes; create
            // the ChannelBuffer to pass on.
            remaining = cumulation.readBytes(cumulation.readableBytes());
        }

        // Let the VM know we're done with the cumulation buffer
        cumulation = null;

        return remaining;
    }

    protected ChannelBuffer createCumulationBuffer(ChannelHandlerContext ctx) {
        return ChannelBuffers.dynamicBuffer();
    }

    // TODO Make abstract and fill in decoders.
    protected Object decodeBufferLast(ChannelBuffer buffer) throws Exception {
        return null;
    }

    protected abstract Object decodeBuffer(ChannelBuffer buffer) throws Exception;
}
