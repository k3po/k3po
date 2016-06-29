/**
 * Copyright 2007-2015, Kaazing Corporation. All rights reserved.
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
package org.kaazing.k3po.driver.internal.netty.bootstrap.agrona;

import static org.jboss.netty.util.internal.ExecutorUtil.shutdownNow;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ServerChannelFactory;
import org.jboss.netty.util.ThreadRenamingRunnable;

public class AgronaServerChannelFactory implements ServerChannelFactory {

    private final AgronaServerChannelSink channelSink;
    private final AgronaServerBoss boss;
    private final ExecutorService bossExecutor;
    private final AgronaWorker worker;
    private final ExecutorService workerExecutor;

    public AgronaServerChannelFactory() {
        this.channelSink = new AgronaServerChannelSink();

        // TODO: boss pool
        this.boss = new AgronaServerBoss();
        this.bossExecutor = Executors.newFixedThreadPool(1);
        this.bossExecutor.execute(new ThreadRenamingRunnable(boss, "Agrona server boss"));

        // TODO: worker pool
        this.worker = new AgronaWorker();
        this.workerExecutor = Executors.newFixedThreadPool(1);
        this.workerExecutor.execute(new ThreadRenamingRunnable(worker, "Agrona worker"));
    }

    @Override
    public AgronaServerChannel newChannel(ChannelPipeline pipeline) {
        return new AgronaServerChannel(this, pipeline, channelSink, boss, worker);
    }

    @Override
    public void shutdown() {
        boss.shutdown();
        worker.shutdown();
    }

    @Override
    public void releaseExternalResources() {
        shutdown();
        shutdownNow(bossExecutor);
        shutdownNow(workerExecutor);
    }

}
