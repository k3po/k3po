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
package org.kaazing.robot.netty.channel.socket.nio;

import java.util.concurrent.Executor;

import org.jboss.netty.channel.socket.nio.BossPool;
import org.jboss.netty.channel.socket.nio.NioClientBoss;
import org.jboss.netty.channel.socket.nio.NioClientBossPool;

import org.kaazing.executor.ExecutorServiceFactory;

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

