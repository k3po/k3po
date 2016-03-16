/**
 * Copyright 2007-2015, Kaazing Corporation. All rights reserved.
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
package org.kaazing.k3po.driver.internal.behavior.handler.codec;

import static org.jboss.netty.buffer.ChannelBuffers.buffer;
import static org.jboss.netty.buffer.ChannelBuffers.wrappedBuffer;

import javax.el.ValueExpression;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.kaazing.k3po.lang.internal.el.ExpressionContext;

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

        final byte[] value;
        // TODO: Remove when JUEL sync bug is fixed https://github.com/k3po/k3po/issues/147
        synchronized (context) {
            value = (byte[]) expression.getValue(context);
        }
        final ChannelBuffer result;

        if (value != null) {
            result = wrappedBuffer(value);
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Value of expression is null. Encoding as a 0 length buffer");
            }
            result = buffer(0);
        }
        return result;
    }

    @Override
    public String toString() {
        return expression.getExpressionString();
    }

}
