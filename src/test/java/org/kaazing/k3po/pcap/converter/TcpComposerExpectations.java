/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.k3po.pcap.converter;

import org.kaazing.k3po.pcap.converter.author.emitter.Emitter;
import org.kaazing.k3po.pcap.converter.author.emitter.EmitterFactory;
import org.kaazing.k3po.pcap.converter.author.emitter.OutputType;

public class TcpComposerExpectations extends org.jmock.Expectations {
    public void willSetScriptEmitter(EmitterFactory emitterFactory, Emitter clientEmitter, OutputType otOfEmitter) {
        oneOf(emitterFactory).getRptScriptEmitter(with(otOfEmitter), with(any(String.class)));
        will(returnValue(clientEmitter));
    }
}
