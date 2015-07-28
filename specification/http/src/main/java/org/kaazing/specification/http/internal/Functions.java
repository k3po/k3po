/**
 * Copyright (c) 2007-2014 Kaazing Corporation. All rights reserved.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.kaazing.specification.http.internal;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.kaazing.k3po.lang.el.Function;
import org.kaazing.k3po.lang.el.spi.FunctionMapperSpi;

public final class Functions {

    private static final Random RANDOM = new Random();

    @Function
    public static String loginBase64Encoder(String login) {
        byte[] bytes = login.getBytes();
        return bytesToString(Base64.encode(bytes));
    }

    @Function
    public static String append(String... strings) {
        StringBuilder x = new StringBuilder();
        for (String s:strings) {
            x.append(s);
        }
        return x.toString();
    }

    private static String bytesToString(byte[] x) {
        String answer = "";
        for (int i = 0; i < x.length; i++) {
            answer += (char) x[i];
        }
        return answer;
    }

    @Function
    public static String randomInvalidVersion() {
        String randomVersion = null;
        Pattern validVersionPattern = Pattern.compile("HTTP/1\\.(\\d)+");
        Matcher validVersionMatcher = null;
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" + "1234567890!@#$%^&*()_+-=`~[]\\{}|;':\",./<>?";
        StringBuilder result;
        do {
            result = new StringBuilder();
            int randomLength = RANDOM.nextInt(30) + 1;
            for (int i = 0; i < randomLength; i++) {
                result.append(chars.charAt(RANDOM.nextInt(chars.length())));
            }
            randomVersion = result.toString();
            validVersionMatcher = validVersionPattern.matcher(randomVersion);
        } while (randomVersion.length() > 1 && validVersionMatcher.matches());
        return randomVersion;
    }

    @Function
    public static byte[] randomAscii(int length) {
        Random r = new Random();
        byte[] result = new byte[length];
        String alphabet =
                "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" + "1234567890!@#$%^&*()_+-=`~[]\\{}|;':\",./<>?";
        for (int i = 0; i < length; i++) {
            result[i] = (byte) alphabet.charAt(r.nextInt(alphabet.length()));
        }
        return result;
    }

    public static class Mapper extends FunctionMapperSpi.Reflective {

        public Mapper() {
            super(Functions.class);
        }

        @Override
        public String getPrefixName() {
            return "http";
        }
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
