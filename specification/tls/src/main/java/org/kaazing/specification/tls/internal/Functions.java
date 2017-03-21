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
package org.kaazing.specification.tls.internal;

import java.util.Random;

import org.kaazing.k3po.lang.el.Function;
import org.kaazing.k3po.lang.el.spi.FunctionMapperSpi;

public final class Functions {

    private static final Random RANDOM = new Random();

    public static class Mapper extends FunctionMapperSpi.Reflective {

        public Mapper() {
            super(Functions.class);
        }

        @Override
        public String getPrefixName() {
            return "tls";
        }
    }

    @Function
    public static byte[] gmt_unix_time() {
        long unixTime = System.currentTimeMillis() / 1000L;
        byte[] productionDate =
                new byte[]{(byte) (unixTime >> 24), (byte) (unixTime >> 16), (byte) (unixTime >> 8), (byte) unixTime};
        return productionDate;
    }

    @Function
    public static byte[] subjectPublicKey() {
        byte[] example_public_key = new byte[]{
                0x04, (byte) 0x9e, (byte) 0xcd, (byte) 0xf9, 0x72, 0x38, 0x33, (byte) 0xe6,
                (byte) 0xf4, (byte) 0x97, 0x52, 0x22, (byte) 0xdb, 0x2c, 0x26, (byte) 0xa8,
                0x02, 0x34, 0x54, 0x75, (byte) 0x8e, 0x52, (byte) 0xc6, 0x37,
                (byte) 0xfa, 0x1f, (byte) 0xe7, 0x39, 0x4e, 0x7b, (byte) 0x93, 0x74,
                (byte) 0xdb, 0x79, (byte) 0xf0, (byte) 0xc6, 0x00, 0x11, (byte) 0x9f, 0x67,
                (byte) 0x9b, (byte) 0xc2, 0x6c, 0x3f, 0x27, (byte) 0x80, (byte) 0xb3, 0x49,
                (byte) 0xab, 0x14, 0x5b, (byte) 0xd7, (byte) 0xa9, 0x02, (byte) 0xcc, (byte) 0xf4,
                0x23, (byte) 0xc5, (byte) 0x86, 0x42, (byte) 0x9b, (byte) 0xb3, 0x5a, (byte) 0xc4,
                0x1f
                };
        return example_public_key;
    }
    
