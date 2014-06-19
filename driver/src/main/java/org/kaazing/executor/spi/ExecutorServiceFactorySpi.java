/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.executor.spi;

import java.util.concurrent.ExecutorService;

public abstract class ExecutorServiceFactorySpi {

    public abstract String getName();

    public final ExecutorService newExecutorService(String executorName) {
        // TODO: validate executorName
        return newExecutorService0(executorName);
    }

    protected abstract ExecutorService newExecutorService0(String executorName);
}
