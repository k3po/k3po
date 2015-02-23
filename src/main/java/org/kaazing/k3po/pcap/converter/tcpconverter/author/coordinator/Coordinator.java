/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */
package org.kaazing.k3po.pcap.converter.tcpconverter.author.coordinator;

import org.kaazing.k3po.pcap.converter.tcpconverter.packet.Packet;
import org.kaazing.k3po.pcap.converter.tcpconverter.author.emitter.Emitter;

public interface Coordinator {

    /**
     * Starts a script fragments / init state
     * @param packet
     */
    public abstract void startScript(Packet packet);

    /**
     * Handles a conversation fragment of packet
     * @param packet
     */
    public abstract void conversation(Packet packet);

    /**
     * Returns whether all script fragments completed with a closed
     * @return
     */
    public abstract boolean isFinished();

    /**
     * Flushes all script fragments to files
     */
    public abstract void commitToFile();

    /**
     * Adds scripts to emitter that match ip
     * @param emitter
     * @param ip
     * @return
     */
    public Emitter addScriptToEmitter(Emitter emitter, String ip, String protocol);

    /**
     * Gets Script By Ip
     * @param ip
     * @return
     */
    public abstract String getScriptsByIp(String ip);

    public abstract String getServerScriptsByIp(String ip);

    public abstract String getClientScriptsByIp(String ip);
}
