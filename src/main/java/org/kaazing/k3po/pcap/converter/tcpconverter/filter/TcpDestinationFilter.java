/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.k3po.pcap.converter.tcpconverter.filter;

import org.kaazing.k3po.pcap.converter.tcpconverter.packet.Packet;

/**
 * Simple tcp filter that can be used to filter packets based on destination ip and port
 *
 */
public class TcpDestinationFilter implements Filter {

    private Integer port;
    private String ip;
    
    public TcpDestinationFilter(int port, String ip) {
        super();
        this.port = port;
        this.ip = ip;
    }

    @Override
    public boolean passesFilter(Packet pc) throws FilterFailureException {
        if(!pc.isTcp())
            return false;
        if(pc.getTcpPayloadSize() < 1)
            return false;
        if(!(pc.getTcpDestPort() == port))
            return false;
        if(!(pc.getDestIpAddr().equals(ip)))
            return false;
        return true;
    }
}
