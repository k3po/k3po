/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.k3po.pcap.converter.tcpconverter;

import org.kaazing.k3po.pcap.converter.tcpconverter.author.ConversationId;
import org.kaazing.k3po.pcap.converter.tcpconverter.author.SupportedProtocol;
import org.kaazing.k3po.pcap.converter.tcpconverter.author.composer.ComposerFactory;
import org.kaazing.k3po.pcap.converter.tcpconverter.author.composer.Composer;
import org.kaazing.k3po.pcap.converter.tcpconverter.author.composer.ComposerType;
import org.kaazing.k3po.pcap.converter.tcpconverter.author.emitter.Emitter;
import org.kaazing.k3po.pcap.converter.tcpconverter.author.emitter.EmitterFactory;
import org.kaazing.k3po.pcap.converter.tcpconverter.author.emitter.OutputType;

public class TcpCoordinatorExpectations extends org.jmock.Expectations {
    public void willInit(EmitterFactory emitterFactory, ConversationId conversationId, ComposerFactory composerFactory,
            Emitter ipEmitter1, Emitter ipEmitter2) {

        oneOf(emitterFactory).getRptScriptEmitter(OutputType.TCP_COORDINATOR, conversationId.getIpAddr1());
        will(returnValue(ipEmitter1));
        oneOf(emitterFactory).getRptScriptEmitter(OutputType.TCP_COORDINATOR, conversationId.getIpAddr2());
        will(returnValue(ipEmitter2));
        
    }

    public void willCreateTcpClientComposer(ComposerFactory composerFactory, String srcIpAddr, String name,
            Composer clientComp) {
        oneOf(composerFactory).getComposer(with(equal(SupportedProtocol.TCP)), with(equal(ComposerType.CLIENT)),
                with(equal(srcIpAddr)), with(equal(name)));
        will(returnValue(clientComp));
      
        allowing(clientComp).getIp(); will(returnValue(srcIpAddr));
        allowing(clientComp).getScript(); will(returnValue("# dummy script set in expectation"));
    }

    public void willCreateTcpServerComposer(ComposerFactory composerFactory, String destIpAddr, String name,
            Composer serverComp) {
        oneOf(composerFactory).getComposer(with(equal(SupportedProtocol.TCP)), with(equal(ComposerType.SERVER)),
                with(equal(destIpAddr)), with(equal(name)));
        will(returnValue(serverComp));
        
        allowing(serverComp).getIp(); will(returnValue(destIpAddr));
        allowing(serverComp).getScript(); will(returnValue("# dummy script set in expectation"));
    }

}
