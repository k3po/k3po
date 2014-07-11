/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.behavior.handler.codec;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

import org.kaazing.robot.lang.el.ExpressionContext;

public abstract class ReadFixedLengthBytesDecoder<T> extends MessageDecoder {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(ReadFixedLengthBytesDecoder.class);

    private final int length;
    private final ExpressionContext environment;
    private final String captureName;

    public ReadFixedLengthBytesDecoder(int length) {
        this.length = length;
        environment = null;
        captureName = null;
    }

    public ReadFixedLengthBytesDecoder(int length, ExpressionContext environment, String captureName) {
        this.length = length;
        this.environment = environment;
        this.captureName = captureName;
    }

    protected abstract T readBuffer(ChannelBuffer buffer);


    public int getLength() {
        return length;
    }

    @Override
    protected Object decodeBuffer(ChannelBuffer buffer) throws Exception {

        if (buffer.readableBytes() < length) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("not enough bytes are ready to read. Expecting " + length + " bytes. Read to read is "
                        + buffer.readableBytes());
            }
            return null;
        }
        if (captureName == null) {
            buffer.readSlice(length);
        } else {
            T value = readBuffer(buffer);
            environment.getELResolver().setValue(environment, null, captureName, value);
        }
        return buffer;
    }
}
