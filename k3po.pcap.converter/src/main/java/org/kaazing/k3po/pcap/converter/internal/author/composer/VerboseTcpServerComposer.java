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

import org.kaazing.k3po.pcap.converter.internal.author.emitter.Emitter;
import org.kaazing.k3po.pcap.converter.internal.author.emitter.EmitterFactory;
import org.kaazing.k3po.pcap.converter.internal.packet.Packet;

public class VerboseTcpServerComposer extends TcpServerComposer {
    private final String NOTE_HEADER = "Scripts in this directory will be formatted contain all the ruperts scripts by server url, then client url, then script type \n";
    private final Emitter noteForScriptFragments = emitterFactory.getNoteEmitter(OUTPUT_TYPE, NOTE_HEADER);

    public VerboseTcpServerComposer(EmitterFactory emitterFactory, Emitter emitter, String ipaddress) {
        super(emitterFactory, emitter, ipaddress);
    }

    protected void processSynAckPacket(Packet packet) {
        super.processSynAckPacket(packet);
        noteForScriptFragments.add(formatFragmentName(packet.getSrcIpAddr(), packet.getSrcPort()) + " accepting form client "
                + packet.getDestIpAddr() + "-" + packet.getDestPort() + "\n");
    }
    
    public void writeToFile(){
        super.writeToFile();
        noteForScriptFragments.commitToFile();
    }
}
