/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.tcpconverter.rptScriptsCreator.emitter;

public interface Emitter {
    
    public void add(String str);
    
    public void clearBuffer();
    
    public String getBuffer();

    public void commitToFile();
}
