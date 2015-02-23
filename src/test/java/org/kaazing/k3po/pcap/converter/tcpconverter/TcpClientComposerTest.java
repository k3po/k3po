/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.k3po.pcap.converter.tcpconverter;

import java.util.LinkedList;
import java.util.List;

import org.jmock.Mockery;
import org.junit.Test;

import org.kaazing.k3po.pcap.converter.tcpconverter.packet.Packet;
import org.kaazing.k3po.pcap.converter.tcpconverter.author.composer.TcpClientComposer;
import org.kaazing.k3po.pcap.converter.tcpconverter.author.emitter.Emitter;
import org.kaazing.k3po.pcap.converter.tcpconverter.author.emitter.EmitterFactory;
import org.kaazing.k3po.pcap.converter.tcpconverter.author.emitter.EmitterFactoryImpl;
import org.kaazing.k3po.pcap.converter.tcpconverter.author.emitter.OutputType;
import org.kaazing.k3po.pcap.converter.tcpconverter.utils.PacketUtil;
import org.kaazing.k3po.pcap.converter.tcpconverter.utils.ScriptTestUtil;

public class TcpClientComposerTest extends AbstractTcpTest {

    @Test
    public void testTwoTcpSessionsWithSameClientConnect(){
        Mockery context = new Mockery();
        final int CLIENT_PORT2 = 55555;
        final EmitterFactory emitterFactory = context.mock(EmitterFactory.class);
        final Emitter composerNoteEmitter = context.mock(Emitter.class, "composerNoteEmitter");
        final Emitter clientEmitter1 = new EmitterFactoryImpl().getRptScriptEmitter(OutputType.TCP_CLIENT_COMPOSER, "TcpClientComposerTest1");
        final Emitter clientEmitter2 = new EmitterFactoryImpl().getRptScriptEmitter(OutputType.TCP_CLIENT_COMPOSER, "TcpClientComposerTest2");
        final Packet synAck = PacketUtil.getTcpSynAckPacket(CLIENT_IP, SERVER_IP, CLIENT_PORT, SERVER_PORT);
        final Packet synAck2 = PacketUtil.getTcpSynAckPacket(CLIENT_IP, SERVER_IP, CLIENT_PORT2, SERVER_PORT);
        context.checking(new TcpComposerExpectations() {
            {
                willSetScriptEmitter(emitterFactory, clientEmitter1, OutputType.TCP_CLIENT_SCRIPT);
                willSetScriptEmitter(emitterFactory, clientEmitter2, OutputType.TCP_CLIENT_SCRIPT);
            }
        });
        
        TcpClientComposer tcpClientComposer = new TcpClientComposer(emitterFactory, composerNoteEmitter, CLIENT_IP);
        tcpClientComposer.emitConversation(synAck);
        tcpClientComposer.emitConversation(synAck2);
        
        context.assertIsSatisfied();   

        assertTrue(ScriptTestUtil.scriptHasNonCommentLineContainingLineAndNoOtherCommandsInScript(clientEmitter1.getBuffer(),
                "connect tcp://" + SERVER_IP + ":" + SERVER_PORT));
        assertTrue(ScriptTestUtil.scriptHasNonCommentLineContainingLineAndNoOtherCommandsInScript(clientEmitter2.getBuffer(),
                "connect tcp://" + SERVER_IP + ":" + SERVER_PORT));
    }
    
    @Test
    public void testReopenTcpSession(){
        Mockery context = new Mockery();

        final EmitterFactory emitterFactory = context.mock(EmitterFactory.class);
        final Emitter composerNoteEmitter = context.mock(Emitter.class, "composerNoteEmitter");
        final Emitter clientEmitter1 = new EmitterFactoryImpl().getRptScriptEmitter(OutputType.TCP_CLIENT_COMPOSER, "TcpClientComposerTest1");
        final Packet synAck = PacketUtil.getTcpSynAckPacket(CLIENT_IP, SERVER_IP, CLIENT_PORT, SERVER_PORT);
        context.checking(new TcpComposerExpectations() {
            {
                willSetScriptEmitter(emitterFactory, clientEmitter1, OutputType.TCP_CLIENT_SCRIPT);
            }
        });
        
        TcpClientComposer tcpClientComposer = new TcpClientComposer(emitterFactory, composerNoteEmitter, CLIENT_IP);
        tcpClientComposer.emitConversation(synAck);
        tcpClientComposer.emitConversation(CLIENT_FIN_PACKET);
        tcpClientComposer.emitConversation(SERVER_FIN_PACKET);
        tcpClientComposer.emitConversation(CLIENT_ACK_PACKET);
        tcpClientComposer.emitConversation(SERVER_ACK_PACKET);
        tcpClientComposer.emitConversation(synAck);
        context.assertIsSatisfied();
        List<String> expectedScript = new LinkedList<String>();
        expectedScript.add("connect tcp://" + SERVER_IP + ":" + SERVER_PORT);
        expectedScript.add("connected");
        expectedScript.add("# close-read");
        expectedScript.add("# close-write");
        expectedScript.add("close");
        expectedScript.add("closed");
        expectedScript.add("connect tcp://" + SERVER_IP + ":" + SERVER_PORT);
        
        assertTrue(ScriptTestUtil.scriptIsInstanceOfScript(clientEmitter1.getBuffer(), expectedScript));
    }
    
    @Test
    public void testIsFinished(){
        Mockery context = new Mockery();

        final EmitterFactory emitterFactory = context.mock(EmitterFactory.class);
        final Emitter composerNoteEmitter = context.mock(Emitter.class, "composerNoteEmitter");
        final Emitter clientEmitter1 = new EmitterFactoryImpl().getRptScriptEmitter(OutputType.TCP_CLIENT_COMPOSER, "TcpClientComposerTest1");
        final Packet synAck = PacketUtil.getTcpSynAckPacket(CLIENT_IP, SERVER_IP, CLIENT_PORT, SERVER_PORT);
        context.checking(new TcpComposerExpectations() {
            {
                willSetScriptEmitter(emitterFactory, clientEmitter1, OutputType.TCP_CLIENT_SCRIPT);
            }
        });
        
        TcpClientComposer tcpClientComposer = new TcpClientComposer(emitterFactory, composerNoteEmitter, CLIENT_IP);
        tcpClientComposer.emitConversation(synAck);
        tcpClientComposer.emitConversation(CLIENT_FIN_PACKET);
        tcpClientComposer.emitConversation(SERVER_FIN_PACKET);
        tcpClientComposer.emitConversation(CLIENT_ACK_PACKET);
        tcpClientComposer.emitConversation(SERVER_ACK_PACKET);

        context.assertIsSatisfied();
        
        assertTrue(tcpClientComposer.isFinished());
        
        tcpClientComposer.emitConversation(synAck);
        
        assertFalse(tcpClientComposer.isFinished());
    }
    
}