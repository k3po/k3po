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
package org.kaazing.k3po.pcap.converter.internal.packet;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Packet class that encapsulates all the necessary info needed to identify filtered packets for the rupert script, and
 * has the properties needed to generate the rupert script
 * 
 */
public class Packet {
    // geninfo
    private int packetNumber;

    private int packetSize;
    private Date timestamp;
    private double timeInSecondsFromEpoch;

    // private
    private String SrcIpAddr = null;
    private String destIpAddr = null;

    // false until proven true
    private boolean isTcp = false;
    private boolean isUdp = false;
    private boolean isIp = false;
    private boolean isHttp = false;
    private boolean isFragmented = false;

    // tcp
    private int tcpSrcPort;
    private int tcpDestPort;
    private int tcpStream;
    private int tcpLen;
    private int tcpRelativeSeqNum;
    private int tcpNextRelativeSeqNum;
    private int tcpPayloadStart;
    private int tcpPayloadSize;
    private byte[] tcpPayload;
    private int tcpFlags;
    // Convenience flags (can get all of them if useful)
    private boolean tcpFlagsAck;
    private boolean tcpFlagsSyn;
    private boolean tcpFlagsReset;
    private boolean tcpFlagsFin;
    private long tcpSequenceNumber;
    private long tcpAcknowledgementNumber;

    public Packet() {

    }

    /* Getters and Setters */

    public boolean isIp() {
        return isIp;
    }

    public void setIp(boolean isIp) {
        this.isIp = isIp;
    }
    
    public String getSrcIpAddr() {
        return SrcIpAddr;
    }

    public void setSrcIpAddr(String ip) {
        SrcIpAddr = ip;
    }

    public String getDestIpAddr() {
        return destIpAddr;
    }

    public void setDestIpAddr(String destIp) {
        this.destIpAddr = destIp;
    }
    
    public int getPacketSize() {
        return packetSize;
    }

    public void setPacketSize(int packetSize) {
        this.packetSize = packetSize;
    }

    public boolean isTcp() {
        return isTcp;
    }

    public void setTcp(boolean isTcp) {
        this.isTcp = isTcp;
    }

    public boolean isUdp() {
        return isUdp;
    }

    public void setUdp(boolean isUdp) {
        this.isUdp = isUdp;
    }

    public int getTcpSrcPort() {
        return tcpSrcPort;
    }

    public void setTcpSrcPort(int tcpSrcPort) {
        this.tcpSrcPort = tcpSrcPort;
    }

    public int getTcpDestPort() {
        return tcpDestPort;
    }

    public void setTcpDestPort(int tcpDestPort) {
        this.tcpDestPort = tcpDestPort;
    }

    public int getTcpStream() {
        return tcpStream;
    }

    public void setTcpStream(int tcpStream) {
        this.tcpStream = tcpStream;
    }

    public int getTcpLen() {
        return tcpLen;
    }

    public void setTcpLen(int tcpLen) {
        this.tcpLen = tcpLen;
    }

    public int getTcpPayloadStart() {
        return tcpPayloadStart;
    }

    public void setTcpPayloadStart(int tcpPayloadStart) {
        this.tcpPayloadStart = tcpPayloadStart;
    }

    public int getTcpPayloadSize() {
        return tcpPayloadSize;
    }

    public void setTcpPayloadLength(int tcpPayloadSize) {
        this.tcpPayloadSize = tcpPayloadSize;
    }

    public boolean isTcpFlagsAck() {
        return tcpFlagsAck;
    }

    public void setTcpFlagsAck(boolean tcpFlagsAck) {
        this.tcpFlagsAck = tcpFlagsAck;
    }

    public boolean isTcpFlagsSyn() {
        return tcpFlagsSyn;
    }

    public void setTcpFlagsSyn(boolean tcpFlagsSyn) {
        this.tcpFlagsSyn = tcpFlagsSyn;
    }

    public boolean isTcpFlagsFin() {
        return tcpFlagsFin;
    }

    public void setTcpFlagsFin(boolean tcoFlagsFin) {
        this.tcpFlagsFin = tcoFlagsFin;
    }

    public byte[] getTcpPayload() {
        return tcpPayload;
    }

    public void setTcpPayload(byte[] bs) {
        this.tcpPayload = bs;
    }

    public void setTimeStamp(Date date) {
        this.timestamp = date;
    }

    public Date getTimeStamp() {
        return timestamp;
    }

    public int getRelativeTcpSeqNum() {
        return tcpRelativeSeqNum;
    }

