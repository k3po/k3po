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
import java.net.URL;
import java.util.Enumeration;

import org.junit.Assert;
import org.junit.Test;
import org.kaazing.k3po.pcap.converter.internal.filter.Filter;
import org.kaazing.k3po.pcap.converter.internal.filter.NonEmptyDestPortTcpPacketFilter;
import org.kaazing.k3po.pcap.converter.internal.packet.Packet;
import org.kaazing.k3po.pcap.converter.internal.parser.Parser;
import org.kaazing.k3po.pcap.converter.internal.utils.WireSharkLocation;

public class ParserTest {
    // If running from eclipse may need actual location
    private String tsharkPath = WireSharkLocation.wiresharkLocation;

    //Full Run Tests
//    @Test
//    public void testCanInitiateClass() throws Exception {
//        // get capture file
//        Enumeration<URL> captureFileEnum = getClass().getClassLoader().getResources("tcpdumps/ServerHelloRobot.cap");
//        String captureFileLoc = captureFileEnum.nextElement().getPath();
//        Assert.assertEquals(captureFileLoc != null, true);
//
//        // get pre created pdml file
//        Enumeration<URL> pdmlConversion = getClass().getClassLoader().getResources("pdmls/ServerHelloRobot.pdml");
//        String pdmlFileLoc = pdmlConversion.nextElement().getPath();
//        Assert.assertEquals(pdmlFileLoc != null, true);
//
//        // Initialize
//        Parser parser = new Parser(captureFileLoc, "target/output.pdml", tsharkPath);
//        Assert.assertNotNull(parser);
//    }

//    @Test
//    public void testTcpDumpToPdmlConversion() throws Exception {
//        // get capture file
//        Enumeration<URL> captureFileEnum = getClass().getClassLoader().getResources("tcpdumps/ServerHelloRobot.cap");
//        String captureFileLoc = captureFileEnum.nextElement().getPath();
//        Assert.assertEquals(captureFileLoc != null, true);
//
//        // get pre created pdml file
//        Enumeration<URL> pdmlConversion = getClass().getClassLoader().getResources("pdmls/ServerHelloRobot.pdml");
//        String pdmlFileLoc = pdmlConversion.nextElement().getPath();
//        Assert.assertEquals(pdmlFileLoc != null, true);
//
//        PdmlGenerator converter = null;
//        converter = new PdmlGenerator(captureFileLoc, "target/output.pdml", tsharkPath);
//        // if you want to check pdml compatibility you can compare the output file against a pdml from wireshark 1.4.3
//        File pdmlOutputFile = null;
//        try {
//            pdmlOutputFile = converter.convertTcpDumpIntoPdml();
//            converter.convertTcpDumpIntoPdml();
//        }
//        catch (PcapConverterFailureException e) {
//            System.out.println(e.getMessage());
//        }
//        Assert.assertNotNull(pdmlOutputFile);
//        // Compare to tested PDML, this will not happend based on which version of wireshark it is running
//        // Assert.assertEquals(true, FileUtils.contentEquals(pdmlOutputFile, new File(pdmlFileLoc)));
//    }

    @Test
    public void testTcpDumpParserParsing() throws Exception {
        // get capture file
        Enumeration<URL> captureFileEnum = getClass().getClassLoader().getResources("tcpdumps/ServerHelloRobot.cap");
        String captureFileLoc = captureFileEnum.nextElement().getPath();
        Assert.assertEquals(captureFileLoc != null, true);

        // get pre created pdml file
        Enumeration<URL> pdmlConversion = getClass().getClassLoader().getResources("pdmls/ServerHelloRobot.pdml");
        String pdmlFileLoc = pdmlConversion.nextElement().getPath();
        Assert.assertEquals(pdmlFileLoc != null, true);

        Parser parser = null;
        parser = new Parser(new FileInputStream(captureFileLoc), new FileInputStream(pdmlFileLoc), tsharkPath);

        Filter simpleFilter = new NonEmptyDestPortTcpPacketFilter(57321);
        Packet packet;
        int packetCnted = 0;
        int matchingPackets = 0;
        try {
            while (true) {
                packetCnted++;
                packet = parser.getNextPacket();
                if ( packet == null ) {
                    break;
                }
                if ( simpleFilter.passesFilter(packet) ) {
                    byte[] data = ((byte[]) packet.getTcpPayload());
                    System.out.println(new String(data));
                    Assert.assertEquals("Hello Robot\n", new String(data));
                    matchingPackets++;
                }
            }
        }
        catch (Exception e) {
            System.out.println("Failed test on matching packet " + packetCnted + " with exception : " + e);
            matchingPackets = 0;
            e.printStackTrace();
        }
        Assert.assertTrue(matchingPackets == 1);
    }
}
