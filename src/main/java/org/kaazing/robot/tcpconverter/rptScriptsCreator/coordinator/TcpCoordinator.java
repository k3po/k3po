/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */
package org.kaazing.robot.tcpconverter.rptScriptsCreator.coordinator;

import java.util.HashMap;
import java.util.Map;

import org.kaazing.robot.tcpconverter.packet.Packet;
import org.kaazing.robot.tcpconverter.rptScriptsCreator.ConversationId;
import org.kaazing.robot.tcpconverter.rptScriptsCreator.SupportedProtocol;
import org.kaazing.robot.tcpconverter.rptScriptsCreator.composer.ComposerFactory;
import org.kaazing.robot.tcpconverter.rptScriptsCreator.composer.Composer;
import org.kaazing.robot.tcpconverter.rptScriptsCreator.composer.ComposerType;
import org.kaazing.robot.tcpconverter.rptScriptsCreator.emitter.EmitterFactory;
import org.kaazing.robot.tcpconverter.rptScriptsCreator.emitter.OutputType;

/**
 * Coordinates the two sides of a tcp conversation, and the composers that will each write one side
 * 
 */
public class TcpCoordinator extends AbstractCoordinator implements Coordinator {

    // Maps String of ipaddresses to clients or servers   
    private Map<String, Composer> clients;
    private Map<String, Composer> servers;

    public TcpCoordinator(EmitterFactory emitterFactory, ConversationId conversationId, ComposerFactory composerFactory) {
        super(emitterFactory, emitterFactory.getRptScriptEmitter(OutputType.TCP_COORDINATOR,
                conversationId.getIpAddr1()), emitterFactory.getRptScriptEmitter(OutputType.TCP_COORDINATOR,
                conversationId.getIpAddr2()), conversationId, composerFactory);
        clients = new HashMap<String, Composer>(2);
        servers = new HashMap<String, Composer>(2);

    }

    @Override
    public void startScript(Packet packet) {
        if ( packet.isTcpFlagsAck() && packet.isTcpFlagsSyn() ) {
            synack(packet);
        }
    }

    @Override
    public void conversation(Packet packet) {
        if ( packet.isTcpFlagsAck() && packet.isTcpFlagsSyn() ) {
            synack(packet);
        }
        else {
            String ip1 = packet.getDestIpAddr();
            String ip2 = packet.getSrcIpAddr();
            if ( clients.keySet().contains(ip1) ) {
                clients.get(ip1).emitConversation(packet);
            }
            if ( clients.keySet().contains(ip2) && !ip1.equals(ip2) ) {
                clients.get(ip2).emitConversation(packet);
            }
            if ( servers.keySet().contains(ip1) ) {
                servers.get(ip1).emitConversation(packet);
            }
            if ( servers.keySet().contains(ip2) && !ip1.equals(ip2) ) {
                servers.get(ip2).emitConversation(packet);
            }
        }
    }

    @Override
    public boolean isFinished() {
        if ( servers.keySet().size() == 0 && clients.keySet().size() == servers.keySet().size() ) {
            return false;
        }
        for (String iter : clients.keySet()) {
            if ( !clients.get(iter).isFinished() ) {
                return false;
            }
        }
        for (String iter : servers.keySet()) {
            if ( !servers.get(iter).isFinished() ) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void commitToFile() {
        for (Composer composer : clients.values()) {
            if ( composer.getIp().equals(ipAddr1) ) {
                ip1Emitter.add(composer.getScript());
            }
            else {
                ip2Emitter.add(composer.getScript());
            }
            composer.writeToFile();
        }
        for (Composer composer : servers.values()) {
            if ( composer.getIp().equals(ipAddr1) ) {
                ip1Emitter.add(composer.getScript());
            }
            else {
                ip2Emitter.add(composer.getScript());
            }
            composer.writeToFile();
        }
        ip1Emitter.commitToFile();
        ip2Emitter.commitToFile();
    }

    @Override
    public String getScriptsByIp(String ip) {
        String scripts = "";
        for (Composer composer : clients.values()) {
            if ( composer.getIp().equals(ip) ) {
                scripts += composer.getScript();
            }
        }
        for (Composer composer : servers.values()) {
            if ( composer.getIp().equals(ip) ) {
                scripts += composer.getScript();
            }
        }
        return scripts;
    }

    @Override
    public String getClientScriptsByIp(String ip) {
        String scripts = "";
        for (Composer composer : clients.values()) {
            if ( composer.getIp().equals(ip) ) {
                scripts += composer.getScript();
            }
        }
        return scripts;
    }

    @Override
    public String getServerScriptsByIp(String ip) {
        String scripts = "";
        for (Composer composer : servers.values()) {
            if ( composer.getIp().equals(ip) ) {
                scripts += composer.getScript();
            }
        }
        return scripts;
    }

    protected void synack(Packet packet) {
        String clientIp = packet.getDestIpAddr();
        String serverIp = packet.getSrcIpAddr();
        if ( !clients.containsKey(clientIp) ) { // if client does not exists
            clients.put(clientIp, composerFactory.getComposer(SupportedProtocol.TCP, ComposerType.CLIENT, clientIp,
                    clientName(packet)));
        }
        clients.get(clientIp).emitConversation(packet);

        if ( !servers.containsKey(serverIp) ) {
            servers.put(serverIp, composerFactory.getComposer(SupportedProtocol.TCP, ComposerType.SERVER, serverIp,
                    serverName(packet)));
        }
        servers.get(serverIp).emitConversation(packet);
    }

    protected static String clientName(Packet p) {
        return p.getDestIpAddr() + "-" + p.getSrcIpAddr();
    }

    protected static String serverName(Packet p) {
        return p.getSrcIpAddr() + "-" + p.getDestIpAddr();
    }
    
}
