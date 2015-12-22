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

package org.kaazing.specification.wse.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.kaazing.k3po.lang.el.Function;
import org.kaazing.k3po.lang.el.spi.FunctionMapperSpi;

public final class Functions {
    private static final Random RANDOM = new Random();

    @Function
    public static byte[] uniqueId() {
        byte[] bytes = new byte[16];
        RANDOM.nextBytes(bytes);
        return Base64.encode(bytes);
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
            if (i / 2 == numberOfEscapedBytesToInclude) {
                bytes[i] = escapedBytes[RANDOM.nextInt(escapedBytes.length)];
                numberOfEscapedBytesToInclude--;
            } else {
                byte randomByte = (byte) RANDOM.nextInt(0x100);
                if (numberOfEscapedBytesToInclude > 0 && Arrays.asList(escapedBytes).contains(randomByte)) {
                    bytes[i] = randomByte;
                    numberOfEscapedBytesToInclude--;
                } else {
                    bytes[i] = randomByte;
                }
            }
        }
        return bytes;
    }

    @Function
    public static byte[] escapeBytes(byte[] bytes) {
        System.out.println("++++++++++1" + bytes.length);
        List<Byte> listOfEscapedBytes = new ArrayList<Byte>();
        byte[] escapedBytes = {0b00000000, 0b00001101, 0b00001010, 0b01111111};
        for (int i = 0; i < bytes.length; i++) {
            if (Arrays.asList(escapedBytes).contains(bytes[i])) {
                listOfEscapedBytes.add(new Byte((byte) 0b01111111));
            }
            listOfEscapedBytes.add(new Byte(bytes[i]));
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
