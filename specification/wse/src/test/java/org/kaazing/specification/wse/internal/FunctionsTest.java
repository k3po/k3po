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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FunctionsTest {

    @Test
    public void shouldReturnAllPossibleByteValues() {
        byte[] all = Functions.allBytes();
        assertEquals(256, all.length);
        assertEquals(0, all[0]);
        assertEquals(255, all[255] & 0xFF);
    }

    @Test
    public void shouldEncodeAndDecodeAllBytes() {
        byte[] all = Functions.allBytes();
        byte[] encoded = Functions.encodeBytesAsUtf8(all);
        byte[] decoded = Functions.decodeUtf8Bytes(encoded);
        assertEquals(128 * 3, encoded.length);
        assertArrayEquals(all, decoded);
    }

}
