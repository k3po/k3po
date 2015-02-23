/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.k3po.pcap.converter.filter;

import org.kaazing.k3po.pcap.converter.packet.Packet;

/**
 * Filter interface for filters that can check whether a Packet matches 
 * specified criteria
 *
 */
public interface Filter {
    public boolean passesFilter(Packet pc) throws FilterFailureException;
}
