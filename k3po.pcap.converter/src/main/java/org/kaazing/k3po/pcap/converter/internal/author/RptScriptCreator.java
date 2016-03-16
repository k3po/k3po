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
package org.kaazing.k3po.pcap.converter.internal.author;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Logger;

import org.kaazing.k3po.pcap.converter.internal.author.composer.ComposerFactory;
import org.kaazing.k3po.pcap.converter.internal.author.composer.GenericComposerFactory;
import org.kaazing.k3po.pcap.converter.internal.author.coordinator.Coordinator;
import org.kaazing.k3po.pcap.converter.internal.author.coordinator.CoordinatorFactory;
import org.kaazing.k3po.pcap.converter.internal.author.coordinator.CoordinatorFactoryImpl;
import org.kaazing.k3po.pcap.converter.internal.author.emitter.Emitter;
import org.kaazing.k3po.pcap.converter.internal.author.emitter.EmitterFactory;
import org.kaazing.k3po.pcap.converter.internal.author.emitter.EmitterFactoryImpl;
import org.kaazing.k3po.pcap.converter.internal.author.emitter.OutputType;
import org.kaazing.k3po.pcap.converter.internal.packet.Packet;
import org.kaazing.k3po.pcap.converter.internal.parser.Parser;

/**
 * RptScriptsCreator
 * 
 * Creates the Rupert Scripts by feeding it a packet at a time
 * 
 */
public class RptScriptCreator {

    private final static Logger LOG = Logger.getLogger(Parser.class.getName());
    private final Map<ConversationId, Stack<Coordinator>> coordinators;
    private final EmitterFactory emitterFactory;
    private final CoordinatorFactory coordinatorFactory;

    private final String CREATOR_NOTE_HEADER = "See subfolders for more specific scripts, see READMEs for what each dir contains \n\n"
            + "Scripts saved in this folder correspond to a complete interaction on a node qualified \n"
            + "by either protocol and/or the side (client/server) of conversation \n "
            + "ex)\t ip1.rpt will have all interaction on a node \n"
            + "\t ip1-tcp.rpt will have all tcp interaction on a node \n"
            + "\t ip1-tcp-server.rpt will have all tcp server interaction on a node \n";
    private final Emitter creatorNote;

    
    public RptScriptCreator() {
        emitterFactory = new EmitterFactoryImpl();
        ComposerFactory composerFactory = new GenericComposerFactory(emitterFactory);
        coordinatorFactory = new CoordinatorFactoryImpl(composerFactory, emitterFactory);
        creatorNote = emitterFactory.getNoteEmitter(OutputType.CREATOR, CREATOR_NOTE_HEADER);
        coordinators = new HashMap<>();

    }
    
    public RptScriptCreator(EmitterFactory emitterFactory, CoordinatorFactory coordinatorFactory){
        this.emitterFactory = emitterFactory;
        this.coordinatorFactory = coordinatorFactory;
        creatorNote = emitterFactory.getNoteEmitter(OutputType.CREATOR, CREATOR_NOTE_HEADER);
        coordinators = new HashMap<>();
    }

    public void addPacketToScripts(Packet packet) {
        LOG.info("Emitting packet " + packet.getPacketNumber());

        if ( packet.isTcp() ) {
            LOG.info("Emitting tcp packet");
            ConversationId conversationId = new ConversationId(packet, SupportedProtocol.TCP);
            
            if ( !coordinators.containsKey(conversationId) ) { // if no key for host pair create it
                coordinators.put(conversationId, new Stack<Coordinator>());
            }
            
            Stack<Coordinator> set = coordinators.get(conversationId);
            if ( set.empty() || set.peek().isFinished() ) {
                Coordinator coordinator = coordinatorFactory.getCoordinator(conversationId);
                coordinator.startScript(packet);
                set.push(coordinator);
                coordinators.put(conversationId, set);
            }
            else {
                set.peek().conversation(packet);
            }
            return;
        }
        return;
    }

    public void commitToFile() {
        for (Stack<Coordinator> stack : coordinators.values()) {
            for (Coordinator cord : stack) {
                cord.commitToFile();
            }
        }

        KeyToEmitterHashMap topLevelEmitters = new KeyToEmitterHashMap();

        for (ConversationId convId : coordinators.keySet()) {
            topLevelEmitters.put(convId.getIpAddr1());
            topLevelEmitters.put(convId.getIpAddr2());
            // tcp
            topLevelEmitters.put(convId.getIpAddr1() + "-" + convId.getProtocol());
            topLevelEmitters.put(convId.getIpAddr1() + "-" + convId.getProtocol() + "-server");
            topLevelEmitters.put(convId.getIpAddr1() + "-" + convId.getProtocol() + "-client");
            topLevelEmitters.put(convId.getIpAddr2() + "-" + convId.getProtocol());
            topLevelEmitters.put(convId.getIpAddr2() + "-" + convId.getProtocol() + "-server");
            topLevelEmitters.put(convId.getIpAddr2() + "-" + convId.getProtocol() + "-client");
        }

        for (ConversationId convId : coordinators.keySet()) {
            creatorNote.add((convId.getName() + "\n"));
            for (Coordinator coordinator : coordinators.get(convId)) {
                topLevelEmitters.get(convId.getIpAddr1() + "-" + convId.getProtocol()).add(
                        coordinator.getScriptsByIp(convId.getIpAddr1()));
                topLevelEmitters.get(convId.getIpAddr2() + "-" + convId.getProtocol()).add(
                        coordinator.getScriptsByIp(convId.getIpAddr2()));
                topLevelEmitters.get(convId.getIpAddr1() + "-" + convId.getProtocol() + "-server").add(
                        coordinator.getServerScriptsByIp(convId.getIpAddr1()));
                topLevelEmitters.get(convId.getIpAddr2() + "-" + convId.getProtocol() + "-server").add(
                        coordinator.getServerScriptsByIp(convId.getIpAddr2()));
                topLevelEmitters.get(convId.getIpAddr1() + "-" + convId.getProtocol() + "-client").add(
                        coordinator.getClientScriptsByIp(convId.getIpAddr1()));
                topLevelEmitters.get(convId.getIpAddr2() + "-" + convId.getProtocol() + "-client").add(
                        coordinator.getClientScriptsByIp(convId.getIpAddr2()));
            }
        }
        creatorNote.commitToFile();

        for (Emitter emitter : topLevelEmitters.values()) {
            emitter.commitToFile();
        }

    }

    /**
     * Special HashMap where key matches emitter's name (ie the value's name)
     * 
     */
    private class KeyToEmitterHashMap extends HashMap<String, Emitter> {

        private static final long serialVersionUID = 1L;

        @Override
        public Emitter put(String key, Emitter value) {
            throw new RptScriptsCreatorFailureException(
                    "Misuse of this Class, This method is not implemented for this special hashmap");
        }

        public Emitter put(String key) {
            if ( !containsKey(key) ) {
                super.put(key, emitterFactory.getRptScriptEmitter(OutputType.CREATOR, key));
            }
            return get(key);
        }
    }

	public void saveMemory() {
		emitterFactory.setMemSaver(true);	
	}
}
