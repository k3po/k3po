/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public final class RobotServerFactories {

    private RobotServerFactories() {

    }

    private static final Map<String, RobotServerFactorySPI> factories;

    static {
        Class<RobotServerFactorySPI> clazz = RobotServerFactorySPI.class;
        ServiceLoader<RobotServerFactorySPI> loader = ServiceLoader.load(clazz);
        factories = new HashMap<String, RobotServerFactorySPI>();

        for (RobotServerFactorySPI factory : loader) {
            String name = factory.getName();

            if (name != null) {
                factories.put(name, factory);
            }
        }
    }

    public static RobotServerFactory createRobotServerFactory() {
        return new DefaultRobotServerFactory();
    }

    public static RobotServerFactory createRobotServerFactory(String name) {
        RobotServerFactory result;
        if ("Default".equalsIgnoreCase("name")) {
            result = new DefaultRobotServerFactory();
        } else {
            result = factories.get(name);
        }
        return result;
    }

    private static final class DefaultRobotServerFactory implements RobotServerFactory {

        public RobotServer createRobotServer() {
            return new DefaultRobotServer();
        }
    }
}
