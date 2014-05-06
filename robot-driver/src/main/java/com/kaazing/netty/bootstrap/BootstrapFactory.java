/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.netty.bootstrap;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.kaazing.executor.ExecutorServiceFactory;
import com.kaazing.netty.bootstrap.spi.BootstrapFactorySpi;

public final class BootstrapFactory {
    private final Map<String, BootstrapFactorySpi> bootstrapFactories;

    private BootstrapFactory(Map<String, BootstrapFactorySpi> bootstrapFactories) {
        this.bootstrapFactories = bootstrapFactories;
    }

    public static BootstrapFactory newBootstrapFactory() {
        Map<Class<?>, Object> injectables = new HashMap<Class<?>, Object>();
        injectables.put(ExecutorServiceFactory.class, ExecutorServiceFactory.newInstance());
        return newBootstrapFactory(injectables);
    }

    public static BootstrapFactory newBootstrapFactory(Map<Class<?>, Object> injectables) {
        ServiceLoader<BootstrapFactorySpi> loader = loadBootstrapFactorySpi();

        // load BootstrapFactorySpi instances
        ConcurrentMap<String, BootstrapFactorySpi> bootstrapFactories = new ConcurrentHashMap<String, BootstrapFactorySpi>();
        for (BootstrapFactorySpi bootstrapFactorySpi : loader) {
            String transportName = bootstrapFactorySpi.getTransportName();
            BootstrapFactorySpi oldBootstrapFactorySpi = bootstrapFactories.putIfAbsent(transportName,
                    bootstrapFactorySpi);
            if (oldBootstrapFactorySpi != null) {
                throw new BootstrapException(String.format("Duplicate transport bootstrap factory: %s", transportName));
            }
        }

        // inject resources into BootstrapFactorySpi instances
        BootstrapFactory bootstrapFactory = new BootstrapFactory(bootstrapFactories);
        for (BootstrapFactorySpi bootstrapFactorySpi : bootstrapFactories.values()) {
            Utils.inject(bootstrapFactorySpi, BootstrapFactory.class, bootstrapFactory);
            Utils.injectAll(bootstrapFactorySpi, injectables);
        }

        return bootstrapFactory;
    }

    public ServerBootstrap newServerBootstrap(String transportName) throws Exception {

        BootstrapFactorySpi bootstrapFactory = findBootstrapFactory(transportName);
        return bootstrapFactory.newServerBootstrap();
    }

    public ClientBootstrap newClientBootstrap(String transportName) throws Exception {

        BootstrapFactorySpi bootstrapFactory = findBootstrapFactory(transportName);
        return bootstrapFactory.newClientBootstrap();
    }

    private static ServiceLoader<BootstrapFactorySpi> loadBootstrapFactorySpi() {
        Class<BootstrapFactorySpi> service = BootstrapFactorySpi.class;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return (classLoader != null) ? ServiceLoader.load(service, classLoader) : ServiceLoader.load(service);
    }

    private BootstrapFactorySpi findBootstrapFactory(String transportName) throws BootstrapException {

        if (transportName == null) {
            throw new NullPointerException("transportName");
        }

        BootstrapFactorySpi bootstrapFactory = bootstrapFactories.get(transportName);
        if (bootstrapFactory == null) {
            throw new BootstrapException(String.format("Unable to load transport: %s", transportName));
        }

        return bootstrapFactory;
    }
}
