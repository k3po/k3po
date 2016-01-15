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

import org.junit.Test;
import org.kaazing.k3po.pcap.converter.internal.author.composer.TcpClientComposer;
import org.kaazing.k3po.pcap.converter.internal.author.emitter.Emitter;
import org.kaazing.k3po.pcap.converter.internal.author.emitter.EmitterFactory;
import org.kaazing.k3po.pcap.converter.internal.author.emitter.EmitterFactoryImpl;
import org.kaazing.k3po.pcap.converter.internal.author.emitter.OutputType;
import org.kaazing.k3po.pcap.converter.internal.packet.Packet;
import org.kaazing.k3po.pcap.converter.internal.utils.PacketUtil;
import org.kaazing.k3po.pcap.converter.internal.utils.ScriptTestUtil;
import org.kaazing.k3po.pcap.converter.internal.utils.Util;

public class TcpClientComposerSingleScriptTest extends AbstractTcpTest {
    
    private final EmitterFactory emitterFactory = new EmitterFactoryImpl();
    
    private TcpClientComposer getComposerWithOpenConnection() {
        final Emitter emitter = emitterFactory.getRptScriptEmitter(OutputType.TCP_CLIENT_COMPOSER, "testSynAck");
        final TcpClientComposer composer = new TcpClientComposer(emitterFactory, emitter, CLIENT_IP);
        final Packet synAck = PacketUtil.getTcpSynAckPacket(CLIENT_IP, SERVER_IP, CLIENT_PORT, SERVER_PORT);
        final Packet ackToSynAck = PacketUtil.getTcpAckPacket(SERVER_IP, CLIENT_IP, SERVER_PORT, CLIENT_PORT);
        composer.emitConversation(synAck);
        composer.emitConversation(ackToSynAck);
        return composer;
    }
    
    @Test
    public void testFirstPacket() {
        final Emitter emitter = emitterFactory.getRptScriptEmitter(OutputType.TCP_CLIENT_COMPOSER, "testSynAck");
        final TcpClientComposer composer = new TcpClientComposer(emitterFactory, emitter, CLIENT_IP);
        final Packet synAck = PacketUtil.getTcpSynAckPacket(CLIENT_IP, SERVER_IP, CLIENT_PORT, SERVER_PORT);
        composer.emitConversation(synAck);
        assertTrue(ScriptTestUtil.scriptHasNonCommentLineContainingLineAndNoOtherCommandsInScript(composer.getScript(),
                "connect tcp://" + SERVER_IP + ":" + SERVER_PORT));
    }

    @Test
    public void testFirstPacketAndConnect() {
        final TcpClientComposer composer = getComposerWithOpenConnection();
        final List<String> expectedScript = new LinkedList<>();
        expectedScript.add("connect tcp://" + SERVER_IP + ":" + SERVER_PORT);
        expectedScript.add("connected");
        assertTrue(ScriptTestUtil.scriptIsInstanceOfScript(composer.getScript(), expectedScript));
    }
    
    @Test
    public void testClientReadData(){
        TcpClientComposer composer = getComposerWithOpenConnection();
        final String preTestScript = composer.getScript();
        final List<String> expectedScript = new LinkedList<>();
        composer.emitConversation(toClientPayloadPacket);
        expectedScript.add(payloadScriptRead);
        final String testScript = composer.getScript().substring(preTestScript.length());
        assertTrue(ScriptTestUtil.scriptIsInstanceOfScript(testScript, expectedScript));
    }
    
    @Test
    public void testClientReadAsciiData(){
        TcpClientComposer composer = getComposerWithOpenConnection();
        final String preTestScript = composer.getScript();
        final List<String> expectedScript = new LinkedList<>();
        composer.emitConversation(toClientAsciiPayloadPacket);
        expectedScript.add(asciiPayloadScriptRead);
        final String testScript = composer.getScript().substring(preTestScript.length());
        assertTrue(ScriptTestUtil.scriptIsInstanceOfScript(testScript, expectedScript));
    }
    
    @Test
    public void testClientAvoidDuplicateReadData(){
        TcpClientComposer composer = getComposerWithOpenConnection();
        final String preTestScript = composer.getScript();
        final List<String> expectedScript = new LinkedList<>();
        composer.emitConversation(toClientPayloadPacket);
        composer.emitConversation(toClientPayloadPacket);
        expectedScript.add(payloadScriptRead);
        final String testScript = composer.getScript().substring(preTestScript.length());
        assertTrue(ScriptTestUtil.scriptIsInstanceOfScript(testScript, expectedScript));
    }
    
