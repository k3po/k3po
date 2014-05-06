/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.behavior.handler;

import static com.kaazing.netty.channel.ChannelFutureListeners.chainedFuture;
import static org.jboss.netty.channel.Channels.succeededFuture;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

import com.kaazing.robot.lang.LocationInfo;
import com.kaazing.robot.behavior.handler.prepare.DownstreamPreparationEvent;
import com.kaazing.robot.behavior.handler.prepare.PreparationEvent;

public class CompletionHandler extends ExecutionHandler {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(CompletionHandler.class);

    private LocationInfo progressInfo;

    public LocationInfo getProgressInfo() {
        return progressInfo;
    }

    @Override
    public void prepareRequested(final ChannelHandlerContext ctx, final PreparationEvent evt) {

        super.prepareRequested(ctx, evt);

        // when the pipeline future completes, trigger success of this handler
        // future
        ChannelFuture pipelineFuture = getPipelineFuture();
        ChannelFuture handlerFuture = getHandlerFuture();

        /*
         * I don't like this because it depends on the order the listeners fire
         * which I don't think we can guarentee. In particular this assumes the
         * listener created in AbstractPreparationEvent is fired before this
         * one. We also assume that this listener fires before the next listener
         * created. The next listener sets the completion handlers future to
         * success and we have listeners on that in order to extract the
         * progress info
         */
        pipelineFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                progressInfo = evt.getProgressInfo();
                if (progressInfo == null) {
                    progressInfo = getStreamStartLocation();
                }
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("pipeline handler complete. Location info is " + progressInfo);
                }
                // Need to let the last event logger know we are done so we don't pick up the wrong event.
                ctx.getPipeline().get(LogLastEventHandler.class).setDone();
            }
        });
        pipelineFuture.addListener(chainedFuture(handlerFuture));

        ChannelFuture prepareFuture = evt.getFuture();
        prepareFuture.setSuccess();

        Channel channel = evt.getChannel();
        ctx.sendDownstream(new DownstreamPreparationEvent(channel, succeededFuture(channel)));

        /*
         * We were doing this before. But the problem was that the other
         * listener on this future used to extract the progress info in
         * ControlServerHandler was firing first ... so the progressInfo was
         * null handlerFuture.addListener(new ChannelFutureListener() {
         *
         * @Override public void operationComplete(ChannelFuture future) throws
         * Exception { progressInfo = evt.getProgressInfo(); if(
         * logger.isTraceEnabled() ) { logger.trace(
         * "completion handler complete. Location info is " + progressInfo ); }
         * } });
         */
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        LOGGER.error("Unexpected handled exception ", e.getCause());
    }
}
