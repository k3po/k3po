/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.tcpconverter.rptScriptsCreator.composer;

import org.kaazing.robot.tcpconverter.packet.Packet;

public interface Composer {

    /**
     * Returns this nodes IP
     * @return
     */
    public String getIp();
    
    /**
     * Emits the conversation of a script
     * @param packet
     */
    public void emitConversation(Packet packet);
    
    /**
     * Tells whether all script segments are in a finished state (i.e. at a rupert Closed)
     * @return
     */
    public boolean isFinished();
    
    /**
     * Will write all script fragments it has to file
     */
    public void writeToFile();
    
    /**
     * Will return a copy of the script which will be the sum total of the fragments
     */
    public String getScript();
    
}
