/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.tcpconverter.rptScriptsCreator;

import java.util.Arrays;

import org.apache.commons.lang.builder.HashCodeBuilder;

import org.kaazing.robot.tcpconverter.packet.Packet;

/**
 * Helper class that generates unique conversation ids, and allows those ids to be used as hash keys
 * 
 */
public class ConversationId {
    private final String ipAddr1;
    private final String ipAddr2;
    private final String id;
    private final SupportedProtocol protocol;

    public ConversationId(String ipAddr1, String ipAddr2, SupportedProtocol protocol) {
        super();
        if ( ipAddr1.compareTo(ipAddr2) > 0 ) {
            this.ipAddr1 = ipAddr1;
            this.ipAddr2 = ipAddr2;
        } else {
            this.ipAddr1 = ipAddr2;
            this.ipAddr2 = ipAddr1;
        }
        this.protocol = protocol;
        id = this.ipAddr1 + "-" + this.ipAddr2;
    }

    public ConversationId(Packet pc, SupportedProtocol protocol) {
        this(pc.getDestIpAddr(), pc.getSrcIpAddr(), protocol);

    }

    public String getId() {
        return id;
    }

    public String getIpAddr1() {
        return ipAddr1;
    }

    public String getIpAddr2() {
        return ipAddr2;
    }

    public boolean equals(Object obj) {
        ConversationId rcn = (ConversationId) obj;
        if ( obj == null ) {
            return false;
        }
        if ( obj == this ) {
            return true;
        }
        if ( obj.getClass() != getClass() ) {
            return false;
        }
        if ( id.equals(rcn.getId()) ) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        char[] chars = id.toCharArray();
        Arrays.sort(chars);
        String sorted = new String(chars) + protocol;
        int hash = new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
                append(sorted).toHashCode();
        return hash;
    }

    public String getName() {
        return protocol + "-" + id;
    }

    @Override
    public String toString() {
        return String.format(getName());
    }

    public SupportedProtocol getProtocol() {
        return protocol;
    }
}
