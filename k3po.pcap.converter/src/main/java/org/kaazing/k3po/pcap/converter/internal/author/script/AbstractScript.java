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

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.kaazing.k3po.pcap.converter.internal.author.emitter.Emitter;

public abstract class AbstractScript {

    private final Emitter emitter;
    private final ByteArrayWriter readWriter;
    private final ByteArrayWriter writeWriter;
    protected double lastActionTime;
    
    public AbstractScript(Emitter emitter) {
        this.emitter = emitter;
        readWriter = new ByteArrayWriter(ByteArrayWriter.Type.READ, emitter);
        writeWriter = new ByteArrayWriter(ByteArrayWriter.Type.WRITE, emitter);
    }

    public final void writeln(String str) {
        emitter.add(str);
        emitter.add("\n");
    }

    public void writeBufferToFile() {
        emitter.commitToFile();
    }

    public final void attachBuffer(String buf) {
        emitter.add("\n");
        emitter.add(buf);
        emitter.add("\n");
    }

    protected final void writeWaitAndSwapDate(double date) {
        writeln("# wait " + Math.round(((1000000 * date) - (1000000 * getLastActionTime()))));   
        setLastActionTime(date);
    }

    protected final String getHexFromBytes(byte[] bs) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bs) {
            sb.append(String.format("0x%02X ", b));
        }
        return sb.substring(0, sb.length() - 1);
    }

    public final void logPayload(byte[] bs){
        writeln("");
        writeln("# Packet length " + bs.length + " bytes");
    }
    
    // Write 
    public final void writeWriteBytes(byte[] bs, double date) {
        writeWaitAndSwapDate(date);
        writeWriteBytes(bs);
    }    
    
    protected final int writeWriteBytes(byte[] bytes) {
        writeWriter.write(bytes);
        return bytes.length;
    }
    
    private final static CharsetEncoder asciiEncoder = Charset.forName("US-ASCII").newEncoder();
    
    protected int writeWriteBytesInStringFormat(byte[] bytes){
    	String stringToWrite = new String(bytes).replace("\r", "\\r").replace("\n", "\\n");
    	if(asciiEncoder.canEncode(stringToWrite)){
            writeln("write \"" + stringToWrite + "\"");
    	}
    	else{
    		return writeWriteBytes(bytes);
    	}
        return bytes.length;
    }
    
    //Read
    public final void writeReadBytes(byte[] bytes, double date) {
        writeWaitAndSwapDate(date);
        writeReadBytes(bytes);
    }
    
    protected int writeReadBytes(byte[] bytes){
        readWriter.write(bytes);
        return bytes.length;
    }
    
    protected int writeReadBytesInStringFormat(byte[] bytes){
    	String stringToWrite =  new String(bytes).replace("\r", "\\r").replace("\n", "\\n");
    	if(asciiEncoder.canEncode(stringToWrite)){
            writeln("read \"" + stringToWrite + "\"");
    	}
    	else{
    		return writeReadBytes(bytes);
    	}
        return bytes.length;
    }

    public void writeConnected(double date) {
        writeWaitAndSwapDate(date);
        writeln("connected");
    }

    public void writeClosed() {
        writeln("closed");
    }

    public final void writeClose(double date) {
        writeWaitAndSwapDate(date);
        writeClose();
    }
    
    private void writeClose(){
        writeln("close"); 
    }
    
    public final void setLastActionTime(double date) {
        this.lastActionTime = date;     
    }
    
    public final double getLastActionTime() {
        return lastActionTime;   
    }

    public final void writeMetaData(String string) {
        writeln("# META INFO: " + string);
    }
    
    public final void write(String str){
        emitter.add(str);
    }
    
    public final String getBuffer(){
        return emitter.getBuffer();
    }
    
}
