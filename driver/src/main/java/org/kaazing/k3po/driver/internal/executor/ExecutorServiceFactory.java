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
package org.kaazing.k3po.driver.internal.executor;

import java.util.ServiceLoader;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.kaazing.k3po.driver.internal.executor.spi.ExecutorServiceFactorySpi;

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
                new TreeMap<>();

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
