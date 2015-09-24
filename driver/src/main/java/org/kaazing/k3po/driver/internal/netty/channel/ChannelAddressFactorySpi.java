/*
 * Copyright 2014, Kaazing Corporation. All rights reserved.
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

package org.kaazing.k3po.driver.internal.netty.channel;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import javax.annotation.Resource;

import org.jboss.netty.channel.ChannelException;

public abstract class ChannelAddressFactorySpi {

    private ChannelAddressFactory channelAddressFactory;

    @Resource
    public void setChannelAddressFactory(ChannelAddressFactory channelAddressFactory) {
        this.channelAddressFactory = channelAddressFactory;
    }

    /**
     * Returns the name of the scheme provided by factories using this service provider.
     */
    public abstract String getSchemeName();

    /**
     * Returns a {@link ChannelAddress} instance for the named scheme.
     * @param options TODO
     */
    public final ChannelAddress newChannelAddress(URI location, Map<String, Object> options) {
        String locationSchemeName = location.getScheme();
        String schemeName = getSchemeName();

        if (locationSchemeName == null || !locationSchemeName.equals(schemeName)) {
            throw new ChannelException(String.format("Location scheme %s does not match expected scheme %s", location,
                    schemeName));
        }

        // make the port explicit
        int locationPort = location.getPort();
        if (locationPort == -1) {
            int newLocationPort = getSchemePort();
            if (newLocationPort != -1) {
                try {
                    String locationUserInfo = location.getUserInfo();
                    String locationHost = location.getHost();
                    String locationPath = location.getPath();
                    String locationQuery = location.getQuery();
                    String locationFragment = location.getFragment();
                    location = new URI(locationSchemeName, locationUserInfo, locationHost, newLocationPort,
                            locationPath, locationQuery, locationFragment);
                } catch (URISyntaxException e) {
                    throw new ChannelException(e);
                }
            }
        }

        URI transportURI = (URI) options.remove("transport");
        if (transportURI == null) {
            LocationFactory transportFactory = getTransportFactory();
            if (transportFactory != null) {
                transportURI = transportFactory.createURI(location);
            }
        }

        ChannelAddress transport = null;
        if (transportURI != null) {
            transport = channelAddressFactory.newChannelAddress(transportURI, options);
        }

        return newChannelAddress0(location, transport, options);
    }

    protected LocationFactory getTransportFactory() {
        return null;
    }

    protected ChannelAddress newChannelAddress0(URI location, ChannelAddress transport, Map<String, Object> options) {
        return new ChannelAddress(location, transport);
    }

    /**
     * Returns the default port for the scheme provided by factories using this service provider.
     * @return the default scheme port
     */
    protected int getSchemePort() {
        return -1;
    }
}
