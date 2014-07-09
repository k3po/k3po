/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.behavior.handler.codec;

import static org.jboss.netty.buffer.ChannelBuffers.wrappedBuffer;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

import org.kaazing.robot.util.Utils;

public class WriteBytesEncoder implements MessageEncoder {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(WriteBytesEncoder.class);

    private final byte[] bytes;

    public WriteBytesEncoder(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public ChannelBuffer encode() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Encoding " + bytes.length + " exact bytes. Bytes: " + Utils.byteArrayToString(bytes));
        }
        return wrappedBuffer(bytes);
    }

    @Override
    public String encodeToString() {
        return new String(bytes);
    }
}
