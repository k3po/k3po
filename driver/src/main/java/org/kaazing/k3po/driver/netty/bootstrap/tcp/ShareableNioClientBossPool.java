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

package org.kaazing.k3po.driver.netty.bootstrap.tcp;

import java.util.concurrent.Executor;

import org.jboss.netty.channel.socket.nio.NioClientBoss;
import org.jboss.netty.channel.socket.nio.NioClientBossPool;

public final class ShareableNioClientBossPool extends ShareableBossPool<NioClientBoss> {

    public ShareableNioClientBossPool(Executor bossExecutor, int bossCount) {
        super(new NioClientBossPool(bossExecutor, bossCount));
    }
}

