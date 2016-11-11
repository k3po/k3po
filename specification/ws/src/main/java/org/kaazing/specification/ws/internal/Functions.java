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
package org.kaazing.specification.ws.internal;

import static java.lang.Character.toLowerCase;
import static java.lang.Character.toUpperCase;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

import org.kaazing.k3po.lang.el.Function;
import org.kaazing.k3po.lang.el.spi.FunctionMapperSpi;

public final class Functions {

    // See RFC-6455, section 1.3 Opening Handshake
    private static final byte[] WEBSOCKET_GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11".getBytes(UTF_8);
    private static final Random RANDOM = new Random();
    private static final int MAX_ACCEPTABLE_HEADER_LENGTH = 200;

    @Function
    public static String base64Encode(String login) {
        byte[] bytes = login.getBytes();
        return new String(Base64.encode(bytes));
    }

    @Function
    public static String append(String... strings) {
        StringBuilder x = new StringBuilder();
        for (String s:strings) {
            x.append(s);
        }
        return x.toString();
    }

    @Function
    public static byte[] handshakeKey() {
        byte[] bytes = new byte[16];
        RANDOM.nextBytes(bytes);
        return Base64.encode(bytes);
    }

    @Function
    public static byte[] handshakeHash(byte[] wsKeyBytes) throws NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        sha1.update(wsKeyBytes);
        byte[] digest = sha1.digest(WEBSOCKET_GUID);
        return Base64.encode(digest);
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

    @Function
    public static byte[] randomBytesUnalignedUTF8(int length, int unalignAt) {
        assert unalignAt < length;

        byte[] bytes = new byte[length];
        int straddleWidth = RANDOM.nextInt(3) + 2;
        int straddleAt = unalignAt - straddleWidth + 1;
        randomBytesUTF8(bytes, 0, straddleAt);
        int realignAt = randomCharBytesUTF8(bytes, straddleAt, straddleWidth);
        randomBytesUTF8(bytes, realignAt, length);
        return bytes;
    }

    @Function
    public static byte[] copyOfRange(byte[] original, int from, int to) {
        return Arrays.copyOfRange(original, from, to);
    }

    /**
     * Takes a string and randomizes which letters in the text are upper or
     * lower case
     * @param text
     * @return
     */
    @Function
    public static String randomizeLetterCase(String text) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (RANDOM.nextBoolean()) {
                c = toUpperCase(c);
            } else {
                c = toLowerCase(c);
            }
            result.append(c);
        }
        return result.toString();
    }

    @Function
    public static String randomHeaderNot(String header) {
        // random strings from bytes can generate random bad chars like \n \r \f \v etc which are not allowed
        // except under special conditions, and will crash the http pipeline
        String commonHeaderChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "1234567890!@#$%^&*()_+-=`~[]\\{}|;':\",./<>?";
        StringBuilder result = new StringBuilder();
        do {
            int randomHeaderLength = RANDOM.nextInt(MAX_ACCEPTABLE_HEADER_LENGTH) + 1;
            for (int i = 0; i < randomHeaderLength; i++) {
                result.append(commonHeaderChars.charAt(RANDOM.nextInt(commonHeaderChars.length())));
            }
        } while (result.toString().equalsIgnoreCase(header));
        return result.toString();
    }

    @Function
    public static String randomCaseNot(String value) {
        String result;
        char[] resultChars = new char[value.length()];

        do {
            for (int i = 0; i < value.length(); i++) {
                char c = value.charAt(i);
                resultChars[i] = RANDOM.nextBoolean() ? toUpperCase(c) : toLowerCase(c);
            }
            result = new String(resultChars);
        } while(!result.equals(value));

        return result;
    }

    @Function
    public static String randomMethodNot(String method) {
        String[] methods = new String[]{"GET", "OPTIONS", "HEAD", "POST", "PUT", "DELETE", "TRACE", "CONNECT"};
        String result;
        do {
            result = methods[RANDOM.nextInt(methods.length)];
        } while (result.equalsIgnoreCase(method));
        return result;
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
            return "ws";
        }

    }

    private Functions() {
        // utility
    }

}

