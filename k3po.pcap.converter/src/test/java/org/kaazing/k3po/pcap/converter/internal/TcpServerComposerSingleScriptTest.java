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
import org.kaazing.k3po.pcap.converter.internal.author.composer.TcpServerComposer;
import org.kaazing.k3po.pcap.converter.internal.author.emitter.Emitter;
import org.kaazing.k3po.pcap.converter.internal.author.emitter.EmitterFactoryImpl;
import org.kaazing.k3po.pcap.converter.internal.author.emitter.OutputType;
import org.kaazing.k3po.pcap.converter.internal.packet.Packet;
import org.kaazing.k3po.pcap.converter.internal.utils.PacketUtil;
import org.kaazing.k3po.pcap.converter.internal.utils.ScriptTestUtil;
import org.kaazing.k3po.pcap.converter.internal.utils.Util;

public class TcpServerComposerSingleScriptTest extends AbstractTcpTest {
    
    private final EmitterFactoryImpl emitterFactory = new EmitterFactoryImpl();
    
    private TcpServerComposer getComposerWithOpenConnection() {
        final Emitter emitter = emitterFactory.getRptScriptEmitter(OutputType.TCP_SERVER_COMPOSER, "testSynAck");
        final TcpServerComposer composer = new TcpServerComposer(emitterFactory, emitter, SERVER_IP);
        final Packet synAck = PacketUtil.getTcpSynAckPacket(CLIENT_IP, SERVER_IP, CLIENT_PORT, SERVER_PORT);
        final Packet ackToSynAck = PacketUtil.getTcpAckPacket(SERVER_IP, CLIENT_IP, SERVER_PORT, CLIENT_PORT);
        composer.emitConversation(synAck);
        composer.emitConversation(ackToSynAck);
        return composer;
    }

    @Test
    public void testFirstPacket() {
        final Emitter emitter = emitterFactory.getRptScriptEmitter(OutputType.TCP_SERVER_COMPOSER, "testSynAck");
        final TcpServerComposer composer = new TcpServerComposer(emitterFactory, emitter, SERVER_IP);
        final Packet synAck = PacketUtil.getTcpSynAckPacket(CLIENT_IP, SERVER_IP, CLIENT_PORT, SERVER_PORT);
        composer.emitConversation(synAck);
        final List<String> expectedScript = new LinkedList<>();
        expectedScript.add("accept tcp://" + SERVER_IP + ":" + SERVER_PORT);
        expectedScript.add("accepted");

        assertTrue(ScriptTestUtil.scriptIsInstanceOfScript(composer.getScript(),
                expectedScript));

    }

    @Test
    public void testFirstPacketAndConnect() {
        final TcpServerComposer composer = getComposerWithOpenConnection();
        final List<String> expectedScript = new LinkedList<>();
        expectedScript.add("accept tcp://" + SERVER_IP + ":" + SERVER_PORT);
        expectedScript.add("accepted");
        expectedScript.add("connected");
        assertTrue(ScriptTestUtil.scriptIsInstanceOfScript(composer.getScript(), expectedScript));
    }
    
    @Test
    public void testServerReadData(){
        TcpServerComposer composer = getComposerWithOpenConnection();
        final String preTestScript = composer.getScript();
        final List<String> expectedScript = new LinkedList<>();
        composer.emitConversation(toServerPayloadPacket);
        expectedScript.add(payloadScriptRead);
        final String testScript = composer.getScript().substring(preTestScript.length());
        assertTrue(ScriptTestUtil.scriptIsInstanceOfScript(testScript, expectedScript));
    }
    
    @Test
    public void testServerAvoidDuplicateReadData(){
        TcpServerComposer composer = getComposerWithOpenConnection();
        final String preTestScript = composer.getScript();
        final List<String> expectedScript = new LinkedList<>();
        composer.emitConversation(toServerPayloadPacket);
        composer.emitConversation(toServerPayloadPacket);
        expectedScript.add(payloadScriptRead);
        final String testScript = composer.getScript().substring(preTestScript.length());
        assertTrue(ScriptTestUtil.scriptIsInstanceOfScript(testScript, expectedScript));
    }
       
    @Test
    public void testServerWriteData(){
        TcpServerComposer composer = getComposerWithOpenConnection();
        final String preTestScript = composer.getScript();
        final List<String> expectedScript = new LinkedList<>();
        composer.emitConversation(toClientPayloadPacket);
        expectedScript.add(payloadScriptWrite);
        final String testScript = composer.getScript().substring(preTestScript.length());
        assertTrue(ScriptTestUtil.scriptIsInstanceOfScript(testScript, expectedScript));
    }
    
    @Test
    public void testServerAvoidDuplicateWriteData(){
        TcpServerComposer composer = getComposerWithOpenConnection();
        final String preTestScript = composer.getScript();
        final List<String> expectedScript = new LinkedList<>();
        composer.emitConversation(toClientPayloadPacket);
        composer.emitConversation(toClientPayloadPacket);
        expectedScript.add(payloadScriptWrite);
        final String testScript = composer.getScript().substring(preTestScript.length());
        assertTrue(ScriptTestUtil.scriptIsInstanceOfScript(testScript, expectedScript));
    }

