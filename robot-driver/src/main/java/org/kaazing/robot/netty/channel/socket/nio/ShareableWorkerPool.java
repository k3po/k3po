/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */
package org.kaazing.robot.netty.channel.socket.nio;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.jboss.netty.channel.socket.nio.NioWorker;
import org.jboss.netty.channel.socket.nio.NioWorkerPool;
import org.jboss.netty.channel.socket.nio.WorkerPool;

import org.kaazing.executor.ExecutorServiceFactory;

// ShareableWorkerPool in org.jboss.netty does not provide a built in way to shut it down. This accomplishes that
// by using a singleton instance and reference counting.
public final class ShareableWorkerPool implements WorkerPool<NioWorker> {

    // private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(ShareableWorkerPool.class);

    private static final int NUMBER_WORKERS = 1;

    private static volatile ShareableWorkerPool instance;
    // Exclusive lock used in getInstance. Shared lock used in shutdown method.
    private static final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private static final Lock readLock = rwl.readLock();
    private static final Lock writeLock = rwl.writeLock();

    private final AtomicInteger referenceCount = new AtomicInteger();
    private final AtomicBoolean locked = new AtomicBoolean();
    private final org.jboss.netty.channel.socket.nio.ShareableWorkerPool<NioWorker> wrapped;

    public static ShareableWorkerPool getInstance(ExecutorServiceFactory executorServiceFactory) {
        writeLock.lock();
        try {
            if (instance == null) {
                Executor executor = executorServiceFactory.newExecutorService("worker");

                // @formatter:off
                org.jboss.netty.channel.socket.nio.ShareableWorkerPool<NioWorker> wrapped =
                        new org.jboss.netty.channel.socket.nio.ShareableWorkerPool<NioWorker>(
                                new NioWorkerPool(executor, NUMBER_WORKERS));
                // @formatter:on

                instance = new ShareableWorkerPool(wrapped);
            }
            instance.referenceCount.incrementAndGet();
        } finally {
            writeLock.unlock();
        }
        return instance;
    }

    @Override
    public void rebuildSelectors() {
        wrapped.rebuildSelectors();

    }

    @Override
    public NioWorker nextWorker() {
        return wrapped.nextWorker();
    }

    @Override
    public void shutdown() {
        // Wait if another thread is retrieving another instance.
        readLock.lock();
        try {
            if (instance != null && referenceCount.decrementAndGet() < 1) {
                destroy();
            }
        } catch (RuntimeException e) {
            // This happens if you try to destroy a worker from itself (worker will throw the exception). It can also happen
            // if another thread is already in the process of shutting it down. This can happen because we are sharing
            // 1 worker from all the socket factories. When the Robot control channel is closed (channelClosed event) it will
            // release the robot's resources. If this occurs after the resources from the server control channel are
            // released. Then the reference count will be 0, but the call will fail since it can't shut it down. If/when
            // this occurs care needs to be taken to shut it down from another thread.
            referenceCount.incrementAndGet();
            throw e;
        } finally {
            readLock.unlock();
        }

    }

    private ShareableWorkerPool(org.jboss.netty.channel.socket.nio.ShareableWorkerPool<NioWorker> wrapped) {
        this.wrapped = wrapped;
    }

    private void destroy() {
        // We do not block and throw and exception if we can not acquire the lock. This is because
        // the thread will block internally waiting for the worker to shutdown. BUT if that worker winds
        // up calling this and we block, it will deadlock.
        if (locked.compareAndSet(false, true)) {
            try {
                wrapped.destroy();
                instance = null;
            } finally {
                locked.set(false);
            }
        } else {
            throw new IllegalStateException("Can not destroy worker pool more than once");
        }
    }


}

