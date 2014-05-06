/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.behavior.handler.codec;

import java.util.Arrays;

import javax.el.ValueExpression;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

import org.kaazing.robot.lang.el.ExpressionContext;
import org.kaazing.robot.util.Utils;

public class ReadExpressionDecoder extends MessageDecoder {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(ReadExpressionDecoder.class);

    private final ValueExpression expression;
    private final ExpressionContext environment;

    public ReadExpressionDecoder(ValueExpression expression, ExpressionContext environment) {
        this.expression = expression;
        this.environment = environment;
    }

    @Override
    protected Object decodeBuffer(ChannelBuffer buffer) throws Exception {

        byte[] expected = (byte[]) expression.getValue(environment);

        if (buffer.readableBytes() < expected.length) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Not enough bytes ready to read. expecting " + expected.length + " ready to read "
                        + buffer.readableBytes());
            }
            return null;
        }

        byte[] observed = new byte[expected.length];
        buffer.readBytes(observed);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Read " + observed.length + " bytes " + Utils.byteArrayToString(observed));
        }
        if (!Arrays.equals(observed, expected)) {
            LOGGER.error("observed bytes do not match expected bytes");
            if (LOGGER.isInfoEnabled()) {
                LOGGER.error("\texpected: " + Utils.byteArrayToString(expected));
                LOGGER.error("\tobserved: " + Utils.byteArrayToString(observed));
            }
            // Use a mismatch exception subclass, include the expression?
            throw new MessageMismatchException("Expression mismatch", expected, observed);
        }

        return buffer;
    }
}
