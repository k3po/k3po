/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.k3po.pcap.converter.filter;

import org.kaazing.k3po.pcap.converter.packet.Packet;

public class IpFilter implements Filter{

    private String ip;
    
    public IpFilter(String ip){
        super();
        this.ip = ip;
    }
    
    @Override
    public boolean passesFilter(Packet pc) throws FilterFailureException {
        if(!pc.isIp())
            return false;
        if(pc.getSrcIpAddr().equals(ip) || pc.getDestIpAddr().equals(ip)){       
            return true;
        }
        return false;
    }

}
