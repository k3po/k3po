/*
 * Copyright (c) 2014 "Kaazing Corporation," (www.kaazing.com)
 *
 * This file is part of Robot.
 *
 * Robot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.kaazing.k3po.lang.el;

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
