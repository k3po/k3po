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

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.kaazing.k3po.pcap.converter.internal.author.RptScriptsCreatorFailureException;
import org.kaazing.k3po.pcap.converter.internal.author.emitter.Emitter;
import org.kaazing.k3po.pcap.converter.internal.author.emitter.EmitterFactory;
import org.kaazing.k3po.pcap.converter.internal.author.emitter.OutputType;
import org.kaazing.k3po.pcap.converter.internal.author.script.AbstractScript;
import org.kaazing.k3po.pcap.converter.internal.author.script.ScriptState;
import org.kaazing.k3po.pcap.converter.internal.author.script.TcpScript;
import org.kaazing.k3po.pcap.converter.internal.packet.Packet;

/**
 * Composes the rupert scripts by having several script fragments linked to port locations and in different states
 * 
 */
public class TcpServerComposer extends AbstractComposer {

    private final Map<String, TcpServerScript> scriptFragments = new HashMap<>();;
    private final static Logger LOG = Logger.getLogger(TcpServerComposer.class.getName());
    protected final static OutputType OUTPUT_TYPE = OutputType.TCP_SERVER_SCRIPT;

    public TcpServerComposer(EmitterFactory emitterFactory, Emitter emitter, String ipaddress) {
        super(emitterFactory, emitter, ipaddress);
        LOG.fine("Creating tcp server composer for " + ipaddress);
    }

    @Override
    public void emitConversation(Packet packet) {
        if ( packet.isTcpFlagsAck() && packet.isTcpFlagsSyn() ) {
            processSynAckPacket(packet);
            return;
        }
        String destId = makeClientId(packet.getDestIpAddr(), packet.getDestPort());
        String srcId = makeClientId(packet.getSrcIpAddr(), packet.getSrcPort());
        long sequenceNumber = packet.getTcpSequenceNumber();
        if ( scriptFragments.containsKey(destId) ) {
            TcpServerScript fragment = scriptFragments.get(destId);
            // Outbound Packet
            if ( packet.getTcpPayloadSize() > 0 ) {
                if ( !fragment.recordSeqNumAndReturnTrueOnNewEntry(sequenceNumber, packet.getTcpPayloadSize()) ) {
                    LOG.fine("Replayed tcp packet at packet: " + packet.getPacketNumber());
                    return;
                }
                fragment.writePayloadOfTcpPacket(packet);
            }
            if ( packet.isTcpFlagsFin() ) {
                fragment.setClosingWriteAck(sequenceNumber);
            }
            if ( fragment.isClosingReadAck(packet.getTcpAcknowledgementNumber()) ) {
                fragment.writeCloseRead(packet.getTimeInMicroSecondsFromEpoch());
            }
        }
        else if ( scriptFragments.containsKey(srcId) ) {
            // Inbound Packet
            TcpServerScript fragment = scriptFragments.get(srcId);
            if ( fragment.getState() == ScriptState.ACCEPT && packet.isTcpFlagsAck() ) {
                fragment.writeConnected(packet.getTimeInMicroSecondsFromEpoch());
            }
            if ( packet.getTcpPayloadSize() > 0 ) {
                if ( !fragment.recordSeqNumAndReturnTrueOnNewEntry(sequenceNumber, packet.getTcpPayloadSize()) ) {
                    LOG.info("Replayed tcp packet at packet: " + packet.getPacketNumber());
                    return;
                }
                fragment.readPayloadOfTcpPacket(packet);
            }
            if ( packet.isTcpFlagsFin() ) {
                fragment.setClosingReadAck(sequenceNumber);
            }
            if ( fragment.isClosingWriteAck(packet.getTcpAcknowledgementNumber()) ) {
                fragment.writeCloseWrite(packet.getTimeInMicroSecondsFromEpoch());
            }
        }
    }

    @Override
    public boolean isFinished() {
        for (String i : scriptFragments.keySet()) {
            if ( scriptFragments.get(i).getState() != ScriptState.CLOSED
                    && scriptFragments.get(i).getState() != ScriptState.NOT_INITED ) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void writeToFile() {
        for (TcpServerScript iter : scriptFragments.values()) {
            iter.writeBufferToFile();
        }
        addScriptFragmentsIntoBuffer();
        commitToFile();
    }

    @Override
    public String getScript() {
        addScriptFragmentsIntoBuffer();
        return getBuffer();
    }

    /**
     * Processes the syn-ack packet which notes the start of tcp conversation
     * @param packet
     */
    protected void processSynAckPacket(Packet packet) {
        String serverIp = packet.getSrcIpAddr();
        int serverPort = packet.getSrcPort();
        String clientIp = packet.getDestIpAddr();
        int clientPort = packet.getDestPort();
        String clientId = makeClientId(clientIp, clientPort);
        TcpServerScript serverFragmentWriter = scriptFragments.get(clientId);
        if ( serverFragmentWriter == null ) {
            serverFragmentWriter = new TcpServerScript(emitterFactory.getRptScriptEmitter(OUTPUT_TYPE,
                    "tcp-server-"
                            + serverIp + "-" + serverPort + "-client-" + clientIp + "-" + clientPort + "-ServerSide"));
        }
        else if ( serverFragmentWriter.getState() != ScriptState.CLOSED ) {
            throw new RptScriptsCreatorFailureException(
                    "Attempting to open already opened tcp connection from server composer:" + clientId);
        }
        serverFragmentWriter.writeAccept(serverIp, serverPort, packet);
        scriptFragments.put(clientId, serverFragmentWriter);
    }

    private void addScriptFragmentsIntoBuffer() {
        clearBuffer();
        for (AbstractScript rw1 : scriptFragments.values()) {
            addToScript(rw1.getBuffer());
        }
    }

    private static String makeClientId(String clientIp, int port) {
        return clientIp + ":" + port;
    }

    private class TcpServerScript extends TcpScript {

        public TcpServerScript(Emitter emitter) {
            super(emitter);
        }

        public void writeAccept(String ipAddress, int port, Packet packet) {
            setLastActionTime(packet.getTimeInMicroSecondsFromEpoch());
            writeMetaData("Accepting at epoch " + packet.getTimeStamp() + "  -  " + packet.getTimeInMicroSecondsFromEpoch());
            writeln("accept tcp://" + ipAddress + ":" + port);
            writeln("accepted");
            setState(ScriptState.ACCEPT);
        }
    }

    protected static String formatFragmentName(String ipAddr, Integer serverPort) {
        return formatFragmentName(ipAddr, serverPort.toString());
    }

    protected static String formatFragmentName(String ipAddr, String serverPort) {
        return ipAddr + SEP + serverPort;
    }





}
