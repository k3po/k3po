package org.kaazing.robot.behavior.handler.codec;

import javax.el.ValueExpression;

import org.jboss.netty.buffer.ChannelBuffer;
import org.kaazing.el.util.ExpressionContext;

public final class MaskingDecoders {

    public static MaskingDecoder newMaskingDecoder(byte[] maskingKey) {
        return new ExactBytesMaskingDecoder(maskingKey);
    }

    public static MaskingDecoder newMaskingDecoder(ValueExpression expression, ExpressionContext environment) {
        return new ExpressionMaskingDecoder(expression, environment);
    }

    private MaskingDecoders() {
        // utility class
    }

    private static class ExactBytesMaskingDecoder extends AbstractMaskingDecoder {

        private final byte[] maskingKey;

        public ExactBytesMaskingDecoder(byte[] maskingKey) {
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

    private static class ExpressionMaskingDecoder extends AbstractMaskingDecoder {

        private final ValueExpression expression;
        private final ExpressionContext environment;

        public ExpressionMaskingDecoder(ValueExpression expression, ExpressionContext environment) {
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

    private abstract static class AbstractMaskingDecoder extends MaskingDecoder {

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