    public void setRelativeTcpSeqNum(int tcpSeq) {
        this.tcpRelativeSeqNum = tcpSeq;
    }

    public int getTcpFlags() {
        return tcpFlags;
    }

    public void setTcpFlags(int tcpFlags) {
        this.tcpFlags = tcpFlags;
    }

    public boolean isTcpFlagsReset() {
        return tcpFlagsReset;
    }

    public void setTcpFlagsReset(boolean tcpFlagsReset) {
        this.tcpFlagsReset = tcpFlagsReset;
    }

    public int getTcpNextRelativeSeqNum() {
        return tcpNextRelativeSeqNum;
    }

    public void setTcpNextRelativeSeqNum(int tcpNextSeq) {
        this.tcpNextRelativeSeqNum = tcpNextSeq;
    }

    public int getDestPort() {
        if(isTcp())
            return getTcpDestPort();
        throw new PacketFailureException("Asking for packet dest port when that has not been decoded, (ONLY TCP HAS BEEN IMPLEMENTED so far )");
    }
    
    public int getSrcPort() {
        if(isTcp())
            return getTcpSrcPort();
        throw new PacketFailureException("Asking for packet src port when that has not been decoded, (ONLY TCP HAS BEEN IMPLEMENTED so far)");
    }
    
    public double getTimeInMicroSecondsFromEpoch() {
        return timeInSecondsFromEpoch;
    }

    public void setTimeInSecondsFromEpoch(double timeInSecondsFromEpoch) {
        this.timeInSecondsFromEpoch = timeInSecondsFromEpoch;
    }
    
    public long getTcpSequenceNumber() {
        return tcpSequenceNumber;
    }

    public void setTcpSequenceNumber(long sequenceNumber) {
        this.tcpSequenceNumber = sequenceNumber;
    }
    
    public void setTcpSequenceNumber(String hexNumber) {
        this.tcpSequenceNumber = Long.parseLong(hexNumber, 16);
    }

    public long getTcpAcknowledgementNumber() {
        return tcpAcknowledgementNumber;
    }

    public void setTcpAcknowledgementNumber(long acknowledgementNumber) {
        this.tcpAcknowledgementNumber = acknowledgementNumber;
    }
    
    public void setTcpAcknowledgementNumber(String hexNumber) {
        this.tcpAcknowledgementNumber = Long.parseLong(hexNumber, 16);
    }
    
    public int getPacketNumber() {
        return packetNumber;
    }

    public void setPacketNumber(int packetNumber) {
        this.packetNumber = packetNumber;
    }
    
    //HTTP
    
    private RequestType requestType;
    private LinkedList<HttpField> listOfHttpFields = new LinkedList<>();
    private String requestURI;
    
    public RequestType getHttpRequestType() {
        return requestType;
    }

    public void setHttpRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public LinkedList<HttpField> getListOfHttpFields() {
        return listOfHttpFields;
    }

    public void setListOfFields(LinkedList<HttpField> listOfFields) {
        this.listOfHttpFields = listOfFields;
    }
    
    public void addHttpField(String name, String value, int pos, int size){
        listOfHttpFields.add(new HttpField(name, value, pos, size));
    }
    
    public String getHttpRequestURI() {
        return requestURI;
    }

    public void setHttpRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    public boolean isHttp() {
        return isHttp;
    }

    public void setHttp(boolean isHttp) {
        this.isHttp = isHttp;
    }

    public boolean isFragmented() {
        return isFragmented;
    }

    public void setFragmented(boolean isFragmented) {
        this.isFragmented = isFragmented;
    }

    public enum RequestType {
        GET, POST, HEAD, OPTIONS, PUT, DELETE, TRACE, CONNECT
    }
    
    public class HttpField{
        int pos;
        int size;
        String value;
        String name;
        
        public HttpField(String name, String value, int pos, int size) {
            super();
            this.pos = pos;
            this.size = size;
            this.value = value;
            this.name = name;
        }
        
        public int getPos() {
            return pos;
        }
        public void setPos(int pos) {
            this.pos = pos;
        }
        public int getSize() {
            return size;
        }
        public void setSize(int size) {
            this.size = size;
        }
        public String getValue() {
            return value;
        }
        public void setValue(String value) {
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }    
    }
    
    public Map<Integer, Integer> getHttpFieldPositionsAndSize(){
        Map<Integer, Integer> posAndSize = new HashMap<>();
        for(HttpField iter: listOfHttpFields){
            if(!posAndSize.containsKey(iter.getPos())){
                posAndSize.put(iter.getPos(), iter.getSize());
            }
        }
        return posAndSize;
    }
    
}
