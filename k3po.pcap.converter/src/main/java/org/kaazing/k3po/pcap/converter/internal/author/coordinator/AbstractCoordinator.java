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
import org.kaazing.k3po.pcap.converter.internal.author.composer.ComposerFactory;
import org.kaazing.k3po.pcap.converter.internal.author.emitter.Emitter;
import org.kaazing.k3po.pcap.converter.internal.author.emitter.EmitterFactory;

public abstract class AbstractCoordinator implements Coordinator {
    protected final ConversationId conversationId;
    protected final Emitter ip1Emitter;
    protected final Emitter ip2Emitter;
    protected final String ipAddr1;
    protected final String ipAddr2;
    protected final EmitterFactory emitterFactory;
    protected final ComposerFactory composerFactory;

    protected AbstractCoordinator(EmitterFactory emitterFactory, Emitter ip1Emitter, Emitter ip2Emitter, 
            ConversationId conversationId, ComposerFactory composerFactory) {
        this.ip1Emitter = ip1Emitter;
        this.ip2Emitter = ip2Emitter;
        this.conversationId = conversationId;
        this.emitterFactory = emitterFactory;
        this.composerFactory = composerFactory;
        ipAddr1 = conversationId.getIpAddr1();
        ipAddr2 = conversationId.getIpAddr2();
    }
    
    public final Emitter addScriptToEmitter(Emitter emitter, String ip, String protocol){
        if(ip.equals(ipAddr1)){
            emitter.add(ip1Emitter.getBuffer());
        }
        if(ip.equals(ipAddr2)){
            emitter.add(ip1Emitter.getBuffer());
        }
        return emitter;
    }
}
