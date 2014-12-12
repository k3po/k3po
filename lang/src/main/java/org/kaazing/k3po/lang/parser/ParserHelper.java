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

package org.kaazing.k3po.lang.parser;

import java.util.Arrays;

/**
 * This helper class implements static methods used only by the parser.
 */
public final class ParserHelper {

    public static byte[] parseHexBytes(String data) {
        if (data.startsWith("[") &&
            data.endsWith("]")) {

            // Trim off the enclosing square brackets
            data = data.substring(1, data.length() - 1);
        }

        final int inlen = data.length();
        byte[] out = new byte[inlen / 2];
        int outlen = 0;

        final char[] chars = data.toCharArray();
        for (int i = 0, j = 0; i < inlen; i++) {
            // Skip the leading 0x/0X prefix
            if (chars[i] == '0' &&
                (chars[i + 1] == 'x' || chars[i + 1] == 'X')) {
                continue;
            }

            // Skip non-hex-digits
            if (!isHexDigit(chars[i])) {
              continue;
            }

            out[j++] = (byte) ((Character.digit(chars[i], 16) << 4) +
                                Character.digit(chars[i + 1], 16));
            outlen++;

            // We consumed two digits here, so increment the index by one
            // (the loop handler will increment by one as well)
            i += 1;
        }

        return Arrays.copyOf(out, outlen);
    }

    private static boolean isHexDigit(char c) {
        switch (c) {
        case 'a':
        case 'b':
        case 'c':
        case 'd':
        case 'e':
        case 'f':
        case 'A':
        case 'B':
        case 'C':
        case 'D':
        case 'E':
        case 'F':
        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
            return true;
        default:
            return false;
        }
    }

    private ParserHelper() {
        // utility class
    }
}
