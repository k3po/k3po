/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.k3po.pcap.converter.tcpconverter.author.coordinator;

import org.kaazing.k3po.pcap.converter.tcpconverter.author.ConversationId;

public interface CoordinatorFactory {

    public Coordinator getCoordinator(ConversationId cID);

}
