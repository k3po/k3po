/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.k3po.pcap.converter;

import org.jmock.Mockery;
import org.junit.Test;

import org.kaazing.k3po.pcap.converter.packet.Packet;
import org.kaazing.k3po.pcap.converter.author.ConversationId;
import org.kaazing.k3po.pcap.converter.author.SupportedProtocol;
import org.kaazing.k3po.pcap.converter.author.composer.ComposerFactory;
import org.kaazing.k3po.pcap.converter.author.composer.Composer;
import org.kaazing.k3po.pcap.converter.author.coordinator.TcpCoordinator;
import org.kaazing.k3po.pcap.converter.author.emitter.Emitter;
import org.kaazing.k3po.pcap.converter.author.emitter.EmitterFactory;
import org.kaazing.k3po.pcap.converter.utils.PacketUtil;

public class TcpCoordinatorTest extends AbstractTcpTest {

    final private static class TcpCoordinatorStub extends TcpCoordinator {

        public TcpCoordinatorStub(EmitterFactory emitterFactory, ConversationId conversationId,
                ComposerFactory composerFactory) {
            super(emitterFactory, conversationId, composerFactory);
        }

        public static final String clientNameStub(Packet p) {
            return clientName(p);
        }

        public static final String serverNameStub(Packet p) {
            return serverName(p);
        }
    }

    @Test
    public void testCreateCoordinator() {
        final Mockery context = new Mockery();

        final EmitterFactory emitterFactory = context.mock(EmitterFactory.class);
        final ConversationId conversationId = new ConversationId(CLIENT_IP, SERVER_IP, SupportedProtocol.TCP);
        final ComposerFactory composerFactory = context.mock(ComposerFactory.class);
        final Emitter ipEmitter1 = context.mock(Emitter.class, "emitter1");
        final Emitter ipEmitter2 = context.mock(Emitter.class, "emitter2");
        context.checking(new TcpCoordinatorExpectations() {
            {
                willInit(emitterFactory, conversationId, composerFactory, ipEmitter1, ipEmitter2);
            }
        });

        new TcpCoordinator(emitterFactory, conversationId, composerFactory);
        context.assertIsSatisfied();
    }

    @Test
    public void testCreateTcpClient() {
        final Mockery context = new Mockery();
        final EmitterFactory emitterFactory = context.mock(EmitterFactory.class);
        final ConversationId conversationId = new ConversationId(CLIENT_IP, SERVER_IP, SupportedProtocol.TCP);
        final ComposerFactory composerFactory = context.mock(ComposerFactory.class);
        final Emitter ipEmitter1 = context.mock(Emitter.class, "emitter1");
        final Emitter ipEmitter2 = context.mock(Emitter.class, "emitter2");
        final Composer clientComp = context.mock(Composer.class, "clientComp");
        final Composer serverComp = context.mock(Composer.class, "serverComp");
        final Packet synAck = PacketUtil.getTcpSynAckPacket(CLIENT_IP, SERVER_IP, CLIENT_PORT, SERVER_PORT);
        context.checking(new TcpCoordinatorExpectations() {
            {
                willInit(emitterFactory, conversationId, composerFactory, ipEmitter1, ipEmitter2);
                willCreateTcpClientComposer(composerFactory, synAck.getDestIpAddr(),
                        TcpCoordinatorStub.clientNameStub(synAck), clientComp);
                oneOf(clientComp).emitConversation(synAck);
                willCreateTcpServerComposer(composerFactory, synAck.getSrcIpAddr(),
                        TcpCoordinatorStub.serverNameStub(synAck), serverComp);
                oneOf(serverComp).emitConversation(synAck);
            }
        });

        TcpCoordinator cord = new TcpCoordinator(emitterFactory, conversationId, composerFactory);
        cord.startScript(synAck);
        context.assertIsSatisfied();
    }
    
