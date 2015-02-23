/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.k3po.pcap.converter.tcpconverter.author.coordinator;

import org.kaazing.k3po.pcap.converter.tcpconverter.author.ConversationId;
import org.kaazing.k3po.pcap.converter.tcpconverter.author.SupportedProtocol;
import org.kaazing.k3po.pcap.converter.tcpconverter.author.SupportedProtocolException;
import org.kaazing.k3po.pcap.converter.tcpconverter.author.composer.ComposerFactory;
import org.kaazing.k3po.pcap.converter.tcpconverter.author.emitter.EmitterFactory;

public class CoordinatorFactoryImpl implements CoordinatorFactory {
    private ComposerFactory composerFactory;
    private EmitterFactory emitterFactory;
    private boolean verbose = true;

    public CoordinatorFactoryImpl(ComposerFactory composerFactoryImpl, EmitterFactory emitterFactory) {
        this.composerFactory = composerFactoryImpl;
        this.emitterFactory = emitterFactory;
    }

    @Override
    public Coordinator getCoordinator(ConversationId cID) {
        if ( cID.getProtocol() == SupportedProtocol.TCP ) {
            if(verbose){
                return new VerboseTcpCoordinator(emitterFactory, cID, composerFactory);
            }
            return new TcpCoordinator(emitterFactory, cID, composerFactory);
        }
        else {
            throw new SupportedProtocolException(
                    "Supported Protocol has unimplemented method 'getCoordinator', protocol:" + cID.getProtocol());
        }
    }

}
