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
package org.kaazing.k3po.driver.internal.netty.channel.agrona;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import org.jboss.netty.channel.ChannelException;
import org.junit.Test;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddressFactorySpi;

public class AgronaChannelAddressFactorySpiTest {

    @Test
    public void shouldLoadServiceImplementation() {

        for (ChannelAddressFactorySpi channelAddressFactorySpi : ServiceLoader.load(ChannelAddressFactorySpi.class)) {
            if (channelAddressFactorySpi instanceof AgronaChannelAddressFactorySpi) {
                return;
            }
        }

        fail();
    }

    @Test
    public void shouldCreateChannelAddress() throws Exception {
        AgronaChannelAddressFactorySpi channelAddressFactorySpi = new AgronaChannelAddressFactorySpi();
        Map<String, Object> options = new HashMap<>();
        options.put("reader", ChannelReader.NO_OP);
        options.put("writer", ChannelWriter.NO_OP);
        URI location = URI.create("agrona://stream/bidirectional");
        ChannelAddress channelAddress = channelAddressFactorySpi.newChannelAddress(location, options);

        assertEquals(new ChannelAddress(location), channelAddress);
    }

    @Test(expected = ChannelException.class)
    public void shouldNotCreateChannelAddressWithPort() throws Exception {
        AgronaChannelAddressFactorySpi channelAddressFactorySpi = new AgronaChannelAddressFactorySpi();
        Map<String, Object> options = Collections.emptyMap();
        URI location = URI.create("agrona://stream:1234/bidirectional");
        channelAddressFactorySpi.newChannelAddress(location, options);
    }

    @Test(expected = ChannelException.class)
    public void shouldNotCreateChannelAddressWithNonStreamHost() throws Exception
    {
        AgronaChannelAddressFactorySpi channelAddressFactorySpi = new AgronaChannelAddressFactorySpi();
        Map<String, Object> options = Collections.emptyMap();
        URI location = URI.create("agrona://non-stream/bidirectional");
        channelAddressFactorySpi.newChannelAddress(location, options);
    }

    @Test(expected = ChannelException.class)
    public void shouldNotCreateChannelAddressWithNonBidirectionalPath() throws Exception {
        AgronaChannelAddressFactorySpi channelAddressFactorySpi = new AgronaChannelAddressFactorySpi();
        Map<String, Object> options = Collections.emptyMap();
        URI location = URI.create("agrona://stream/non-bidirectional");
        channelAddressFactorySpi.newChannelAddress(location, options);
    }
}
