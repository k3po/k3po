/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.k3po.pcap.converter.tcpconverter.author.emitter;

public class NullEmitter implements Emitter {

    private StringBuilder buffer = new StringBuilder();
    private boolean memSaver = false;
    
    protected NullEmitter(boolean memSaver){
    	this.memSaver = memSaver;
    }

    @Override
    public void add(String str) {
    	if(!memSaver){
    		buffer.append(str);
    	}
    }

    @Override
    public void clearBuffer() {
        buffer = new StringBuilder();
    }

    @Override
    public String getBuffer() {
        return buffer.toString();
    }

    @Override
    public void commitToFile() {

    }

}
