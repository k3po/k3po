/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot;

import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.lang.String.format;

public class RobotServerFactories {

    public static RobotServerFactory createRobotServerFactory() {
        return createRobotServerFactory(Thread.currentThread().getContextClassLoader());

    }

    public static RobotServerFactory createRobotServerFactory(ClassLoader classLoader) {
        Class<RobotServerFactorySPI> clazz = RobotServerFactorySPI.class;
        ServiceLoader<RobotServerFactorySPI> loader = (classLoader != null) ?
                ServiceLoader.load(clazz, classLoader) : ServiceLoader.load(clazz);
        ConcurrentMap<String, RobotServerFactorySPI> factories = new ConcurrentHashMap<>();
        for (RobotServerFactorySPI factory : loader) {
            // just return first one, maybe in the future we will look for them by a parameter or name
            factories.putIfAbsent(factory.getSchemeName(), factory);
        }
        return new RobotServerFactoryImpl(factories);
    }

    private static class RobotServerFactoryImpl implements RobotServerFactory {

        private final Map<String, RobotServerFactorySPI> factories;

        public RobotServerFactoryImpl(Map<String, RobotServerFactorySPI> factories) {
            this.factories = factories;
        }

        @Override
        public RobotServer createRobotServer(URI uri, boolean verbose) {
            final String schemeName = uri.getScheme();
            if (schemeName == null) {
                throw new NullPointerException("scheme");
            }

            RobotServerFactorySPI factory = factories.get(schemeName);
            if(factory == null){
                throw new IllegalArgumentException(format("Unable to load scheme '%s': No appropriate Robot Server" +
                        " factory found", schemeName));
            }
            return factory.createRobotServer(uri, verbose);
        }
    }
}
