/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.k3po.pcap.converter.author.coordinator;

import org.kaazing.k3po.pcap.converter.author.ConversationId;

public interface CoordinatorFactory {

    public Coordinator getCoordinator(ConversationId cID);

}
