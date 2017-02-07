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

import static java.lang.String.format;
import static java.lang.Thread.currentThread;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.jboss.netty.channel.Channels.fireChannelClosed;
import static org.jboss.netty.channel.Channels.fireChannelDisconnected;
import static org.jboss.netty.channel.Channels.fireChannelUnbound;
import static org.jboss.netty.channel.Channels.fireWriteComplete;
import static org.kaazing.k3po.driver.internal.netty.channel.Channels.fireFlushed;
import static org.kaazing.k3po.driver.internal.netty.channel.Channels.fireOutputShutdown;
import static org.agrona.BitUtil.SIZE_OF_INT;

import java.util.Deque;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.channel.ChannelFuture;
import org.kaazing.k3po.driver.internal.netty.channel.agrona.AgronaChannelAddress;
import org.kaazing.k3po.driver.internal.netty.channel.agrona.ChannelReader;
import org.kaazing.k3po.driver.internal.netty.channel.agrona.ChannelWriter;

import org.agrona.concurrent.BackoffIdleStrategy;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.UnsafeBuffer;

public final class AgronaWorker implements Runnable {

    private static final long MAX_PARK_NS = MILLISECONDS.toNanos(100L);

    private static final long MIN_PARK_NS = MILLISECONDS.toNanos(1L);

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

    public void write(AgronaChannel channel, ChannelBuffer channelBuffer, ChannelFuture future) {
        registerTask(new WriteTask(channel, channelBuffer, future));
    }

    public void flush(AgronaChannel channel, ChannelFuture future) {
        registerTask(new FlushTask(channel, future));
    }

    public void shutdownOutput(AgronaChannel channel, ChannelFuture future) {
        registerTask(new ShutdownOutputTask(channel, future));
    }

    public void close(AgronaChannel channel, ChannelFuture future) {
        readableChannels.remove(channel);
        registerTask(new CloseTask(channel, future));
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

    private static boolean flushWriteBufferIfNecessary(AgronaChannel channel) {
        ChannelBuffer writeBuffer = channel.writeBuffer;
        int readableBytes = writeBuffer.readableBytes();
        if (readableBytes == 0) {
            return false;
        }
        else if (readableBytes < SIZE_OF_INT) {
            String message = format("Minimum %d bytes needed for message type id", SIZE_OF_INT);
            throw new ChannelException(message);
        }
        else {
            UnsafeBuffer srcBuffer = new UnsafeBuffer(new byte[readableBytes - SIZE_OF_INT]);
            int msgTypeId = writeBuffer.getInt(0);
            writeBuffer.getBytes(SIZE_OF_INT, srcBuffer.byteArray());
            writeBuffer.writerIndex(0);

            AgronaChannelAddress remoteAddress = channel.getRemoteAddress();
            ChannelWriter writer = remoteAddress.getWriter();
            writer.write(msgTypeId, srcBuffer, 0, srcBuffer.capacity());

            return true;
        }
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
            try {
                ChannelBuffer writeBuffer = channel.writeBuffer;
                int readableBytes = buffer.readableBytes();
                writeBuffer.writeBytes(buffer);
                future.setSuccess();
                fireWriteComplete(channel, readableBytes);
            }
            catch (ChannelException ex) {
                future.setFailure(ex);
            }
        }

    }

    private static final class FlushTask implements Runnable {

        private final AgronaChannel channel;
        private final ChannelFuture future;

        public FlushTask(
                AgronaChannel channel,
                ChannelFuture future) {
            this.channel = channel;
            this.future = future;
        }

        @Override
        public void run() {
            try {
                flushWriteBufferIfNecessary(channel);
                future.setSuccess();
                fireFlushed(channel);
            }
            catch (ChannelException ex) {
                future.setFailure(ex);
            }
        }

    }

    private static final class ShutdownOutputTask implements Runnable {

        private final AgronaChannel channel;
        private final ChannelFuture future;

        public ShutdownOutputTask(AgronaChannel channel, ChannelFuture future) {
            this.channel = channel;
            this.future = future;
        }

        @Override
        public void run() {
            try {
                flushWriteBufferIfNecessary(channel);
                future.setSuccess();
                fireOutputShutdown(channel);
            }
            catch (ChannelException ex) {
                future.setFailure(ex);
            }
        }
    }

    private static final class CloseTask implements Runnable {

        private final AgronaChannel channel;
        private final ChannelFuture future;

        public CloseTask(
                AgronaChannel channel,
                ChannelFuture future) {
            this.channel = channel;
            this.future = future;
        }

        @Override
        public void run() {
            try {
                // note: no implicit flush-on-close to allow cleanup without side-effects
                future.setSuccess();
                if (channel.setClosed()) {
                    fireChannelDisconnected(channel);
                    fireChannelUnbound(channel);
                    fireChannelClosed(channel);
                }
            }
            catch (ChannelException ex) {
                future.setFailure(ex);
            }
        }

    }

}
