/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.k3po.pcap.converter.author.emitter;

public interface EmitterFactory {
    public Emitter getRptScriptEmitter(OutputType ot, String name);
    public Emitter getNoteEmitter(OutputType ot, String noteHeader);
	public void setMemSaver(boolean b);
}
    
