/*
 * Copyright 2014, Kaazing Corporation. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kaazing.k3po.lang.el;

import java.math.BigInteger;

/**
 * Solves the problem of having a variable that may be used as an integer
 * expression ${len - 1})
 */
public class SetExpressionValueContext {

//    private final ExpressionContext integerContext = new ExpressionContext();
//    private final ExpressionContext byteArrayContext = new ExpressionContext();
//
//    public ExpressionContext getIntegerContext() {
//        return integerContext;
//    }
//
//    public ExpressionContext getByteArrayContext() {
//        return byteArrayContext;
//    }
//
//    /**
//     * Sets the value of the named property in the byte array context. If the
//     * number of bytes is less than or equal to 4. We also set the value in the
//     * integer context. The array of bytes is expected to be big endian.
//     *
//     * @param property
//     * @param bytes
//     */
//    public void setValue(final String property, final byte[] bytes) {
//
//        byteArrayContext.getELResolver().setValue(byteArrayContext, null, property, bytes);
//
//        if (bytes.length <= Integer.SIZE) {
//            // Use BigInt just for zero padding the array
//            integerContext.getELResolver().setValue(integerContext, null, property, new BigInteger(bytes));
//        }
//    }

}