    @Test
    public void testDistributeToSingleTcpClient() {
        final Mockery context = new Mockery();
        final EmitterFactory emitterFactory = context.mock(EmitterFactory.class);
        final ConversationId conversationId = new ConversationId(CLIENT_IP, SERVER_IP, SupportedProtocol.TCP);
        final ComposerFactory composerFactory = context.mock(ComposerFactory.class);
        final Emitter ipEmitter1 = context.mock(Emitter.class, "emitter1");
        final Emitter ipEmitter2 = context.mock(Emitter.class, "emitter2");
        final Composer clientComp = context.mock(Composer.class, "clientComp");
        final Composer serverComp = context.mock(Composer.class, "serverComp");
        final Packet synAck = PacketUtil.getTcpSynAckPacket(CLIENT_IP, SERVER_IP, CLIENT_PORT, SERVER_PORT);
        final Packet fin = PacketUtil.getTcpFinAckPacket(CLIENT_IP, SERVER_IP, CLIENT_PORT, SERVER_PORT, 1, 2);
        context.checking(new TcpCoordinatorExpectations() {
            {
                willInit(emitterFactory, conversationId, composerFactory, ipEmitter1, ipEmitter2);
                willCreateTcpClientComposer(composerFactory, synAck.getDestIpAddr(),
                        TcpCoordinatorStub.clientNameStub(synAck), clientComp);
                oneOf(clientComp).emitConversation(synAck);
                willCreateTcpServerComposer(composerFactory, synAck.getSrcIpAddr(),
                        TcpCoordinatorStub.serverNameStub(synAck), serverComp);
                oneOf(serverComp).emitConversation(synAck);
                oneOf(clientComp).emitConversation(fin);
                oneOf(serverComp).emitConversation(fin);
            }
        });

        TcpCoordinator cord = new TcpCoordinator(emitterFactory, conversationId, composerFactory);
        cord.startScript(synAck);
        cord.conversation(fin);
        context.assertIsSatisfied();
    }
    
    @Test
    public void testDistributeToSingleTcpClientMultlipleSynAcks() {
        final Mockery context = new Mockery();
        final EmitterFactory emitterFactory = context.mock(EmitterFactory.class);
        final ConversationId conversationId = new ConversationId(CLIENT_IP, SERVER_IP, SupportedProtocol.TCP);
        final ComposerFactory composerFactory = context.mock(ComposerFactory.class);
        final Emitter ipEmitter1 = context.mock(Emitter.class, "emitter1");
        final Emitter ipEmitter2 = context.mock(Emitter.class, "emitter2");
        final Composer clientComp = context.mock(Composer.class, "clientComp");
        final Composer serverComp = context.mock(Composer.class, "serverComp");
        final Packet synAck = PacketUtil.getTcpSynAckPacket(CLIENT_IP, SERVER_IP, CLIENT_PORT, SERVER_PORT);
        context.checking(new TcpCoordinatorExpectations() {
            {
                willInit(emitterFactory, conversationId, composerFactory, ipEmitter1, ipEmitter2);
                willCreateTcpClientComposer(composerFactory, synAck.getDestIpAddr(),
                        TcpCoordinatorStub.clientNameStub(synAck), clientComp);
                oneOf(clientComp).emitConversation(synAck);
                willCreateTcpServerComposer(composerFactory, synAck.getSrcIpAddr(),
                        TcpCoordinatorStub.serverNameStub(synAck), serverComp);
                oneOf(serverComp).emitConversation(synAck);
                oneOf(clientComp).emitConversation(synAck);
                oneOf(serverComp).emitConversation(synAck);
            }
        });
        TcpCoordinator cord = new TcpCoordinator(emitterFactory, conversationId, composerFactory);
        cord.startScript(synAck);
        cord.conversation(synAck);
        context.assertIsSatisfied();
    }
    
    @Test
    public void testTcp2SetsOfCoordinators(){
        final Mockery context = new Mockery();
        final EmitterFactory emitterFactory = context.mock(EmitterFactory.class);
        final ConversationId conversationId = new ConversationId(CLIENT_IP, SERVER_IP, SupportedProtocol.TCP);
        final ComposerFactory composerFactory = context.mock(ComposerFactory.class);
        final Emitter ipEmitter1 = context.mock(Emitter.class, "emitter1");
        final Emitter ipEmitter2 = context.mock(Emitter.class, "emitter2");
        final Composer clientComp = context.mock(Composer.class, "clientComp");
        final Composer serverComp = context.mock(Composer.class, "serverComp");
        final Composer clientComp2 = context.mock(Composer.class, "clientComp2");
        final Composer serverComp2 = context.mock(Composer.class, "serverComp2");
        final Packet synAck = PacketUtil.getTcpSynAckPacket(CLIENT_IP, SERVER_IP, CLIENT_PORT, SERVER_PORT);
        final Packet synAck2 = PacketUtil.getTcpSynAckPacket(SERVER_IP, CLIENT_IP, CLIENT_PORT, SERVER_PORT);
        context.checking(new TcpCoordinatorExpectations() {
            {
                willInit(emitterFactory, conversationId, composerFactory, ipEmitter1, ipEmitter2);
                
                willCreateTcpClientComposer(composerFactory, synAck.getDestIpAddr(),
                        TcpCoordinatorStub.clientNameStub(synAck), clientComp);
                oneOf(clientComp).emitConversation(synAck);
                willCreateTcpServerComposer(composerFactory, synAck.getSrcIpAddr(),
                        TcpCoordinatorStub.serverNameStub(synAck), serverComp);
                oneOf(serverComp).emitConversation(synAck);
                
                willCreateTcpClientComposer(composerFactory, synAck2.getDestIpAddr(),
                        TcpCoordinatorStub.clientNameStub(synAck2), clientComp2);
                oneOf(clientComp2).emitConversation(synAck2);
                willCreateTcpServerComposer(composerFactory, synAck2.getSrcIpAddr(),
                        TcpCoordinatorStub.serverNameStub(synAck2), serverComp2);
                oneOf(serverComp2).emitConversation(synAck2);
            }
        });
        TcpCoordinator cord = new TcpCoordinator(emitterFactory, conversationId, composerFactory);
        cord.startScript(synAck);
        cord.conversation(synAck2);
        context.assertIsSatisfied();
    }
    
