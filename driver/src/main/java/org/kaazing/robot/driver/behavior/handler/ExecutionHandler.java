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

package org.kaazing.robot.driver.behavior.handler;

import static java.lang.String.format;
import static org.kaazing.robot.driver.channel.Channels.prepare;

import java.util.concurrent.atomic.AtomicBoolean;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.DefaultChannelFuture;
import org.jboss.netty.channel.LifeCycleAwareChannelHandler;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.kaazing.robot.driver.behavior.handler.barrier.AwaitBarrierDownstreamHandler;
import org.kaazing.robot.driver.behavior.handler.prepare.PreparationEvent;
import org.kaazing.robot.driver.behavior.handler.prepare.SimplePrepareUpstreamHandler;
import org.kaazing.robot.lang.LocationInfo;

public class ExecutionHandler extends SimplePrepareUpstreamHandler implements LifeCycleAwareChannelHandler {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(AwaitBarrierDownstreamHandler.class);

    private ChannelFuture handlerFuture;
    private ChannelFuture pipelineFuture;
    private ChannelFuture cancelFuture;

    private LocationInfo locationInfo;
    private LocationInfo streamStartLocation;

    private final AtomicBoolean preparationLatch = new AtomicBoolean();

    private Channel channel;

    // Same as canceling the pipeline future ... except sometimes you might want to cancel before it is prepared
    // in which case you need to wait for the prepare. Calling this method works around that.
    public ChannelFuture cancel() {
        if (pipelineFuture != null) {
            // TODO. Change to cancel
            pipelineFuture.setSuccess();
            cancelFuture = Channels.succeededFuture(pipelineFuture.getChannel());
        } else {
            cancelFuture = Channels.future(null);
        }
        return cancelFuture;
    }

    public LocationInfo getLocationInfo() {
        return locationInfo;
    }

    public void setLocationInfo(LocationInfo locationInfo) {
        this.locationInfo = locationInfo;
    }

    public LocationInfo getStreamStartLocation() {
        return streamStartLocation;
    }

    public void setStreamStartLocation(LocationInfo streamStart) {
        streamStartLocation = streamStart;
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

        pipelineFuture = evt.checkpoint(locationInfo, handlerFuture);

        if (cancelFuture != null) {
            pipelineFuture.setSuccess();
            cancelFuture.setSuccess();
        }

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
                    LOGGER.debug(format("[id: 0x%08x] %s [FAILED]", id, ExecutionHandler.this));
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
            // Note this happens when the Robot is sent the abort command. The pipeline for the completion future is set to
            // success. And then we detach all handlers. Doing this is essentially a no-op in that case.
            handlerFuture.setFailure(new IllegalStateException("ChannelHandler removed before completion").fillInStackTrace());
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
