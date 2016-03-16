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
package org.kaazing.k3po.driver.internal.buffer;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferIndexFinder;

/**
 * Use the Knuth-Morris-Pratt (KMP) pattern matching algorithm for matching a
 * byte array (pattern or "word") within a given byte array ("text"):
 *
 * http://en.wikipedia.org/wiki/Knuth%E2%80%93Morris%E2%80%93Pratt_algorithm
 *
 * This is similar to String.contains(), except that we can use it on byte
 * arrays (e.g. hex strings) as well.
 *
 * This particular implementation was found at:
 *
 * http://helpdesk.objects.com.au/java/search-a-byte-array-for-a-byte-sequence
 */
public class FastIndexFinder implements ChannelBufferIndexFinder {

    private byte[] pattern;
    private int[] partialMatch;

    // Generate the "partial match" table (aka the "failure function")
    private void generatePartialMatch() {
        partialMatch = new int[pattern.length];

        int j = 0;

        for (int i = 1; i < pattern.length; i++) {
            while (j > 0 && pattern[j] != pattern[i]) {
                j = partialMatch[j - 1];
            }

            if (pattern[j] == pattern[i]) {
                j++;
            }

            partialMatch[i] = j;
        }
    }

    public FastIndexFinder(byte[] pattern) {
        this.pattern = pattern;

        generatePartialMatch();
    }

    public FastIndexFinder(ChannelBuffer buf) {
        int len = buf.readableBytes();
        pattern = new byte[len];
        buf.getBytes(buf.readerIndex(), pattern);

        generatePartialMatch();
    }

    @Override
    public boolean find(ChannelBuffer buf, int guessedIndex) {
        int j = 0;

        // Due to the structure of the ChannelBufferIndexFinder API (i.e.
        // a single method which only returns true/false, not the index at
        // which a match is found), we have to have this simplistic check
        // first.
        if (buf.getByte(guessedIndex) != pattern[j]) {
            return false;
        }

        for (int i = guessedIndex; i < buf.readableBytes(); i++) {
            while (j > 0 && pattern[j] != buf.getByte(i)) {
                j = partialMatch[j - 1];
                // Same problem as previous comment. If we have to go back and
                // look for the first character in the pattern
                // then we have to return false.
                if (j == 0) {
                    return false;
                }
            }

            if (pattern[j] == buf.getByte(i)) {
                j++;
            }

            if (j == pattern.length) {
                return true;
            }
        }

        return false;
    }
}
