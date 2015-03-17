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

package org.kaazing.k3po.driver.behavior.handler.codec;

import static org.kaazing.k3po.lang.RegionInfo.newSequential;

import java.util.Arrays;

import javax.el.ValueExpression;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.kaazing.k3po.driver.behavior.ScriptProgressException;
import org.kaazing.k3po.driver.util.Utils;
import org.kaazing.k3po.lang.RegionInfo;
import org.kaazing.k3po.lang.el.ExpressionContext;

public class ReadExpressionDecoder extends MessageDecoder {

    private final ValueExpression expression;
    private final ExpressionContext environment;
    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(ReadExpressionDecoder.class);

    public ReadExpressionDecoder(RegionInfo regionInfo, ValueExpression expression, ExpressionContext environment) {
        super(regionInfo);
        this.expression = expression;
        this.environment = environment;
    }

    @Override
    public String toString() {
        return expression.toString();
    }

    @Override
    protected Object decodeBuffer(ChannelBuffer buffer) throws Exception {

        final boolean isDebugEnabled = LOGGER.isDebugEnabled();
        if (isDebugEnabled) {
            LOGGER.debug("Getting value to read from " + environment);
        }
        final byte[] expected;
        synchronized (environment) {
            expected = (byte[]) expression.getValue(environment);
        }

        if (buffer.readableBytes() < expected.length) {
            return null;
        }

        byte[] observed = new byte[expected.length];
        buffer.readBytes(observed);
        if (!Arrays.equals(observed, expected)) {
            // Use a mismatch exception subclass, include the expression?
            throw new ScriptProgressException(getRegionInfo(), Utils.format(observed));
        }

        return buffer;
    }

    // unit tests
    ReadExpressionDecoder(ValueExpression expression, ExpressionContext environment) {
        this(newSequential(0, 0), expression, environment);
    }

}
