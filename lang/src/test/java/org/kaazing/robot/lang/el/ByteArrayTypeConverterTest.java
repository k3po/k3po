/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.lang.el;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;

import javax.el.ELException;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import org.junit.Test;

import de.odysseus.el.misc.TypeConverter;
import de.odysseus.el.util.SimpleContext;

public class ByteArrayTypeConverterTest {

    @Test()
    public void shouldConvertByteArray() throws Exception {
        TypeConverter converter = new ByteArrayTypeConverter();
        byte[] byteArr = { 1, 2, 3, 4, 5, 6 };

        Object o = converter.convert(byteArr, byte[].class);

        assertTrue(o instanceof byte[]);

        assertArrayEquals(byteArr, (byte[]) o);
    }

    @Test()
    public void shouldConvertLong() throws Exception {
        TypeConverter converter = new ByteArrayTypeConverter();

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
    public void shouldConvertInteger() throws Exception {
        TypeConverter converter = new ByteArrayTypeConverter();

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
    public void shouldConvertShort() throws Exception {
        TypeConverter converter = new ByteArrayTypeConverter();

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
    public void shouldConvertByte() throws Exception {
        TypeConverter converter = new ByteArrayTypeConverter();

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
    public void shouldConvertAnObject() throws Exception {
        TypeConverter converter = new ByteArrayTypeConverter();

        Object o = converter.convert(converter, TypeConverter.class);

        assertTrue(o instanceof TypeConverter);
        assertTrue(o instanceof ByteArrayTypeConverter);

        assertEquals(converter, o);
    }

    @Test(expected = ELException.class)
    public void shouldNotConvertToByteArray() throws Exception {
        TypeConverter converter = new ByteArrayTypeConverter();

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
