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

package org.kaazing.k3po.driver.internal.netty.bootstrap.agrona;

import static java.lang.Thread.currentThread;
import static org.jboss.netty.channel.Channels.fireChannelClosed;
import static org.jboss.netty.channel.Channels.fireChannelDisconnected;
import static org.jboss.netty.channel.Channels.fireChannelUnbound;
import static org.jboss.netty.channel.Channels.fireWriteComplete;

import java.util.Deque;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelFuture;
import org.kaazing.k3po.driver.internal.netty.channel.agrona.AgronaChannelAddress;
import org.kaazing.k3po.driver.internal.netty.channel.agrona.ChannelReader;
import org.kaazing.k3po.driver.internal.netty.channel.agrona.ChannelWriter;

import uk.co.real_logic.agrona.concurrent.BackoffIdleStrategy;
import uk.co.real_logic.agrona.concurrent.IdleStrategy;
import uk.co.real_logic.agrona.concurrent.UnsafeBuffer;

public final class AgronaWorker implements Runnable {

    private static final int MAX_PARK_NS = 100;

    private static final int MIN_PARK_NS = 1;

    private static final int MAX_YIELDS = 30;

    private static final int MAX_SPINS = 20;

    private final Deque<Runnable> taskQueue;
    private final Set<AgronaChannel> readableChannels;

    private final CountDownLatch shutdownLatch = new CountDownLatch(1);
    private volatile boolean shutdown;

    AgronaWorker() {
        this.taskQueue = new ConcurrentLinkedDeque<>();
        this.readableChannels = new ConcurrentSkipListSet<AgronaChannel>();
    }

    public void register(AgronaChannel channel) {
        readableChannels.add(channel);
    }

    public void write(AgronaChannel channel, ChannelBuffer channelBuffer, ChannelFuture writeFuture) {
        registerTask(new WriteTask(channel, channelBuffer, writeFuture));
    }

    public void close(AgronaChannel channel) {
        readableChannels.remove(channel);
        registerTask(new CloseTask(channel));
    }

    @Override
    public void run() {
        final IdleStrategy idleStrategy = new BackoffIdleStrategy(MAX_SPINS, MAX_YIELDS, MIN_PARK_NS, MAX_PARK_NS);

        while (!shutdown) {
            int workCount = 0;

            workCount += executeTasks();
            workCount += readMessges();

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

    private int readMessges() {
        int workCount = 0;

        for (final AgronaChannel channel : readableChannels) {
            AgronaChannelAddress remoteAddress = channel.getRemoteAddress();
            ChannelReader reader = remoteAddress.getReader();
            workCount += reader.read(channel.messageHandler);
        }

        return workCount;
    }

    private void registerTask(Runnable task) {
        taskQueue.offer(task);
    }

    private static final class WriteTask implements Runnable {

        private final AgronaChannel channel;
        private final ChannelBuffer buffer;
        private final ChannelFuture future;

        public WriteTask(
                AgronaChannel channel,
                ChannelBuffer buffer,
                ChannelFuture future) {
            this.channel = channel;
            this.buffer = buffer;
            this.future = future;
        }

        @Override
        public void run() {
            int readableBytes = buffer.readableBytes();
            byte[] array = buffer.array();
            int arrayOffset = buffer.arrayOffset();
            int readerIndex = buffer.readerIndex();

            UnsafeBuffer srcBuffer = new UnsafeBuffer(array, arrayOffset + readerIndex, readableBytes);

            ChannelWriter writer = channel.getRemoteAddress().getWriter();
            // TODO: msgTypeId is fixed (!)
            writer.write(0x01, srcBuffer, 0, srcBuffer.capacity());
            future.setSuccess();
            fireWriteComplete(channel, readableBytes);
        }

    }

    private static final class CloseTask implements Runnable {

        private final AgronaChannel channel;

        public CloseTask(AgronaChannel channel) {
            this.channel = channel;
        }

        @Override
        public void run() {
            if (channel.setClosed()) {
                fireChannelDisconnected(channel);
                fireChannelUnbound(channel);
                fireChannelClosed(channel);
            }
        }

    }
}
