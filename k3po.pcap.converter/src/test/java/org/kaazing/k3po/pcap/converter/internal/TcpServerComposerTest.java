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

import java.util.LinkedList;
import java.util.List;

import org.jmock.Mockery;
import org.junit.Test;
import org.kaazing.k3po.pcap.converter.internal.author.composer.TcpServerComposer;
import org.kaazing.k3po.pcap.converter.internal.author.emitter.Emitter;
import org.kaazing.k3po.pcap.converter.internal.author.emitter.EmitterFactory;
import org.kaazing.k3po.pcap.converter.internal.author.emitter.EmitterFactoryImpl;
import org.kaazing.k3po.pcap.converter.internal.author.emitter.OutputType;
import org.kaazing.k3po.pcap.converter.internal.packet.Packet;
import org.kaazing.k3po.pcap.converter.internal.utils.PacketUtil;
import org.kaazing.k3po.pcap.converter.internal.utils.ScriptTestUtil;

public class TcpServerComposerTest extends AbstractTcpTest {

    @Test
    public void testTwoTcpSessionsWithSameServerConnect(){
        Mockery context = new Mockery();
        final int CLIENT_PORT2 = 55555;
        final EmitterFactory emitterFactory = context.mock(EmitterFactory.class);
        final Emitter composerNoteEmitter = context.mock(Emitter.class, "composerNoteEmitter");
        final Emitter serverEmitter1 = new EmitterFactoryImpl().getRptScriptEmitter(OutputType.TCP_SERVER_COMPOSER, "TcpServerComposerTest1");
        final Emitter serverEmitter2 = new EmitterFactoryImpl().getRptScriptEmitter(OutputType.TCP_SERVER_COMPOSER, "TcpServerComposerTest2");
        final Packet synAck = PacketUtil.getTcpSynAckPacket(CLIENT_IP, SERVER_IP, CLIENT_PORT, SERVER_PORT);
        final Packet synAck2 = PacketUtil.getTcpSynAckPacket(CLIENT_IP, SERVER_IP, CLIENT_PORT2, SERVER_PORT);
        context.checking(new TcpComposerExpectations() {
            {
                willSetScriptEmitter(emitterFactory, serverEmitter1, OutputType.TCP_SERVER_SCRIPT);
                willSetScriptEmitter(emitterFactory, serverEmitter2, OutputType.TCP_SERVER_SCRIPT);
            }
        });
        
        TcpServerComposer tcpServerComposer = new TcpServerComposer(emitterFactory, composerNoteEmitter, CLIENT_IP);
        tcpServerComposer.emitConversation(synAck);
        tcpServerComposer.emitConversation(synAck2);
        
        context.assertIsSatisfied();   
        
        List<String> expectedScript = new LinkedList<>();
        expectedScript.add("accept tcp://" + SERVER_IP + ":" + SERVER_PORT);
        expectedScript.add("accepted");
        
        List<String> expectedScript2 = new LinkedList<>();
        expectedScript2.add("accept tcp://" + SERVER_IP + ":" + SERVER_PORT);
        expectedScript2.add("accepted");
        assertTrue(ScriptTestUtil.scriptIsInstanceOfScript(serverEmitter1.getBuffer(), expectedScript));
        assertTrue(ScriptTestUtil.scriptIsInstanceOfScript(serverEmitter2.getBuffer(), expectedScript));
//        assertTrue(ScriptTestUtil.scriptHasNonCommentLineContainingLineAndNoOtherCommandsInScript(clientEmitter1.getBuffer(),
//                "accept tcp://" + SERVER_IP + ":" + SERVER_PORT));
//        assertTrue(ScriptTestUtil.scriptHasNonCommentLineContainingLineAndNoOtherCommandsInScript(clientEmitter2.getBuffer(),
//                "accept tcp://" + SERVER_IP + ":" + SERVER_PORT));
    }
    
    @Test
    public void testReopenTcpSession(){
        Mockery context = new Mockery();

        final EmitterFactory emitterFactory = context.mock(EmitterFactory.class);
        final Emitter composerNoteEmitter = context.mock(Emitter.class, "composerNoteEmitter");
        final Emitter serverEmitter1 = new EmitterFactoryImpl().getRptScriptEmitter(OutputType.TCP_SERVER_COMPOSER, "TcpServerComposerTest1");
        final Packet synAck = PacketUtil.getTcpSynAckPacket(CLIENT_IP, SERVER_IP, CLIENT_PORT, SERVER_PORT);
        context.checking(new TcpComposerExpectations() {
            {
                willSetScriptEmitter(emitterFactory, serverEmitter1, OutputType.TCP_SERVER_SCRIPT);
            }
        });
        
        TcpServerComposer tcpServerComposer = new TcpServerComposer(emitterFactory, composerNoteEmitter, CLIENT_IP);
        tcpServerComposer.emitConversation(synAck);
        tcpServerComposer.emitConversation(CLIENT_FIN_PACKET);
        tcpServerComposer.emitConversation(SERVER_FIN_PACKET);
        tcpServerComposer.emitConversation(CLIENT_ACK_PACKET);
        tcpServerComposer.emitConversation(SERVER_ACK_PACKET);
        tcpServerComposer.emitConversation(synAck);
        context.assertIsSatisfied();
        List<String> expectedScript = new LinkedList<>();
        expectedScript.add("accept tcp://" + SERVER_IP + ":" + SERVER_PORT);
        expectedScript.add("accepted");
        expectedScript.add("connected");
        expectedScript.add("# close-write");
        expectedScript.add("# close-read");
        expectedScript.add("close");
        expectedScript.add("closed");
        expectedScript.add("accept tcp://" + SERVER_IP + ":" + SERVER_PORT);
        expectedScript.add("accepted");
        
        assertTrue(ScriptTestUtil.scriptIsInstanceOfScript(serverEmitter1.getBuffer(), expectedScript));
    }
    
    @Test
    public void testIsFinished(){
        Mockery context = new Mockery();

        final EmitterFactory emitterFactory = context.mock(EmitterFactory.class);
        final Emitter composerNoteEmitter = context.mock(Emitter.class, "composerNoteEmitter");
        final Emitter serverEmitter1 = new EmitterFactoryImpl().getRptScriptEmitter(OutputType.TCP_SERVER_COMPOSER, "TcpServerComposerTest1");
        final Packet synAck = PacketUtil.getTcpSynAckPacket(CLIENT_IP, SERVER_IP, CLIENT_PORT, SERVER_PORT);
        context.checking(new TcpComposerExpectations() {
            {
                willSetScriptEmitter(emitterFactory, serverEmitter1, OutputType.TCP_SERVER_SCRIPT);
            }
        });
        
        TcpServerComposer tcpServerComposer = new TcpServerComposer(emitterFactory, composerNoteEmitter, CLIENT_IP);
        tcpServerComposer.emitConversation(synAck);
        tcpServerComposer.emitConversation(CLIENT_FIN_PACKET);
        tcpServerComposer.emitConversation(SERVER_FIN_PACKET);
        tcpServerComposer.emitConversation(CLIENT_ACK_PACKET);
        tcpServerComposer.emitConversation(SERVER_ACK_PACKET);
        context.assertIsSatisfied();
        context.assertIsSatisfied();
        
        assertTrue(tcpServerComposer.isFinished());
        
        tcpServerComposer.emitConversation(synAck);
        
        assertFalse(tcpServerComposer.isFinished());
    }

}
