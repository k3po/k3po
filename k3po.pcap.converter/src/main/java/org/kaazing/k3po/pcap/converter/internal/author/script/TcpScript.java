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
package org.kaazing.k3po.pcap.converter.internal.author.script;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.kaazing.k3po.pcap.converter.internal.author.emitter.Emitter;
import org.kaazing.k3po.pcap.converter.internal.packet.Packet;

/**
 * TcpRptScriptWriter extends RptScriptWriter with specific knowledge of how Tcp works
 *
 */
public abstract class TcpScript extends AbstractScript{
    private ScriptState state;
    private final HashMap<Long, Integer> seqNumbers = new HashMap<>();
    private final static Logger LOG = Logger.getLogger(TcpScript.class.getName());
    private Long closingReadAck = null;
    private Long closingWriteAck = null;
    
    public TcpScript(Emitter emitter) {
        super(emitter);
        this.setState(ScriptState.NOT_INITED);
    }

    public final boolean recordSeqNumAndReturnTrueOnNewEntry(long seqNum, int length){
        if(length == 0)
            return true;
        if(seqNumbers.containsKey(seqNum) && seqNumbers.get(seqNum) == length)
            return false;
        else
            seqNumbers.put(seqNum, length);
        return true;
    }
    
    public final ScriptState getState() {
        return state;
    }

    public final void setState(ScriptState state) {
        this.state = state;
    }    
    
    public final void readPayloadOfTcpPacket(Packet packet) {
        logPayload(packet.getTcpPayload());
        if ( packet.isHttp() && !packet.getHttpFieldPositionsAndSize().isEmpty() ) {
            writeWaitAndSwapDate(packet.getTimeInMicroSecondsFromEpoch());
            int payloadLength = packet.getTcpPayloadSize();
            byte[] payload = packet.getTcpPayload();
            Map<Integer, Integer> httpFields = new TreeMap<>(packet.getHttpFieldPositionsAndSize());
            int currentPos = 0;
            Iterator<Entry<Integer, Integer>> iter = httpFields.entrySet().iterator();
            Map.Entry<Integer, Integer> entry = iter.next();
            while (currentPos < (payloadLength) && iter != null) {
                if ( entry.getKey() > currentPos ) {
                    byte[] toWrite = new byte[entry.getKey()];
                    System.arraycopy(payload, currentPos, toWrite, 0, entry.getKey());
                    currentPos += writeReadBytes(toWrite);
                }
                else if(entry.getValue() == 0 || entry.getKey() < currentPos) {
                    if ( iter.hasNext() ) {
                        entry = (Map.Entry<Integer, Integer>) iter.next();
                    }else {
                        break;
                    }
                }else if(currentPos + entry.getValue() > payload.length){
                	break;
                }else{
                    byte[] toWrite = new byte[entry.getValue()];
                    System.arraycopy(payload, currentPos, toWrite, 0, entry.getValue());
                    currentPos += writeReadBytesInStringFormat(toWrite);
                    if ( iter.hasNext() ) {
                        entry = iter.next();
                    }
                    else {
                        break;
                    }
                }
            }
            if ( payload.length > currentPos ) {
                byte[] toWrite = new byte[payload.length - currentPos];
                System.arraycopy(payload, currentPos, toWrite, 0, payload.length - currentPos);
                currentPos += writeReadBytes(toWrite);
            }
        }
        else {
            writeReadBytes(packet.getTcpPayload(), packet.getTimeInMicroSecondsFromEpoch());
        }
    }

