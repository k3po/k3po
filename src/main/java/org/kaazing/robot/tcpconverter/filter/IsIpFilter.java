/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.tcpconverter.filter;

import org.kaazing.robot.tcpconverter.packet.Packet;

public class IsIpFilter implements Filter{

    public IsIpFilter(){
        super();
    }
    
    @Override
    public boolean passesFilter(Packet pc) throws FilterFailureException {
        return pc.isIp();
    }

}
