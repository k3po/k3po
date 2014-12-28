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

package org.kaazing.specification.ws;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.kaazing.k3po.lang.el.Function;
import org.kaazing.k3po.lang.el.spi.FunctionMapperSpi;

public final class Functions {

    // See RFC-6455, section 1.3 Opening Handshake
    private static final byte[] WEBSOCKET_GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11".getBytes(UTF_8);
    private static final Random RANDOM = new Random();

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
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) RANDOM.nextInt(0x100);
        }
        return bytes;
    }

    @Function
    public static byte[] randomBytesUTF8(int length) {
        byte[] bytes = new byte[length];
        for (int offset = 0; offset < bytes.length; ) {
            int remaining = bytes.length - offset;
            int width = Math.min(RANDOM.nextInt(5) + 1, remaining);

            switch (width) {
            case 1:
                bytes[offset++] = (byte) RANDOM.nextInt(0x80);
                break;
            case 2:
                bytes[offset++] = (byte) (0xc0 | RANDOM.nextInt(0x20));
                bytes[offset++] = (byte) (0x80 | RANDOM.nextInt(0x40));
                break;
            case 3:
                bytes[offset++] = (byte) (0xe0 | RANDOM.nextInt(0x10));
                bytes[offset++] = (byte) (0x80 | RANDOM.nextInt(0x40));
                bytes[offset++] = (byte) (0x80 | RANDOM.nextInt(0x40));
                break;
            case 4:
                bytes[offset++] = (byte) (0xf0 | RANDOM.nextInt(0x08));
                bytes[offset++] = (byte) (0x80 | RANDOM.nextInt(0x40));
                bytes[offset++] = (byte) (0x80 | RANDOM.nextInt(0x40));
                bytes[offset++] = (byte) (0x80 | RANDOM.nextInt(0x40));
                break;
            }
        }
        return bytes;
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

