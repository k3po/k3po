/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.k3po.pcap.converter;

import org.kaazing.k3po.pcap.converter.author.coordinator.Coordinator;
import org.kaazing.k3po.pcap.converter.author.emitter.Emitter;
import org.kaazing.k3po.pcap.converter.author.emitter.EmitterFactory;
import org.kaazing.k3po.pcap.converter.author.emitter.OutputType;

public class RptScriptCreatorExpectations extends org.jmock.Expectations {
    public void willInitCreator(EmitterFactory emitterFactory, Emitter creatorNote, Emitter dummyEmitter) {
        allowing(emitterFactory).getNoteEmitter(with(equal(OutputType.CREATOR)), with(any(String.class)));
        will(returnValue(creatorNote));
        allowing(creatorNote).add(with(any(String.class)));
        allowing(creatorNote).commitToFile();
        allowing(emitterFactory).getNoteEmitter(with(any(OutputType.class)), with(any(String.class)));
        will(returnValue(dummyEmitter));
        allowing(emitterFactory).getRptScriptEmitter(with(any(OutputType.class)), with(any(String.class)));
        will(returnValue(dummyEmitter));
        allowing(dummyEmitter).add(with(any(String.class)));
        allowing(dummyEmitter).commitToFile();
    }
    
    public void allowGettingScripts(Coordinator coord) {
        allowing(coord).getClientScriptsByIp(with(any(String.class)));
        allowing(coord).getScriptsByIp(with(any(String.class)));
        allowing(coord).getServerScriptsByIp(with(any(String.class)));     
    }
}
