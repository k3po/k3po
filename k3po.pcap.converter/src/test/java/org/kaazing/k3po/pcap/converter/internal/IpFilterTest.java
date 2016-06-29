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
import org.junit.Before;
import org.junit.Test;
import org.kaazing.k3po.pcap.converter.internal.filter.IpFilter;
import org.kaazing.k3po.pcap.converter.internal.packet.Packet;
import org.kaazing.k3po.pcap.converter.internal.parser.Parser;
import org.kaazing.k3po.pcap.converter.internal.utils.WireSharkLocation;

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
