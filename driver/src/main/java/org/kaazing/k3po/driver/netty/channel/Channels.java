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

package org.kaazing.k3po.driver.netty.channel;

import static org.jboss.netty.channel.Channels.future;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelUpstreamHandler;

public final class Channels {

    /**
     * Sends a {@code "shutdownInput"} event to the
     * {@link ChannelUpstreamHandler} which is placed in the closest upstream
     * from the handler associated with the specified
     * {@link ChannelHandlerContext}.
     */
    public static void fireInputShutdown(ChannelHandlerContext ctx) {
        ctx.getPipeline().sendUpstream(
                new UpstreamShutdownInputEvent(ctx.getChannel()));
    }

    /**
     * Sends a {@code "shutdownInput"} event to the first
     * {@link ChannelUpstreamHandler} in the {@link ChannelPipeline} of
     * the specified {@link Channel}.
     */
    public static void fireInputShutdown(Channel channel) {
        channel.getPipeline().sendUpstream(
                new UpstreamShutdownInputEvent(channel));
    }

    /**
     * Sends a {@code "flushed"} event to the first
     * {@link ChannelUpstreamHandler} in the {@link ChannelPipeline} of
     * the specified {@link Channel}.
     */
    public static void fireFlushed(Channel channel) {
        channel.getPipeline().sendUpstream(
                new UpstreamFlushEvent(channel));
    }

    /**
     * Sends a {@code "shutdownInput"} request to the last
     * {@link ChannelDownstreamHandler} in the {@link ChannelPipeline} of
     * the specified {@link Channel}.
     *
     * @param channel  the channel to bind
     *
     * @return the {@link ChannelFuture} which will be notified when the
     *         shutdownInput operation is done
     */
    public static ChannelFuture shutdownInput(Channel channel) {
        ChannelFuture future = future(channel);
        channel.getPipeline().sendDownstream(
                new DownstreamShutdownInputEvent(channel, future));
        return future;
    }

    /**
     * Sends a {@code "shutdownInput"} request to the
     * {@link ChannelDownstreamHandler} which is placed in the closest
     * downstream from the handler associated with the specified
     * {@link ChannelHandlerContext}.
     *
     * @param ctx     the context
     * @param future  the future which will be notified when the shutdownInput
     *                operation is done
     */
    public static void shutdownInput(ChannelHandlerContext ctx, ChannelFuture future) {
        ctx.sendDownstream(
                new DownstreamShutdownInputEvent(ctx.getChannel(), future));
    }

    /**
     * Sends a {@code "shutdownOutput"} event to the
     * {@link ChannelUpstreamHandler} which is placed in the closest upstream
     * from the handler associated with the specified
     * {@link ChannelHandlerContext}.
     */
    public static void fireOutputShutdown(ChannelHandlerContext ctx) {
        ctx.getPipeline().sendUpstream(
                new UpstreamShutdownOutputEvent(ctx.getChannel()));
    }

    /**
     * Sends a {@code "shutdownOutput"} event to the first
     * {@link ChannelUpstreamHandler} in the {@link ChannelPipeline} of
     * the specified {@link Channel}.
     */
    public static void fireOutputShutdown(Channel channel) {
        channel.getPipeline().sendUpstream(
                new UpstreamShutdownOutputEvent(channel));
    }

    /**
     * Sends a {@code "shutdownOutput"} request to the last
     * {@link ChannelDownstreamHandler} in the {@link ChannelPipeline} of
     * the specified {@link Channel}.
     *
     * @param channel  the channel to bind
     *
     * @return the {@link ChannelFuture} which will be notified when the
     *         shutdownOutput operation is done
     */
    public static ChannelFuture shutdownOutput(Channel channel) {
        ChannelFuture future = future(channel);
        channel.getPipeline().sendDownstream(
                new DownstreamShutdownOutputEvent(channel, future));
        return future;
    }

    /**
     * Sends a {@code "shutdownOutput"} request to the
     * {@link ChannelDownstreamHandler} which is placed in the closest
     * downstream from the handler associated with the specified
     * {@link ChannelHandlerContext}.
     *
     * @param ctx     the context
     * @param future  the future which will be notified when the shutdownOutput
     *                operation is done
     */
    public static void shutdownOutput(ChannelHandlerContext ctx, ChannelFuture future) {
        ctx.sendDownstream(
                new DownstreamShutdownOutputEvent(ctx.getChannel(), future));
    }

    /**
     * Sends a {@code "flush"} request to the last
     * {@link ChannelDownstreamHandler} in the {@link ChannelPipeline} of
     * the specified {@link Channel}.
     *
     * @param channel  the channel to bind
     *
     * @return the {@link ChannelFuture} which will be notified when the
     *         flush operation is done
     */
    public static ChannelFuture flush(Channel channel) {
        ChannelFuture future = future(channel);
        channel.getPipeline().sendDownstream(
                new DownstreamFlushEvent(channel, future));
        return future;
    }

    /**
     * Sends a {@code "flush"} request to the
     * {@link ChannelDownstreamHandler} which is placed in the closest
     * downstream from the handler associated with the specified
     * {@link ChannelHandlerContext}.
     *
     * @param ctx     the context
     * @param future  the future which will be notified when the flush
     *                operation is done
     */
    public static void flush(ChannelHandlerContext ctx, ChannelFuture future) {
        ctx.sendDownstream(
                new DownstreamFlushEvent(ctx.getChannel(), future));
    }

    private Channels() {
        // no instances
    }
}
