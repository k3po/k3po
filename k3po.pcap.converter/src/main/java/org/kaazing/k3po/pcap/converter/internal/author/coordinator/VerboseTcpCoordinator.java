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
import org.kaazing.k3po.pcap.converter.internal.author.emitter.OutputType;
import org.kaazing.k3po.pcap.converter.internal.packet.Packet;

class VerboseTcpCoordinator extends TcpCoordinator {
    public final Emitter tcpServerNote;
    public final Emitter tcpClientNote;
    private final Emitter tcpCoordinatorNote;
    private static final String SERVER_NOTE_HEADER = "Scripts Saved in this folder corrispond to \"ThisServerIP-ConnectTo-ClientIp\" \n"
            + "where the script is written from ThisServerIp's perspective ";
    private static final String CLIENT_NOTE_HEADER = "Scripts Saved in this folder corrispond to \"ThisClientIP-ConnectTo-ServerIp\" \n"
            + "where the script is written from ThisClientIp's perspective ";
    private static final String COORDINATOR_NOTE_HEADER = "Scripts Saved in this folder corrispond to a nodes complete set of interactions \n"
            + "between to specific ips.  For example as long as there is a connection open between \n "
            + "ip1 and ip2, all rpt scripts (both client and server) will be added to the ip script \n"
            + "here.  Multiple ip scripts with differentiated tag # may appear for two reasons: \n\n"
            + "1) 2 or more sessions where recorded (ip1 talking to ip2, and ip1 talking to ip3 \n\n"
            + "2) Two sessions between endpoints with a break in the middle (ip1 talks to ip2, \n"
            + "communication stops, ip1 communicates again to ip2 a second time";
    
    public VerboseTcpCoordinator(EmitterFactory emitterFactory, ConversationId conversationId,
            ComposerFactory composerFactory) {
        super(emitterFactory, conversationId, composerFactory);
        tcpServerNote = emitterFactory.getNoteEmitter(OutputType.TCP_SERVER_COMPOSER, SERVER_NOTE_HEADER);
        tcpClientNote = emitterFactory.getNoteEmitter(OutputType.TCP_CLIENT_COMPOSER, CLIENT_NOTE_HEADER);
        tcpCoordinatorNote = emitterFactory.getNoteEmitter(OutputType.TCP_COORDINATOR, COORDINATOR_NOTE_HEADER);
        tcpCoordinatorNote.add(conversationId.getName() + "\n");
        tcpCoordinatorNote.commitToFile();
    }

    protected void synack(Packet packet) {
        super.synack(packet);
        tcpClientNote.add(clientName(packet) + " synack" + packet.getTimeStamp() + "\n");
        tcpServerNote.add(serverName(packet) + " synack " + packet.getTimeStamp() + "\n");
    }
}
