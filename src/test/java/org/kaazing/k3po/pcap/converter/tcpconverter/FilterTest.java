/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.k3po.pcap.converter.tcpconverter;

import org.junit.Assert;
import org.junit.Test;

import org.kaazing.k3po.pcap.converter.tcpconverter.packet.Packet;
import org.kaazing.k3po.pcap.converter.tcpconverter.filter.Filter;
import org.kaazing.k3po.pcap.converter.tcpconverter.filter.NonEmptyDestPortTcpPacketFilter;

public class FilterTest {

    @Test
    public void testInitiatePdmlNonEmptyDestPacketFilter() {
        Filter pf = new NonEmptyDestPortTcpPacketFilter(53);
        Assert.assertNotNull(pf);
    }

    @Test
    public void testPacketFilterCompareSuccessOnNonEmptyPayload() throws Exception {
        Filter pf = new NonEmptyDestPortTcpPacketFilter(53);
        Packet p1 = new Packet();
        p1.setTcpDestPort(53);
        p1.setTcpPayloadLength(100);
        p1.setTcp(true);
        Assert.assertTrue(pf.passesFilter(p1));
    }

    @Test
    public void testPacketFilterCompareFailOnNonEmpty() throws Exception {
        Filter pf = new NonEmptyDestPortTcpPacketFilter(53);
        Packet p1 = new Packet();
        p1.setTcpDestPort(53);
        p1.setTcpPayloadLength(0);
        p1.setTcp(true);
        Assert.assertFalse(pf.passesFilter(p1));
    }

    @Test
    public void testPacketFilterCompareFailOnDestPort() throws Exception {
        Filter pf = new NonEmptyDestPortTcpPacketFilter(53);
        Packet p1 = new Packet();
        p1.setTcpDestPort(51);
        p1.setTcpPayloadLength(100);
        p1.setTcp(true);
        Assert.assertFalse(pf.passesFilter(p1));
    }

    @Test
    public void testPacketFilterCompareFailOnTcp() throws Exception {
        Filter pf = new NonEmptyDestPortTcpPacketFilter(53);
        Packet p1 = new Packet();
        p1.setTcpDestPort(51);
        p1.setTcpPayloadLength(100);
        Assert.assertFalse(pf.passesFilter(p1));
    }

}