    @Test
    public void testClientAvoidDuplicateReadAsciiData(){
        TcpClientComposer composer = getComposerWithOpenConnection();
        final String preTestScript = composer.getScript();
        final List<String> expectedScript = new LinkedList<>();
        composer.emitConversation(toClientAsciiPayloadPacket);
        composer.emitConversation(toClientAsciiPayloadPacket);
        expectedScript.add(asciiPayloadScriptRead);
        final String testScript = composer.getScript().substring(preTestScript.length());
        assertTrue(ScriptTestUtil.scriptIsInstanceOfScript(testScript, expectedScript));
    }
       
    @Test
    public void testClientWriteData(){
        TcpClientComposer composer = getComposerWithOpenConnection();
        final String preTestScript = composer.getScript();
        final List<String> expectedScript = new LinkedList<>();
        composer.emitConversation(toServerPayloadPacket);
        expectedScript.add(payloadScriptWrite);
        final String testScript = composer.getScript().substring(preTestScript.length());
        assertTrue(ScriptTestUtil.scriptIsInstanceOfScript(testScript, expectedScript));
    }
    
    @Test
    public void testClientWriteAsciiData(){
        TcpClientComposer composer = getComposerWithOpenConnection();
        final String preTestScript = composer.getScript();
        final List<String> expectedScript = new LinkedList<>();
        composer.emitConversation(toServerAsciiPayloadPacket);
        expectedScript.add(asciiPayloadScriptWrite);
        final String testScript = composer.getScript().substring(preTestScript.length());
        assertTrue(ScriptTestUtil.scriptIsInstanceOfScript(testScript, expectedScript));
    }

    @Test
    public void testClientAvoidDuplicateWriteData(){
        TcpClientComposer composer = getComposerWithOpenConnection();
        final String preTestScript = composer.getScript();
        final List<String> expectedScript = new LinkedList<>();
        composer.emitConversation(toServerPayloadPacket);
        composer.emitConversation(toServerPayloadPacket);
        expectedScript.add(payloadScriptWrite);
        final String testScript = composer.getScript().substring(preTestScript.length());
        assertTrue(ScriptTestUtil.scriptIsInstanceOfScript(testScript, expectedScript));
    }

    @Test
    public void testClientAvoidDuplicateWriteAsciiData(){
        TcpClientComposer composer = getComposerWithOpenConnection();
        final String preTestScript = composer.getScript();
        final List<String> expectedScript = new LinkedList<>();
        composer.emitConversation(toServerAsciiPayloadPacket);
        composer.emitConversation(toServerAsciiPayloadPacket);
        expectedScript.add(asciiPayloadScriptWrite);
        final String testScript = composer.getScript().substring(preTestScript.length());
        assertTrue(ScriptTestUtil.scriptIsInstanceOfScript(testScript, expectedScript));
    }

    @Test
    public void testCloseRead() {
        TcpClientComposer composer = getComposerWithOpenConnection();
        final String preTestScript = composer.getScript();
        final long initialFinSeqNum = 01L;
        final long initialFinAckNum = 101L;
        final Packet finPacket = PacketUtil.getTcpFinPacket(CLIENT_IP, SERVER_IP, CLIENT_PORT, SERVER_PORT,
                initialFinSeqNum, initialFinAckNum);
        final long responseFinSeqNum = 201L;
        final long resonseFinAckNum = initialFinSeqNum + 1L;
        final Packet ackPacket = PacketUtil.getTcpAckPacket(SERVER_IP, CLIENT_IP, SERVER_PORT, CLIENT_PORT,
                responseFinSeqNum, resonseFinAckNum);
        composer.emitConversation(finPacket);
        composer.emitConversation(ackPacket);
        final String testScript = composer.getScript().substring(preTestScript.length());
        final List<String> expectedScript = new LinkedList<>();
        expectedScript.add(Util.CLOSE_READ);
        assertTrue(ScriptTestUtil.scriptIsInstanceOfScript(testScript, expectedScript));
    }
    
