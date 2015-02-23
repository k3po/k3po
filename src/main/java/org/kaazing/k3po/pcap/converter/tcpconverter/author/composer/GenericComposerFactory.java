/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.k3po.pcap.converter.tcpconverter.author.composer;

import org.kaazing.k3po.pcap.converter.tcpconverter.author.SupportedProtocol;
import org.kaazing.k3po.pcap.converter.tcpconverter.author.SupportedProtocolException;
import org.kaazing.k3po.pcap.converter.tcpconverter.author.emitter.EmitterFactory;
import org.kaazing.k3po.pcap.converter.tcpconverter.author.emitter.OutputType;

public class GenericComposerFactory implements ComposerFactory {
    private EmitterFactory emitterFactory;
    private boolean verbose = true;

    public GenericComposerFactory(EmitterFactory emitterFactory) {
        this.emitterFactory = emitterFactory;
    }

    public GenericComposerFactory(EmitterFactory emitterFactory, boolean verbose) {
        this(emitterFactory);
        this.verbose = verbose;
    }
    @Override
    public AbstractComposer getComposer(SupportedProtocol sp, ComposerType et, String endpointIp,
            String identifier) {
        if ( sp == SupportedProtocol.TCP ) {
            if ( et == ComposerType.CLIENT ) {
                if(verbose){
                    return new VerboseTcpClientComposer(emitterFactory, emitterFactory.getRptScriptEmitter(
                            OutputType.TCP_CLIENT_COMPOSER, identifier), endpointIp);
                }
                return new TcpClientComposer(emitterFactory, emitterFactory.getRptScriptEmitter(
                        OutputType.TCP_CLIENT_COMPOSER, identifier), endpointIp);
            }
            else if ( et == ComposerType.SERVER ) {
                if(verbose){
                    return new VerboseTcpServerComposer(emitterFactory, emitterFactory.getRptScriptEmitter(
                            OutputType.TCP_SERVER_COMPOSER, identifier), endpointIp);
                }
                return new TcpServerComposer(emitterFactory, emitterFactory.getRptScriptEmitter(
                        OutputType.TCP_SERVER_COMPOSER, identifier), endpointIp);
            }
            else {
                throw new ComposerTypeException("Endpoint type of " + et + " not supported with " + sp);
            }

        }
        else {
            throw new SupportedProtocolException("Supported Protocol has unimplemented method 'getComposer', protocol:"
                    + sp);
        }
    }

}
