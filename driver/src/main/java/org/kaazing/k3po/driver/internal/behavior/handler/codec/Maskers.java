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

import static org.kaazing.k3po.driver.internal.behavior.handler.codec.Masker.IDENTITY_MASKER;

import javax.el.ValueExpression;

import org.jboss.netty.buffer.ChannelBuffer;
import org.kaazing.k3po.lang.internal.el.ExpressionContext;

public final class Maskers {

    public static Masker newMasker(byte[] maskingKey) {
        for (byte aMaskingKey : maskingKey) {
            if (aMaskingKey != 0x00) {
                return new ExactBytesMasker(maskingKey);
            }
        }

        return IDENTITY_MASKER;
    }

    public static Masker newMasker(ValueExpression expression, ExpressionContext environment) {
        return new ExpressionMasker(expression, environment);
    }

    private Maskers() {
        // utility class
    }

    private static class ExactBytesMasker extends AbstractMasker {

        private final byte[] maskingKey;

        public ExactBytesMasker(byte[] maskingKey) {
            this.maskingKey = maskingKey;
        }

        @Override
        public ChannelBuffer applyMask(ChannelBuffer buffer) throws Exception {
            return applyMask(buffer, maskingKey);
        }

        @Override
        public ChannelBuffer undoMask(ChannelBuffer buffer) throws Exception {
            return undoMask(buffer, maskingKey);
        }
    }

    private static class ExpressionMasker extends AbstractMasker {

        private final ValueExpression expression;
        private final ExpressionContext environment;

        public ExpressionMasker(ValueExpression expression, ExpressionContext environment) {
            this.expression = expression;
            this.environment = environment;
        }

        @Override
        public ChannelBuffer applyMask(ChannelBuffer buffer) throws Exception {
            final byte[] maskingKey;
            // TODO: Remove when JUEL sync bug is fixed https://github.com/k3po/k3po/issues/147
            synchronized (environment) {
                maskingKey = (byte[]) expression.getValue(environment);
            }
            return applyMask(buffer, maskingKey);
        }

        @Override
        public ChannelBuffer undoMask(ChannelBuffer buffer) throws Exception {
            final byte[] maskingKey;
            // TODO: Remove when JUEL sync bug is fixed https://github.com/k3po/k3po/issues/147
            synchronized (environment) {
                maskingKey = (byte[]) expression.getValue(environment);
            }
            return undoMask(buffer, maskingKey);
        }
    }

    private abstract static class AbstractMasker extends Masker {

        private int offset;

        protected final ChannelBuffer applyMask(ChannelBuffer buffer, byte[] maskingKey) throws Exception {

            int readerIndex = buffer.readerIndex();
            int writerIndex = buffer.writerIndex();

            for (int index = readerIndex; index < writerIndex; index++) {
                int maskIndex = (index + offset) % maskingKey.length;
                byte mask = maskingKey[maskIndex];
                if (mask != 0x00) {
                    byte value = buffer.getByte(index);
                    value ^= mask;
                    buffer.setByte(index, value);
                }
            }

            offset = (offset + writerIndex - readerIndex) % maskingKey.length;

            return buffer;
        }

        protected final ChannelBuffer undoMask(ChannelBuffer buffer, byte[] maskingKey) throws Exception {

            int readerIndex = buffer.readerIndex();
            int writerIndex = buffer.writerIndex();

            offset = (offset - (writerIndex - readerIndex)) % maskingKey.length;
            if (offset < 0) {
                offset += maskingKey.length;
            }

            for (int index = readerIndex; index < writerIndex; index++) {
                int maskIndex = (index + offset) % maskingKey.length;
                byte mask = maskingKey[maskIndex];
                if (mask != 0x00) {
                    byte value = buffer.getByte(index);
                    value ^= mask;
                    buffer.setByte(index, value);
                }
            }

            return buffer;
        }
    }
}
