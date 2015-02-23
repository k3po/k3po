/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.k3po.pcap.converter;

import java.io.FileInputStream;
import java.net.URL;
import java.util.Enumeration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.kaazing.k3po.pcap.converter.filter.IpFilter;
import org.kaazing.k3po.pcap.converter.packet.Packet;
import org.kaazing.k3po.pcap.converter.parser.Parser;
import org.kaazing.k3po.pcap.converter.utils.WireSharkLocation;

public class IpFilterTest {
    private Packet testPacket;
    private String tsharkPath = WireSharkLocation.wiresharkLocation;

    /**
     * Gets a test packet for use in every other test;
     */
    @Before
    public void setUp() throws Exception {
        Enumeration<URL> captureFileEnum = getClass().getClassLoader().getResources("tcpdumps/httpRequestDpwspoon.cap");
        String captureFileLoc = captureFileEnum.nextElement().getPath();
        Assert.assertEquals(captureFileLoc != null, true);

        // get pre created pdml file
        Enumeration<URL> pdmlConversion = getClass().getClassLoader().getResources("pdmls/httpRequestDpwspoon.pdml");
        String pdmlFileLoc = pdmlConversion.nextElement().getPath();
        Assert.assertEquals(pdmlFileLoc != null, true);

        Parser parser = null;
        parser = new Parser(new FileInputStream(captureFileLoc), new FileInputStream(pdmlFileLoc), tsharkPath);

        testPacket = parser.getNextPacket();
    }

    @Test
    public void testIpFilterFailsCorrectlyTest() {
        IpFilter ipfilter = new IpFilter("145.5.4.3");
        Assert.assertFalse(ipfilter.passesFilter(testPacket));
    }

    @Test
    public void testPassesDestinationTest() {
        IpFilter ipfilter = new IpFilter("107.21.212.123");
        Assert.assertTrue(ipfilter.passesFilter(testPacket));
    }

    @Test
    public void testPassesSrcTest() {
        IpFilter ipfilter = new IpFilter("192.168.4.223");
        Assert.assertTrue(ipfilter.passesFilter(testPacket));
    }

}