    @Test
    public void testCloseWrite() {
        TcpClientComposer composer = getComposerWithOpenConnection();
        final String preTestScript = composer.getScript();
        final long initialFinSeqNum = 01L;
        final long initialFinAckNum = 101L;
        final Packet finPacket = PacketUtil.getTcpFinPacket(SERVER_IP, CLIENT_IP, SERVER_PORT, CLIENT_PORT,
                initialFinSeqNum, initialFinAckNum);
        final long responseFinSeqNum = 201L;
        final long resonseFinAckNum = initialFinSeqNum + 1L;
        final Packet ackPacket = PacketUtil.getTcpAckPacket(CLIENT_IP, SERVER_IP, CLIENT_PORT, SERVER_PORT,
                responseFinSeqNum, resonseFinAckNum);
        composer.emitConversation(finPacket);
        composer.emitConversation(ackPacket);
        final String testScript = composer.getScript().substring(preTestScript.length());
        final List<String> expectedScript = new LinkedList<>();
        expectedScript.add(Util.CLOSE_WRITE);
        assertTrue(ScriptTestUtil.scriptIsInstanceOfScript(testScript, expectedScript));
    }
    
    @Test
    public void testClosing3WayHandshakeCloseInitedByClient() {
        TcpClientComposer composer = getComposerWithOpenConnection();
        final String preTestScript = composer.getScript();
        final long initialFinSeqNum = 01L;
        final long initialFinAckNum = 101L;
        final Packet finPacket = PacketUtil.getTcpFinPacket(SERVER_IP, CLIENT_IP, SERVER_PORT, CLIENT_PORT,
                initialFinSeqNum, initialFinAckNum);
        final long responseFinSeqNum = 201L;
        final long resonseFinAckNum = initialFinSeqNum + 1L;
        final Packet finAckPacket = PacketUtil.getTcpFinAckPacket(CLIENT_IP, SERVER_IP, CLIENT_PORT, SERVER_PORT,
                responseFinSeqNum, resonseFinAckNum);
        final long responseAckSeqNum = 02L;
        final long responseAckAckNum = responseFinSeqNum + 1L;
        final Packet ackPacket = PacketUtil.getTcpAckPacket(SERVER_IP, CLIENT_IP, SERVER_PORT, CLIENT_PORT,
                responseAckSeqNum, responseAckAckNum);
        composer.emitConversation(finPacket);
        composer.emitConversation(finAckPacket);
        composer.emitConversation(ackPacket);
        final String testScript = composer.getScript().substring(preTestScript.length());
        final List<String> expectedScript = new LinkedList<>();
        expectedScript.add(Util.CLOSE_WRITE);
        expectedScript.add(Util.CLOSE_READ);
        expectedScript.add("close");
        expectedScript.add("closed");
        assertTrue(ScriptTestUtil.scriptIsInstanceOfScript(testScript, expectedScript));
    }
   
    @Test    
    public void testClosing3WayHandshakeCloseInitedByServer() {
        TcpClientComposer composer = getComposerWithOpenConnection();
        final String preTestScript = composer.getScript();
        final long initialFinSeqNum = 01L;
        final long initialFinAckNum = 101L;
        final Packet finPacket = PacketUtil.getTcpFinPacket(CLIENT_IP, SERVER_IP, CLIENT_PORT, SERVER_PORT,
                initialFinSeqNum, initialFinAckNum);
        final long responseFinSeqNum = 201L;
        final long resonseFinAckNum = initialFinSeqNum + 1L;
        final Packet finAckPacket = PacketUtil.getTcpFinAckPacket(SERVER_IP, CLIENT_IP, SERVER_PORT, CLIENT_PORT,
                responseFinSeqNum, resonseFinAckNum);
        final long responseAckSeqNum = 02L;
        final long responseAckAckNum = responseFinSeqNum + 1L;
        final Packet ackPacket = PacketUtil.getTcpAckPacket(CLIENT_IP, SERVER_IP, CLIENT_PORT, SERVER_PORT,
                responseAckSeqNum, responseAckAckNum);
        composer.emitConversation(finPacket);
        composer.emitConversation(finAckPacket);
        composer.emitConversation(ackPacket);
        final String testScript = composer.getScript().substring(preTestScript.length());
        final List<String> expectedScript = new LinkedList<>();
        expectedScript.add(Util.CLOSE_READ);
        expectedScript.add(Util.CLOSE_WRITE);
        expectedScript.add("close");
        expectedScript.add("closed");
        assertTrue(ScriptTestUtil.scriptIsInstanceOfScript(testScript, expectedScript));
    }

