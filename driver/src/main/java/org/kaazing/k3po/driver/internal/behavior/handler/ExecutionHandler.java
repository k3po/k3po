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

package org.kaazing.k3po.driver.internal.behavior.handler;

import static java.lang.String.format;
import static org.kaazing.k3po.driver.internal.channel.Channels.prepare;

import java.util.concurrent.atomic.AtomicBoolean;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.DefaultChannelFuture;
import org.jboss.netty.channel.LifeCycleAwareChannelHandler;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.kaazing.k3po.driver.internal.behavior.ScriptProgressException;
import org.kaazing.k3po.driver.internal.behavior.handler.barrier.AwaitBarrierDownstreamHandler;
import org.kaazing.k3po.driver.internal.behavior.handler.prepare.PreparationEvent;
import org.kaazing.k3po.driver.internal.behavior.handler.prepare.SimplePrepareUpstreamHandler;
import org.kaazing.k3po.lang.internal.RegionInfo;

public class ExecutionHandler extends SimplePrepareUpstreamHandler implements LifeCycleAwareChannelHandler {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(AwaitBarrierDownstreamHandler.class);

    private ChannelFuture handlerFuture;
    private ChannelFuture pipelineFuture;

    private RegionInfo regionInfo;

    private final AtomicBoolean preparationLatch = new AtomicBoolean();

    private Channel channel;

    public RegionInfo getRegionInfo() {
        return regionInfo;
    }

    public void setRegionInfo(RegionInfo regionInfo) {
        this.regionInfo = regionInfo;
    }

    public Channel getChannel() {
        return channel;
    }

    @Override
    public void prepareRequested(ChannelHandlerContext ctx, PreparationEvent evt) {

        // Ideally one could extract the future from the handlerFuture. But we are
        // creating them before the channel is set up :(
        channel = ctx.getChannel();

        // set latch in case prepare triggered by handler earlier in pipeline
        preparationLatch.set(true);

        pipelineFuture = evt.checkpoint(handlerFuture);

        super.prepareRequested(ctx, evt);
    }

    @Override
    public void beforeAdd(final ChannelHandlerContext ctx) throws Exception {
        assert handlerFuture == null;
        // note: the context channel is null if handler added to pipeline before channel has been created
        handlerFuture = new DefaultChannelFuture(null, false) {
            @Override
            public Channel getChannel() {
                return ctx.getChannel();
            }

            @Override
            public String toString() {
                return ExecutionHandler.this.toString();
            }
        };

        handlerFuture.addListener(new ChannelFutureListener() {

            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                Channel channel = future.getChannel();
                int id = (channel != null) ? channel.getId() : 0;
                if (future.isSuccess()) {
                    LOGGER.debug(format("[id: 0x%08x] %s", id, ExecutionHandler.this));
                }
                else {
                    LOGGER.debug(format("[id: 0x%08x] %s [FAILED], pipelineFuture:\n%s",
                            id, ExecutionHandler.this, pipelineFuture));
                }
            }
        });
    }

    @Override
    public void afterAdd(ChannelHandlerContext ctx) throws Exception {
        assert handlerFuture != null;
    }

    @Override
    public void beforeRemove(ChannelHandlerContext ctx) throws Exception {
        assert handlerFuture != null;
    }

    @Override
    public void afterRemove(ChannelHandlerContext ctx) throws Exception {

        assert handlerFuture != null;
        if (!handlerFuture.isDone()) {
            ScriptProgressException exception = new ScriptProgressException(getRegionInfo(), "");
            exception.fillInStackTrace();
            handlerFuture.setFailure(exception);
        }
        handlerFuture = null;
    }

    public ChannelFuture getHandlerFuture() {
        if (handlerFuture == null) {
            throw new IllegalStateException("ChannelHandler not added to pipeline yet");
        }

        return handlerFuture;
    }

    public ChannelFuture getPipelineFuture() {
        if (pipelineFuture == null) {
            throw new IllegalStateException("ChannelHandler not prepared yet");
        }

        return pipelineFuture;
    }

    @Override
    protected final void handleUpstream0(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {

        // prepare on receiving first channel open event
        if (preparationLatch.compareAndSet(false, true)) {
            prepare(ctx.getChannel());
        }

        handleUpstream1(ctx, e);
    }

    protected void handleUpstream1(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        super.handleUpstream0(ctx, e);
    }


}
