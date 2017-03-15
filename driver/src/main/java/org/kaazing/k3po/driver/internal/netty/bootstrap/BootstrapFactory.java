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
package org.kaazing.k3po.driver.internal.netty.bootstrap;

import static java.util.Collections.emptyMap;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import org.jboss.netty.util.ExternalResourceReleasable;
import org.kaazing.k3po.driver.internal.executor.ExecutorServiceFactory;

public final class BootstrapFactory implements ExternalResourceReleasable {

    private final Map<String, BootstrapFactorySpi> bootstrapFactories;

    private BootstrapFactory(Map<String, BootstrapFactorySpi> bootstrapFactories) {
        this.bootstrapFactories = Collections.unmodifiableMap(bootstrapFactories);
    }

    public static BootstrapFactory newBootstrapFactory() {
        Map<Class<?>, Object> injectables = emptyMap();
        return newBootstrapFactory(injectables);
    }

    public static BootstrapFactory newBootstrapFactory(Map<Class<?>, Object> injectables) {
        ServiceLoader<BootstrapFactorySpi> loader = loadBootstrapFactorySpi();

        // load BootstrapFactorySpi instances
        Map<String, BootstrapFactorySpi> bootstrapFactories = new HashMap<>();
        for (BootstrapFactorySpi bootstrapFactorySpi : loader) {
            String transportName = bootstrapFactorySpi.getTransportName();
            BootstrapFactorySpi oldBootstrapFactorySpi = bootstrapFactories.put(transportName,
                    bootstrapFactorySpi);
            if (oldBootstrapFactorySpi != null) {
                throw new BootstrapException(String.format("Duplicate transport bootstrap factory: %s", transportName));
            }
        }

        ExecutorServiceFactory executorServiceFactory = ExecutorServiceFactory.newInstance();

        // inject resources into BootstrapFactorySpi instances
        BootstrapFactory bootstrapFactory = new BootstrapFactory(bootstrapFactories);
        for (BootstrapFactorySpi bootstrapFactorySpi : bootstrapFactories.values()) {
            Utils.inject(bootstrapFactorySpi, BootstrapFactory.class, bootstrapFactory);
            Utils.inject(bootstrapFactorySpi, ExecutorServiceFactory.class, executorServiceFactory);
            Utils.injectAll(bootstrapFactorySpi, injectables);
        }

        return bootstrapFactory;
    }

    public void shutdown() {
        for (BootstrapFactorySpi bootstrapFactory : bootstrapFactories.values()) {
            bootstrapFactory.shutdown();
        }
    }

    @Override
    public void releaseExternalResources() {
        for (BootstrapFactorySpi bootstrapFactory : bootstrapFactories.values()) {
            bootstrapFactory.releaseExternalResources();
        }
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
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        return (loader != null) ? ServiceLoader.load(BootstrapFactorySpi.class, loader) : ServiceLoader.load(BootstrapFactorySpi.class);
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
