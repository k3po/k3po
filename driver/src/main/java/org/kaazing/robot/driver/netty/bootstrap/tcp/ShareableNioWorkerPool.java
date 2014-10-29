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

import org.jboss.netty.channel.socket.nio.NioWorker;
import org.jboss.netty.channel.socket.nio.NioWorkerPool;
import org.jboss.netty.channel.socket.nio.ShareableWorkerPool;
import org.jboss.netty.channel.socket.nio.WorkerPool;

public final class ShareableNioWorkerPool implements WorkerPool<NioWorker> {

    private final ShareableWorkerPool<NioWorker> workerPool;

    public ShareableNioWorkerPool(Executor workerExecutor, int workerCount) {
        workerPool = new ShareableWorkerPool<NioWorker>(new NioWorkerPool(workerExecutor, workerCount));
    }

    @Override
    public void rebuildSelectors() {
        workerPool.rebuildSelectors();
    }

    @Override
    public void shutdown() {
        workerPool.shutdown();
    }

    @Override
    public NioWorker nextWorker() {
        return workerPool.nextWorker();
    }

    public void destroy() {
        workerPool.destroy();
    }
}

