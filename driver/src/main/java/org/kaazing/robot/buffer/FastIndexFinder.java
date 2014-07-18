/*
 * Copyright (c) 2014 "Kaazing Corporation," (www.kaazing.com)
 *
 * This file is part of Robot.
 *
 * Robot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.kaazing.robot.buffer;

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
