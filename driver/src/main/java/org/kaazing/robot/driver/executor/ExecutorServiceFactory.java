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

package org.kaazing.robot.driver.executor;

import java.util.ServiceLoader;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.kaazing.robot.driver.executor.spi.ExecutorServiceFactorySpi;

public final class ExecutorServiceFactory {

    private final SortedMap<String, ExecutorServiceFactorySpi> executorServiceFactories;

    public static ExecutorServiceFactory newInstance(ClassLoader loader) {
        return newInstance(ServiceLoader.load(ExecutorServiceFactorySpi.class, loader));
    }

    public static ExecutorServiceFactory newInstance() {
        ServiceLoader<ExecutorServiceFactorySpi> loader = loadExecutorServiceFactorySpi();
        return newInstance(loader);
    }

    private ExecutorServiceFactory(SortedMap<String, ExecutorServiceFactorySpi> executorServiceFactories) {
        this.executorServiceFactories = executorServiceFactories;
    }

    private static ExecutorServiceFactory newInstance(
            ServiceLoader<ExecutorServiceFactorySpi> loader) {
        SortedMap<String, ExecutorServiceFactorySpi> executorServiceFactories =
                new TreeMap<String, ExecutorServiceFactorySpi>();

        for (ExecutorServiceFactorySpi spi : loader) {
            String executorName = spi.getName();

            ExecutorServiceFactorySpi oldExecutorServiceFactorySpi = executorServiceFactories.get(executorName);
            if (oldExecutorServiceFactorySpi != null) {
                throw new IllegalArgumentException(String.format(
                        "Duplicate ExecutorServiceFactorySpi for executor name: %s", executorName));
            }
            executorServiceFactories.put(executorName, spi);
        }

        // default implementation
        if (executorServiceFactories.isEmpty()) {
            executorServiceFactories.put("", new ExecutorServiceFactorySpi() {

                @Override
                protected ExecutorService newExecutorService0(String executorName) {
                    return Executors.newCachedThreadPool();
                }

                @Override
                public String getName() {
                    return "";
                }
            });
        }

        return new ExecutorServiceFactory(executorServiceFactories);
    }

    public ExecutorService newExecutorService(String executorName) {
        String executorPath = findExecutorPath(executorName, executorServiceFactories.keySet());
        ExecutorServiceFactorySpi executorServiceFactorySpi = executorServiceFactories.get(executorPath);
        if (executorServiceFactorySpi == null) {
            throw new IllegalArgumentException(String.format("Unrecognized executor name: %s", executorName));
        }
        return executorServiceFactorySpi.newExecutorService(executorName);
    }

    private static String findExecutorPath(String executorName, Set<String> executorPaths) {
        if (executorPaths.contains(executorName)) {
            return executorName;
        }

        int lastDotAt = executorName.lastIndexOf('.');
        if (lastDotAt != -1) {
            executorName = executorName.substring(0, lastDotAt);
            return findExecutorPath(executorName, executorPaths);
        }

        return "";
    }

    private static ServiceLoader<ExecutorServiceFactorySpi> loadExecutorServiceFactorySpi() {
        Class<ExecutorServiceFactorySpi> service = ExecutorServiceFactorySpi.class;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return (classLoader != null) ? ServiceLoader.load(service, classLoader) : ServiceLoader.load(service);
    }
}
