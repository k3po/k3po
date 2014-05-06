/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.behavior.handler.barrier;

import static com.kaazing.netty.channel.ChannelFutureListeners.chainedFuture;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

import com.kaazing.robot.behavior.Barrier;
import com.kaazing.robot.behavior.handler.prepare.PreparationEvent;

public class NotifyBarrierHandler extends AbstractBarrierHandler {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(NotifyBarrierHandler.class);

    public NotifyBarrierHandler(Barrier barrier) {
        super(barrier);
    }

    @Override
    public void prepareRequested(final ChannelHandlerContext ctx, PreparationEvent evt) {

        super.prepareRequested(ctx, evt);

        Barrier barrier = getBarrier();
        final ChannelFuture barrierFuture = barrier.getFuture();

        ChannelFuture pipelineFuture = getPipelineFuture();
        ChannelFuture handlerFuture = getHandlerFuture();

        // Add a listener for logging
        if (LOGGER.isInfoEnabled()) {

            pipelineFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(final ChannelFuture f) throws Exception {
                    if (f.isSuccess()) {
                        LOGGER.info("Notifying barrier");
                        // We only want to set barrier future when the pipeline is success. Otherwise it could cause other
                        // streams
                        // to "fail" incorrectly.
                        barrierFuture.setSuccess();
                    }
                }
            });
        }

        pipelineFuture.addListener(chainedFuture(handlerFuture));
    }

}
