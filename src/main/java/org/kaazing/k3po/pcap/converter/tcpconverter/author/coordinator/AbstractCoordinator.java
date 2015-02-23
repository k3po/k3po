/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.k3po.pcap.converter.tcpconverter.author.coordinator;

import org.kaazing.k3po.pcap.converter.tcpconverter.author.ConversationId;
import org.kaazing.k3po.pcap.converter.tcpconverter.author.composer.ComposerFactory;
import org.kaazing.k3po.pcap.converter.tcpconverter.author.emitter.Emitter;
import org.kaazing.k3po.pcap.converter.tcpconverter.author.emitter.EmitterFactory;

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
