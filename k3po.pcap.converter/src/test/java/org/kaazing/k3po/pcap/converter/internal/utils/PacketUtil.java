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
package org.kaazing.k3po.pcap.converter.internal.utils;

import org.kaazing.k3po.pcap.converter.internal.packet.Packet;

public class PacketUtil {

    public static Packet createBasicPacket(String destIp, String srcIp, int destPort, int srcPort) {
        Packet pc = new Packet();
        pc.setDestIpAddr(destIp);
        pc.setSrcIpAddr(srcIp);
        pc.setTcpDestPort(destPort);
        pc.setTcpSrcPort(srcPort);
        return pc;
    }
    
    public static Packet createBasicTcpPacket(String destIp, String srcIp, int destPort, int srcPort) {
        Packet pc = new Packet();
        pc.setDestIpAddr(destIp);
        pc.setSrcIpAddr(srcIp);
        pc.setTcpDestPort(destPort);
        pc.setTcpSrcPort(srcPort);
        pc.setTcp(true);
        return pc;
    }

    public static Packet getTcpAckPacket(String destIp, String srcIp, int destPort, int srcPort) {
        Packet pc = createBasicTcpPacket(destIp, srcIp, destPort, srcPort);
        pc.setTcpFlagsAck(true);
        return pc;
    }
    
    public static Packet getTcpAckPacket(String destIp, String srcIp, int destPort, int srcPort, long seqNum, long ackNum) {
        Packet pc = createBasicTcpPacket(destIp, srcIp, destPort, srcPort);
        pc.setTcpFlagsAck(true);
        pc.setTcpSequenceNumber(seqNum);
        pc.setTcpAcknowledgementNumber(ackNum);
        return pc;
    }

    public static Packet getTcpSynAckPacket(String clientIp, String serverIp, int clientPort, int serverPort) {
        Packet pc = getTcpAckPacket(clientIp, serverIp, clientPort, serverPort);
        pc.setTcpFlagsSyn(true);
        return pc;
    }

     public static Packet getTcpFinPacket(String destIp, String srcIp, int destPort, int srcPort, long seqNum, long ackNum){
         Packet pc = createBasicTcpPacket(destIp, srcIp, destPort, srcPort);
         pc.setTcpFlagsFin(true);
         pc.setTcpSequenceNumber(seqNum);
         pc.setTcpAcknowledgementNumber(ackNum);
         return pc;
     }

     public static Packet getTcpFinAckPacket(String destIp, String srcIp, int destPort, int srcPort, long seqNum, long ackNum){
         Packet pc = createBasicTcpPacket(destIp, srcIp, destPort, srcPort);
         pc.setTcpFlagsFin(true);
         pc.setTcpFlagsAck(true);
         pc.setTcpSequenceNumber(seqNum);
         pc.setTcpAcknowledgementNumber(ackNum);
         return pc;
     }
     
     public static Packet getTcpPayloadPacket(String destIp, String srcIp, int destPort, int srcPort, byte[] payload){
         Packet pc = createBasicTcpPacket(destIp, srcIp, destPort, srcPort);
         pc.setTcpPayloadLength(payload.length);
         pc.setTcpPayload(payload);
         return pc;
     }
     
}
