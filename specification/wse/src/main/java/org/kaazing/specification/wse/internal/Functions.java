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
package org.kaazing.specification.wse.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.kaazing.k3po.lang.el.Function;
import org.kaazing.k3po.lang.el.spi.FunctionMapperSpi;

public final class Functions {
    private static final Random RANDOM = new Random();

    private static final byte[] allBytes = new byte[256];

    private static final Byte BYTE_7F = new Byte((byte) 0x7f);

    static {
        for (int i = 0; i < 256; i++) {
            allBytes[i] = (byte) i;
        }
    }

    @Function
    public static byte[] uniqueId() {
        byte[] bytes = new byte[16];
        RANDOM.nextBytes(bytes);
        return Base64.encode(bytes);
    }

    @Function
    public static byte[] allBytes() {
        byte[] bytes = new byte[256];
        for (int i = 0; i < 256; i++) {
            bytes[i] = (byte) i;
        }
        return bytes;
    }

    @Function
    public static byte[] randomBytes(int length) {
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            bytes[i] = (byte) RANDOM.nextInt(0x100);
        }
        return bytes;
    }

    @Function
    public static byte[] randomBytesIncludingNumberOfEscapedBytes(int length, int numberOfEscapedBytesToInclude) {
        byte[] bytes = new byte[length];
        byte[] escapedBytes = {0b00000000, 0b00001101, 0b00001010, 0b01111111};

        for (int i = 0; i < length; i++) {
            if ((length - i) / 2 < numberOfEscapedBytesToInclude) {
                bytes[i] = escapedBytes[RANDOM.nextInt(escapedBytes.length)];
                numberOfEscapedBytesToInclude--;
            } else {
                byte randomByte = (byte) RANDOM.nextInt(100);
                switch (randomByte) {
                case 0b00000000:
                case 0b00001101:
                case 0b00001010:
                case 0b01111111:
                    if (numberOfEscapedBytesToInclude > 0) {
                        bytes[i] = randomByte;
                        numberOfEscapedBytesToInclude--;
                    } else {
                        i--;
                    }
                    break;
                default:
                    bytes[i] = randomByte;

                }
            }
        }
        return bytes;
    }

    @Function
    public static byte[] convertEscapedUtf8BytesToEscapedWindows1252(byte[] in) {
        byte[] out = new byte[in.length];

        for (int i = 0; i < in.length; i++) {
            out[i] = in[i] == 0x00 ? 0x30 : in[i];
        }
        return out;
    }

    @Function
    public static byte[] decodeUtf8Bytes(byte[] bytes) {
        return Encoding.UTF8.decode(bytes);
    }

    @Function
    public static byte[] encodeBytesAsUtf8(byte[] bytes) {
        return Encoding.UTF8.encode(bytes);
    }

    @Function
    public static byte[] escapeBytesForUtf8(byte[] bytes) {
        List<Byte> listOfEscapedBytes = new ArrayList<Byte>();
        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];
            switch (b) {
            case 0x00:
                listOfEscapedBytes.add(BYTE_7F);
                listOfEscapedBytes.add(new Byte((byte) 0x00));
                break;
            case 0x0a:
                listOfEscapedBytes.add(BYTE_7F);
                listOfEscapedBytes.add(new Byte((byte) 0x6e));
                break;
            case 0x0d:
                listOfEscapedBytes.add(BYTE_7F);
                listOfEscapedBytes.add(new Byte((byte) 0x72));
                break;
            case 0x7f:
                listOfEscapedBytes.add(BYTE_7F);
                listOfEscapedBytes.add(BYTE_7F);
                break;
            default:
                listOfEscapedBytes.add(new Byte(b));
            }
        }
        bytes = new byte[listOfEscapedBytes.size()];
        for (int i = 0; i < listOfEscapedBytes.size(); i++) {
            bytes[i] = listOfEscapedBytes.get(i);
        }
        return bytes;
    }

    @Function
    public static byte[] escapeBytesForWindows1252(byte[] bytes) {
        List<Byte> listOfEscapedBytes = new ArrayList<Byte>();
        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];
            switch (b) {
            case 0x00:
                listOfEscapedBytes.add(BYTE_7F);
                listOfEscapedBytes.add(new Byte((byte) 0x30));
                break;
            case 0x0a:
                listOfEscapedBytes.add(BYTE_7F);
                listOfEscapedBytes.add(new Byte((byte) 0x6e));
                break;
            case 0x0d:
                listOfEscapedBytes.add(BYTE_7F);
                listOfEscapedBytes.add(new Byte((byte) 0x72));
                break;
            case 0x7f:
                listOfEscapedBytes.add(BYTE_7F);
                listOfEscapedBytes.add(BYTE_7F);
                break;
            default:
                listOfEscapedBytes.add(new Byte(b));
            }
        }
        bytes = new byte[listOfEscapedBytes.size()];
        for (int i = 0; i < listOfEscapedBytes.size(); i++) {
            bytes[i] = listOfEscapedBytes.get(i);
        }
        return bytes;
    }

    @Function
    public static byte[] padding(int size) {
        if ((size & 1) != 0) {
            size += 1;
        }
        byte[] padding = new byte[size + 2];
        padding[0] = 0x01;
        for (int i = 1; i < padding.length - 2; i += 2) {
            padding[i] = 0x30;
            padding[i + 1] = 0x30;
        }

        padding[padding.length - 1] = (byte) 0xFF;
        return padding;
    }

    @Function
    public static int randomInt(int upperBound) {
        return RANDOM.nextInt(upperBound);
    }

    @Function
    public static String asString(int value) {
        return Integer.toString(value);
    }

    @Function
    public static String append(String... strings) {
        StringBuilder result = new StringBuilder();
        for (String string : strings) {
            result.append(string);
        }
        return result.toString();
    }

    public static class Mapper extends FunctionMapperSpi.Reflective {

        public Mapper() {
            super(Functions.class);
        }

        @Override
        public String getPrefixName() {
            return "wse";
        }

    }

    private Functions() {
        // utility
    }
}
