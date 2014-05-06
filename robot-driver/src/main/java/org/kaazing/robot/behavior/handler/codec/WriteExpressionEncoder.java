/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.behavior.handler.codec;

import static org.kaazing.robot.util.Utils.byteArrayToString;
import static org.jboss.netty.buffer.ChannelBuffers.buffer;
import static org.jboss.netty.buffer.ChannelBuffers.wrappedBuffer;

import javax.el.ValueExpression;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

import org.kaazing.robot.lang.el.ExpressionContext;

public class WriteExpressionEncoder implements MessageEncoder {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(WriteExpressionEncoder.class);

    private final ExpressionContext context;
    private final ValueExpression expression;

    public WriteExpressionEncoder(ValueExpression expression, ExpressionContext context) {
        this.context = context;
        this.expression = expression;
    }

    @Override
    public ChannelBuffer encode() {

        final boolean isDebugEnabled = LOGGER.isDebugEnabled();
        final byte[] value = (byte[]) expression.getValue(context);
        final ChannelBuffer result;

        if (value != null) {
            if (isDebugEnabled) {
                LOGGER.debug("Encoding expression results. " + value.length + " bytes. Bytes: "
                        + byteArrayToString(value));
            }
            result = wrappedBuffer(value);
        } else {
            if (isDebugEnabled) {
                LOGGER.debug("Value of expression is null. Encoding as a 0 length buffer");
            }
            result = buffer(0);
        }
        return result;
    }

    @Override
    public String encodeToString() {
        final boolean isDebugEnabled = LOGGER.isDebugEnabled();
        byte[] value = (byte[]) expression.getValue(context);
        String result;
        if (value == null) {
            if (isDebugEnabled) {
                LOGGER.debug("Value of expression is null. Encoding as a 0 length String");
            }
            result = new String();
        } else {
            result = new String(value);
            if (isDebugEnabled) {
                LOGGER.debug("Encoding expression results. " + value.length + " bytes. String: "
                        + result);
            }
        }
        return result;
    }

}
