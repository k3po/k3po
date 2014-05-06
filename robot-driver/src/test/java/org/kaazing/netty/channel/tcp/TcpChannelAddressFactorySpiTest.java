/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.netty.channel.tcp;

import static org.junit.Assert.fail;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.ServiceLoader;

import junit.framework.Assert;

import org.junit.Test;

import org.kaazing.netty.channel.ChannelAddress;
import org.kaazing.netty.channel.spi.ChannelAddressFactorySpi;

public class TcpChannelAddressFactorySpiTest {

    @Test
    public void shouldLoadServiceImplementation() {

        for (ChannelAddressFactorySpi channelAddressFactorySpi : ServiceLoader.load(ChannelAddressFactorySpi.class)) {
            if (channelAddressFactorySpi instanceof TcpChannelAddressFactorySpi) {
                return;
            }
        }

        fail();
    }

    @Test
    public void shouldCreateChannelAddressWithoutTransport() throws Exception {

        TcpChannelAddressFactorySpi channelAddressFactorySpi = new TcpChannelAddressFactorySpi();
        Map<String, Object> options = Collections.emptyMap();
        ChannelAddress channelAddress = channelAddressFactorySpi.newChannelAddress(URI.create("tcp://127.0.0.1:8000"),
                options);

        Assert.assertEquals(new ChannelAddress(URI.create("tcp://127.0.0.1:8000")), channelAddress);
    }
}
