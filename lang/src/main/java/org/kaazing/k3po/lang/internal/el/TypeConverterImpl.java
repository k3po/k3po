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

import static java.nio.charset.StandardCharsets.UTF_8;

import java.net.URI;
import java.nio.ByteBuffer;

import javax.el.ELException;

import de.odysseus.el.misc.LocalMessages;

public class TypeConverterImpl extends de.odysseus.el.misc.TypeConverterImpl {

    private static final long serialVersionUID = 2186717155880503427L;

    @Override
    @SuppressWarnings("unchecked")
    public <T> T convert(Object value, Class<T> type) throws ELException {

        if (type == URI.class && value instanceof String) {
            return (T) URI.create((String) value);
        }
        else if (type == byte[].class) {
            return (T) coerceToByteArray(value);
        }
        else if (value instanceof byte[]) {
            return coerceFromByteArray((byte[]) value, type);
        }

        return super.convert(value, type);
    }

    @SuppressWarnings("unchecked")
    private <T> T coerceFromByteArray(byte[] value, Class<T> type) {
        if (type == String.class) {
            return (T) new String(value, UTF_8);
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
