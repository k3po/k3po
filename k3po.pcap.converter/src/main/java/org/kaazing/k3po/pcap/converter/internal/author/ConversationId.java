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
package org.kaazing.k3po.pcap.converter.internal.author;

import java.util.Arrays;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.kaazing.k3po.pcap.converter.internal.packet.Packet;

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
