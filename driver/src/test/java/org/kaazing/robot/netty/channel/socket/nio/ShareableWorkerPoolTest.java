/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */
package org.kaazing.robot.netty.channel.socket.nio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import org.kaazing.executor.ExecutorServiceFactory;

public class ShareableWorkerPoolTest {

    private static ExecutorServiceFactory executorServiceFactory;

    private ShareableWorkerPool instance1;
    private ShareableWorkerPool instance2;

    @BeforeClass
    public static void setup() {
        executorServiceFactory = ExecutorServiceFactory.newInstance();
    }

    @After
    public void afterTest() {
        if (instance1 == null) {
            instance1.shutdown();
        }
        if (instance2 == null) {
            instance2.shutdown();
        }
    }


    @Test
    public void getInstanceOK() {
        instance1 = ShareableWorkerPool.getInstance(executorServiceFactory);
        instance2 = ShareableWorkerPool.getInstance(executorServiceFactory);

        assertEquals(instance1, instance2);

        instance2.shutdown();

        instance2 = ShareableWorkerPool.getInstance(executorServiceFactory);

        assertEquals(instance1, instance2);

        instance1.shutdown();
        instance2.shutdown();

        instance2 = ShareableWorkerPool.getInstance(executorServiceFactory);
        // getInstance returns a new instance if both instances were shutdown
        assertNotEquals(instance1, instance2);
    }

    @Test
    public void getNextWorkerOK() {
        instance1 = ShareableWorkerPool.getInstance(executorServiceFactory);
        // There should only be 1 worker so calling twice should get the same worker
        assertEquals(instance1.nextWorker(), instance1.nextWorker());

        instance2 = ShareableWorkerPool.getInstance(executorServiceFactory);

        assertEquals(instance1.nextWorker(), instance2.nextWorker());
    }

}

