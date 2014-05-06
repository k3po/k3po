/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.lang.el;

import java.math.BigInteger;

/**
 * Solves the problem of having a variable that may be used as an integer
 * expression ${len - 1})
 */
public class SetExpressionValueContext {

    private final ExpressionContext integerContext = new ExpressionContext();
    private final ExpressionContext byteArrayContext = new ExpressionContext();

    public ExpressionContext getIntegerContext() {
        return integerContext;
    }

    public ExpressionContext getByteArrayContext() {
        return byteArrayContext;
    }

    /**
     * Sets the value of the named property in the byte array context. If the
     * number of bytes is less than or equal to 4. We also set the value in the
     * integer context. The array of bytes is expected to be big endian.
     *
     * @param property
     * @param bytes
     */
    public void setValue(final String property, final byte[] bytes) {

        byteArrayContext.getELResolver().setValue(byteArrayContext, null, property, bytes);

        if (bytes.length <= Integer.SIZE) {
            // Use BigInt just for zero padding the array
            integerContext.getELResolver().setValue(integerContext, null, property, new BigInteger(bytes));
        }
    }

}
