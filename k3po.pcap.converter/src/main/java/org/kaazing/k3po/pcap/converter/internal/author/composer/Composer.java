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

import org.kaazing.k3po.pcap.converter.internal.packet.Packet;

public interface Composer {

    /**
     * Returns this nodes IP
     * @return
     */
    String getIp();
    
    /**
     * Emits the conversation of a script
     * @param packet
     */
    void emitConversation(Packet packet);
    
    /**
     * Tells whether all script segments are in a finished state (i.e. at a rupert Closed)
     * @return
     */
    boolean isFinished();
    
    /**
     * Will write all script fragments it has to file
     */
    void writeToFile();
    
    /**
     * Will return a copy of the script which will be the sum total of the fragments
     */
    String getScript();
    
}
