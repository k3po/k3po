/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.tcpconverter.rptScriptsCreator.composer;

import org.kaazing.robot.tcpconverter.rptScriptsCreator.RptScriptsCreatorFailureException;
import org.kaazing.robot.tcpconverter.rptScriptsCreator.emitter.Emitter;
import org.kaazing.robot.tcpconverter.rptScriptsCreator.emitter.EmitterFactory;

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
