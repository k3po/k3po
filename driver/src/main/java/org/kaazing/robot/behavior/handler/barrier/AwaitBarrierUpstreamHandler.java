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

package org.kaazing.robot.behavior.handler.barrier;

import static org.kaazing.netty.channel.ChannelFutureListeners.chainedFuture;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

import org.kaazing.robot.behavior.Barrier;
import org.kaazing.robot.behavior.handler.prepare.PreparationEvent;

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
                        LOGGER.debug("Barrier has been notified. Releasing queued upstream events");
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
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Awaiting on barrier notification. Queueing upstream event " + evt);
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
