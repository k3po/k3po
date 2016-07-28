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

import static java.lang.Thread.currentThread;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.jboss.netty.channel.Channels.fireChannelConnected;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch;

import org.jboss.netty.channel.ChannelFuture;
import org.kaazing.k3po.driver.internal.netty.channel.agrona.AgronaChannelAddress;

import org.agrona.concurrent.BackoffIdleStrategy;
import org.agrona.concurrent.IdleStrategy;

public final class AgronaClientBoss implements Runnable {

    private static final long MAX_PARK_NS = MILLISECONDS.toNanos(100L);

    private static final long MIN_PARK_NS = MILLISECONDS.toNanos(1L);

    private static final int MAX_YIELDS = 30;

    private static final int MAX_SPINS = 20;

    private final Deque<Runnable> taskQueue;

    private final CountDownLatch shutdownLatch = new CountDownLatch(1);
    private volatile boolean shutdown;

    AgronaClientBoss() {
        this.taskQueue = new ConcurrentLinkedDeque<>();
    }

    public void connect(
            final AgronaClientChannel channel,
            final AgronaChannelAddress remoteAddress,
            final ChannelFuture future) {
        registerTask(new ConnectTask(channel, remoteAddress, future));
    }

    @Override
    public void run() {
        final IdleStrategy idleStrategy = new BackoffIdleStrategy(MAX_SPINS, MAX_YIELDS, MIN_PARK_NS, MAX_PARK_NS);

        while (!shutdown) {
            int workCount = 0;

            workCount += executeTasks();

            idleStrategy.idle(workCount);
        }

        shutdownLatch.countDown();
    }

    public void shutdown() {

        shutdown = true;

        try {
            shutdownLatch.await();
        } catch (InterruptedException e) {
            currentThread().interrupt();
        }
    }

    private int executeTasks() {
        int workCount = 0;

        Runnable task;
        while ((task = taskQueue.poll()) != null) {
            task.run();
            workCount++;
        }

        return workCount;
    }

    private void registerTask(Runnable task) {
        taskQueue.offer(task);
    }

    private static final class ConnectTask implements Runnable {

        private final AgronaClientChannel clientChannel;
        private final AgronaChannelAddress remoteAddress;
        private final ChannelFuture connectFuture;

        public ConnectTask(
                AgronaClientChannel clientChannel,
                AgronaChannelAddress remoteAddress,
                ChannelFuture bindFuture) {
            this.clientChannel = clientChannel;
            this.connectFuture = bindFuture;
            this.remoteAddress = remoteAddress;
        }

        @Override
        public void run() {
            clientChannel.setRemoteAddress(remoteAddress);
            clientChannel.setConnected();

            fireChannelConnected(clientChannel, clientChannel.getRemoteAddress());
            connectFuture.setSuccess();

            clientChannel.worker.register(clientChannel);
        }

    }

}
