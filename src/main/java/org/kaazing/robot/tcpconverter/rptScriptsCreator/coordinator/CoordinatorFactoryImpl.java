/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.tcpconverter.rptScriptsCreator.coordinator;

import org.kaazing.robot.tcpconverter.rptScriptsCreator.ConversationId;
import org.kaazing.robot.tcpconverter.rptScriptsCreator.SupportedProtocol;
import org.kaazing.robot.tcpconverter.rptScriptsCreator.SupportedProtocolException;
import org.kaazing.robot.tcpconverter.rptScriptsCreator.composer.ComposerFactory;
import org.kaazing.robot.tcpconverter.rptScriptsCreator.emitter.EmitterFactory;

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
