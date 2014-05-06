/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.netty.channel.socket.nio;

import org.jboss.netty.channel.socket.nio.Boss;
import org.jboss.netty.channel.socket.nio.BossPool;
import org.jboss.netty.channel.socket.nio.ShareableWorkerPool;
import org.jboss.netty.util.ExternalResourceReleasable;

// Same thing as ShareableWorkerPool in org.jboss.netty but for BossPools.
public class ShareableBossPool<E extends Boss> implements BossPool<E> {

    private final BossPool<E> wrappedBossPool;

    public ShareableBossPool(BossPool<E> bossPool) {
        wrappedBossPool = bossPool;
    }

    @Override
    public E nextBoss() {
        return wrappedBossPool.nextBoss();
    }

    @Override
    public void rebuildSelectors() {
        wrappedBossPool.rebuildSelectors();
    }

    /**
     * Destroy the {@link ShareableWorkerPool} and release all resources. After this is called its not usable anymore
     */
    public void destroy() {
        wrappedBossPool.shutdown();
        if (wrappedBossPool instanceof ExternalResourceReleasable) {
            ((ExternalResourceReleasable) wrappedBossPool).releaseExternalResources();
        }
    }

    @Override
    public void shutdown() {
        // do nothing
    }

}

