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

package org.kaazing.robot.driver.behavior.handler.prepare;

import java.util.Collection;
import java.util.HashSet;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.kaazing.robot.driver.netty.channel.CompositeChannelFuture;
import org.kaazing.robot.lang.LocationInfo;

public abstract class AbstractPreparationEvent implements PreparationEvent {

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
                    if (future.isSuccess()) {
                        AbstractPreparationEvent.this.progressInfo = locationInfo;
                    }
                }
            });
        }

        return pipelineFuture;
    }

}