    @Test
    public void testCloseWrite() {
        TcpServerComposer composer = getComposerWithOpenConnection();
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
        expectedScript.add(Util.CLOSE_WRITE);
        assertTrue(ScriptTestUtil.scriptIsInstanceOfScript(testScript, expectedScript));
    }
    
    @Test
    public void testCloseRead() {
        TcpServerComposer composer = getComposerWithOpenConnection();
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
        expectedScript.add(Util.CLOSE_READ);
        assertTrue(ScriptTestUtil.scriptIsInstanceOfScript(testScript, expectedScript));
    }
    
    @Test
    public void testClosing3WayHandshakeCloseInitedByClient() {
        TcpServerComposer composer = getComposerWithOpenConnection();
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
        expectedScript.add(Util.CLOSE_READ);
        expectedScript.add(Util.CLOSE_WRITE);
        expectedScript.add("close");
        expectedScript.add("closed");
        assertTrue(ScriptTestUtil.scriptIsInstanceOfScript(testScript, expectedScript));
    }
   
    @Test    
    public void testClosing3WayHandshakeCloseInitedByServer() {
        TcpServerComposer composer = getComposerWithOpenConnection();
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
        expectedScript.add(Util.CLOSE_WRITE);
        expectedScript.add(Util.CLOSE_READ);
        expectedScript.add("close");
        expectedScript.add("closed");
        assertTrue(ScriptTestUtil.scriptIsInstanceOfScript(testScript, expectedScript));
    }

    @Test
    public void testClosing2WayHandshakeCloseInitedByClient() {
        TcpServerComposer composer = getComposerWithOpenConnection();
        final String preTestScript = composer.getScript();
        composer.emitConversation(CLIENT_FIN_PACKET);
        composer.emitConversation(SERVER_FIN_PACKET);
        composer.emitConversation(SERVER_ACK_PACKET);
        composer.emitConversation(CLIENT_ACK_PACKET);
        final String testScript = composer.getScript().substring(preTestScript.length());
        final List<String> expectedScript = new LinkedList<>();
        expectedScript.add(Util.CLOSE_READ);
        expectedScript.add(Util.CLOSE_WRITE);
        expectedScript.add("close");
        expectedScript.add("closed");
        assertTrue(ScriptTestUtil.scriptIsInstanceOfScript(testScript, expectedScript));
    }
    
    @Test
    public void testClosing2WayHandshakeCloseInitedByClientReverseAck() {
        TcpServerComposer composer = getComposerWithOpenConnection();
        final String preTestScript = composer.getScript();
        composer.emitConversation(CLIENT_FIN_PACKET);
        composer.emitConversation(SERVER_FIN_PACKET);
        composer.emitConversation(CLIENT_ACK_PACKET);
        composer.emitConversation(SERVER_ACK_PACKET);
        final String testScript = composer.getScript().substring(preTestScript.length());
        final List<String> expectedScript = new LinkedList<>();
        expectedScript.add(Util.CLOSE_WRITE);
        expectedScript.add(Util.CLOSE_READ);
        expectedScript.add("close");
        expectedScript.add("closed");
        assertTrue(ScriptTestUtil.scriptIsInstanceOfScript(testScript, expectedScript));
    }
    
    @Test
    public void testClosing2WayHandshakeServerInitedClose() {
        TcpServerComposer composer = getComposerWithOpenConnection();
        final String preTestScript = composer.getScript();
        composer.emitConversation(SERVER_FIN_PACKET);
        composer.emitConversation(CLIENT_FIN_PACKET);
        composer.emitConversation(CLIENT_ACK_PACKET);
        composer.emitConversation(SERVER_ACK_PACKET);
        final String testScript = composer.getScript().substring(preTestScript.length());
        final List<String> expectedScript = new LinkedList<>();
        expectedScript.add(Util.CLOSE_WRITE);
        expectedScript.add(Util.CLOSE_READ);
        expectedScript.add("close");
        expectedScript.add("closed");
        assertTrue(ScriptTestUtil.scriptIsInstanceOfScript(testScript, expectedScript));
    }
    
    @Test
    public void testClosing2WayHandshakeCloseInnitedServerReverseAck() {
        TcpServerComposer composer = getComposerWithOpenConnection();
        final String preTestScript = composer.getScript();
        composer.emitConversation(SERVER_FIN_PACKET);
        composer.emitConversation(CLIENT_FIN_PACKET);
        composer.emitConversation(SERVER_ACK_PACKET);
        composer.emitConversation(CLIENT_ACK_PACKET);
        final String testScript = composer.getScript().substring(preTestScript.length());
        final List<String> expectedScript = new LinkedList<>();
        expectedScript.add(Util.CLOSE_READ);
        expectedScript.add(Util.CLOSE_WRITE);
        expectedScript.add("close");
        expectedScript.add("closed");
        assertTrue(ScriptTestUtil.scriptIsInstanceOfScript(testScript, expectedScript));
    }

