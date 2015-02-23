/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.k3po.pcap.converter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.kaazing.k3po.pcap.converter.packet.Packet;
import org.kaazing.k3po.pcap.converter.parser.Parser;
import org.kaazing.k3po.pcap.converter.author.RptScriptCreator;

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
