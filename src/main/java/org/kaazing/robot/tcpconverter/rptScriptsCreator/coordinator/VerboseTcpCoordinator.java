/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.tcpconverter.rptScriptsCreator.coordinator;

import org.kaazing.robot.tcpconverter.packet.Packet;
import org.kaazing.robot.tcpconverter.rptScriptsCreator.ConversationId;
import org.kaazing.robot.tcpconverter.rptScriptsCreator.composer.ComposerFactory;
import org.kaazing.robot.tcpconverter.rptScriptsCreator.emitter.Emitter;
import org.kaazing.robot.tcpconverter.rptScriptsCreator.emitter.EmitterFactory;
import org.kaazing.robot.tcpconverter.rptScriptsCreator.emitter.OutputType;

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
