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
package org.kaazing.k3po.pcap.converter.internal.author.coordinator;

import org.kaazing.k3po.pcap.converter.internal.author.ConversationId;
import org.kaazing.k3po.pcap.converter.internal.author.SupportedProtocol;
import org.kaazing.k3po.pcap.converter.internal.author.SupportedProtocolException;
import org.kaazing.k3po.pcap.converter.internal.author.composer.ComposerFactory;
import org.kaazing.k3po.pcap.converter.internal.author.emitter.EmitterFactory;

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
