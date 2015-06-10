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

package org.kaazing.k3po.lang.internal.el;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.nio.ByteBuffer;

import javax.el.ELException;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import org.junit.Test;

import de.odysseus.el.misc.TypeConverter;
import de.odysseus.el.util.SimpleContext;

public class K3poTypeConverterTest {

    @Test
    public void shouldConvertStringToURI() throws Exception {
        TypeConverter converter = new K3poTypeConverter();
        String uriString = "http://localhost:8001/path?query";
        URI expectedURI = URI.create(uriString);
        Object o = converter.convert(uriString, URI.class);

        assertTrue(o instanceof URI);
        assertTrue(expectedURI.equals(o));
    }

    @Test(expected = ELException.class)
    public void shouldNotConvertToURI() throws Exception {
        TypeConverter converter = new K3poTypeConverter();

        converter.convert(converter, URI.class);
    }


    @Test()
    public void shouldConvertByteArrayToByteArray() throws Exception {
        TypeConverter converter = new K3poTypeConverter();
        byte[] byteArr = { 1, 2, 3, 4, 5, 6 };

        Object o = converter.convert(byteArr, byte[].class);

        assertTrue(o instanceof byte[]);

        assertArrayEquals(byteArr, (byte[]) o);
    }

    @Test()
    public void shouldConvertLongToByteArray() throws Exception {
        TypeConverter converter = new K3poTypeConverter();

        long l = 4096L;
        byte[] expected = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(l).array();

        Object o = converter.convert(l, byte[].class);

        assertTrue(o instanceof byte[]);

        assertArrayEquals(expected, (byte[]) o);

        Long lg = new Long(l);

        o = converter.convert(lg, byte[].class);

        assertArrayEquals(expected, (byte[]) o);

    }

    @Test()
    public void shouldConvertIntegerToByteArray() throws Exception {
        TypeConverter converter = new K3poTypeConverter();

        int l = 4096;
        byte[] expected = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE).putInt(l).array();

        Object o = converter.convert(l, byte[].class);

        assertTrue(o instanceof byte[]);

        assertArrayEquals(expected, (byte[]) o);

        Integer lg = new Integer(l);

        o = converter.convert(lg, byte[].class);

        assertArrayEquals(expected, (byte[]) o);

    }

    @Test()
    public void shouldConvertShortToByteArray() throws Exception {
        TypeConverter converter = new K3poTypeConverter();

        short l = 4096;
        byte[] expected = ByteBuffer.allocate(Short.SIZE / Byte.SIZE).putShort(l).array();

        Object o = converter.convert(l, byte[].class);

        assertTrue(o instanceof byte[]);

        assertArrayEquals(expected, (byte[]) o);

        Short lg = new Short(l);

        o = converter.convert(lg, byte[].class);

        assertArrayEquals(expected, (byte[]) o);

    }

    @Test()
    public void shouldConvertByteToByteArray() throws Exception {
        TypeConverter converter = new K3poTypeConverter();

        byte l = 16;
        byte[] expected = ByteBuffer.allocate(Byte.SIZE / Byte.SIZE).put(l).array();

        Object o = converter.convert(l, byte[].class);

        assertTrue(o instanceof byte[]);

        assertArrayEquals(expected, (byte[]) o);

        Byte lg = new Byte(l);

        o = converter.convert(lg, byte[].class);

        assertArrayEquals(expected, (byte[]) o);
    }

    @Test()
    public void shouldConvertAnObjectToCompatibleObject() throws Exception {
        TypeConverter converter = new K3poTypeConverter();

        Object o = converter.convert(converter, TypeConverter.class);

        assertTrue(o instanceof TypeConverter);
        assertTrue(o instanceof K3poTypeConverter);

        assertEquals(converter, o);
    }

    @Test(expected = ELException.class)
    public void shouldNotConvertToByteArray() throws Exception {
        TypeConverter converter = new K3poTypeConverter();

        converter.convert(converter, byte[].class);

    }

    @Test()
    public void doByteArrayExpression() throws Exception {

        ExpressionFactory factory = ExpressionFactoryUtils.newExpressionFactory();

        SimpleContext evalContext = new SimpleContext();

        ValueExpression byteExpr = factory.createValueExpression(new SimpleContext(), "${bytes}", byte[].class);

        byte[] expected = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };

        evalContext.getELResolver().setValue(new SimpleContext(), null, "bytes", expected);

        byte[] result = (byte[]) byteExpr.getValue(evalContext);

        assertArrayEquals(expected, result);

    }

    @Test()
    public void doLongtoByteArrayExpression() throws Exception {

        ExpressionFactory factory = ExpressionFactoryUtils.newExpressionFactory();

        SimpleContext evalContext = new SimpleContext();

        ValueExpression byteExpr = factory.createValueExpression(new SimpleContext(), "${bytes}", byte[].class);

        long number = 4096L;

        evalContext.getELResolver().setValue(new SimpleContext(), null, "bytes", number);

        byte[] expected = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(number).array();

        byte[] result = (byte[]) byteExpr.getValue(evalContext);

        assertArrayEquals(expected, result);
    }

    @Test()
    public void shouldDoArithmeticExpression() throws Exception {

        ExpressionFactory factory = ExpressionFactoryUtils.newExpressionFactory();

        SimpleContext evalContext = new SimpleContext();

        ValueExpression byteExpr = factory.createValueExpression(new SimpleContext(), "${bytes-1}", byte[].class);

        short number = 4096;

        evalContext.getELResolver().setValue(new SimpleContext(), null, "bytes", number);

        // Note that the expression should result in a widening conversion. EL
        // specification Section 1.17
        byte[] expected = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(number - 1).array();

        byte[] result = (byte[]) byteExpr.getValue(evalContext);

        assertArrayEquals(expected, result);
    }

    @Test(expected = ELException.class)
    public void doNotByteArrayExpression() throws Exception {

        ExpressionFactory factory = ExpressionFactoryUtils.newExpressionFactory();

        SimpleContext evalContext = new SimpleContext();

        ValueExpression byteExpr = factory.createValueExpression(new SimpleContext(), "${bytes-1}", byte[].class);

        byte[] in = { 1, 2, 3, 4 };

        evalContext.getELResolver().setValue(new SimpleContext(), null, "bytes", in);

        byteExpr.getValue(evalContext);
    }

}
