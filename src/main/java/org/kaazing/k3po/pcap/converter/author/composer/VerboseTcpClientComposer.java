/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.k3po.pcap.converter.author.composer;

import org.kaazing.k3po.pcap.converter.packet.Packet;
import org.kaazing.k3po.pcap.converter.author.emitter.Emitter;
import org.kaazing.k3po.pcap.converter.author.emitter.EmitterFactory;

public class VerboseTcpClientComposer extends TcpClientComposer {
    private final String NOTE_HEADER = "Scripts in this directory will be formatted contain all the ruperts scripts by server url, then client url, then script type \n";
    private final Emitter noteForScriptFragments = emitterFactory.getNoteEmitter(OUTPUT_TYPE, NOTE_HEADER);

    public VerboseTcpClientComposer(EmitterFactory emitterFactory, Emitter emitter, String ipaddress) {
        super(emitterFactory, emitter, ipaddress);
    }

    @Override
    public void writeToFile() {
        super.writeToFile();
        noteForScriptFragments.commitToFile();
    }

    protected void processSynAckPacket(Packet packet) {
        super.processSynAckPacket(packet);
        noteForScriptFragments.add(formatFragmentName(ipaddress, packet.getSrcPort()) + " connect to server " + packet.getDestIpAddr() + "-"
                + packet.getDestPort() + "\n");
    }
}
