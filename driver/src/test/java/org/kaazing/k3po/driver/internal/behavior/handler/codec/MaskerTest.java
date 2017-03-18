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

import static org.jboss.netty.buffer.ChannelBuffers.wrappedBuffer;
import static org.junit.Assert.assertEquals;
import static org.kaazing.k3po.driver.internal.behavior.handler.codec.Maskers.newMasker;
import static org.kaazing.k3po.lang.internal.el.ExpressionFactoryUtils.synchronizedSupplier;

import java.util.function.Supplier;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import org.jboss.netty.buffer.ChannelBuffer;
import org.junit.Test;
import org.kaazing.k3po.lang.internal.el.ExpressionContext;

public class MaskerTest {
    @Test
    public void shouldMaskExactMultipleBuffer() throws Exception {
        Masker decoder = newMasker(new byte[]{0x01, 0x02, 0x03, 0x04});
        ChannelBuffer originalBuf = wrappedBuffer(new byte[]{0x11, 0x12, 0x13, 0x14, 0x21, 0x22, 0x23, 0x24});
        ChannelBuffer maskedBuf = decoder.applyMask(originalBuf);

        assertEquals(wrappedBuffer(new byte[]{0x10, 0x10, 0x10, 0x10, 0x20, 0x20, 0x20, 0x20}), maskedBuf);
    }

    @Test
    public void shouldMaskFragmentedExactMultipleBuffer() throws Exception {
        Masker decoder = newMasker(new byte[]{0x01, 0x02, 0x03, 0x04});
        ChannelBuffer originalBuf1 = wrappedBuffer(new byte[]{0x11, 0x12, 0x13, 0x14});
        ChannelBuffer maskedBuf1 = decoder.applyMask(originalBuf1);
        ChannelBuffer originalBuf2 = wrappedBuffer(new byte[]{0x21, 0x22, 0x23, 0x24});
        ChannelBuffer maskedBuf2 = decoder.applyMask(originalBuf2);

        assertEquals(wrappedBuffer(new byte[]{0x10, 0x10, 0x10, 0x10}), maskedBuf1);
        assertEquals(wrappedBuffer(new byte[]{0x20, 0x20, 0x20, 0x20}), maskedBuf2);
    }

    @Test
    public void shouldMaskFragmentedNonMultipleBuffer() throws Exception {
        Masker decoder = newMasker(new byte[]{0x01, 0x02, 0x03, 0x04});
        ChannelBuffer originalBuf1 = wrappedBuffer(new byte[]{0x11, 0x12, 0x13, 0x14, 0x11});
        ChannelBuffer maskedBuf1 = decoder.applyMask(originalBuf1);
        ChannelBuffer originalBuf2 = wrappedBuffer(new byte[]{0x22, 0x23, 0x24, 0x21});
        ChannelBuffer maskedBuf2 = decoder.applyMask(originalBuf2);

        assertEquals(wrappedBuffer(new byte[]{0x10, 0x10, 0x10, 0x10, 0x10}), maskedBuf1);
        assertEquals(wrappedBuffer(new byte[]{0x20, 0x20, 0x20, 0x20}), maskedBuf2);
    }

    @Test
    public void shouldMaskExactMultipleBufferWithExpressionKey() throws Exception {
        ExpressionContext environment = new ExpressionContext();
        ExpressionFactory factory = ExpressionFactory.newInstance();
        ValueExpression expression = factory.createValueExpression(new byte[]{0x01, 0x02, 0x03, 0x04}, byte[].class);
        Supplier<byte[]> supplier = synchronizedSupplier(expression, environment, byte[].class);

        Masker decoder = newMasker(supplier);
        ChannelBuffer originalBuf = wrappedBuffer(new byte[]{0x11, 0x12, 0x13, 0x14, 0x21, 0x22, 0x23, 0x24});
        ChannelBuffer maskedBuf = decoder.applyMask(originalBuf);

        assertEquals(wrappedBuffer(new byte[]{0x10, 0x10, 0x10, 0x10, 0x20, 0x20, 0x20, 0x20}), maskedBuf);
    }

    @Test
    public void shouldMaskFragmentedExactMultipleBufferWithExpressionKey() throws Exception {
        ExpressionContext environment = new ExpressionContext();
        ExpressionFactory factory = ExpressionFactory.newInstance();
        ValueExpression expression = factory.createValueExpression(new byte[]{0x01, 0x02, 0x03, 0x04}, byte[].class);
        Supplier<byte[]> supplier = synchronizedSupplier(expression, environment, byte[].class);

        Masker decoder = newMasker(supplier);
        ChannelBuffer originalBuf1 = wrappedBuffer(new byte[]{0x11, 0x12, 0x13, 0x14});
        ChannelBuffer maskedBuf1 = decoder.applyMask(originalBuf1);
        ChannelBuffer originalBuf2 = wrappedBuffer(new byte[]{0x21, 0x22, 0x23, 0x24});
        ChannelBuffer maskedBuf2 = decoder.applyMask(originalBuf2);

        assertEquals(wrappedBuffer(new byte[]{0x10, 0x10, 0x10, 0x10}), maskedBuf1);
        assertEquals(wrappedBuffer(new byte[]{0x20, 0x20, 0x20, 0x20}), maskedBuf2);
    }

    @Test
    public void shouldMaskFragmentedNonMultipleBufferWithExpressionKey() throws Exception {
        ExpressionContext environment = new ExpressionContext();
        ExpressionFactory factory = ExpressionFactory.newInstance();
        ValueExpression expression = factory.createValueExpression(new byte[]{0x01, 0x02, 0x03, 0x04}, byte[].class);
        Supplier<byte[]> supplier = synchronizedSupplier(expression, environment, byte[].class);

        Masker decoder = newMasker(supplier);
        ChannelBuffer originalBuf1 = wrappedBuffer(new byte[]{0x11, 0x12, 0x13, 0x14, 0x11});
        ChannelBuffer maskedBuf1 = decoder.applyMask(originalBuf1);
        ChannelBuffer originalBuf2 = wrappedBuffer(new byte[]{0x22, 0x23, 0x24, 0x21});
        ChannelBuffer maskedBuf2 = decoder.applyMask(originalBuf2);

        assertEquals(wrappedBuffer(new byte[]{0x10, 0x10, 0x10, 0x10, 0x10}), maskedBuf1);
        assertEquals(wrappedBuffer(new byte[]{0x20, 0x20, 0x20, 0x20}), maskedBuf2);
    }
}
