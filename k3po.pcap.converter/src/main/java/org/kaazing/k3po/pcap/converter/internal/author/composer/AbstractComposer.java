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
package org.kaazing.k3po.pcap.converter.internal.author.composer;

import org.kaazing.k3po.pcap.converter.internal.author.RptScriptsCreatorFailureException;
import org.kaazing.k3po.pcap.converter.internal.author.emitter.Emitter;
import org.kaazing.k3po.pcap.converter.internal.author.emitter.EmitterFactory;

public abstract class AbstractComposer implements Composer {

    private final Emitter emitter;
    public static final String SEP = "-";
    protected final String ipaddress; 
    protected final EmitterFactory emitterFactory;

    public AbstractComposer(EmitterFactory emitterFactory, Emitter emitter, String ipaddress) {
        super();
        this.emitterFactory = emitterFactory;
        if(emitter == null){
            throw new RptScriptsCreatorFailureException("Cannot initialize composer with null value");
        }
        this.ipaddress = ipaddress;
        this.emitter = emitter;
    }    
    
    public final String getIp() {
        return ipaddress;
    }
    
    protected final String getBuffer(){
        return emitter.getBuffer();
    }

    protected final void clearBuffer(){
        emitter.clearBuffer();
    }
    
    protected final void addToScript(String str){
        emitter.add(str);
    }
    
    protected final void commitToFile(){
        emitter.commitToFile();
    }
    
}
