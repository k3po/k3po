/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.k3po.pcap.converter.tcpconverter.utils;

public class Util {
    
    public final static String CLOSE_READ = "# close-read";
    public final static String CLOSE_WRITE= "# close-write";
    
    public final static String getHexFromBytes(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("0x%02X ", b));
        }
        return sb.substring(0, sb.length() - 1);
    }
}