    public final void writePayloadOfTcpPacket(Packet packet) {
        logPayload(packet.getTcpPayload());
        if ( packet.isHttp() && !packet.getHttpFieldPositionsAndSize().isEmpty() ) {
            writeWaitAndSwapDate(packet.getTimeInMicroSecondsFromEpoch());
            int payloadLength = packet.getTcpPayloadSize();
            byte[] payload = packet.getTcpPayload();
            Map<Integer, Integer> httpFields = new TreeMap<>(packet.getHttpFieldPositionsAndSize());
            int currentPos = 0;
            Iterator<Entry<Integer, Integer>> iter = httpFields.entrySet().iterator();
            Map.Entry<Integer, Integer> entry = iter.next();
            while (currentPos < (payloadLength) && iter != null) {
                if ( entry.getKey() > currentPos ) {
                    byte[] toWrite = new byte[entry.getKey()];
                    System.arraycopy(payload, currentPos, toWrite, 0, entry.getKey());
                    currentPos += writeWriteBytes(toWrite);
                }
                else if(entry.getValue() == 0 || entry.getKey() < currentPos) {
                    if ( iter.hasNext() ) {
                        entry = iter.next();
                    }else {
                        break;
                    }
                }else if(currentPos + entry.getValue() > payload.length){
                	break;
                }else{
                    byte[] toWrite = new byte[entry.getValue()];
                    System.arraycopy(payload, currentPos, toWrite, 0, entry.getValue());
                    currentPos += writeWriteBytesInStringFormat(toWrite);
                    if ( iter.hasNext() ) {
                        entry = iter.next();
                    }
                    else {
                        break;
                    }
                }
            }
            if ( payload.length > currentPos ) {
                byte[] toWrite = new byte[payload.length - currentPos];
                System.arraycopy(payload, currentPos, toWrite, 0, payload.length - currentPos);
                currentPos += writeWriteBytes(toWrite);
            }
        }
        else {
            writeWriteBytes(packet.getTcpPayload(), packet.getTimeInMicroSecondsFromEpoch());
        }
    }

    @Override
    public final void writeClosed(){
        super.writeClosed();
        state = ScriptState.CLOSED;
    }
    
    @Override
    public final void writeBufferToFile(){
        if(this.state == ScriptState.NOT_INITED)
            return;
        if(this.state != ScriptState.CLOSED)
            LOG.warning("Writing script to file that did not complete lifecycle: (ie not in closed state)");
        super.writeBufferToFile();
    }
    
    public final void writeCloseWrite(double d){
        if(this.state == ScriptState.CLOSE_WRITE)  //Repeat Packet
            return;
        writeWaitAndSwapDate(d);
        writeln("# close-write");
        if ( this.state == ScriptState.CLOSE_READ ){
        	if(closingScript){
        		writeln("close");
        	}
            writeClosed();
        }
        else{
            this.state = ScriptState.CLOSE_WRITE;
    }
    }
    
    public final void writeCloseRead(double date){
        if(this.state == ScriptState.CLOSE_READ)   //Repeat Packet
            return;
        writeWaitAndSwapDate(date);
        writeln("# close-read");
        if ( this.state == ScriptState.CLOSE_WRITE ){  
        	if(closingScript){
        		writeln("close");
        	}
            writeClosed();
        }else{
            this.state = ScriptState.CLOSE_READ;
    }
    }
    
    public final boolean isClosingWriteAck(long ack) {
        if(closingWriteAck == null)
            return false;
        else if(closingWriteAck == ack){
            return true;
        }
        return false;
    }
    
    public final boolean isClosingReadAck(long ack) {   
        if(closingReadAck == null)
            return false;
        else if(closingReadAck == ack){    
        	closedRead = true;
            return true;
        }  
        return false;
    }
    
    public final void setClosingWriteAck(long closingAck) {
        if(closingWriteAck != null){

    		return;
        }
    	if(!closedRead){
    		closingScript = true;
    	}
        closingWriteAck = closingAck + 1L;
    }
    
    public final void setClosingReadAck(long closingAck) {
        if(closingReadAck != null){
            return;
        }
        closingReadAck = closingAck + 1L;
    }
    
    @Override
    public final void writeConnected(double date){
        state = ScriptState.CONNECTED;
        super.writeConnected(date);
    }
    
    private boolean closingScript = false;
    private boolean closedRead = false;
}
