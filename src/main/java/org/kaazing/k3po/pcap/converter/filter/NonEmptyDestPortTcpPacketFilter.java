/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.k3po.pcap.converter.filter;

import org.kaazing.k3po.pcap.converter.packet.Packet;

public class NonEmptyDestPortTcpPacketFilter implements Filter {

    private int port;
    
    public NonEmptyDestPortTcpPacketFilter(int port) {
        super();
        this.port = port;
    }

    @Override
    public boolean passesFilter(Packet pc) throws FilterFailureException {
        if(!pc.isTcp())
            return false;
        if(pc.getTcpPayloadSize() < 1)
            return false;
        if(!(pc.getTcpDestPort() == port))
            return false;
        return true;
    }
}
