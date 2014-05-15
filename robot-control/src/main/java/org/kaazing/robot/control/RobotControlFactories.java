/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.control;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.lang.String.format;

public final class RobotControlFactories {

    public static RobotControlFactory createRobotControlFactory() {
        return createRobotControlFactory(Thread.currentThread().getContextClassLoader());
    }

    public static RobotControlFactory createRobotControlFactory(ClassLoader classLoader) {
        Class<RobotControlFactorySPI> clazz = RobotControlFactorySPI.class;
        ServiceLoader<RobotControlFactorySPI> loader = (classLoader != null) ?
                ServiceLoader.load(clazz, classLoader) : ServiceLoader.load(clazz);
        ConcurrentMap<String, RobotControlFactorySPI> factories = new ConcurrentHashMap<>();
        for (RobotControlFactorySPI factory : loader) {
            // just return first one, maybe in the future we will look for them by a parameter or name
            factories.putIfAbsent(factory.getSchemeName(), factory);
        }
        return new RobotServerFactoryImpl(factories);
    }

    private static class RobotServerFactoryImpl implements RobotControlFactory {
        private final Map<String,RobotControlFactorySPI> factories;

        public RobotServerFactoryImpl(Map<String, RobotControlFactorySPI> factories) {
            this.factories = factories;
        }

        @Override
        public RobotControl newClient(URI controlURI) throws Exception {
            final String schemeName = controlURI.getScheme();
            if (schemeName == null) {
                throw new NullPointerException("scheme");
            }

            RobotControlFactorySPI factory = factories.get(schemeName);
            if(factory == null){
                throw new IllegalArgumentException(format("Unable to load scheme '%s': No appropriate Robot Control " +
                        "factory found", schemeName));
            }
            return factory.newClient(controlURI);
        }
    }
}
