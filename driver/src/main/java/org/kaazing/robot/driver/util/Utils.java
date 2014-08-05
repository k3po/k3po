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

package org.kaazing.robot.driver.util;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.List;

public final class Utils {

    public static String byteArrayToString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        // int count = 0;
        for (byte b : bytes) {
            // if( count % 2 == 0 ) {
            // sb.append( " 0x");
            // }
            sb.append(String.format("0x%02x ", b & 0xff));
        }

        return sb.toString();
    }

    public static String readFileIntoString(String path) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
            StringBuilder sb = new StringBuilder();
            for (String line : lines) {
                sb.append(line);
                sb.append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalArgumentException("Error reading from file:" + path);
        }
    }

    private Utils() {
        // utility class
    }
}
