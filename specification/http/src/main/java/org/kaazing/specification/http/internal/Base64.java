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

/**
 * Internal class. This class manages the Base64 encoding and decoding
 */
final class Base64 {

    private static final byte[] INDEXED = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".getBytes();
    private static final byte PADDING_BYTE = (byte) '=';

    public static byte[] encode(byte[] decodedArray) {
        int decodedSize = decodedArray.length;
        int effectiveDecodedSize = ((decodedSize + 2) / 3) * 3;
        int decodedFragmentSize = decodedSize % 3;

        int encodedArraySize = effectiveDecodedSize / 3 * 4;
        byte[] encodedArray = new byte[encodedArraySize];
        int encodedArrayPosition = 0;

        int decodedArrayOffset = 0;
        int decodedArrayPosition = decodedArrayOffset + 0;
        int decodedArrayLimit = decodedArrayOffset + decodedArray.length - decodedFragmentSize;

        while (decodedArrayPosition < decodedArrayLimit) {
            int byte0 = decodedArray[decodedArrayPosition++] & 0xff;
            int byte1 = decodedArray[decodedArrayPosition++] & 0xff;
            int byte2 = decodedArray[decodedArrayPosition++] & 0xff;

            encodedArray[encodedArrayPosition++] = INDEXED[(byte0 >> 2) & 0x3f];
            encodedArray[encodedArrayPosition++] = INDEXED[((byte0 << 4) & 0x30) | ((byte1 >> 4) & 0x0f)];
            encodedArray[encodedArrayPosition++] = INDEXED[((byte1 << 2) & 0x3c) | ((byte2 >> 6) & 0x03)];
            encodedArray[encodedArrayPosition++] = INDEXED[byte2 & 0x3f];
        }

        if (decodedFragmentSize == 1) {
            int byte0 = decodedArray[decodedArrayPosition++] & 0xff;

            encodedArray[encodedArrayPosition++] = INDEXED[(byte0 >> 2) & 0x3f];
            encodedArray[encodedArrayPosition++] = INDEXED[(byte0 << 4) & 0x30];
            encodedArray[encodedArrayPosition++] = PADDING_BYTE;
            encodedArray[encodedArrayPosition++] = PADDING_BYTE;
        }
        else if (decodedFragmentSize == 2) {
            int byte0 = decodedArray[decodedArrayPosition++] & 0xff;
            int byte1 = decodedArray[decodedArrayPosition++] & 0xff;

            encodedArray[encodedArrayPosition++] = INDEXED[(byte0 >> 2) & 0x3f];
            encodedArray[encodedArrayPosition++] = INDEXED[((byte0 << 4) & 0x30) | ((byte1 >> 4) & 0x0f)];
            encodedArray[encodedArrayPosition++] = INDEXED[(byte1 << 2) & 0x3c];
            encodedArray[encodedArrayPosition++] = PADDING_BYTE;
        }

        return encodedArray;
    }

    private Base64() {
        // utility
    }
}

