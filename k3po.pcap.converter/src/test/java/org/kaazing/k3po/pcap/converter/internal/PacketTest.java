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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kaazing.k3po.pcap.converter.internal.packet.Packet;
import org.kaazing.k3po.pcap.converter.internal.packet.Packet.RequestType;
import org.kaazing.k3po.pcap.converter.internal.parser.Parser;
import org.kaazing.k3po.pcap.converter.internal.utils.WireSharkLocation;

public class PacketTest {
    private Packet testPacket;
    // If running from eclipse may need actual location
    private String tsharkPath = WireSharkLocation.wiresharkLocation;

    /**
     * Gets a test packet for use in every other test;
     */
    @Before
    public void setUp() throws Exception {
        Enumeration<URL> captureFileEnum = getClass().getClassLoader().getResources("tcpdumps/httpRequestDpwspoon.cap");
        String captureFileLoc = captureFileEnum.nextElement().getPath();
        Assert.assertEquals(captureFileLoc != null, true);

        Enumeration<URL> pdmlFiles = getClass().getClassLoader()
                .getResources("pdmls/httpRequestDpwspoon.pdml");
        String pdmlFile = pdmlFiles.nextElement().getPath();
        Assert.assertEquals(pdmlFile != null, true);
        
        Parser parser =  new Parser(new FileInputStream(captureFileLoc), new FileInputStream(pdmlFile) ,tsharkPath);

        testPacket = parser.getNextPacket();
    }

    @Test
    public void testPacketSize() {
        Assert.assertEquals(451, testPacket.getPacketSize());
    }

    @Test
    public void testPacketTimeStamp() throws ParseException {
        Date correctDate = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss.SS", Locale.ENGLISH)
                .parse("Jan 21, 2013 10:57:28.425137000 PST");
        Assert.assertEquals(testPacket.getTimeStamp(), correctDate);
    }

    @Test
    public void testItTcp() {
        Assert.assertEquals(true, testPacket.isTcp());
    }

    @Test
    public void testItUdp() {
        Assert.assertEquals(false, testPacket.isUdp());
    }

    @Test
    public void testTcpSrcPort() {
        Assert.assertEquals(64697, testPacket.getTcpSrcPort());
    }

    @Test
    public void testTcpDestPort() {
        Assert.assertEquals(80, testPacket.getTcpDestPort());
    }

    @Test
    public void testTcpStream() {
        Assert.assertEquals(0, testPacket.getTcpStream());
    }

    @Test
    public void testTcpLen() {
        Assert.assertEquals(385, testPacket.getTcpLen());
    }

    @Test
    public void testTcpSeq() {
        Assert.assertEquals(1, testPacket.getRelativeTcpSeqNum());
    }

    @Test
    public void testTcpNextSeq() {
        Assert.assertEquals(386, testPacket.getTcpNextRelativeSeqNum());
    }

    @Test
    public void testTcpPayloadStart() {
        Assert.assertEquals(66, testPacket.getTcpPayloadStart());
    }

    @Test
    public void testTcpPayloadSize() {
        Assert.assertEquals(385, testPacket.getTcpPayloadSize());
    }

    @Test
    public void testTcpPayload() {
        Assert.assertTrue(new String(testPacket.getTcpPayload()).startsWith("GET / HTTP/1.1"));
    }

    @Test
    public void tcpFlags() {
        Assert.assertEquals(24, testPacket.getTcpFlags());
    }

    @Test
    public void testTcpAck() {
        Assert.assertEquals(true, testPacket.isTcpFlagsAck());
    }

    @Test
    public void testTcpSyn() {
        Assert.assertEquals(false, testPacket.isTcpFlagsSyn());
    }

    @Test
    public void testTcpReset() {
        Assert.assertEquals(false, testPacket.isTcpFlagsReset());
    }

    @Test
    public void testTcpFin() {
        Assert.assertEquals(false, testPacket.isTcpFlagsFin());
    }

    @Test
    public void testDestIp() {
        Assert.assertEquals("107.21.212.123", testPacket.getDestIpAddr());
    }

    @Test
    public void testSrcIp() {
        Assert.assertEquals("192.168.4.223", testPacket.getSrcIpAddr());
    }

    @Test    
    public void testDoubleEpochTime() {
        Assert.assertTrue(1358794648.425137000 == testPacket.getTimeInMicroSecondsFromEpoch());
    }

    @Test    
    public void testTcpSeqNum() {
        Assert.assertEquals(1759284929L, testPacket.getTcpSequenceNumber());
    }

    @Test
    public void testTcpAckNum() {
        Assert.assertEquals(2348663704L, (long) testPacket.getTcpAcknowledgementNumber());
    }
    
    @Test
    public void testHttpRequestURI(){
        Assert.assertEquals("/", testPacket.getHttpRequestURI());
    }
    
    @Test
    public void testHttpRequestType(){
        Assert.assertEquals(RequestType.GET, testPacket.getHttpRequestType());
    }
    
    @Test
    public void testHttpFields(){
        Assert.assertTrue(!testPacket.getListOfHttpFields().isEmpty());
        Assert.assertEquals(10, testPacket.getListOfHttpFields().size());
        Map<Integer, Integer> fields = testPacket.getHttpFieldPositionsAndSize();
        for(String pair : new String("102:24;368:33;66:16;82:20;126:73;332:36;199:133;401:48;449:2").split(";")){
            String value[] = pair.split(":");
            Assert.assertTrue(fields.containsKey(Integer.parseInt(value[0]) - 66));
            Assert.assertTrue(fields.get(Integer.parseInt(value[0]) - 66 ) == Integer.parseInt(value[1]) );
        }
    }
}