    @Test
    public void testIsFinishedWithAllFinished(){
        final Mockery context = new Mockery();
        final EmitterFactory emitterFactory = context.mock(EmitterFactory.class);
        final ConversationId conversationId = new ConversationId(CLIENT_IP, SERVER_IP, SupportedProtocol.TCP);
        final ComposerFactory composerFactory = context.mock(ComposerFactory.class);
        final Emitter ipEmitter1 = context.mock(Emitter.class, "emitter1");
        final Emitter ipEmitter2 = context.mock(Emitter.class, "emitter2");
        final Composer clientComp = context.mock(Composer.class, "clientComp");
        final Composer serverComp = context.mock(Composer.class, "serverComp");
        final Packet synAck = PacketUtil.getTcpSynAckPacket(CLIENT_IP, SERVER_IP, CLIENT_PORT, SERVER_PORT);
        context.checking(new TcpCoordinatorExpectations() {
            {
                //setup
                willInit(emitterFactory, conversationId, composerFactory, ipEmitter1, ipEmitter2);
                
                willCreateTcpClientComposer(composerFactory, synAck.getDestIpAddr(),
                        TcpCoordinatorStub.clientNameStub(synAck), clientComp);
                oneOf(clientComp).emitConversation(synAck);
                willCreateTcpServerComposer(composerFactory, synAck.getSrcIpAddr(),
                        TcpCoordinatorStub.serverNameStub(synAck), serverComp);  
                oneOf(serverComp).emitConversation(synAck);
                
                allowing(clientComp).isFinished(); will(returnValue(true));
                allowing(serverComp).isFinished(); will(returnValue(true));
            }
        });
        TcpCoordinator cord = new TcpCoordinator(emitterFactory, conversationId, composerFactory);
        cord.startScript(synAck);
        assertTrue(cord.isFinished());
        context.assertIsSatisfied();
    }
    
    @Test
    public void testIsFinishedWithNotAllFinished(){
        final Mockery context = new Mockery();
        final EmitterFactory emitterFactory = context.mock(EmitterFactory.class);
        final ConversationId conversationId = new ConversationId(CLIENT_IP, SERVER_IP, SupportedProtocol.TCP);
        final ComposerFactory composerFactory = context.mock(ComposerFactory.class);
        final Emitter ipEmitter1 = context.mock(Emitter.class, "emitter1");
        final Emitter ipEmitter2 = context.mock(Emitter.class, "emitter2");
        final Composer clientComp = context.mock(Composer.class, "clientComp");
        final Composer serverComp = context.mock(Composer.class, "serverComp");
        final Packet synAck = PacketUtil.getTcpSynAckPacket(CLIENT_IP, SERVER_IP, CLIENT_PORT, SERVER_PORT);
        context.checking(new TcpCoordinatorExpectations() {
            {
                //setup
                willInit(emitterFactory, conversationId, composerFactory, ipEmitter1, ipEmitter2);
                
                willCreateTcpClientComposer(composerFactory, synAck.getDestIpAddr(),
                        TcpCoordinatorStub.clientNameStub(synAck), clientComp);
                oneOf(clientComp).emitConversation(synAck);
                willCreateTcpServerComposer(composerFactory, synAck.getSrcIpAddr(),
                        TcpCoordinatorStub.serverNameStub(synAck), serverComp);  
                oneOf(serverComp).emitConversation(synAck);
                
                allowing(clientComp).isFinished(); will(returnValue(true));
                allowing(serverComp).isFinished(); will(returnValue(false));
            }
        });
        TcpCoordinator cord = new TcpCoordinator(emitterFactory, conversationId, composerFactory);
        cord.startScript(synAck);
        assertFalse(cord.isFinished());
        context.assertIsSatisfied();
    }
   