    @Test
    public void testClosing2WayHandshakeCloseInitedByClient() {
        TcpClientComposer composer = getComposerWithOpenConnection();
        final String preTestScript = composer.getScript();
        composer.emitConversation(CLIENT_FIN_PACKET);
        composer.emitConversation(SERVER_FIN_PACKET);
        composer.emitConversation(SERVER_ACK_PACKET);
        composer.emitConversation(CLIENT_ACK_PACKET);
        final String testScript = composer.getScript().substring(preTestScript.length());
        final List<String> expectedScript = new LinkedList<>();
        expectedScript.add(Util.CLOSE_WRITE);
        expectedScript.add(Util.CLOSE_READ);
        expectedScript.add("close");
        expectedScript.add("closed");
        assertTrue(ScriptTestUtil.scriptIsInstanceOfScript(testScript, expectedScript));
    }
    
    @Test
    public void testClosing2WayHandshakeCloseInitedByClientReverseAck() {
        TcpClientComposer composer = getComposerWithOpenConnection();
        final String preTestScript = composer.getScript();
        composer.emitConversation(CLIENT_FIN_PACKET);
        composer.emitConversation(SERVER_FIN_PACKET);
        composer.emitConversation(CLIENT_ACK_PACKET);
        composer.emitConversation(SERVER_ACK_PACKET);
        final String testScript = composer.getScript().substring(preTestScript.length());
        final List<String> expectedScript = new LinkedList<>();
        expectedScript.add(Util.CLOSE_READ);
        expectedScript.add(Util.CLOSE_WRITE);
        expectedScript.add("close");
        expectedScript.add("closed");
        assertTrue(ScriptTestUtil.scriptIsInstanceOfScript(testScript, expectedScript));
    }
    
    @Test
    public void testClosing2WayHandshakeServerInnitedClose() {
        TcpClientComposer composer = getComposerWithOpenConnection();
        final String preTestScript = composer.getScript();
        composer.emitConversation(SERVER_FIN_PACKET);
        composer.emitConversation(CLIENT_FIN_PACKET);
        composer.emitConversation(CLIENT_ACK_PACKET);
        composer.emitConversation(SERVER_ACK_PACKET);
        final String testScript = composer.getScript().substring(preTestScript.length());
        final List<String> expectedScript = new LinkedList<>();
        expectedScript.add(Util.CLOSE_READ);
        expectedScript.add(Util.CLOSE_WRITE);
        expectedScript.add("close");
        expectedScript.add("closed");
        assertTrue(ScriptTestUtil.scriptIsInstanceOfScript(testScript, expectedScript));
    }
    
    @Test
    public void testClosing2WayHandshakeCloseInnitedServerReverseAck() {
        TcpClientComposer composer = getComposerWithOpenConnection();
        final String preTestScript = composer.getScript();
        composer.emitConversation(SERVER_FIN_PACKET);
        composer.emitConversation(CLIENT_FIN_PACKET);
        composer.emitConversation(SERVER_ACK_PACKET);
        composer.emitConversation(CLIENT_ACK_PACKET);
        final String testScript = composer.getScript().substring(preTestScript.length());
        final List<String> expectedScript = new LinkedList<>();
        expectedScript.add(Util.CLOSE_WRITE);
        expectedScript.add(Util.CLOSE_READ);
        expectedScript.add("close");
        expectedScript.add("closed");
        assertTrue(ScriptTestUtil.scriptIsInstanceOfScript(testScript, expectedScript));
    }

    @Test
    public void testClosing4WayHandshakeCloseInittedByServer() {
        TcpClientComposer composer = getComposerWithOpenConnection();
        final String preTestScript = composer.getScript();
        composer.emitConversation(SERVER_FIN_PACKET);
        composer.emitConversation(CLIENT_ACK_PACKET);
        composer.emitConversation(CLIENT_FIN_PACKET);
        composer.emitConversation(SERVER_ACK_PACKET);
        final String testScript = composer.getScript().substring(preTestScript.length());
        final List<String> expectedScript = new LinkedList<>();
        expectedScript.add(Util.CLOSE_READ);
        expectedScript.add(Util.CLOSE_WRITE);
        expectedScript.add("closed");
        assertTrue(ScriptTestUtil.scriptIsInstanceOfScript(testScript, expectedScript));
    }
   
