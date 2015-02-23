/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.k3po.pcap.converter.tcpconverter.author.composer;

import org.kaazing.k3po.pcap.converter.tcpconverter.author.SupportedProtocol;

public interface ComposerFactory {

    Composer getComposer(SupportedProtocol sp, ComposerType et, String endpointIp, String identifier);

}
