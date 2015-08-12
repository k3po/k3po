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

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.net.URI;
import java.nio.ByteBuffer;

import javax.el.ELException;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import org.junit.Test;

import de.odysseus.el.misc.TypeConverter;
import de.odysseus.el.util.SimpleContext;

public class TypeConverterImplTest {

    @Test
    public void shouldConvertStringToURI() throws Exception {
        TypeConverter converter = new TypeConverterImpl();
        String location = "http://localhost:8001/path?query";
        URI expected = URI.create(location);
        Object o = converter.convert(location, URI.class);

        assertThat(o, instanceOf(URI.class));
        assertEquals(expected, o);
    }

    @Test(expected = ELException.class)
    public void shouldNotConvertToURI() throws Exception {
        TypeConverter converter = new TypeConverterImpl();

        converter.convert(converter, URI.class);
    }


    @Test()
    public void shouldConvertByteArrayToByteArray() throws Exception {
        TypeConverter converter = new TypeConverterImpl();
        byte[] byteArr = { 1, 2, 3, 4, 5, 6 };

        Object o = converter.convert(byteArr, byte[].class);

        assertThat(o, instanceOf(byte[].class));

        assertArrayEquals(byteArr, (byte[]) o);
    }

    @Test()
    public void shouldConvertLongToByteArray() throws Exception {
        TypeConverter converter = new TypeConverterImpl();

        long l = 4096L;
        byte[] expected = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(l).array();

        Object o = converter.convert(l, byte[].class);

        assertThat(o, instanceOf(byte[].class));

        assertArrayEquals(expected, (byte[]) o);

        Long lg = new Long(l);

        o = converter.convert(lg, byte[].class);

        assertArrayEquals(expected, (byte[]) o);

    }

    @Test()
    public void shouldConvertIntegerToByteArray() throws Exception {
        TypeConverter converter = new TypeConverterImpl();

        int l = 4096;
        byte[] expected = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE).putInt(l).array();

        Object o = converter.convert(l, byte[].class);

        assertThat(o, instanceOf(byte[].class));

        assertArrayEquals(expected, (byte[]) o);

        Integer lg = new Integer(l);

        o = converter.convert(lg, byte[].class);

        assertArrayEquals(expected, (byte[]) o);

    }

    @Test()
    public void shouldConvertShortToByteArray() throws Exception {
        TypeConverter converter = new TypeConverterImpl();

        short l = 4096;
        byte[] expected = ByteBuffer.allocate(Short.SIZE / Byte.SIZE).putShort(l).array();

        Object o = converter.convert(l, byte[].class);

        assertThat(o, instanceOf(byte[].class));

        assertArrayEquals(expected, (byte[]) o);

        Short lg = new Short(l);

        o = converter.convert(lg, byte[].class);

        assertArrayEquals(expected, (byte[]) o);

    }

    @Test()
    public void shouldConvertByteToByteArray() throws Exception {
        TypeConverter converter = new TypeConverterImpl();

        byte l = 16;
        byte[] expected = ByteBuffer.allocate(Byte.SIZE / Byte.SIZE).put(l).array();

        Object o = converter.convert(l, byte[].class);

        assertThat(o, instanceOf(byte[].class));

        assertArrayEquals(expected, (byte[]) o);

        Byte lg = new Byte(l);

        o = converter.convert(lg, byte[].class);

        assertArrayEquals(expected, (byte[]) o);
    }

    @Test()
    public void shouldConvertAnObjectToCompatibleObject() throws Exception {
        TypeConverter converter = new TypeConverterImpl();

        Object o = converter.convert(converter, TypeConverter.class);

        assertThat(o, instanceOf(TypeConverter.class));
        assertThat(o, instanceOf(TypeConverterImpl.class));

        assertEquals(converter, o);
    }

    @Test(expected = ELException.class)
    public void shouldNotConvertToByteArray() throws Exception {
        TypeConverter converter = new TypeConverterImpl();

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