    @Test
    public void testIsFinishedWithAllFinishedAnd2Sets(){
        final Mockery context = new Mockery();
        final EmitterFactory emitterFactory = context.mock(EmitterFactory.class);
        final ConversationId conversationId = new ConversationId(CLIENT_IP, SERVER_IP, SupportedProtocol.TCP);
        final ComposerFactory composerFactory = context.mock(ComposerFactory.class);
        final Emitter ipEmitter1 = context.mock(Emitter.class, "emitter1");
        final Emitter ipEmitter2 = context.mock(Emitter.class, "emitter2");
        final Composer clientComp = context.mock(Composer.class, "clientComp");
        final Composer serverComp = context.mock(Composer.class, "serverComp");
        final Composer clientComp2 = context.mock(Composer.class, "clientComp2");
        final Composer serverComp2 = context.mock(Composer.class, "serverComp2");
        final Packet synAck = PacketUtil.getTcpSynAckPacket(CLIENT_IP, SERVER_IP, CLIENT_PORT, SERVER_PORT);
        final Packet synAck2 = PacketUtil.getTcpSynAckPacket(SERVER_IP, CLIENT_IP, CLIENT_PORT, SERVER_PORT);
        context.checking(new TcpCoordinatorExpectations() {
            {
                //setup
                willInit(emitterFactory, conversationId, composerFactory, ipEmitter1, ipEmitter2);
                
                willCreateTcpClientComposer(composerFactory, synAck.getDestIpAddr(),
                        TcpCoordinatorStub.clientNameStub(synAck), clientComp);
                oneOf(clientComp).emitConversation(synAck);
                willCreateTcpServerComposer(composerFactory, synAck.getSrcIpAddr(),
                        TcpCoordinatorStub.serverNameStub(synAck), serverComp);  
                oneOf(serverComp).emitConversation(synAck);
                
                willCreateTcpClientComposer(composerFactory, synAck2.getDestIpAddr(),
                        TcpCoordinatorStub.clientNameStub(synAck2), clientComp2);
                oneOf(clientComp2).emitConversation(synAck2);
                willCreateTcpServerComposer(composerFactory, synAck2.getSrcIpAddr(),
                        TcpCoordinatorStub.serverNameStub(synAck2), serverComp2);
                oneOf(serverComp2).emitConversation(synAck2);
                
                allowing(clientComp).isFinished(); will(returnValue(true));
                allowing(serverComp).isFinished(); will(returnValue(true));
                allowing(clientComp2).isFinished(); will(returnValue(true));
                allowing(serverComp2).isFinished(); will(returnValue(true));
            }
        });
        TcpCoordinator cord = new TcpCoordinator(emitterFactory, conversationId, composerFactory);
        cord.startScript(synAck);
        cord.startScript(synAck2);
        assertTrue(cord.isFinished());
        context.assertIsSatisfied();
    }
    
    @Test
    public void testIsFinishedWithNotAllFinishedAnd2Sets(){
        final Mockery context = new Mockery();
        final EmitterFactory emitterFactory = context.mock(EmitterFactory.class);
        final ConversationId conversationId = new ConversationId(CLIENT_IP, SERVER_IP, SupportedProtocol.TCP);
        final ComposerFactory composerFactory = context.mock(ComposerFactory.class);
        final Emitter ipEmitter1 = context.mock(Emitter.class, "emitter1");
        final Emitter ipEmitter2 = context.mock(Emitter.class, "emitter2");
        final Composer clientComp = context.mock(Composer.class, "clientComp");
        final Composer serverComp = context.mock(Composer.class, "serverComp");
        final Composer clientComp2 = context.mock(Composer.class, "clientComp2");
        final Composer serverComp2 = context.mock(Composer.class, "serverComp2");
        final Packet synAck = PacketUtil.getTcpSynAckPacket(CLIENT_IP, SERVER_IP, CLIENT_PORT, SERVER_PORT);
        final Packet synAck2 = PacketUtil.getTcpSynAckPacket(SERVER_IP, CLIENT_IP, CLIENT_PORT, SERVER_PORT);
        context.checking(new TcpCoordinatorExpectations() {
            {
                //setup
                willInit(emitterFactory, conversationId, composerFactory, ipEmitter1, ipEmitter2);
                
                willCreateTcpClientComposer(composerFactory, synAck.getDestIpAddr(),
                        TcpCoordinatorStub.clientNameStub(synAck), clientComp);
                oneOf(clientComp).emitConversation(synAck);
                willCreateTcpServerComposer(composerFactory, synAck.getSrcIpAddr(),
                        TcpCoordinatorStub.serverNameStub(synAck), serverComp);  
                oneOf(serverComp).emitConversation(synAck);
                
                willCreateTcpClientComposer(composerFactory, synAck2.getDestIpAddr(),
                        TcpCoordinatorStub.clientNameStub(synAck2), clientComp2);
                oneOf(clientComp2).emitConversation(synAck2);
                willCreateTcpServerComposer(composerFactory, synAck2.getSrcIpAddr(),
                        TcpCoordinatorStub.serverNameStub(synAck2), serverComp2);
                oneOf(serverComp2).emitConversation(synAck2);
                
                allowing(clientComp).isFinished(); will(returnValue(true));
                allowing(serverComp).isFinished(); will(returnValue(false));
                allowing(clientComp2).isFinished(); will(returnValue(true));
                allowing(serverComp2).isFinished(); will(returnValue(true));
            }
        });
        TcpCoordinator cord = new TcpCoordinator(emitterFactory, conversationId, composerFactory);
        cord.startScript(synAck);
        cord.startScript(synAck2);
        assertFalse(cord.isFinished());
        context.assertIsSatisfied();
    }
    
