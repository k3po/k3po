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
package org.kaazing.k3po.pcap.converter.internal.author.composer;

import org.kaazing.k3po.pcap.converter.internal.author.SupportedProtocol;
import org.kaazing.k3po.pcap.converter.internal.author.SupportedProtocolException;
import org.kaazing.k3po.pcap.converter.internal.author.emitter.EmitterFactory;
import org.kaazing.k3po.pcap.converter.internal.author.emitter.OutputType;

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
