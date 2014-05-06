/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.lang.el;

import java.nio.ByteBuffer;

import javax.el.ELException;

import de.odysseus.el.misc.LocalMessages;
import de.odysseus.el.misc.TypeConverterImpl;

public class ByteArrayTypeConverter extends TypeConverterImpl {

    private static final long serialVersionUID = 2186717155880503427L;

    @Override
    @SuppressWarnings("unchecked")
    public <T> T convert(Object value, Class<T> type) throws ELException {

        if (type == byte[].class) {
            return (T) coerceToByteArray(value);
        }

        return super.convert(value, type);
    }

    private byte[] coerceToByteArray(Object value) {
        if (value instanceof byte[]) {
            return (byte[]) value;
        }
        if (value instanceof Long) {
            return ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong((Long) value).array();
        }
        if (value instanceof Integer) {
            return ByteBuffer.allocate(Integer.SIZE / Byte.SIZE).putInt((Integer) value).array();
        }
        if (value instanceof Short) {
            return ByteBuffer.allocate(Short.SIZE / Byte.SIZE).putShort((Short) value).array();
        }
        if (value instanceof Byte) {
            return new byte[] { ((Byte) value).byteValue() };
        }
        throw new ELException(LocalMessages.get("error.coerce.type", value.getClass(), byte[].class));
    }

}
