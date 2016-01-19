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
public class TcpClientComposer extends AbstractComposer {

    private final Map<Integer, TcpClientScript> scripts = new HashMap<>();
    private final static Logger LOG = Logger.getLogger(TcpClientComposer.class.getName());
    protected final static OutputType OUTPUT_TYPE = OutputType.TCP_CLIENT_SCRIPT;

    public TcpClientComposer(EmitterFactory emitterFactory, Emitter emitter, String ipaddress) {
        super(emitterFactory, emitter, ipaddress);
        LOG.fine("Creating tcp client composer for " + this.ipaddress);
    }

    @Override
    public void emitConversation(Packet packet) {
        if ( packet.isTcpFlagsAck() && packet.isTcpFlagsSyn() ) {
            processSynAckPacket(packet);
            return;
        }
        int destPort = packet.getDestPort();
        int srcPort = packet.getSrcPort();
        long sequenceNumber = packet.getTcpSequenceNumber();
        if ( packet.getDestIpAddr().equals(ipaddress) && scripts.containsKey(destPort) ) {
            // Inbound packet
            TcpClientScript script = scripts.get(destPort);
            if ( packet.getTcpPayloadSize() > 0 ) {
                if ( !script.recordSeqNumAndReturnTrueOnNewEntry(sequenceNumber, packet.getTcpPayloadSize()) ) {
                    LOG.fine("Replayed tcp packet at packet:" + packet.getPacketNumber());
                    return;
                }
                script.readPayloadOfTcpPacket(packet);
            }
            if ( packet.isTcpFlagsFin() ) {
                script.setClosingReadAck(sequenceNumber);
            }
            if ( script.isClosingWriteAck(packet.getTcpAcknowledgementNumber()) && packet.isTcpFlagsAck() ) {
                script.writeCloseWrite(packet.getTimeInMicroSecondsFromEpoch());
            }
        }
        else if ( ipaddress.equals(packet.getSrcIpAddr()) && scripts.containsKey(srcPort) ) {
            // Outbound packet
            TcpClientScript script = scripts.get(srcPort);
            if ( script.getState() == ScriptState.CONNECT ) {
                if ( packet.isTcpFlagsAck() ) {
                    script.writeConnected(packet.getTimeInMicroSecondsFromEpoch());
                }
            }
            if ( packet.getTcpPayloadSize() > 0 ) {
                if ( !script.recordSeqNumAndReturnTrueOnNewEntry(sequenceNumber, packet.getTcpPayloadSize()) ) {
                    LOG.fine("Replayed tcp packet at packet:" + packet.getPacketNumber());
                    return;
                }
                script.writePayloadOfTcpPacket(packet);
            }
            if ( packet.isTcpFlagsFin() ) {
                script.setClosingWriteAck(sequenceNumber);
            }
            if ( script.isClosingReadAck(packet.getTcpAcknowledgementNumber()) ) {
                script.writeCloseRead(packet.getTimeInMicroSecondsFromEpoch());
            }
        }
    }

    @Override
    public void writeToFile() {
        for (TcpClientScript iter : scripts.values()) {
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

    @Override
    public boolean isFinished() {
        for (Integer i : scripts.keySet()) {
            if ( scripts.get(i).getState() != ScriptState.CLOSED
                    && scripts.get(i).getState() != ScriptState.NOT_INITED ) {
                return false;
            }
        }
        return true;
    }

    /**
     * Process a connection packet which is read as an syn-ack
     * @param packet
     */
    protected void processSynAckPacket(Packet packet) {
        Integer clientPort = packet.getDestPort();
        Integer serverPort = packet.getSrcPort();
        String serverIp = packet.getSrcIpAddr();
        TcpClientScript scriptFragmentWriter = scripts.get(clientPort);
        if ( scriptFragmentWriter == null ) {
            scriptFragmentWriter = new TcpClientScript(emitterFactory.getRptScriptEmitter(OUTPUT_TYPE, "tcp-server-"
                    + serverIp + "-" + serverPort + "-client-" + ipaddress + "-" + clientPort + "-ClientSide"));
        }
        else if ( scriptFragmentWriter.getState() != ScriptState.CLOSED ) {
            throw new RptScriptsCreatorFailureException(
                    "Attempting to open already opened tcp connection from client port:" + clientPort);
        }

        scriptFragmentWriter.writeConnect(serverIp, serverPort, packet);
        scripts.put(clientPort, scriptFragmentWriter);

    }

    private void addScriptFragmentsIntoBuffer() {
        clearBuffer();
        for (AbstractScript rw1 : scripts.values()) {
            addToScript(rw1.getBuffer());
        }
    }

    private class TcpClientScript extends TcpScript {

        public TcpClientScript(Emitter emitter) {
            super(emitter);
        }

        public void writeConnect(String serverIp, int serverPort, Packet packet) {
            super.setLastActionTime(packet.getTimeInMicroSecondsFromEpoch());
            writeMetaData("Connect occured at epoch " + packet.getTimeStamp() + "  -  " + packet.getTimeInMicroSecondsFromEpoch() );
            setState(ScriptState.CONNECT);
            writeln("connect tcp://" + serverIp + ":" + serverPort);
        }
    }

    protected static String formatFragmentName(String ipAddr, Integer clientPort) {
        return formatFragmentName(ipAddr, clientPort.toString());
    }

    protected static String formatFragmentName(String ipAddr, String clientPort) {
        return ipAddr + SEP + clientPort;
    }
}
