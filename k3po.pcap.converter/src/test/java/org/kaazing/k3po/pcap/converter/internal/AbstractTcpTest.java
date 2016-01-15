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

import junit.framework.TestCase;

import org.kaazing.k3po.pcap.converter.internal.packet.Packet;
import org.kaazing.k3po.pcap.converter.internal.utils.PacketUtil;
import org.kaazing.k3po.pcap.converter.internal.utils.Util;

public abstract class AbstractTcpTest extends TestCase  {
    protected static final String CLIENT_IP = "111.111.1.11";
    protected static final String SERVER_IP = "222.222.22.222";
    protected static final Integer SERVER_PORT = 8080;
    protected static final Integer CLIENT_PORT = 99999;
    
    //client fin
    protected static final long INITIAL_CLIENT_FIN_SEQNUM = 01L;
    protected static final long INITIAL_CLIENT_FIN_ACKNUM = 101L;
    protected static final Packet CLIENT_FIN_PACKET = PacketUtil.getTcpFinPacket(SERVER_IP, CLIENT_IP, SERVER_PORT, CLIENT_PORT,
            INITIAL_CLIENT_FIN_SEQNUM, INITIAL_CLIENT_FIN_ACKNUM);
    //server fin
    protected static final long INITIAL_SERVER_FIN_SEQNUM = 201L;
    protected static final long INITIAL_SERVER_FIN_ACKNUM = 2101L;
    protected static final Packet SERVER_FIN_PACKET = PacketUtil.getTcpFinPacket(CLIENT_IP, SERVER_IP, CLIENT_PORT, SERVER_PORT,
            INITIAL_SERVER_FIN_SEQNUM, INITIAL_SERVER_FIN_ACKNUM);
    //server ack
    protected static final long SERVER_RESPONSE_ACK_SEQNUM = 202L;
    protected static final long SERVER_RESPONSE_ACK_ACKNUM = INITIAL_CLIENT_FIN_SEQNUM + 1L;
    protected static final Packet SERVER_ACK_PACKET = PacketUtil.getTcpAckPacket(CLIENT_IP, SERVER_IP, CLIENT_PORT, SERVER_PORT,
            SERVER_RESPONSE_ACK_SEQNUM, SERVER_RESPONSE_ACK_ACKNUM);
    //client ack
    protected static final long CLIENT_RESPONSE_ACK_SEQNUM = 02L;
    protected static final long CLIENT_RESPONSE_ACK_ACKNUM = INITIAL_SERVER_FIN_SEQNUM + 1L;
    protected static final Packet CLIENT_ACK_PACKET = PacketUtil.getTcpAckPacket(SERVER_IP, CLIENT_IP, SERVER_PORT, CLIENT_PORT,
            CLIENT_RESPONSE_ACK_SEQNUM, CLIENT_RESPONSE_ACK_ACKNUM);
    
    protected final byte[] defaultPayloadByteArray = {0x01, 0x30, 0x30, 0x30, 0x30, (byte)0xFF};
    protected final int defaultPayloadLength = defaultPayloadByteArray.length;
    
    protected final Packet toClientPayloadPacket = PacketUtil.getTcpPayloadPacket(CLIENT_IP, SERVER_IP, CLIENT_PORT, SERVER_PORT, defaultPayloadByteArray);
    protected final Packet toServerPayloadPacket = PacketUtil.getTcpPayloadPacket(SERVER_IP, CLIENT_IP, SERVER_PORT, CLIENT_PORT, defaultPayloadByteArray);
    protected final String payloadScriptWrite = "write [" + Util.getHexFromBytes(defaultPayloadByteArray) + "]";
    protected final String payloadScriptRead = "read [" + Util.getHexFromBytes(defaultPayloadByteArray) + "]";
    
    protected final String defaultAsciiPayloadStr = "Hello Robot\n";
    protected final byte[] defaultAsciiPayloadByteArray = defaultAsciiPayloadStr.getBytes();
    protected final int defaultAsciiPayloadLength = defaultAsciiPayloadByteArray.length;
    
    protected final Packet toClientAsciiPayloadPacket = PacketUtil.getTcpPayloadPacket(CLIENT_IP, SERVER_IP, CLIENT_PORT, SERVER_PORT, defaultAsciiPayloadByteArray);
    protected final Packet toServerAsciiPayloadPacket = PacketUtil.getTcpPayloadPacket(SERVER_IP, CLIENT_IP, SERVER_PORT, CLIENT_PORT, defaultAsciiPayloadByteArray);
    protected final String asciiPayloadScriptWrite = "write \"" + defaultAsciiPayloadStr.replace("\n", "\\n") + "\"";
    protected final String asciiPayloadScriptRead = "read \"" + defaultAsciiPayloadStr.replace("\n", "\\n") + "\"";
}
