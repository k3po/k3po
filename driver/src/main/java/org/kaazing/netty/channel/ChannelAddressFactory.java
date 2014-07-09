/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.netty.channel;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.jboss.netty.channel.ChannelException;

import org.kaazing.netty.channel.spi.ChannelAddressFactorySpi;

public final class ChannelAddressFactory {
    private final Map<String, ChannelAddressFactorySpi> channelAddressFactories;

    private ChannelAddressFactory(Map<String, ChannelAddressFactorySpi> channelAddressFactories) {
        this.channelAddressFactories = channelAddressFactories;
    }

    public static ChannelAddressFactory newChannelAddressFactory() {
        ServiceLoader<ChannelAddressFactorySpi> loader = loadChannelAddressFactorySpi();

        // load ChannelAddressFactorySpi instances
        ConcurrentMap<String, ChannelAddressFactorySpi> channelAddressFactories =
                new ConcurrentHashMap<String, ChannelAddressFactorySpi>();
        for (ChannelAddressFactorySpi channelAddressFactorySpi : loader) {
            String schemeName = channelAddressFactorySpi.getSchemeName();
            ChannelAddressFactorySpi oldChannelAddressFactorySpi = channelAddressFactories.putIfAbsent(schemeName,
                    channelAddressFactorySpi);
            if (oldChannelAddressFactorySpi != null) {
                throw new ChannelException(String.format("Duplicate scheme channel address factory: %s", schemeName));
            }
        }

        // inject ChannelAddressFactory into ChannelAddressFactorySpi instances
        ChannelAddressFactory channelAddressFactory = new ChannelAddressFactory(channelAddressFactories);
        for (ChannelAddressFactorySpi channelAddressFactorySpi : channelAddressFactories.values()) {
            Utils.inject(channelAddressFactorySpi, ChannelAddressFactory.class, channelAddressFactory);
        }

        return channelAddressFactory;
    }

    public ChannelAddress newChannelAddress(URI location) {
        return newChannelAddress(location, Collections.<String, Object>emptyMap());
    }

    public ChannelAddress newChannelAddress(URI location, Map<String, Object> options) {
        ChannelAddressFactorySpi channelAddressFactory = findChannelAddressFactory(location.getScheme());
        return channelAddressFactory.newChannelAddress(location, options);
    }

    private static ServiceLoader<ChannelAddressFactorySpi> loadChannelAddressFactorySpi() {
        Class<ChannelAddressFactorySpi> service = ChannelAddressFactorySpi.class;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return (classLoader != null) ? ServiceLoader.load(service, classLoader) : ServiceLoader.load(service);
    }

    private ChannelAddressFactorySpi findChannelAddressFactory(String schemeName) throws ChannelException {

        if (schemeName == null) {
            throw new NullPointerException("schemeName");
        }

        ChannelAddressFactorySpi channelAddressFactory = channelAddressFactories.get(schemeName);
        if (channelAddressFactory == null) {
            throw new ChannelException(String.format(
                    "Unable to load scheme '%s': No appropriate channel factory found", schemeName));
        }

        return channelAddressFactory;
    }
}
