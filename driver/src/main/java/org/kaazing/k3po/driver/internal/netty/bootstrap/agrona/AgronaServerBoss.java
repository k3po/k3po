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
import static org.jboss.netty.channel.Channels.fireChannelBound;
import static org.jboss.netty.channel.Channels.fireChannelClosed;
import static org.jboss.netty.channel.Channels.fireChannelUnbound;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch;

import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.kaazing.k3po.driver.internal.netty.channel.agrona.AgronaChannelAddress;

import org.agrona.concurrent.BackoffIdleStrategy;
import org.agrona.concurrent.IdleStrategy;

public final class AgronaServerBoss implements Runnable {

    private static final long MAX_PARK_NS = MILLISECONDS.toNanos(100L);

    private static final long MIN_PARK_NS = MILLISECONDS.toNanos(1L);

    private static final int MAX_YIELDS = 30;

    private static final int MAX_SPINS = 20;

    private final Deque<Runnable> taskQueue;

    private final CountDownLatch shutdownLatch = new CountDownLatch(1);
    private volatile boolean shutdown;

    AgronaServerBoss() {
        this.taskQueue = new ConcurrentLinkedDeque<>();
    }

    void bind(
            final AgronaServerChannel channel,
            final AgronaChannelAddress localAddress,
            final ChannelFuture future) {
        registerTask(new BindTask(channel, localAddress, future));
    }

    void unbind(
            final AgronaServerChannel channel,
            final ChannelFuture future) {
        registerTask(new UnbindTask(channel, future));
    }

    void close(AgronaServerChannel channel) {
        registerTask(new CloseTask(channel));
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

    private static final class BindTask implements Runnable {

        private final AgronaServerChannel serverChannel;
        private final AgronaChannelAddress localAddress;
        private final ChannelFuture bindFuture;

        public BindTask(
                AgronaServerChannel serverChannel,
                AgronaChannelAddress localAddress,
                ChannelFuture bindFuture) {
            this.serverChannel = serverChannel;
            this.bindFuture = bindFuture;
            this.localAddress = localAddress;
        }

        @Override
        public void run() {
            serverChannel.setLocalAddress(localAddress);
            serverChannel.setBound();

            fireChannelBound(serverChannel, serverChannel.getLocalAddress());

            try {
                ChannelPipelineFactory pipelineFactory = serverChannel.getConfig().getPipelineFactory();
                ChannelPipeline pipeline = pipelineFactory.getPipeline();
                bindFuture.setSuccess();

                // fire child channel opened
                ChannelFactory channelFactory = serverChannel.getFactory();
                AgronaChildChannelSink channelSink = new AgronaChildChannelSink();
                AgronaChildChannel childChannel =
                        new AgronaChildChannel(serverChannel, channelFactory, pipeline, channelSink, serverChannel.worker);

                childChannel.setLocalAddress(serverChannel.getLocalAddress());
                fireChannelBound(childChannel, childChannel.getLocalAddress());

                childChannel.setRemoteAddress(childChannel.getLocalAddress());
                Channels.fireChannelConnected(childChannel, childChannel.getRemoteAddress());

                childChannel.worker.register(childChannel);
            }
            catch (Exception e) {
                bindFuture.setFailure(e);
            }
        }

    }

    private static final class UnbindTask implements Runnable {

        private final AgronaServerChannel serverChannel;
        private final ChannelFuture unbindFuture;

        public UnbindTask(
                AgronaServerChannel serverChannel,
                ChannelFuture unbindFuture) {
            this.serverChannel = serverChannel;
            this.unbindFuture = unbindFuture;
        }

        @Override
        public void run() {
            fireChannelUnbound(serverChannel);
            unbindFuture.setSuccess();
        }

    }

    private static final class CloseTask implements Runnable {

        private final AgronaServerChannel serverChannel;

        public CloseTask(AgronaServerChannel serverChannel) {
            this.serverChannel = serverChannel;
        }

        @Override
        public void run() {
            serverChannel.setClosed();
            fireChannelUnbound(serverChannel);
            fireChannelClosed(serverChannel);
            serverChannel.getCloseFuture().setSuccess();
        }

    }

}
