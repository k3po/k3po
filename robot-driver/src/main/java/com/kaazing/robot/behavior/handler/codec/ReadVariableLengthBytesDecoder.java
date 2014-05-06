/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.behavior.handler.codec;

import javax.el.ValueExpression;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

import com.kaazing.robot.lang.el.ExpressionContext;

public class ReadVariableLengthBytesDecoder extends MessageDecoder {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(ReadVariableLengthBytesDecoder.class);

    private final ValueExpression length;
    private final ExpressionContext environment;
    private final String captureName;

    public ReadVariableLengthBytesDecoder(ValueExpression length, ExpressionContext environment) {
        this.length = length;
        this.environment = environment;
        this.captureName = null;
    }

    public ReadVariableLengthBytesDecoder(ValueExpression length, ExpressionContext environment, String captureName) {
        this.length = length;
        this.environment = environment;
        this.captureName = captureName;
    }

    @Override
    protected Object decodeBuffer(ChannelBuffer buffer) throws Exception {

        int resolvedLength = (Integer) length.getValue(environment);

        if (buffer.readableBytes() < resolvedLength) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("not enough bytes are ready to read. Expecting " + resolvedLength + " bytes. Read to read is "
                        + buffer.readableBytes());
            }
            return null;
        }
        if (captureName == null) {
            buffer.readSlice(resolvedLength);
        } else {
            byte[] bytes = new byte[resolvedLength];
            buffer.readBytes(bytes, 0, resolvedLength);
            environment.getELResolver().setValue(environment, null, captureName, bytes);
        }
        return buffer;
    }
}
