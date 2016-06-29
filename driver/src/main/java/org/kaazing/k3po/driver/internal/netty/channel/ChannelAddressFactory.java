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
package org.kaazing.k3po.driver.internal.netty.channel;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.jboss.netty.channel.ChannelException;

public final class ChannelAddressFactory {
    private final Map<String, ChannelAddressFactorySpi> channelAddressFactories;

    private ChannelAddressFactory(Map<String, ChannelAddressFactorySpi> channelAddressFactories) {
        this.channelAddressFactories = channelAddressFactories;
    }

    public static ChannelAddressFactory newChannelAddressFactory() {
        ServiceLoader<ChannelAddressFactorySpi> loader = loadChannelAddressFactorySpi();

        // load ChannelAddressFactorySpi instances
        ConcurrentMap<String, ChannelAddressFactorySpi> channelAddressFactories =
                new ConcurrentHashMap<>();
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
