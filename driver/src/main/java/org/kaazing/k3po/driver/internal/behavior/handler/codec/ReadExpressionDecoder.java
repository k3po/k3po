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
import static org.kaazing.k3po.lang.internal.RegionInfo.newSequential;

import java.nio.ByteBuffer;
import java.util.Arrays;

import javax.el.ValueExpression;

import org.jboss.netty.buffer.ChannelBuffer;
import org.kaazing.k3po.driver.internal.behavior.ScriptProgressException;
import org.kaazing.k3po.driver.internal.util.Utils;
import org.kaazing.k3po.lang.el.BytesMatcher;
import org.kaazing.k3po.lang.internal.RegionInfo;
import org.kaazing.k3po.lang.internal.el.ExpressionContext;

public class ReadExpressionDecoder extends MessageDecoder {

    private final ValueExpression expression;
    private final ExpressionContext environment;
    private BytesMatcher matcher;

    public ReadExpressionDecoder(RegionInfo regionInfo, ValueExpression expression, ExpressionContext environment) {
        super(regionInfo);
        this.expression = expression;
        this.environment = environment;
    }

    @Override
    public String toString() {
        return expression.getExpressionString();
    }

    @Override
    protected Object decodeBuffer(ChannelBuffer buffer) throws Exception {

        final Object expected;
        // TODO: Remove when JUEL sync bug is fixed https://github.com/k3po/k3po/issues/147
        synchronized (environment) {
            expected = expression.getValue(environment);
        }

        Object read;

        if (matcher == null && expected instanceof BytesMatcher)
        {
            matcher = (BytesMatcher) expected;
        }

        if (matcher != null)
        {
            final ByteBuffer byteBuf = buffer.toByteBuffer();
            final int initialPos = byteBuf.position();
            try
            {
                read = matcher.match(byteBuf);
            }
            catch (Exception ex)
            {
                throw new ScriptProgressException(getRegionInfo(), ex.getMessage());
            }
            final int bytesAdvanced = byteBuf.position() - initialPos;
            buffer.skipBytes(bytesAdvanced);
        }
        else
        {
            read = readValue(buffer, expected);
        }

        if (read == null) {
            return null;
        }
        else {
            return buffer;
        }
    }

    // unit tests
    ReadExpressionDecoder(ValueExpression expression, ExpressionContext environment) {
        this(newSequential(0, 0), expression, environment);
    }

    private Object readValue(
        ChannelBuffer buffer,
        Object expected) throws ScriptProgressException
    {
        Object observed = null;
        int available = buffer.readableBytes();
        if (expected instanceof byte[] || expected instanceof String) {
            observed = readByteArrayOrString(buffer, expected);
        }
        else {
            if (expected instanceof Long && available >= Long.BYTES) {
                observed = readLong(buffer, (Long) expected);
            }
            else if (expected instanceof Integer && available >= Integer.BYTES) {
                 observed = readInteger(buffer, (Integer) expected);
            }
            else if (expected instanceof Short && available >= Short.BYTES) {
                observed = readShort(buffer, (Short) expected);
            }
            else if (expected instanceof Byte && available >= Byte.BYTES) {
                observed = readByte(buffer, (Byte) expected);
            }
            else {
                throw new ScriptProgressException(getRegionInfo(), format("Expected value %s has unsupported type",
                        expected));
            }
            if (observed != null && !expected.equals(observed)) {
                throw new ScriptProgressException(getRegionInfo(), observed.toString());
            }
        }
        return observed;
    }

    private Object readByteArrayOrString(
        ChannelBuffer buffer,
        Object expected) throws ScriptProgressException
    {
        Object observed;
        byte[] expectedBytes = expected instanceof String ?
                ((String) expected).getBytes(UTF_8) : (byte[]) expected;
        byte[] read = readByteArray(buffer, expectedBytes);
        if (read != null && !Arrays.equals(read, expectedBytes)) {
            // Use a mismatch exception subclass, include the expression?
            throw new ScriptProgressException(getRegionInfo(), Utils.format(read));
        }
        observed = read;
        return observed;
    }

    private byte[] readByteArray(
        ChannelBuffer buffer,
        byte[] expected)
    {
        byte[] result = null;
        if (buffer.readableBytes() >= expected.length) {
            result = new byte[expected.length];
            buffer.readBytes(result);
        }
        return result;
    }

    private Byte readByte(
        ChannelBuffer buffer,
        Byte expected)
    {
        Byte result = null;
        int length = Byte.BYTES;
        if (buffer.readableBytes() >= length) {
            int index = buffer.readerIndex();
            result = buffer.getByte(buffer.readerIndex());
            buffer.readerIndex(index +  length);
        }
        return result;
    }

    private Short readShort(
        ChannelBuffer buffer,
        Short expected)
    {
        Short result = null;
        int length = Short.BYTES;
        if (buffer.readableBytes() >= length) {
            int index = buffer.readerIndex();
            result = buffer.getShort(buffer.readerIndex());
            buffer.readerIndex(index + length);
        }
        return result;
    }

    private Integer readInteger(
        ChannelBuffer buffer,
        Integer expected)
    {
        Integer result = null;
        int length = Integer.BYTES;
        if (buffer.readableBytes() >= length) {
            int index = buffer.readerIndex();
            result = buffer.getInt(buffer.readerIndex());
            buffer.readerIndex(index + length);
        }
        return result;
    }

    private Long readLong(
        ChannelBuffer buffer,
        Long expected)
    {
        Long result = null;
        int length = Long.BYTES;
        if (buffer.readableBytes() >= length) {
            int index = buffer.readerIndex();
            result = buffer.getLong(buffer.readerIndex());
            buffer.readerIndex(index + length);
        }
        return result;
    }

}
