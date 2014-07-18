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

import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

import org.kaazing.robot.behavior.Barrier;
import org.kaazing.robot.behavior.handler.prepare.PreparationEvent;

public class AwaitBarrierDownstreamHandler extends AbstractBarrierHandler implements ChannelDownstreamHandler {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(AwaitBarrierDownstreamHandler.class);

    private Queue<ChannelEvent> queue;

    public AwaitBarrierDownstreamHandler(Barrier barrier) {
        super(barrier);
    }

    @Override
    public void prepareRequested(final ChannelHandlerContext ctx, PreparationEvent evt) {

        final boolean isDebugEnabled = LOGGER.isDebugEnabled();

        if (isDebugEnabled) {
            LOGGER.debug("await barrier downstream prepare received");
        }

        super.prepareRequested(ctx, evt);

        if (isDebugEnabled) {
            LOGGER.debug("await barrier downstream prepare on super returned");
        }

        // when pipeline future complete, pay attention to barrier future
        final ChannelFuture handlerFuture = getHandlerFuture();
        ChannelFuture pipelineFuture = getPipelineFuture();
        pipelineFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(final ChannelFuture f) throws Exception {
                LOGGER.debug("pipeline future for downstream barrier complete");
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
                        LOGGER.debug("Barrier has been notified. Releasing queued downstream events");
                        Queue<ChannelEvent> pending = queue;
                        queue = null;
                        for (ChannelEvent evt : pending) {
                            ctx.sendDownstream(evt);
                        }
                    }
                } else {
                    LOGGER.debug("handler future for downstream barrier failed - not releasing downstream events");
                }
            }

        });
    }

    @Override
    public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent evt) throws Exception {

        if (queue == null) {
            // We need to wait for the prepare to complete
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("downstream barrier not finished preparing sending event " + evt + " upstream");
            }

        } else if (!getPipelineFuture().isDone()) {
            // Wait for the pipeline to complete before we start holding events.
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("downstream barrier pipeline not finished sending event " + evt + " upstream");
            }

        } else if (!getHandlerFuture().isDone()) {
            // while handler future not complete, queue channel events
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Awaiting on barrier notification. Queueing downstream event " + evt);
            }
            queue.add(evt);
            return;
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("barrier future is done sending event " + evt + " upstream");
            }
        }

        synchronized (ctx) {
            ctx.sendDownstream(evt);
        }
    }

    boolean hasQueuedChannelEvents() {
        return queue != null && !queue.isEmpty();
    }
}
