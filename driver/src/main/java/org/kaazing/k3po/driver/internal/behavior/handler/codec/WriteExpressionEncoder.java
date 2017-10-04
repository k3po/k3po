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

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.jboss.netty.buffer.ChannelBuffers.buffer;

import java.util.function.Supplier;

import javax.el.ELException;
import javax.el.ValueExpression;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferFactory;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

public class WriteExpressionEncoder implements MessageEncoder {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(WriteExpressionEncoder.class);

    private final Supplier<Object> supplier;
    private final ValueExpression expression;

    public WriteExpressionEncoder(Supplier<Object> supplier, ValueExpression expression) {
        this.supplier = supplier;
        this.expression = expression;
    }

    @Override
    public ChannelBuffer encode(ChannelBufferFactory bufferFactory) {

        final Object value = supplier.get();
        final ChannelBuffer result;
        if (value != null) {
            result = asChannelBuffer(bufferFactory, value);
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Value of expression is null. Encoding as a 0 length buffer");
            }
            result = buffer(bufferFactory.getDefaultOrder(), 0);
        }
        return result;
    }

    @Override
    public String toString() {
        return expression.getExpressionString();
    }

    private ChannelBuffer asChannelBuffer(ChannelBufferFactory bufferFactory, Object value) {
        ChannelBuffer result;
        if (value instanceof byte[]) {
            byte[] valueBytes = (byte[]) value;
            result = bufferFactory.getBuffer(valueBytes, 0, valueBytes.length);
        }
        else if (value instanceof Long) {
            result = bufferFactory.getBuffer(Long.BYTES);
            result.setLong(0, (Long) value);
            result.writerIndex(Long.BYTES);
        }
        else if (value instanceof Integer) {
            result = bufferFactory.getBuffer(Integer.BYTES);
            result.setInt(0, (Integer) value);
            result.writerIndex(Integer.BYTES);
        }
        else if (value instanceof Short) {
            result = bufferFactory.getBuffer(Short.BYTES);
            result.setShort(0, (Short) value);
            result.writerIndex(Short.BYTES);
        }
        else if (value instanceof Byte) {
            result = bufferFactory.getBuffer(Byte.BYTES);
            result.setByte(0, (Byte) value);
            result.writerIndex(Byte.BYTES);
        }
        else if (value instanceof String) {
            byte[] valueBytes = ((String) value).getBytes(UTF_8);
            result = bufferFactory.getBuffer(valueBytes, 0, valueBytes.length);
        }
        else {
            throw new ELException(format("Unable to encode expression value \"%s\" of type \"$s\" of expression \"%s\"",
                    value.toString(), value.getClass(), expression.toString()));
        }
        return result;
    }

}
