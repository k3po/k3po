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

import org.jmock.Mockery;
import org.junit.Test;
import org.kaazing.k3po.pcap.converter.internal.author.ConversationId;
import org.kaazing.k3po.pcap.converter.internal.author.RptScriptCreator;
import org.kaazing.k3po.pcap.converter.internal.author.SupportedProtocol;
import org.kaazing.k3po.pcap.converter.internal.author.coordinator.Coordinator;
import org.kaazing.k3po.pcap.converter.internal.author.coordinator.CoordinatorFactory;
import org.kaazing.k3po.pcap.converter.internal.author.emitter.Emitter;
import org.kaazing.k3po.pcap.converter.internal.author.emitter.EmitterFactory;
import org.kaazing.k3po.pcap.converter.internal.packet.Packet;
import org.kaazing.k3po.pcap.converter.internal.utils.PacketUtil;

public class RptScriptCreatorTest {

    private final String ip1 = "111.111.111.111";
    private final String ip2 = "222.222.222.222";
    private final String ip3 = "333.333.333.333";
    private final int port1 = 1;
    private final int port2 = 2;
    private final int port3 = 3;

    @Test
    public void testAddNonSupportedProtocolPacket() {
        final Mockery context = new Mockery();
        final EmitterFactory emitterFactory = context.mock(EmitterFactory.class);
        final CoordinatorFactory coordinatorFactory = context.mock(CoordinatorFactory.class);
        final Emitter creatorNote = context.mock(Emitter.class);
        final Packet p1 = PacketUtil.createBasicPacket(ip1, ip2, port1, port2);
        final Emitter dummyEmitter = context.mock(Emitter.class, "dummyEmitter");

        context.checking(new RptScriptCreatorExpectations() {
            {
                willInitCreator(emitterFactory, creatorNote, dummyEmitter);
            }
        });

        RptScriptCreator creator = new RptScriptCreator(emitterFactory, coordinatorFactory);
        creator.addPacketToScripts(p1);
        context.assertIsSatisfied();
    }

    @Test
    public void testAddTcpPacket() {
        final Mockery context = new Mockery();
        final EmitterFactory emitterFactory = context.mock(EmitterFactory.class);
        final CoordinatorFactory coordinatorFactory = context.mock(CoordinatorFactory.class);
        final Emitter creatorNote = context.mock(Emitter.class);
        final Packet p1 = PacketUtil.createBasicTcpPacket(ip1, ip2, port1, port2);
        final Coordinator coord1 = context.mock(Coordinator.class);
        final Emitter dummyEmitter = context.mock(Emitter.class, "dummyEmitter");

        context.checking(new RptScriptCreatorExpectations() {
            {
                willInitCreator(emitterFactory, creatorNote, dummyEmitter);
                oneOf(coordinatorFactory).getCoordinator(with(equal(new ConversationId(p1, SupportedProtocol.TCP))));
                will(returnValue(coord1));
                allowing(coord1).isFinished();
                will(returnValue(false));
                oneOf(coord1).startScript(p1);
            }
        });

        RptScriptCreator creator = new RptScriptCreator(emitterFactory, coordinatorFactory);
        creator.addPacketToScripts(p1);
        context.assertIsSatisfied();
    }
    
    @Test
    public void testConductTcpConversation() {
        final Mockery context = new Mockery();
        final EmitterFactory emitterFactory = context.mock(EmitterFactory.class);
        final CoordinatorFactory coordinatorFactory = context.mock(CoordinatorFactory.class);
        final Emitter creatorNote = context.mock(Emitter.class);
        final Packet p1 = PacketUtil.createBasicTcpPacket(ip1, ip2, port1, port2);
        final Packet p2 = PacketUtil.createBasicTcpPacket(ip1, ip2, port2, port1);
        final Packet p3 = PacketUtil.createBasicTcpPacket(ip1, ip2, port1, port2);
        final Coordinator coord1 = context.mock(Coordinator.class);
        final Emitter dummyEmitter = context.mock(Emitter.class, "dummyEmitter");

        context.checking(new RptScriptCreatorExpectations() {
            {
                willInitCreator(emitterFactory, creatorNote, dummyEmitter);
                oneOf(coordinatorFactory).getCoordinator(with(equal(new ConversationId(p1, SupportedProtocol.TCP))));
                will(returnValue(coord1));
                allowing(coord1).isFinished();
                will(returnValue(false));
                oneOf(coord1).startScript(p1);
                oneOf(coord1).conversation(p2);
                oneOf(coord1).conversation(p3);
            }
        });

        RptScriptCreator creator = new RptScriptCreator(emitterFactory, coordinatorFactory);
        creator.addPacketToScripts(p1);
        creator.addPacketToScripts(p2);
        creator.addPacketToScripts(p3);
        context.assertIsSatisfied();
    }
    
