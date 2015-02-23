/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.tcpconverter;

import org.kaazing.robot.tcpconverter.rptScriptsCreator.emitter.Emitter;
import org.kaazing.robot.tcpconverter.rptScriptsCreator.emitter.EmitterFactory;
import org.kaazing.robot.tcpconverter.rptScriptsCreator.emitter.OutputType;

public class TcpComposerExpectations extends org.jmock.Expectations {
    public void willSetScriptEmitter(EmitterFactory emitterFactory, Emitter clientEmitter, OutputType otOfEmitter) {
        oneOf(emitterFactory).getRptScriptEmitter(with(otOfEmitter), with(any(String.class)));
        will(returnValue(clientEmitter));
    }
}
