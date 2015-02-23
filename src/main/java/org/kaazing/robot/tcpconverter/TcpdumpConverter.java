/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.tcpconverter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.kaazing.robot.tcpconverter.packet.Packet;
import org.kaazing.robot.tcpconverter.parser.Parser;
import org.kaazing.robot.tcpconverter.rptScriptsCreator.RptScriptCreator;

/**
 * Converts the tcpdump into rupert script
 */
public class TcpdumpConverter {

    private Parser pdmlParser;

    public TcpdumpConverter(InputStream tcpDumpFileLocation, InputStream pdmlOutputFileDestination) {
        super();
        pdmlParser = new Parser(tcpDumpFileLocation, pdmlOutputFileDestination);
    }

    public void convertTcpDumpToRpt() throws TcpdumpConverterFailureException {
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
    		TcpdumpConverter converter = new TcpdumpConverter(new FileInputStream(args[0]), new FileInputStream(args[1]));
    		converter.convertTcpDumpToRpt();
    
    	}
    }
}
