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
