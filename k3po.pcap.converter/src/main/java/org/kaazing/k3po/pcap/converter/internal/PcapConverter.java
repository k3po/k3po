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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.kaazing.k3po.pcap.converter.internal.author.RptScriptCreator;
import org.kaazing.k3po.pcap.converter.internal.packet.Packet;
import org.kaazing.k3po.pcap.converter.internal.parser.Parser;

/**
 * Converts the tcpdump into rupert script
 */
public class PcapConverter {

    private Parser pdmlParser;

    public PcapConverter(InputStream tcpDumpFileLocation, InputStream pdmlOutputFileDestination) {
        super();
        pdmlParser = new Parser(tcpDumpFileLocation, pdmlOutputFileDestination);
    }

    public void convertTcpDumpToRpt() throws PcapConverterFailureException {
        RptScriptCreator creator = new RptScriptCreator();
        creator.saveMemory();
        Packet packet;
        while ((packet = pdmlParser.getNextPacket()) != null) {
            creator.addPacketToScripts(packet);
        }
        creator.commitToFile();
    }
    
    public static void main(String... args) throws FileNotFoundException{
    	if(args.length != 2){
    		System.out.println("Usage arg[0] = tcpDumpFile, arg[1] = pdmlOutputFile");
    	}else{
    		PcapConverter converter = new PcapConverter(new FileInputStream(args[0]), new FileInputStream(args[1]));
    		converter.convertTcpDumpToRpt();
    
    	}
    }
}
