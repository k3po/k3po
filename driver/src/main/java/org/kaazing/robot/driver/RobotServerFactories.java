/*
 * Copyright (c) 2014 "Kaazing Corporation," (www.kaazing.com)
 *
 * This file is part of Robot.
 *
 * Robot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.kaazing.robot.driver;

import static java.lang.String.format;

import java.io.File;
import java.net.URI;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class RobotServerFactories {

    private RobotServerFactories() {

    }

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
        public RobotServer createRobotServer(URI uri, boolean verbose, File scriptDir) {
            final String schemeName = uri.getScheme();
            if (schemeName == null) {
                throw new NullPointerException("scheme");
            }

            RobotServerFactorySPI factory = factories.get(schemeName);
            if (factory == null) {
                throw new IllegalArgumentException(format("Unable to load scheme '%s': No appropriate Robot Server" +
                        " factory found", schemeName));
            }
            return factory.createRobotServer(uri, verbose, scriptDir);
        }
    }
}
