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

public enum Encoding {
    UTF8 {
        public byte[] encode(byte[] decoded) {
            return encodeBinaryAsText(decoded);
        }

        public byte[] decode(byte[] encoded) {
            return decodeTextAsBinary(encoded);
        }
    };

    public abstract byte[] encode(byte[] decoded);

    public abstract byte[] decode(byte[] encoded);

    private static byte[] encodeBinaryAsText(byte[] decodedArray) {
        int decodedArrayPosition = 0;

        byte[] encodedArray = decodedArray;
        int candidateEncodedArrayLength = decodedArray.length;
        int encodedDataLength = 0;
        for (; decodedArrayPosition < decodedArray.length; decodedArrayPosition++) {
            byte decodedValue = decodedArray[decodedArrayPosition];

            if ((decodedValue & 0x80) != 0) {
                // high bit set, requires multi-byte representation in UTF8
                candidateEncodedArrayLength++;
                byte[] tempArray = new byte[candidateEncodedArrayLength];
                System.arraycopy(encodedArray, 0, tempArray, 0,
                        encodedDataLength);
                byte encodedByte0 = (byte) ((((decodedValue & 0xff) >> 6) & 0x03) | 0xc0);
                byte encodedByte1 = (byte) (decodedValue & 0xbf);
                tempArray[encodedDataLength++] = encodedByte0;
                tempArray[encodedDataLength++] = encodedByte1;
                encodedArray = tempArray;
            } else if (encodedArray != null) {
                encodedArray[encodedDataLength++] = decodedValue;
            }
        }
        return encodedArray;
    }

    private static byte[] decodeTextAsBinary(byte[] encodedArray) {
        int encodedArrayPosition = 0;

        byte[] candidateDecodedArray = new byte[encodedArray.length];
        int decodedDataLength = 0;

        for (; encodedArrayPosition < encodedArray.length; encodedArrayPosition++) {
            byte encodedByte1 = encodedArray[encodedArrayPosition];
            if ((encodedByte1 & 0x80) != 0) {
                byte encodedByte2 = encodedArray[++encodedArrayPosition];
                byte decodedByte = (byte) ((encodedByte1 << 6) | (encodedByte2 & 0x3f));
                candidateDecodedArray[decodedDataLength++] = decodedByte;
            } else {
                candidateDecodedArray[decodedDataLength++] = encodedByte1;
            }
        }
        byte[] decodedArray;
        if (decodedDataLength == encodedArray.length) {
            decodedArray = candidateDecodedArray;
        } else {
            decodedArray = new byte[decodedDataLength];
            System.arraycopy(candidateDecodedArray, 0, decodedArray, 0,
                    decodedDataLength);
        }
        return decodedArray;
    }
}