    @Test
    public void testConductMultipleTcpConversation() {
        final Mockery context = new Mockery();
        final EmitterFactory emitterFactory = context.mock(EmitterFactory.class);
        final CoordinatorFactory coordinatorFactory = context.mock(CoordinatorFactory.class);
        final Emitter creatorNote = context.mock(Emitter.class);
        final Packet p1 = PacketUtil.createBasicTcpPacket(ip1, ip2, port1, port2);
        final Packet p2 = PacketUtil.createBasicTcpPacket(ip1, ip2, port2, port1);
        final Packet p3 = PacketUtil.createBasicTcpPacket(ip1, ip1, port1, port2);
        final Packet p4 = PacketUtil.createBasicTcpPacket(ip1, ip1, port1, port2);
        final Packet p5 = PacketUtil.createBasicTcpPacket(ip3, ip2, port3, port2);
        final Packet p6 = PacketUtil.createBasicTcpPacket(ip1, ip2, port1, port2);
        final Coordinator coord1 = context.mock(Coordinator.class, "coord1");
        final Coordinator coord2 = context.mock(Coordinator.class, "coord2");
        final Coordinator coord3 = context.mock(Coordinator.class, "coord3");
        final Emitter dummyEmitter = context.mock(Emitter.class, "dummyEmitter");

        context.checking(new RptScriptCreatorExpectations() {
            {
                willInitCreator(emitterFactory, creatorNote, dummyEmitter);
                oneOf(coordinatorFactory).getCoordinator(with(equal(new ConversationId(p1, SupportedProtocol.TCP))));
                will(returnValue(coord1));
                allowing(coord1).isFinished();
  
                will(returnValue(false));
                oneOf(coord1).startScript(p1);
                oneOf(coord1).conversation(p2);
                
                oneOf(coordinatorFactory).getCoordinator(with(equal(new ConversationId(p3, SupportedProtocol.TCP))));
                will(returnValue(coord2));
                allowing(coord2).isFinished();
                will(returnValue(false));
                
                oneOf(coord2).startScript(p3);
                oneOf(coord2).conversation(p4);
                
                oneOf(coordinatorFactory).getCoordinator(with(equal(new ConversationId(p5, SupportedProtocol.TCP))));
                will(returnValue(coord3));
                allowing(coord3).isFinished();
                will(returnValue(false));
                
                oneOf(coord3).startScript(p5);
                
                oneOf(coord1).conversation(p6);
            }
        });

        RptScriptCreator creator = new RptScriptCreator(emitterFactory, coordinatorFactory);
        creator.addPacketToScripts(p1);
        creator.addPacketToScripts(p2);
        creator.addPacketToScripts(p3);
        creator.addPacketToScripts(p4);
        creator.addPacketToScripts(p5);
        creator.addPacketToScripts(p6);
        context.assertIsSatisfied();
    }
    
    @Test
    public void testCommitToFile(){
        final Mockery context = new Mockery();
        final EmitterFactory emitterFactory = context.mock(EmitterFactory.class);
        final CoordinatorFactory coordinatorFactory = context.mock(CoordinatorFactory.class);
        final Emitter creatorNote = context.mock(Emitter.class);
        final Packet p1 = PacketUtil.createBasicTcpPacket(ip1, ip2, port1, port2);
        final Packet p2 = PacketUtil.createBasicTcpPacket(ip1, ip2, port2, port1);
        final Packet p3 = PacketUtil.createBasicTcpPacket(ip1, ip1, port1, port2);
        final Packet p4 = PacketUtil.createBasicTcpPacket(ip1, ip1, port1, port2);
        final Packet p5 = PacketUtil.createBasicTcpPacket(ip3, ip2, port3, port2);
        final Packet p6 = PacketUtil.createBasicTcpPacket(ip1, ip2, port1, port2);
        final Coordinator coord1 = context.mock(Coordinator.class, "coord1");
        final Coordinator coord2 = context.mock(Coordinator.class, "coord2");
        final Coordinator coord3 = context.mock(Coordinator.class, "coord3");
        final Emitter dummyEmitter = context.mock(Emitter.class, "dummyEmitter");

        context.checking(new RptScriptCreatorExpectations() {
            {
                willInitCreator(emitterFactory, creatorNote, dummyEmitter);
                oneOf(coordinatorFactory).getCoordinator(with(equal(new ConversationId(p1, SupportedProtocol.TCP))));
                will(returnValue(coord1));
                allowing(coord1).isFinished();
  
                will(returnValue(false));
                oneOf(coord1).startScript(p1);
                oneOf(coord1).conversation(p2);
                
                oneOf(coordinatorFactory).getCoordinator(with(equal(new ConversationId(p3, SupportedProtocol.TCP))));
                will(returnValue(coord2));
                allowing(coord2).isFinished();
                will(returnValue(false));
                
                oneOf(coord2).startScript(p3);
                oneOf(coord2).conversation(p4);
                
                oneOf(coordinatorFactory).getCoordinator(with(equal(new ConversationId(p5, SupportedProtocol.TCP))));
                will(returnValue(coord3));
                allowing(coord3).isFinished();
                will(returnValue(false));
                
                oneOf(coord3).startScript(p5);
                
                oneOf(coord1).conversation(p6);
                
                allowGettingScripts(coord1);
                allowGettingScripts(coord2);
                allowGettingScripts(coord3);
                
                atLeast(1).of(coord1).commitToFile();
                atLeast(1).of(coord2).commitToFile();
                atLeast(1).of(coord3).commitToFile();
            }
        });

        RptScriptCreator creator = new RptScriptCreator(emitterFactory, coordinatorFactory);
        creator.addPacketToScripts(p1);
        creator.addPacketToScripts(p2);
        creator.addPacketToScripts(p3);
        creator.addPacketToScripts(p4);
        creator.addPacketToScripts(p5);
        creator.addPacketToScripts(p6);
        creator.commitToFile();
        context.assertIsSatisfied();
        
    }

}
