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

package org.kaazing.robot.driver.netty.channel.http;

import static org.jboss.netty.channel.Channels.future;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelUpstreamHandler;

public final class HttpChannels {

    /**
     * Sends a {@code "contentComplete"} event to the
     * {@link ChannelUpstreamHandler} which is placed in the closest upstream
     * from the handler associated with the specified
     * {@link ChannelHandlerContext}.
     */
    public static void fireHttpContentComplete(ChannelHandlerContext ctx) {
        ctx.getPipeline().sendUpstream(
                new UpstreamHttpContentCompleteEvent(ctx.getChannel()));
    }

    /**
     * Sends a {@code "contentComplete"} event to the first
     * {@link ChannelUpstreamHandler} in the {@link ChannelPipeline} of
     * the specified {@link Channel}.
     */
    public static void fireHttpContentComplete(Channel channel) {
        channel.getPipeline().sendUpstream(
                new UpstreamHttpContentCompleteEvent(channel));
    }

    /**
     * Sends a {@code "completeContent"} request to the last
     * {@link ChannelDownstreamHandler} in the {@link ChannelPipeline} of
     * the specified {@link Channel}.
     *
     * @param channel  the channel to bind
     *
     * @return the {@link ChannelFuture} which will be notified when the
     *         completeContent operation is done
     */
    public static ChannelFuture completeContent(Channel channel) {
        ChannelFuture future = future(channel);
        channel.getPipeline().sendDownstream(
                new DownstreamHttpCompleteContentEvent(channel, future));
        return future;
    }

    /**
     * Sends a {@code "completeContent"} request to the
     * {@link ChannelDownstreamHandler} which is placed in the closest
     * downstream from the handler associated with the specified
     * {@link ChannelHandlerContext}.
     *
     * @param ctx     the context
     * @param future  the future which will be notified when the completeContent
     *                operation is done
     */
    public static void completeContent(ChannelHandlerContext ctx, ChannelFuture future) {
        ctx.sendDownstream(
                new DownstreamHttpCompleteContentEvent(ctx.getChannel(), future));
    }

    private HttpChannels() {
        // no instances
    }
}
