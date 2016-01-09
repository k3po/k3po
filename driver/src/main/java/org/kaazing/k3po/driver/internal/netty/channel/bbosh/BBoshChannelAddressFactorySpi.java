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
package org.kaazing.k3po.driver.internal.netty.channel.bbosh;

import static org.kaazing.k3po.driver.internal.netty.channel.LocationFactories.changeSchemeOnly;

import java.net.URI;
import java.util.Map;

import org.jboss.netty.channel.ChannelException;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddressFactorySpi;
import org.kaazing.k3po.driver.internal.netty.channel.LocationFactory;

public class BBoshChannelAddressFactorySpi extends ChannelAddressFactorySpi {

    private static final LocationFactory TRANSPORT_FACTORY = changeSchemeOnly("http");

    @Override
    public String getSchemeName() {
        return "bbosh";
    }

    @Override
    protected LocationFactory getTransportFactory() {
        return TRANSPORT_FACTORY;
    }

    @Override
    protected ChannelAddress newChannelAddress0(URI location, ChannelAddress transport, Map<String, Object> options) {

        String host = location.getHost();
        int port = location.getPort();
        String path = location.getPath();

        if (host == null) {
            throw new ChannelException(String.format("%s host missing", getSchemeName()));
        }

        if (port == -1) {
            throw new ChannelException(String.format("%s port missing", getSchemeName()));
        }

        if (path == null) {
            throw new ChannelException(String.format("%s path missing", getSchemeName()));
        }

        return super.newChannelAddress0(location, transport, options);
    }
}
