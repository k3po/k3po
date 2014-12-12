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

package org.kaazing.k3po.driver.behavior.handler.barrier;

import static java.lang.String.format;
import static org.kaazing.k3po.driver.netty.channel.ChannelFutureListeners.chainedFuture;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.kaazing.k3po.driver.behavior.Barrier;
import org.kaazing.k3po.driver.behavior.handler.prepare.PreparationEvent;

public class AwaitBarrierDownstreamHandler extends AbstractBarrierHandler implements ChannelDownstreamHandler {

    private Queue<ChannelEvent> queue;

    public AwaitBarrierDownstreamHandler(Barrier barrier) {
        super(barrier);
    }

    @Override
    public void prepareRequested(final ChannelHandlerContext ctx, PreparationEvent evt) {

        super.prepareRequested(ctx, evt);

        // when pipeline future complete, pay attention to barrier future
        final ChannelFuture handlerFuture = getHandlerFuture();
        ChannelFuture pipelineFuture = getPipelineFuture();
        pipelineFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(final ChannelFuture f) throws Exception {
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
                    // TODO: review need for synchronized
                    synchronized (ctx) {
                        Queue<ChannelEvent> pending = queue;
                        queue = null;
                        for (ChannelEvent evt : pending) {
                            ctx.sendDownstream(evt);
                        }
                    }
                }
            }

        });
    }

    @Override
    public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent evt) throws Exception {

        ChannelFuture pipelineFuture = getPipelineFuture();
        ChannelFuture handlerFuture = getHandlerFuture();

        // while handler future not complete, queue channel events
        if (queue != null && pipelineFuture.isDone() && !handlerFuture.isDone()) {
            queue.add(evt);
        }
        else {
            // TODO: review need for synchronized
            synchronized (ctx) {
                ctx.sendDownstream(evt);
            }
        }
    }

    @Override
    public String toString() {
        return format("write await %s", getBarrier());
    }

    boolean hasQueuedChannelEvents() {
        return queue != null && !queue.isEmpty();
    }
}