    @Test
    public void testWriteToFile(){
        final Mockery context = new Mockery();
        final EmitterFactory emitterFactory = context.mock(EmitterFactory.class);
        final ConversationId conversationId = new ConversationId(CLIENT_IP, SERVER_IP, SupportedProtocol.TCP);
        final ComposerFactory composerFactory = context.mock(ComposerFactory.class);
        final Emitter ipEmitter1 = context.mock(Emitter.class, "emitter1");
        final Emitter ipEmitter2 = context.mock(Emitter.class, "emitter2");
        final Composer clientComp = context.mock(Composer.class, "clientComp");
        final Composer serverComp = context.mock(Composer.class, "serverComp");
        final Composer clientComp2 = context.mock(Composer.class, "clientComp2");
        final Composer serverComp2 = context.mock(Composer.class, "serverComp2");
        final Packet synAck = PacketUtil.getTcpSynAckPacket(CLIENT_IP, SERVER_IP, CLIENT_PORT, SERVER_PORT);
        final Packet synAck2 = PacketUtil.getTcpSynAckPacket(SERVER_IP, CLIENT_IP, CLIENT_PORT, SERVER_PORT);
        context.checking(new TcpCoordinatorExpectations() {
            {
                //setup
                willInit(emitterFactory, conversationId, composerFactory, ipEmitter1, ipEmitter2);
                
                willCreateTcpClientComposer(composerFactory, synAck.getDestIpAddr(),
                        TcpCoordinatorStub.clientNameStub(synAck), clientComp);
                oneOf(clientComp).emitConversation(synAck);
                willCreateTcpServerComposer(composerFactory, synAck.getSrcIpAddr(),
                        TcpCoordinatorStub.serverNameStub(synAck), serverComp);  
                oneOf(serverComp).emitConversation(synAck);
                
                willCreateTcpClientComposer(composerFactory, synAck2.getDestIpAddr(),
                        TcpCoordinatorStub.clientNameStub(synAck2), clientComp2);
                oneOf(clientComp2).emitConversation(synAck2);
                willCreateTcpServerComposer(composerFactory, synAck2.getSrcIpAddr(),
                        TcpCoordinatorStub.serverNameStub(synAck2), serverComp2);
                oneOf(serverComp2).emitConversation(synAck2);
                
                allowing(clientComp).isFinished(); will(returnValue(true));
                allowing(serverComp).isFinished(); will(returnValue(true));
                allowing(clientComp2).isFinished(); will(returnValue(true));
                allowing(serverComp2).isFinished(); will(returnValue(true));
                
                allowing(ipEmitter1).add(with(any(String.class)));
                allowing(ipEmitter2).add(with(any(String.class)));
                allowing(ipEmitter1).commitToFile();
                allowing(ipEmitter2).commitToFile();
                
                atLeast(1).of(clientComp).writeToFile();
                atLeast(1).of(serverComp).writeToFile();
                atLeast(1).of(clientComp2).writeToFile();
                atLeast(1).of(serverComp2).writeToFile();
                
            }
        });
        TcpCoordinator cord = new TcpCoordinator(emitterFactory, conversationId, composerFactory);
        cord.startScript(synAck);
        cord.startScript(synAck2);
        cord.commitToFile();
        context.assertIsSatisfied();
    }
}
