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

package org.kaazing.k3po.driver.behavior.handler.codec;

import static org.kaazing.k3po.driver.behavior.handler.codec.Masker.IDENTITY_MASKER;

import javax.el.ValueExpression;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.kaazing.k3po.lang.el.ExpressionContext;

public final class Maskers {

    public static Masker newMasker(byte[] maskingKey) {
        for (int i = 0; i < maskingKey.length; i++) {
            if (maskingKey[i] != 0x00) {
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
            byte[] maskingKey = (byte[]) expression.getValue(environment);
            return applyMask(buffer, maskingKey);
        }

        @Override
        public ChannelBuffer undoMask(ChannelBuffer buffer) throws Exception {
            byte[] maskingKey = (byte[]) expression.getValue(environment);
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
