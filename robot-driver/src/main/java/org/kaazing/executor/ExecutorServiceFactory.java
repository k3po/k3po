/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.executor;

import java.util.ServiceLoader;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.kaazing.executor.spi.ExecutorServiceFactorySpi;

public final class ExecutorServiceFactory {

    private final SortedMap<String, ExecutorServiceFactorySpi> executorServiceFactories;

    public static ExecutorServiceFactory newInstance() {

        SortedMap<String, ExecutorServiceFactorySpi> executorServiceFactories =
                new TreeMap<String, ExecutorServiceFactorySpi>();

        ServiceLoader<ExecutorServiceFactorySpi> loader = loadExecutorServiceFactorySpi();

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

    private ExecutorServiceFactory(SortedMap<String, ExecutorServiceFactorySpi> executorServiceFactories) {
        this.executorServiceFactories = executorServiceFactories;
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
