/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.k3po.pcap.converter.utils;

public class WireSharkLocation {
    // If running from ide (i.e. eclipse) may need to specify direct location
    public static final String wiresharkLocation = "tshark";
    //public static final String wiresharkLocation = "/Users/David/Applications/Wireshark.app/Contents/Resources/bin/tshark";
    public String getWiresharkLoc(){
        return wiresharkLocation;
    }
}
