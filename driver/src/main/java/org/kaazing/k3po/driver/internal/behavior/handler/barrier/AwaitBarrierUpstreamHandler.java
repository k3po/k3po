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
package org.kaazing.k3po.driver.internal.behavior.handler.barrier;

import static java.lang.String.format;
import static org.kaazing.k3po.driver.internal.netty.channel.ChannelFutureListeners.chainedFuture;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.kaazing.k3po.driver.internal.behavior.Barrier;
import org.kaazing.k3po.driver.internal.behavior.handler.prepare.PreparationEvent;

public class AwaitBarrierUpstreamHandler extends AbstractBarrierHandler implements ChannelUpstreamHandler {

    private Queue<ChannelEvent> queue;

    public AwaitBarrierUpstreamHandler(Barrier barrier) {
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
                if (f.isCancelled()) {
                    handlerFuture.cancel();
                } else if (!f.isSuccess()) {
                    handlerFuture.setFailure(f.getCause());
                } else {
                    // on success or incomplete
                    // when barrier future complete, trigger handler future
                    Barrier barrier = getBarrier();
                    ChannelFuture barrierFuture = barrier.getFuture();
                    barrierFuture.addListener(chainedFuture(handlerFuture));
                }
            }
        });

        // when handler future complete, flush queued channel events
        queue = new ConcurrentLinkedQueue<>();
        handlerFuture.addListener(new ChannelFutureListener() {

            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                synchronized (ctx) {
                    if (future.isSuccess()) {
                        // TODO: Remove when JUEL sync bug is fixed https://github.com/k3po/k3po/issues/147
                        Queue<ChannelEvent> pending = queue;
                        queue = null;
                        for (ChannelEvent evt : pending) {
                            ctx.sendUpstream(evt);
                        }
                    }
                }
            }

        });
    }

    @Override
    protected StringBuilder describe(StringBuilder sb) {
        return sb.append(format("read await %s", getBarrier()));
    }

    @Override
    protected void handleUpstream1(ChannelHandlerContext ctx, ChannelEvent evt) throws Exception {

        // while handler future not complete, queue channel events
        ChannelFuture handlerFuture = getHandlerFuture();
        /*
         * Note that we are synchronizing on the channel context because there exists a race when de-queueing the
         * events.
         */
        // TODO: Remove when JUEL sync bug is fixed https://github.com/k3po/k3po/issues/147
        synchronized (ctx) {
            if (!handlerFuture.isDone()) {
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
