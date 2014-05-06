/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.util;

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

    private Utils() {
        // utility class
    }
}
