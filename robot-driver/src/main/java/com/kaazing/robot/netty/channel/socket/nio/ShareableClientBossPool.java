/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */
package com.kaazing.robot.netty.channel.socket.nio;

import java.util.concurrent.Executor;

import org.jboss.netty.channel.socket.nio.BossPool;
import org.jboss.netty.channel.socket.nio.NioClientBoss;
import org.jboss.netty.channel.socket.nio.NioClientBossPool;

import com.kaazing.executor.ExecutorServiceFactory;

public final class ShareableClientBossPool extends ShareableBossPool<NioClientBoss> {

    private static final int NUMBER_BOSSES = 1;

    private static volatile ShareableClientBossPool instance;

    private int referenceCount;


    public static ShareableClientBossPool getInstance(ExecutorServiceFactory executorServiceFactory) {
        synchronized (ShareableClientBossPool.class) {
            if (instance == null) {
                Executor bossExecutor = executorServiceFactory.newExecutorService("boss.client");
                instance = new ShareableClientBossPool(new NioClientBossPool(bossExecutor, NUMBER_BOSSES));
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
        synchronized (ShareableClientBossPool.class) {
            referenceCount--;
            if (referenceCount == 0) {
                destroy();
                instance = null;
            }
        }
    }

    private ShareableClientBossPool(BossPool<NioClientBoss> bossPool) {
        super(bossPool);
    }
}

