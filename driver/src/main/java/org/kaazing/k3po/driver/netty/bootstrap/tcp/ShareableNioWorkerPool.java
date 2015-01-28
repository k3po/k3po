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

