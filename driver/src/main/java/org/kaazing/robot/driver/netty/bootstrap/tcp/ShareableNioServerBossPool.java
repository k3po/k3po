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

package org.kaazing.robot.driver.netty.bootstrap.tcp;

import java.util.concurrent.Executor;

import org.jboss.netty.channel.socket.nio.NioServerBoss;
import org.jboss.netty.channel.socket.nio.NioServerBossPool;
import org.jboss.netty.channel.socket.nio.WorkerPool;

/**
 * This implementation of a {@link WorkerPool} should be used if you plan to share a
 * {@link WorkerPool} between different Factories. You will need to call {@link #destroy()} by your
 * own once you want to release any resources of it.
 *
 *
 */
public final class ShareableNioServerBossPool extends ShareableBossPool<NioServerBoss> {

    public ShareableNioServerBossPool(Executor bossExecutor, int bossCount) {
        super(new NioServerBossPool(bossExecutor, bossCount));
    }

}
