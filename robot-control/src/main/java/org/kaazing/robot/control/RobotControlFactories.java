/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.control;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public final class RobotControlFactories {

    private RobotControlFactories() {

    }

    private static final Map<String, RobotControlFactorySPI> factories;

    static {
        Class<RobotControlFactorySPI> clazz = RobotControlFactorySPI.class;
        ServiceLoader<RobotControlFactorySPI> loader = ServiceLoader.load(clazz);
        factories = new HashMap<String, RobotControlFactorySPI>();

        for (RobotControlFactorySPI factory : loader) {
            String name = factory.getName();

            if (name != null) {
                factories.put(name, factory);
            }
        }
    }

    public static RobotControlFactory createRobotServerFactory() {
        return new DefaultRobotControlFactory();
    }

    public static RobotControlFactory createRobotServerFactory(String name) {
        RobotControlFactory result;
        if ("Default".equalsIgnoreCase(name)) {
            result = new DefaultRobotControlFactory();
        } else {
            result = factories.get(name);
        }
        return result;
    }

    private static class DefaultRobotControlFactory implements RobotControlFactory {

        @Override
        public RobotControl newClient(URI controlURI) throws Exception {

            String scheme = controlURI.getScheme();
            if (!"tcp".equals(scheme)) {
                throw new IllegalArgumentException("Unrecognized scheme: " + scheme);
            }

            URL location = new URL(null, controlURI.toASCIIString(), new TcpURLStreamHandler());

            return new DefaultRobotControl(location);
        }
    }
}
