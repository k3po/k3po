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

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.kaazing.k3po.driver.internal.behavior.ScriptProgressException;
import org.kaazing.k3po.lang.internal.RegionInfo;
import org.kaazing.k3po.lang.internal.ast.value.AstLiteralBytesValue;
import org.kaazing.k3po.lang.internal.el.ExpressionContext;

public abstract class ReadFixedLengthBytesDecoder<T> extends MessageDecoder {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(ReadFixedLengthBytesDecoder.class);

    private final int length;
    private final ExpressionContext environment;
    private final String captureName;

    public ReadFixedLengthBytesDecoder(RegionInfo regionInfo, int length) {
        this(regionInfo, length, null, null);
    }

    public ReadFixedLengthBytesDecoder(RegionInfo regionInfo, int length, ExpressionContext environment, String captureName) {
        super(regionInfo);
        this.length = length;
        this.environment = environment;
        this.captureName = captureName;
    }

    protected abstract T readBuffer(ChannelBuffer buffer);

    public int getLength() {
        return length;
    }

    @Override
    protected Object decodeBufferLast(ChannelBuffer buffer) throws Exception {

        if (buffer.readableBytes() < length) {
            throw new ScriptProgressException(getRegionInfo(), String.format("Expected %d bytes, found %d", length, buffer.readableBytes()));
        }

        if (captureName == null) {
            buffer.readSlice(length);
        } else {
            T value = readBuffer(buffer);
            // TODO: Remove when JUEL sync bug is fixed https://github.com/k3po/k3po/issues/147
            synchronized (environment) {
                environment.getELResolver().setValue(environment, null, captureName, value);
            }

            if (LOGGER.isDebugEnabled()) {
                Object formatValue = (value instanceof byte[]) ? AstLiteralBytesValue.toString((byte[]) value) : value;
                LOGGER.debug(format("Setting value for ${%s} to %s", captureName, formatValue));
            }
        }
        return buffer;
    }

    @Override
    protected Object decodeBuffer(ChannelBuffer buffer) throws Exception {

        if (buffer.readableBytes() < length) {
            return null;
        }
        if (captureName == null) {
            buffer.readSlice(length);
        } else {
            T value = readBuffer(buffer);
            // TODO: Remove when JUEL sync bug is fixed https://github.com/k3po/k3po/issues/147
            synchronized (environment) {
                environment.getELResolver().setValue(environment, null, captureName, value);
            }

            if (LOGGER.isDebugEnabled()) {
                Object formatValue = (value instanceof byte[]) ? AstLiteralBytesValue.toString((byte[]) value) : value;
                LOGGER.debug(format("Setting value for ${%s} to %s", captureName, formatValue));
            }
        }
        return buffer;
    }

    @Override
    public String toString() {
        return String.format("%d bytes", length);
    }
}
