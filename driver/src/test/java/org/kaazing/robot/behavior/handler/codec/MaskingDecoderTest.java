/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */
package org.kaazing.robot.behavior.handler.codec;

import static org.kaazing.robot.behavior.handler.codec.MaskingDecoders.newMaskingDecoder;
import static org.jboss.netty.buffer.ChannelBuffers.wrappedBuffer;
import static org.junit.Assert.assertEquals;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import org.jboss.netty.buffer.ChannelBuffer;
import org.junit.Test;
import org.kaazing.el.util.ExpressionContext;


public class MaskingDecoderTest {
    @Test
    public void shouldMaskExactMultipleBuffer() throws Exception {
        MaskingDecoder decoder = newMaskingDecoder(new byte[] {0x01, 0x02, 0x03, 0x04});
        ChannelBuffer originalBuf = wrappedBuffer(new byte[] {0x11, 0x12, 0x13, 0x14, 0x21, 0x22, 0x23, 0x24});
        ChannelBuffer maskedBuf = decoder.applyMask(originalBuf);

        assertEquals(wrappedBuffer(new byte[] {0x10, 0x10, 0x10, 0x10, 0x20, 0x20, 0x20, 0x20}), maskedBuf);
    }

    @Test
    public void shouldMaskFragmentedExactMultipleBuffer() throws Exception {
        MaskingDecoder decoder = newMaskingDecoder(new byte[] {0x01, 0x02, 0x03, 0x04});
        ChannelBuffer originalBuf1 = wrappedBuffer(new byte[] {0x11, 0x12, 0x13, 0x14});
        ChannelBuffer maskedBuf1 = decoder.applyMask(originalBuf1);
        ChannelBuffer originalBuf2 = wrappedBuffer(new byte[] {0x21, 0x22, 0x23, 0x24});
        ChannelBuffer maskedBuf2 = decoder.applyMask(originalBuf2);

        assertEquals(wrappedBuffer(new byte[] {0x10, 0x10, 0x10, 0x10}), maskedBuf1);
        assertEquals(wrappedBuffer(new byte[] {0x20, 0x20, 0x20, 0x20}), maskedBuf2);
    }

    @Test
    public void shouldMaskFragmentedNonMultipleBuffer() throws Exception {
        MaskingDecoder decoder = newMaskingDecoder(new byte[] {0x01, 0x02, 0x03, 0x04});
        ChannelBuffer originalBuf1 = wrappedBuffer(new byte[] {0x11, 0x12, 0x13, 0x14, 0x11});
        ChannelBuffer maskedBuf1 = decoder.applyMask(originalBuf1);
        ChannelBuffer originalBuf2 = wrappedBuffer(new byte[] {0x22, 0x23, 0x24, 0x21});
        ChannelBuffer maskedBuf2 = decoder.applyMask(originalBuf2);

        assertEquals(wrappedBuffer(new byte[] {0x10, 0x10, 0x10, 0x10, 0x10}), maskedBuf1);
        assertEquals(wrappedBuffer(new byte[] {0x20, 0x20, 0x20, 0x20}), maskedBuf2);
    }

    @Test
    public void shouldMaskExactMultipleBufferWithExpressionKey() throws Exception {
        ExpressionContext environment = new ExpressionContext();
        ExpressionFactory factory = ExpressionFactory.newInstance();
        ValueExpression expression = factory.createValueExpression(new byte[] {0x01, 0x02, 0x03, 0x04}, byte[].class);

        MaskingDecoder decoder = newMaskingDecoder(expression, environment);
        ChannelBuffer originalBuf = wrappedBuffer(new byte[] {0x11, 0x12, 0x13, 0x14, 0x21, 0x22, 0x23, 0x24});
        ChannelBuffer maskedBuf = decoder.applyMask(originalBuf);

        assertEquals(wrappedBuffer(new byte[] {0x10, 0x10, 0x10, 0x10, 0x20, 0x20, 0x20, 0x20}), maskedBuf);
    }

    @Test
    public void shouldMaskFragmentedExactMultipleBufferWithExpressionKey() throws Exception {
        ExpressionContext environment = new ExpressionContext();
        ExpressionFactory factory = ExpressionFactory.newInstance();
        ValueExpression expression = factory.createValueExpression(new byte[] {0x01, 0x02, 0x03, 0x04}, byte[].class);

        MaskingDecoder decoder = newMaskingDecoder(expression, environment);
        ChannelBuffer originalBuf1 = wrappedBuffer(new byte[] {0x11, 0x12, 0x13, 0x14});
        ChannelBuffer maskedBuf1 = decoder.applyMask(originalBuf1);
        ChannelBuffer originalBuf2 = wrappedBuffer(new byte[] {0x21, 0x22, 0x23, 0x24});
        ChannelBuffer maskedBuf2 = decoder.applyMask(originalBuf2);

        assertEquals(wrappedBuffer(new byte[] {0x10, 0x10, 0x10, 0x10}), maskedBuf1);
        assertEquals(wrappedBuffer(new byte[] {0x20, 0x20, 0x20, 0x20}), maskedBuf2);
    }

    @Test
    public void shouldMaskFragmentedNonMultipleBufferWithExpressionKey() throws Exception {
        ExpressionContext environment = new ExpressionContext();
        ExpressionFactory factory = ExpressionFactory.newInstance();
        ValueExpression expression = factory.createValueExpression(new byte[] {0x01, 0x02, 0x03, 0x04}, byte[].class);

        MaskingDecoder decoder = newMaskingDecoder(expression, environment);
        ChannelBuffer originalBuf1 = wrappedBuffer(new byte[] {0x11, 0x12, 0x13, 0x14, 0x11});
        ChannelBuffer maskedBuf1 = decoder.applyMask(originalBuf1);
        ChannelBuffer originalBuf2 = wrappedBuffer(new byte[] {0x22, 0x23, 0x24, 0x21});
        ChannelBuffer maskedBuf2 = decoder.applyMask(originalBuf2);

        assertEquals(wrappedBuffer(new byte[] {0x10, 0x10, 0x10, 0x10, 0x10}), maskedBuf1);
        assertEquals(wrappedBuffer(new byte[] {0x20, 0x20, 0x20, 0x20}), maskedBuf2);
    }
}