    @Test
    public void testClosing4WayHandshakeCloseInittedByServer() {
        TcpServerComposer composer = getComposerWithOpenConnection();
        final String preTestScript = composer.getScript();
        composer.emitConversation(SERVER_FIN_PACKET);
        composer.emitConversation(CLIENT_ACK_PACKET);
        composer.emitConversation(CLIENT_FIN_PACKET);
        composer.emitConversation(SERVER_ACK_PACKET);
        final String testScript = composer.getScript().substring(preTestScript.length());
        final List<String> expectedScript = new LinkedList<>();
        expectedScript.add(Util.CLOSE_WRITE);
        expectedScript.add(Util.CLOSE_READ);
        expectedScript.add("close");
        expectedScript.add("closed");
        assertTrue(ScriptTestUtil.scriptIsInstanceOfScript(testScript, expectedScript));
    }
   
    @Test
    public void testClosing4WayHandshakeCloseInittedByClient() {
        TcpServerComposer composer = getComposerWithOpenConnection();
        final String preTestScript = composer.getScript();
        composer.emitConversation(CLIENT_FIN_PACKET);
        composer.emitConversation(SERVER_ACK_PACKET);
        composer.emitConversation(SERVER_FIN_PACKET);
        composer.emitConversation(CLIENT_ACK_PACKET);
        final String testScript = composer.getScript().substring(preTestScript.length());
        final List<String> expectedScript = new LinkedList<>();
        expectedScript.add(Util.CLOSE_READ);
        expectedScript.add(Util.CLOSE_WRITE);
        expectedScript.add("closed");
        assertTrue(ScriptTestUtil.scriptIsInstanceOfScript(testScript, expectedScript));
    }
    
    @Test
    public void testWriteWhileReadClosed(){
        TcpServerComposer composer = getComposerWithOpenConnection();
        final String preTestScript = composer.getScript();
        final List<String> expectedScript = new LinkedList<>();
        composer.emitConversation(SERVER_FIN_PACKET);
        composer.emitConversation(CLIENT_ACK_PACKET);
        composer.emitConversation(toServerPayloadPacket);
        expectedScript.add(Util.CLOSE_WRITE);
        expectedScript.add(payloadScriptRead);
        final String testScript = composer.getScript().substring(preTestScript.length());
        assertTrue(ScriptTestUtil.scriptIsInstanceOfScript(testScript, expectedScript));
    }
    
    @Test
    public void testWriteWhileReadClosedAndThenClose(){
        TcpServerComposer composer = getComposerWithOpenConnection();
        final String preTestScript = composer.getScript();
        final List<String> expectedScript = new LinkedList<>();
        composer.emitConversation(SERVER_FIN_PACKET);
        composer.emitConversation(CLIENT_ACK_PACKET);
        composer.emitConversation(toServerPayloadPacket);
        composer.emitConversation(CLIENT_FIN_PACKET);
        composer.emitConversation(SERVER_ACK_PACKET);
        expectedScript.add(Util.CLOSE_WRITE);
        expectedScript.add(payloadScriptRead);
        expectedScript.add(Util.CLOSE_READ);
        expectedScript.add("close");
        expectedScript.add("closed");
        final String testScript = composer.getScript().substring(preTestScript.length());
        assertTrue(ScriptTestUtil.scriptIsInstanceOfScript(testScript, expectedScript));
    }
    
    @Test
    public void testReadWhileWriteClosed(){
        TcpServerComposer composer = getComposerWithOpenConnection();
        final String preTestScript = composer.getScript();
        final List<String> expectedScript = new LinkedList<>();
        composer.emitConversation(CLIENT_FIN_PACKET);
        composer.emitConversation(SERVER_ACK_PACKET);
        composer.emitConversation(toClientPayloadPacket);
        expectedScript.add(Util.CLOSE_READ);
        expectedScript.add(payloadScriptWrite);
        final String testScript = composer.getScript().substring(preTestScript.length());
        assertTrue(ScriptTestUtil.scriptIsInstanceOfScript(testScript, expectedScript));
    }
    
    @Test
    public void testReadWhileWriteClosedAndThenClose(){
        TcpServerComposer composer = getComposerWithOpenConnection();
        final String preTestScript = composer.getScript();
        final List<String> expectedScript = new LinkedList<>();
        composer.emitConversation(CLIENT_FIN_PACKET);
        composer.emitConversation(SERVER_ACK_PACKET);
        composer.emitConversation(toClientPayloadPacket);
        composer.emitConversation(SERVER_FIN_PACKET);
        composer.emitConversation(CLIENT_ACK_PACKET);
        expectedScript.add(Util.CLOSE_READ);
        expectedScript.add(payloadScriptWrite);
        expectedScript.add(Util.CLOSE_WRITE);
        expectedScript.add("closed");
        final String testScript = composer.getScript().substring(preTestScript.length());
        assertTrue(ScriptTestUtil.scriptIsInstanceOfScript(testScript, expectedScript));
    }
}
