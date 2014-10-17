/*
 * Copyright (c) 2014 "Kaazing Corporation," (www.kaazing.com)
 *
 * This file is part of Robot.
 *
 * Robot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.kaazing.robot.driver.netty.channel.tcp;

import static org.junit.Assert.*;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.ServiceLoader;

import org.junit.Test;
import org.kaazing.robot.driver.netty.channel.ChannelAddress;
import org.kaazing.robot.driver.netty.channel.ChannelAddressFactorySpi;

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

        assertEquals(new ChannelAddress(URI.create("tcp://127.0.0.1:8000")), channelAddress);
    }
}
