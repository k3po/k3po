/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.behavior.handler.barrier;

import static com.kaazing.netty.channel.ChannelFutureListeners.chainedFuture;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

import com.kaazing.robot.behavior.Barrier;
import com.kaazing.robot.behavior.handler.prepare.PreparationEvent;

public class AwaitBarrierUpstreamHandler extends AbstractBarrierHandler implements ChannelUpstreamHandler {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(AwaitBarrierUpstreamHandler.class);

    private Queue<ChannelEvent> queue;

    public AwaitBarrierUpstreamHandler(Barrier barrier) {
        super(barrier);
    }

    @Override
    public void prepareRequested(final ChannelHandlerContext ctx, PreparationEvent evt) {

        final boolean isDebugEnabled = LOGGER.isDebugEnabled();

        if (isDebugEnabled) {
            LOGGER.debug("await barrier upstream prepare received");
        }

        super.prepareRequested(ctx, evt);

        if (isDebugEnabled) {
            LOGGER.debug("await barrier upstream prepare on super returned");
        }

        // when pipeline future complete, pay attention to barrier future
        final ChannelFuture handlerFuture = getHandlerFuture();
        ChannelFuture pipelineFuture = getPipelineFuture();
        pipelineFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(final ChannelFuture f) throws Exception {
                LOGGER.debug("pipeline future for upstream barrier complete");

                // If the pipeline was not complete successfully we dont want nor need to wait for the barrier
                if (!f.isSuccess()) {
                    if (f.isCancelled()) {
                        handlerFuture.cancel();
                    } else {
                        handlerFuture.setFailure(f.getCause());
                    }
                } else {
                    // when barrier future complete, trigger handler future
                    Barrier barrier = getBarrier();
                    ChannelFuture barrierFuture = barrier.getFuture();
                    barrierFuture.addListener(chainedFuture(handlerFuture));
                }
            }
        });

        // when handler future complete, flush queued channel events
        queue = new ConcurrentLinkedQueue<ChannelEvent>();
        handlerFuture.addListener(new ChannelFutureListener() {

            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    synchronized (ctx) {
                        LOGGER.info("Barrier has been notified. Releasing queued upstream events");
                        Queue<ChannelEvent> pending = queue;
                        queue = null;
                        for (ChannelEvent evt : pending) {
                            ctx.sendUpstream(evt);
                        }
                    }
                } else {
                    LOGGER.debug("handler future for barrier completed unsuccessfully. Not releaseing upstream events");

                }
            }

        });
    }

    @Override
    protected void handleUpstream1(ChannelHandlerContext ctx, ChannelEvent evt) throws Exception {

        // In the AwaitBarrierDownStreamHandler we have to wait for the
        // pipelinefuture to be complete before
        // sending upstream. We shouldn't have to do that here since we wouldn't
        // be here if the earlier upstream handlers
        // hadn't been invoked
        if (!getPipelineFuture().isDone()) {
            LOGGER.warn("Received upstream event " + evt + " in barrier before pipeline future is complete");
        }

        // while handler future not complete, queue channel events
        ChannelFuture handlerFuture = getHandlerFuture();
        /*
         * Note that we are synchronizing on the channel context because there
         * exists a race when de-queueing the events.
         */
        synchronized (ctx) {
            if (!handlerFuture.isDone()) {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("Awaiting on barrier notification. Queueing upstream event " + evt);
                }
                queue.add(evt);
                return;
            }
            ctx.sendUpstream(evt);
        }
    }

    boolean hasQueuedChannelEvents() {
        return queue != null && !queue.isEmpty();
    }
}
