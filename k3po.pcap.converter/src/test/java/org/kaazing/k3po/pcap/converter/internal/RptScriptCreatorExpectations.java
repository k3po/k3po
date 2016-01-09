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

import org.kaazing.k3po.pcap.converter.internal.author.coordinator.Coordinator;
import org.kaazing.k3po.pcap.converter.internal.author.emitter.Emitter;
import org.kaazing.k3po.pcap.converter.internal.author.emitter.EmitterFactory;
import org.kaazing.k3po.pcap.converter.internal.author.emitter.OutputType;

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
