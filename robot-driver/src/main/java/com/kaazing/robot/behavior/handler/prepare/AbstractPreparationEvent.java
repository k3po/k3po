/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.behavior.handler.prepare;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

import com.kaazing.robot.lang.LocationInfo;
import com.kaazing.robot.netty.channel.CompositeChannelFuture;

public abstract class AbstractPreparationEvent implements PreparationEvent {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(AbstractPreparationEvent.class);

    private final Channel channel;
    private final ChannelFuture future;
    private final Collection<ChannelFuture> pipelineFutures;
    private volatile LocationInfo progressInfo;

    public AbstractPreparationEvent(Channel channel, ChannelFuture future) {
        this.channel = channel;
        this.future = future;
        this.pipelineFutures = new HashSet<ChannelFuture>();
        /*
         * Location 0:0 is meaningless. If we find a completion handler with 0:0
         * we don't know which stream it was. Lets use null instead
         */
        // this.progressInfo = new LocationInfo(0, 0);
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public ChannelFuture getFuture() {
        return future;
    }

    @Override
    public Collection<ChannelFuture> getPipelineFutures() {
        return pipelineFutures;
    }

    @Override
    public LocationInfo getProgressInfo() {
        return progressInfo;
    }

    @Override
    public ChannelFuture checkpoint(final LocationInfo locationInfo, final ChannelFuture handlerFuture) {

        if (LOGGER.isDebugEnabled()) {
            Iterator<ChannelFuture> i = pipelineFutures.iterator();

            if (i.hasNext()) {
                LOGGER.debug("Creating pipelineFuture. With the below futures");
                while (i.hasNext()) {
                    LOGGER.debug("\t" + i.next());
                }
            }
            else {
                LOGGER.debug("Creating pipelinedFuture with no futures. Automatically set to success");
            }
        }

        // We set the composite to failFast. This is so that as soon as one handler future fails ... any pipelinefutures
        // that contain it will also fail. This is needed so that the listener in the CompletionHandler will fire
        ChannelFuture pipelineFuture = new CompositeChannelFuture<ChannelFuture>(channel, pipelineFutures, true);

        // Note: add handler future to pipeline futures afterwards
        // so pipelineFuture represents all members of
        // the pipeline up-to-but-not-including this handler
        // and handlerFuture represents just this handler
        pipelineFutures.add(handlerFuture);

        if (locationInfo != null) {
            handlerFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("handler future completed: " + handlerFuture);
                    }
                    if (future.isSuccess()) {
                        AbstractPreparationEvent.this.progressInfo = locationInfo;
                    }
                }
            });
        }

        return pipelineFuture;
    }

}
