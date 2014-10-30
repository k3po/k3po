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

package org.kaazing.robot.lang.el;

import static java.nio.charset.StandardCharsets.UTF_8;

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
        if (value instanceof String) {
            return ((String) value).getBytes(UTF_8);
        }
        throw new ELException(LocalMessages.get("error.coerce.type", value.getClass(), byte[].class));
    }

}
