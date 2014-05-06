/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */
package com.kaazing.robot.netty.channel.socket.nio;

import java.util.concurrent.Executor;

import org.jboss.netty.channel.socket.nio.BossPool;
import org.jboss.netty.channel.socket.nio.NioServerBoss;
import org.jboss.netty.channel.socket.nio.NioServerBossPool;
import org.jboss.netty.channel.socket.nio.WorkerPool;

import com.kaazing.executor.ExecutorServiceFactory;

/**
 * This implementation of a {@link WorkerPool} should be used if you plan to share a
 * {@link WorkerPool} between different Factories. You will need to call {@link #destroy()} by your
 * own once you want to release any resources of it.
 *
 *
 */
public final class ShareableServerBossPool extends ShareableBossPool<NioServerBoss> {

    private static final int NUMBER_BOSSES = 1;

    private static volatile ShareableServerBossPool instance;

    private int referenceCount;


    public static ShareableServerBossPool getInstance(ExecutorServiceFactory executorServiceFactory) {
        synchronized (ShareableServerBossPool.class) {
            if (instance == null) {
                Executor bossExecutor = executorServiceFactory.newExecutorService("boss.server");
                instance = new ShareableServerBossPool(new NioServerBossPool(bossExecutor, NUMBER_BOSSES));
            }
            // CLIENT_REFERENCE_COUNT.incrementAndGet();
            instance.referenceCount++;
        }
        return instance;
    }

    // This does not suffer the same problem we had in ShareableWorkerPool because channel handler's are
    // executed by Worker's and not Boss's.
    @Override
    public void shutdown() {
        synchronized (ShareableServerBossPool.class) {
            referenceCount--;
            if (referenceCount == 0) {
                destroy();
                instance = null;
            }
        }
    }

    private ShareableServerBossPool(BossPool<NioServerBoss> bossPool) {
        super(bossPool);
    }
}
