/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.k3po.pcap.converter.author.composer;

import org.kaazing.k3po.pcap.converter.author.SupportedProtocol;

public interface ComposerFactory {

    Composer getComposer(SupportedProtocol sp, ComposerType et, String endpointIp, String identifier);

}