    @Function
    public static byte[] exampleSignatureValue() {
        byte[] example_signature_value = new byte[] {
                (byte) 0x85, (byte) 0x89, (byte) 0xd2, (byte) 0x8d, 0x0d, 0x60, (byte) 0x87, (byte) 0xc9,
                (byte) (byte) 0xe6, 0x55, 0x05, 0x44, (byte) 0xa9, (byte) 0x94, (byte) 0xcd, (byte) 0xd1,
                (byte) 0xec, 0x65, (byte) 0xd1, (byte) 0xd5, 0x46, 0x1e, (byte) 0xd0, 0x42,
                0x3e, (byte) 0xdd, (byte) 0xaf, 0x7e, (byte) 0x8a, (byte) 0xeb ,0x67, 0x0c,
                0x5b, 0x28, 0x2c, 0x3c, (byte) 0xbb, (byte) 0x8c, 0x7b, 0x2d,
                (byte) 0x94, 0x33, (byte) 0x9d, (byte) 0x82, 0x63, (byte) 0xcc, (byte) 0xd1, 0x6e,
                (byte) 0xd6, (byte) 0xbf, (byte) 0xe5, (byte) 0xed, (byte) 0xfa, 0x4b, 0x15, (byte) 0xfd,
                0x06, (byte) 0x8f, 0x65, (byte) 0xba, (byte) 0xbe, 0x24, (byte) 0xe4, (byte) 0xeb,
                (byte) 0xce, 0x24, (byte) 0xa1, 0x35, 0x0c, (byte) 0xea, (byte) 0xa2, 0x04,
                0x3c, 0x36, 0x64, (byte) 0x9c, (byte) 0xab, 0x12, (byte) 0x9c, 0x72,
                (byte) 0xf4, 0x50, (byte) 0xd8, (byte) 0xcf, 0x5c, 0x46, (byte) 0xe4, 0x60,
                0x2f, 0x1f, 0x75, (byte) 0xa6, (byte) 0xbd, 0x7f, (byte) 0x95, (byte) 0xde,
                (byte) 0xa4, (byte) 0x85, 0x53, 0x26, 0x1a, 0x63, 0x32, (byte) 0xd5,
                0x33, (byte) 0xc2, (byte) 0xad, 0x70, (byte) 0xfe, (byte) 0xc8, 0x48, 0x6f,
                0x7e, (byte) 0xcf, 0x59, (byte) 0xc4, 0x31, (byte) 0x9e, (byte) 0xca, (byte) 0xb5,
                0x60, 0x47, 0x32, 0x16, 0x14, 0x23, (byte) 0xa6, (byte) 0xaf,
                0x32, 0x29, (byte) 0xad, (byte) 0xf5, (byte) 0xb6, (byte) 0xef, (byte) 0xa5, (byte) 0xd8,
                (byte) 0xad, (byte) 0xe2, 0x38, 0x13, 0x18, 0x17, 0x39, (byte) 0xae,
                (byte) 0x9b, 0x56, (byte) 0x87, 0x5e, 0x22, (byte) 0xf7, 0x51, (byte) 0x9f,
                (byte) 0x9f, (byte) 0xcc, (byte) 0xaf, (byte) 0xd4, (byte) 0xa4, 0x61, 0x7e, (byte) 0xf7,
                (byte) 0xd7, (byte) 0xf7, (byte) 0xc7, (byte) 0xae, (byte) 0x9c, (byte) 0x85, (byte) 0x8c, (byte) 0xe2,
                0x53, (byte) 0xb3, 0x70, (byte) 0x80, 0x51, (byte) 0xac, (byte) 0xbd, 0x4f,
                (byte) 0x8e, (byte) 0x98, (byte) 0xaf, (byte) 0xbb, (byte) 0xd7, 0x34, 0x02, (byte) 0xca,
                (byte) 0x89, 0x0f, 0x2f, 0x39, (byte) 0xeb, 0x13, 0x20, (byte) 0xc7,
                0x23, (byte) 0xe8, (byte) 0xb6, (byte) 0x8c, (byte) 0xad, (byte) 0xdc, 0x6d, (byte) 0x9f,
                (byte) 0x8a, 0x60, 0x66, (byte) 0xff, 0x16, 0x7e, (byte) 0xac, (byte) 0xc6,
                (byte) 0xbe, (byte) 0xe8, 0x5b, (byte) 0x83, 0x29, 0x29, (byte) 0x98, 0x68,
                (byte) 0x86, (byte) 0xe2, 0x24, 0x76, (byte) 0xb6, (byte) 0xe2, 0x4b, (byte) 0x99,
                (byte) 0xf3, 0x6b, 0x7a, 0x73, (byte) 0xcb, (byte) 0xf7, 0x72, 0x5f,
                0x39, 0x55, 0x06, 0x3e, 0x41, 0x6d, (byte) 0xae, (byte) 0xd6,
                0x13, 0x6d, (byte) 0xab, (byte) 0xd5, (byte) 0xcb, 0x67, (byte) 0xeb, 0x41,
                (byte) 0x87, 0x4f, 0x28, 0x16, 0x59, (byte) 0xe2, (byte) 0xc4, 0x4c 
        };
        return example_signature_value;
    }

    @Function
    public static byte[] randomBytesUTF8(int length) {
        byte[] bytes = new byte[length];
        randomBytesUTF8(bytes, 0, length);
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

    private Functions() {
        // utility
    }
}