    @Test
    public void testClosing4WayHandshakeCloseInittedByClient() {
        TcpClientComposer composer = getComposerWithOpenConnection();
        final String preTestScript = composer.getScript();
        composer.emitConversation(CLIENT_FIN_PACKET);
        composer.emitConversation(SERVER_ACK_PACKET);
        composer.emitConversation(SERVER_FIN_PACKET);
        composer.emitConversation(CLIENT_ACK_PACKET);
        final String testScript = composer.getScript().substring(preTestScript.length());
        final List<String> expectedScript = new LinkedList<>();
        expectedScript.add(Util.CLOSE_WRITE);
        expectedScript.add(Util.CLOSE_READ);
        expectedScript.add("close");
        expectedScript.add("closed");
        assertTrue(ScriptTestUtil.scriptIsInstanceOfScript(testScript, expectedScript));
    }
    
    @Test
    public void testWriteWhileReadClosed(){
        TcpClientComposer composer = getComposerWithOpenConnection();
        final String preTestScript = composer.getScript();
        final List<String> expectedScript = new LinkedList<>();
        composer.emitConversation(SERVER_FIN_PACKET);
        composer.emitConversation(CLIENT_ACK_PACKET);
        composer.emitConversation(toServerPayloadPacket);
        expectedScript.add(Util.CLOSE_READ);
        expectedScript.add(payloadScriptWrite);
        final String testScript = composer.getScript().substring(preTestScript.length());
        assertTrue(ScriptTestUtil.scriptIsInstanceOfScript(testScript, expectedScript));
    }
    
    @Test
    public void testWriteWhileReadClosedAndThenClose(){
        TcpClientComposer composer = getComposerWithOpenConnection();
        final String preTestScript = composer.getScript();
        final List<String> expectedScript = new LinkedList<>();
        composer.emitConversation(SERVER_FIN_PACKET);
        composer.emitConversation(CLIENT_ACK_PACKET);
        composer.emitConversation(toServerPayloadPacket);
        composer.emitConversation(CLIENT_FIN_PACKET);
        composer.emitConversation(SERVER_ACK_PACKET);
        expectedScript.add(Util.CLOSE_READ);
        expectedScript.add(payloadScriptWrite);
        expectedScript.add(Util.CLOSE_WRITE);
        expectedScript.add("closed");
        final String testScript = composer.getScript().substring(preTestScript.length());
        assertTrue(ScriptTestUtil.scriptIsInstanceOfScript(testScript, expectedScript));
    }
    
    @Test
    public void testReadWhileWriteClosed(){
        TcpClientComposer composer = getComposerWithOpenConnection();
        final String preTestScript = composer.getScript();
        final List<String> expectedScript = new LinkedList<>();
        composer.emitConversation(CLIENT_FIN_PACKET);
        composer.emitConversation(SERVER_ACK_PACKET);
        composer.emitConversation(toClientPayloadPacket);
        expectedScript.add(Util.CLOSE_WRITE);
        expectedScript.add(payloadScriptRead);
        final String testScript = composer.getScript().substring(preTestScript.length());
        assertTrue(ScriptTestUtil.scriptIsInstanceOfScript(testScript, expectedScript));
    }
    
    @Test
    public void testReadWhileWriteClosedAndThenClose(){
        TcpClientComposer composer = getComposerWithOpenConnection();
        final String preTestScript = composer.getScript();
        final List<String> expectedScript = new LinkedList<>();
        composer.emitConversation(CLIENT_FIN_PACKET);
        composer.emitConversation(SERVER_ACK_PACKET);
        composer.emitConversation(toClientPayloadPacket);
        composer.emitConversation(SERVER_FIN_PACKET);
        composer.emitConversation(CLIENT_ACK_PACKET);
        
        expectedScript.add(Util.CLOSE_WRITE);
        expectedScript.add(payloadScriptRead);
        expectedScript.add(Util.CLOSE_READ);
        expectedScript.add("close");
        expectedScript.add("closed");
        final String testScript = composer.getScript().substring(preTestScript.length());
        assertTrue(ScriptTestUtil.scriptIsInstanceOfScript(testScript, expectedScript));
    }

}
