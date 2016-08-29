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
package org.kaazing.specification.turn.internal;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.kaazing.k3po.lang.el.Function;
import org.kaazing.k3po.lang.el.spi.FunctionMapperSpi;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public final class Functions {

    private static final Random RANDOM = new Random();

    @Function
    public static byte[] portXOR(int port) {
        byte[] x = new byte[2];
        byte one = (byte)(port >> 8);
        byte two = (byte)(port);
        x[0] = (byte)(one ^ 0x21); // magic cookie byte 1
        x[1] = (byte)(two ^ 0x12); // magic cookie byte 2
        return x;
    }

    @Function
    public static byte[] ipXOR(String ip) {
        byte[] x = new byte[4];
        int[] temp = new int[4];
        byte[] temp2 = new byte[4];

        String[] string = ip.split("\\.");
        for (int i = 0; i < temp.length; i++) {
            temp2[i] = (byte)Integer.parseInt(string[i]);
        }

        byte magic_cookie_0 = (byte)0x21;
        byte magic_cookie_1 = (byte)0x12;
        byte magic_cookie_2 = (byte)0xA4;
        byte magic_cookie_3 = (byte)0x42;

        x[0] = (byte) (magic_cookie_0 ^ temp2[0]);
        x[1] = (byte) (magic_cookie_1 ^ temp2[1]);
        x[2] = (byte) (magic_cookie_2 ^ temp2[2]);
        x[3] = (byte) (magic_cookie_3 ^ temp2[3]);

        return x;
    }

    @Function
    public static byte[] ipV6XOR(String ip, byte[] transactionId) {
        InetAddress address;
        try {
            address = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            e.printStackTrace(); // TODO replace with log if available
            return null;
        }

        if (!(address instanceof Inet6Address)) {
            System.out.println("Address must be IPv6."); // TODO replace with log if available
            return null;
        }

        byte[] ipAddr = address.getAddress();
        byte[] x = new byte[16];
        x[0] = (byte) ((byte)0x21 ^ ipAddr[0]);
        x[1] = (byte) ((byte)0x12 ^ ipAddr[1]);
        x[2] = (byte) ((byte)0xA4 ^ ipAddr[2]);
        x[3] = (byte) ((byte)0x42 ^ ipAddr[3]);
        for (int i = 4; i < 16; i++) {
            x[i] = (byte) (transactionId[i-4] ^ ipAddr[i]);
        }
        return x;
    }


    @Function
    public static byte[] messageDigestMD5Encoding(String in) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }

        md.update((in).getBytes());

        byte[] mdbytes = md.digest();

        return mdbytes;
    }

    @Function
    public static byte[] messageDigestHMACEncoding(String in) {
        Mac hmac;
        try {
            hmac = Mac.getInstance("HmacSHA1");
            SecretKeySpec signingKey = new SecretKeySpec("SecretKey".getBytes(), "HmacSHA1");
            hmac.init(signingKey);
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (InvalidKeyException e) {
            return null;
        }
        return hmac.doFinal(in.getBytes());
    }

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
        for (byte aX : x) {
            answer += (char) aX;
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

    @Function
    public static byte[] generateTransactionId() {
        byte[] bytes = new byte[12];
        for (int i = 0; i < 12; i++) {
            bytes[i] = (byte) RANDOM.nextInt(0x100);
        }
        return bytes;
    }

    public static class Mapper extends FunctionMapperSpi.Reflective {

        public Mapper() {
            super(Functions.class);
        }

        @Override
        public String getPrefixName() {
            return "turn";
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
