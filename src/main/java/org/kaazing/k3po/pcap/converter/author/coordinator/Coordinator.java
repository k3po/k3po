/*
 * Copyright 2014, Kaazing Corporation. All rights reserved.
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
package org.kaazing.k3po.pcap.converter.author.coordinator;

import org.kaazing.k3po.pcap.converter.packet.Packet;
import org.kaazing.k3po.pcap.converter.author.emitter.Emitter;

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
