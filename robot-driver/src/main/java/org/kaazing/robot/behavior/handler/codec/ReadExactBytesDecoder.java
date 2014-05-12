/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.behavior.handler.codec;

import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

public class ReadExactBytesDecoder extends MessageDecoder {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(ReadExactBytesDecoder.class);

    private final ChannelBuffer expected;

    public ReadExactBytesDecoder(byte[] expected) {
        this.expected = copiedBuffer(expected);
    }

    @Override
    protected Object decodeBuffer(ChannelBuffer buffer) throws Exception {

        if (buffer.readableBytes() < expected.readableBytes()) {
            LOGGER.debug("not enough bytes are ready to read. Expecting " + expected.readableBytes()
                    + " bytes. Read to read is " + buffer.readableBytes());
            return null;
        }

        ChannelBuffer observed = buffer.readSlice(expected.readableBytes());
        if (!observed.equals(expected)) {
            throw new MessageMismatchException("Exact bytes mismatch", expected, observed);
        }

        return buffer;
    }
}
