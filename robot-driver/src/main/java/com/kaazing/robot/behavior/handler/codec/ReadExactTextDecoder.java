/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.behavior.handler.codec;

import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.jboss.netty.util.CharsetUtil.UTF_8;

import java.nio.charset.Charset;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

public class ReadExactTextDecoder extends MessageDecoder {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(ReadExactTextDecoder.class);

        private final ChannelBuffer expected;

    public ReadExactTextDecoder(String expected, Charset charset) {
        this.expected = copiedBuffer(expected, charset);
    }

    @Override
    protected Object decodeBuffer(ChannelBuffer buffer) throws Exception {

        if (buffer.readableBytes() < expected.readableBytes()) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("not enough bytes are ready to read. Expecting " + expected.readableBytes()
                        + " bytes. Read to read is " + buffer.readableBytes());
            }
            return null;
        }

        ChannelBuffer observed = buffer.readSlice(expected.readableBytes());
        if (!observed.equals(expected)) {
            LOGGER.error("observed bytes do not match expected bytes");
            if (LOGGER.isInfoEnabled()) {
                LOGGER.error("\texpected: " + expected.toString(UTF_8));
                LOGGER.error("\tobserved: " + observed.toString(UTF_8));
            }
            // Use a mismatch exception subclass, include the charset
            // expected?
            throw new MessageMismatchException("Exact text mismatch", expected, observed);
        }

        return buffer;
    }
}
