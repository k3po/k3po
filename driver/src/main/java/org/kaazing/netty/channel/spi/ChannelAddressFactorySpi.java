/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.netty.channel.spi;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import javax.annotation.Resource;

import org.jboss.netty.channel.ChannelException;

import org.kaazing.netty.channel.ChannelAddress;
import org.kaazing.netty.channel.ChannelAddressFactory;

public abstract class ChannelAddressFactorySpi {

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

        return newChannelAddress0(location, options);
    }

    protected ChannelAddress newChannelAddress0(URI location, Map<String, Object> options) {
        return new ChannelAddress(location);
    }

    public abstract static class Transportable extends ChannelAddressFactorySpi {

        private ChannelAddressFactory channelAddressFactory;

        @Resource
        public void setChannelAddressFactory(ChannelAddressFactory channelAddressFactory) {
            this.channelAddressFactory = channelAddressFactory;
        }

        protected final ChannelAddress newChannelAddress0(URI location, Map<String, Object> options) {
            URI transportURI = (URI) options.get(String.format("%s.transport", getSchemeName()));
            if (transportURI == null) {
                transportURI = createTransportURI(location);
            }

            ChannelAddress transport = channelAddressFactory.newChannelAddress(transportURI, options);
            return newChannelAddress0(location, transport);
        }

        protected ChannelAddress newChannelAddress0(URI location, ChannelAddress transport) {
            return new ChannelAddress(location, transport);
        }

        protected abstract URI createTransportURI(URI location);

    }

    /**
     * Returns the default port for the scheme provided by factories using this service provider.
     * @return the default scheme port
     */
    protected int getSchemePort() {
        return -1;
    }
}
