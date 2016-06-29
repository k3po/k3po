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
package org.kaazing.specification.sse.internal;

import java.util.Random;

import org.kaazing.k3po.lang.el.Function;
import org.kaazing.k3po.lang.el.spi.FunctionMapperSpi;

public final class Functions {
    private static final Random RANDOM = new Random();

    @Function
    public static byte[] randomBytes(int length) {
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            bytes[i] = (byte) RANDOM.nextInt(0x100);
        }
        return bytes;
    }

    @Function
    public static byte[] randomBytesUTF8(int length) {
        byte[] bytes = new byte[length];
        randomBytesUTF8(bytes, 0, length);
        return bytes;
    }

    @Function
    public static byte[] randomBytesInvalidUTF8(int length) {
        // TODO: make invalid UTF-8 bytes less like valid UTF-8 (!)
        byte[] bytes = new byte[length];
        bytes[0] = (byte) 0x80;
        randomBytesUTF8(bytes, 1, length - 1);
        return bytes;
    }

    private static void randomBytesUTF8(byte[] bytes, int start, int end) {
        for (int offset = start; offset < end;) {
            int remaining = end - offset;
            int width = Math.min(RANDOM.nextInt(4) + 1, remaining);

            offset = randomCharBytesUTF8(bytes, offset, width);
        }
    }

    private static int randomCharBytesUTF8(byte[] bytes, int offset, int width) {
        switch (width) {
        case 1:
            bytes[offset++] = (byte) RANDOM.nextInt(0x80);
            break;
        case 2:
            bytes[offset++] = (byte) (0xc0 | RANDOM.nextInt(0x20) | 1 << (RANDOM.nextInt(4) + 1));
            bytes[offset++] = (byte) (0x80 | RANDOM.nextInt(0x40));
            break;
        case 3:
            // UTF-8 not legal for 0xD800 through 0xDFFF (see RFC 3269)
            bytes[offset++] = (byte) (0xe0 | RANDOM.nextInt(0x08) | 1 << RANDOM.nextInt(3));
            bytes[offset++] = (byte) (0x80 | RANDOM.nextInt(0x40));
            bytes[offset++] = (byte) (0x80 | RANDOM.nextInt(0x40));
            break;
        case 4:
            // UTF-8 ends at 0x10FFFF (see RFC 3269)
            bytes[offset++] = (byte) (0xf0 | RANDOM.nextInt(0x04) | 1 << RANDOM.nextInt(2));
            bytes[offset++] = (byte) (0x80 | RANDOM.nextInt(0x10));
            bytes[offset++] = (byte) (0x80 | RANDOM.nextInt(0x40));
            bytes[offset++] = (byte) (0x80 | RANDOM.nextInt(0x40));
            break;
        }
        return offset;
    }

    public static class Mapper extends FunctionMapperSpi.Reflective {

        public Mapper() {
            super(Functions.class);
        }

        @Override
        public String getPrefixName() {
            return "sse";
        }
    }

    private Functions() {
        // utility
    }
}
