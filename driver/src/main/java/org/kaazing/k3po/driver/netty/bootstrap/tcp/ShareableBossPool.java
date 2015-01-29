/*
 * Copyright 2014, Kaazing Corporation. All rights reserved.
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

package org.kaazing.k3po.driver.netty.bootstrap.tcp;

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

