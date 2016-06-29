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
package org.kaazing.k3po.driver.internal.util;

import org.jboss.netty.buffer.ChannelBuffer;

public final class Utils {

    public static String byteArrayToString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            if (i > 0) {
                sb.append(' ');
            }
            sb.append(String.format("0x%02x ", bytes[i] & 0xff));
        }

        return sb.toString();
    }

    public static String format(ChannelBuffer observed) {
        if (!observed.readable()) {
            return "[]";
        }
        else {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (int readerIndex = observed.readerIndex(); readerIndex < observed.writerIndex(); readerIndex++) {
                sb.append(String.format("0x%02x ", observed.getByte(readerIndex)));
            }
            sb.setCharAt(sb.length() - 1, ']');
            return sb.toString();
        }
    }

    public static String format(byte[] observed) {
        if (observed.length == 0) {
            return "[]";
        }
        else {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (byte anObserved : observed) {
                sb.append(String.format("0x%02x ", anObserved));
            }
            sb.setCharAt(sb.length() - 1, ']');
            return sb.toString();
        }
    }

    private Utils() {
        // utility class
    }
}
