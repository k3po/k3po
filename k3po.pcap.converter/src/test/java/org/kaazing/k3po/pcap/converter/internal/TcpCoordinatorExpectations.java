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
package org.kaazing.k3po.pcap.converter.internal;

import org.kaazing.k3po.pcap.converter.internal.author.ConversationId;
import org.kaazing.k3po.pcap.converter.internal.author.SupportedProtocol;
import org.kaazing.k3po.pcap.converter.internal.author.composer.Composer;
import org.kaazing.k3po.pcap.converter.internal.author.composer.ComposerFactory;
import org.kaazing.k3po.pcap.converter.internal.author.composer.ComposerType;
import org.kaazing.k3po.pcap.converter.internal.author.emitter.Emitter;
import org.kaazing.k3po.pcap.converter.internal.author.emitter.EmitterFactory;
import org.kaazing.k3po.pcap.converter.internal.author.emitter.OutputType;

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
