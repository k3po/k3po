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

