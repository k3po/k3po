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

package org.kaazing.robot.driver.behavior.handler.barrier;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.jboss.netty.channel.ChannelState.OPEN;
import static org.jboss.netty.channel.Channels.close;
import static org.jboss.netty.channel.Channels.pipeline;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.local.DefaultLocalClientChannelFactory;
import org.junit.Before;
import org.junit.Test;

import org.kaazing.robot.driver.jmock.Expectations;
import org.kaazing.robot.driver.jmock.Mockery;
import org.kaazing.robot.driver.behavior.Barrier;
import org.kaazing.robot.driver.behavior.handler.prepare.PreparationEvent;

public class AwaitBarrierDownstreamHandlerTest {

    private Mockery context;
    private ChannelUpstreamHandler upstream;
    private ChannelDownstreamHandler downstream;
    private ChannelPipeline pipeline;
    private ChannelFactory channelFactory;
    private AwaitBarrierDownstreamHandler handler;
    private Barrier barrier;

    @Before
    public void setUp() throws Exception {
        context = new Mockery() {
            {
                setThrowFirstErrorOnAssertIsSatisfied(true);
            }
        };

        upstream = context.mock(ChannelUpstreamHandler.class);
        downstream = context.mock(ChannelDownstreamHandler.class);

        barrier = new Barrier("BARRIER");
        handler = new AwaitBarrierDownstreamHandler(barrier);

        pipeline = pipeline(new SimpleChannelHandler() {
            @Override
            public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent evt) throws Exception {
                downstream.handleDownstream(ctx, evt);
                super.handleDownstream(ctx, evt);
            }
        }, handler, new SimpleChannelHandler() {
            @Override
            public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
                upstream.handleUpstream(ctx, e);
                super.handleUpstream(ctx, e);
            }

            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
                // prevent console error message
            }
        });

        channelFactory = new DefaultLocalClientChannelFactory();
    }

    @Test
    public void shouldPropagateDownstreamCloseOnBarrierFutureSuccess() throws Exception {

        context.checking(new Expectations() {
            {
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(any(PreparationEvent.class)));
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(channelState(OPEN, TRUE)));
                oneOf(downstream).handleDownstream(with(any(ChannelHandlerContext.class)), with(channelState(OPEN, FALSE)));
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(channelState(OPEN, FALSE)));
            }
        });

        Channel channel = channelFactory.newChannel(pipeline);
        close(channel);

        ChannelFuture barrierFuture = barrier.getFuture();
        barrierFuture.setSuccess();

        ChannelFuture handlerFuture = handler.getHandlerFuture();
        assertTrue(handlerFuture.isSuccess());

        assertFalse(handler.hasQueuedChannelEvents());

        context.assertIsSatisfied();
    }

    @Test
    public void shouldNotPropagateDownstreamCloseWhileBarrierFutureNotDone() throws Exception {

        context.checking(new Expectations() {
            {
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(any(PreparationEvent.class)));
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(channelState(OPEN, TRUE)));
            }
        });

        Channel channel = channelFactory.newChannel(pipeline);
        close(channel);

        ChannelFuture handlerFuture = handler.getHandlerFuture();
        assertFalse(handlerFuture.isDone());

        assertTrue(handler.hasQueuedChannelEvents());

        context.assertIsSatisfied();
    }
}
