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
package org.kaazing.k3po.pcap.converter.internal.filter;

import org.kaazing.k3po.pcap.converter.internal.packet.Packet;

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
