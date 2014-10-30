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

package org.kaazing.robot.driver.behavior.handler.event;

import static java.lang.System.currentTimeMillis;
import static org.jboss.netty.channel.ChannelState.BOUND;
import static org.jboss.netty.channel.ChannelState.INTEREST_OPS;
import static org.jboss.netty.channel.Channels.fireChannelBound;
import static org.jboss.netty.channel.Channels.fireChannelClosed;
import static org.jboss.netty.channel.Channels.fireChannelConnected;
import static org.jboss.netty.channel.Channels.fireChannelDisconnected;
import static org.jboss.netty.channel.Channels.fireChannelInterestChanged;
import static org.jboss.netty.channel.Channels.fireChannelUnbound;
import static org.jboss.netty.channel.Channels.fireExceptionCaught;
import static org.jboss.netty.channel.Channels.fireMessageReceived;
import static org.jboss.netty.channel.Channels.fireWriteComplete;
import static org.jboss.netty.channel.Channels.pipeline;
import static org.jboss.netty.channel.Channels.succeededFuture;
import static org.jboss.netty.handler.timeout.IdleState.ALL_IDLE;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kaazing.robot.lang.RegionInfo.newSequential;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.ChildChannelStateEvent;
import org.jboss.netty.channel.DefaultChildChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.WriteCompletionEvent;
import org.jboss.netty.channel.local.DefaultLocalClientChannelFactory;
import org.jboss.netty.channel.local.LocalAddress;
import org.jboss.netty.handler.timeout.DefaultIdleStateEvent;
import org.jboss.netty.handler.timeout.IdleStateEvent;
import org.junit.Before;
import org.junit.Test;
import org.kaazing.robot.driver.jmock.Expectations;
import org.kaazing.robot.driver.jmock.Mockery;
import org.kaazing.robot.driver.behavior.handler.TestChannelEvent;
import org.kaazing.robot.driver.behavior.handler.prepare.PreparationEvent;

public class UnboundHandlerTest {

    private Mockery context;
    private ChannelUpstreamHandler upstream;
    private ChannelPipeline pipeline;
    private ChannelFactory channelFactory;
    private UnboundHandler handler;
    private boolean blockChannelOpen = true;

    @Before
    public void setUp() throws Exception {
        context = new Mockery() {
            {
                setThrowFirstErrorOnAssertIsSatisfied(true);
            }
        };

        upstream = context.mock(ChannelUpstreamHandler.class);

        handler = new UnboundHandler();
        handler.setRegionInfo(newSequential(0, 0));

        pipeline = pipeline(new SimpleChannelHandler() {
            @Override
            public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
                // block implicit channel open?
                if (!blockChannelOpen) {
                    ctx.sendUpstream(e);
                }
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
    public void shouldPropagateUpstreamChildOpenedEvent() throws Exception {

        context.checking(new Expectations() {
            {
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(any(PreparationEvent.class)));
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(any(ChildChannelStateEvent.class)));
            }
        });

        Channel parentChannel = channelFactory.newChannel(pipeline);
        Channel childChannel = channelFactory.newChannel(pipeline(new SimpleChannelUpstreamHandler()));
        // child channel is open
        pipeline.sendUpstream(new DefaultChildChannelStateEvent(parentChannel, childChannel));

        ChannelFuture handlerFuture = handler.getHandlerFuture();
        assertFalse(handlerFuture.isDone());

        context.assertIsSatisfied();
    }

    @Test
    public void shouldPropagateUpstreamChildClosedEvent() throws Exception {

        context.checking(new Expectations() {
            {
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(any(PreparationEvent.class)));
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(any(ChildChannelStateEvent.class)));
            }
        });

        Channel parentChannel = channelFactory.newChannel(pipeline);
        Channel childChannel = channelFactory.newChannel(pipeline(new SimpleChannelUpstreamHandler()));
        childChannel.close().sync();
        // child channel is closed
        pipeline.sendUpstream(new DefaultChildChannelStateEvent(parentChannel, childChannel));

        ChannelFuture handlerFuture = handler.getHandlerFuture();
        assertFalse(handlerFuture.isDone());

        context.assertIsSatisfied();
    }

    @Test
    public void shouldPropagateUpstreamInterestOpsEvent() throws Exception {

        context.checking(new Expectations() {
            {
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(any(PreparationEvent.class)));
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(channelState(INTEREST_OPS)));
            }
        });

        Channel channel = channelFactory.newChannel(pipeline);
        ChannelFuture handlerFuture = handler.getHandlerFuture();
        fireChannelInterestChanged(channel);

        assertFalse(handlerFuture.isDone());

        context.assertIsSatisfied();
    }

    @Test
    public void shouldPropagateUpstreamIdleStateEvent() throws Exception {

        context.checking(new Expectations() {
            {
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(any(PreparationEvent.class)));
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(any(IdleStateEvent.class)));
            }
        });

        Channel channel = channelFactory.newChannel(pipeline);
        ChannelFuture handlerFuture = handler.getHandlerFuture();
        pipeline.sendUpstream(new DefaultIdleStateEvent(channel, ALL_IDLE, currentTimeMillis()));

        assertFalse(handlerFuture.isDone());

        context.assertIsSatisfied();
    }

    @Test
    public void shouldPropagateUpstreamWriteCompletionEvent() throws Exception {

        context.checking(new Expectations() {
            {
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(any(PreparationEvent.class)));
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(any(WriteCompletionEvent.class)));
            }
        });

        Channel channel = channelFactory.newChannel(pipeline);
        fireWriteComplete(channel, 1024);

        ChannelFuture handlerFuture = handler.getHandlerFuture();
        assertFalse(handlerFuture.isDone());

        context.assertIsSatisfied();
    }

    @Test
    public void shouldPropagateUpstreamExceptionEvent() throws Exception {

        context.checking(new Expectations() {
            {
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(any(PreparationEvent.class)));
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(any(ExceptionEvent.class)));
            }
        });

        Channel channel = channelFactory.newChannel(pipeline);
        ChannelFuture handlerFuture = handler.getHandlerFuture();
        fireExceptionCaught(channel, new Exception().fillInStackTrace());

        assertFalse(handlerFuture.isDone());

        context.assertIsSatisfied();
    }

    @Test
    public void shouldPropagateUpstreamUnknownEvent() throws Exception {

        context.checking(new Expectations() {
            {
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(any(PreparationEvent.class)));
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(any(TestChannelEvent.class)));
            }
        });

        Channel channel = channelFactory.newChannel(pipeline);
        ChannelFuture handlerFuture = handler.getHandlerFuture();
        pipeline.sendUpstream(new TestChannelEvent(channel, succeededFuture(channel)));

        assertFalse(handlerFuture.isDone());

        context.assertIsSatisfied();
    }

    @Test
    public void shouldConsumeUpstreamOpenedEvent() throws Exception {

        context.checking(new Expectations() {
            {
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(any(PreparationEvent.class)));
            }
        });

        blockChannelOpen = false;
        channelFactory.newChannel(pipeline);
        ChannelFuture handlerFuture = handler.getHandlerFuture();

        assertTrue(handlerFuture.isDone());
        assertFalse(handlerFuture.isSuccess());

        context.assertIsSatisfied();
    }

    @Test
    public void shouldConsumeUpstreamBoundEvent() throws Exception {

        context.checking(new Expectations() {
            {
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(any(PreparationEvent.class)));
            }
        });

        Channel channel = channelFactory.newChannel(pipeline);
        ChannelFuture handlerFuture = handler.getHandlerFuture();
        fireChannelBound(channel, new LocalAddress("test"));

        assertTrue(handlerFuture.isDone());
        assertFalse(handlerFuture.isSuccess());

        context.assertIsSatisfied();
    }

    @Test
    public void shouldConsumeUpstreamConnectedEvent() throws Exception {

        context.checking(new Expectations() {
            {
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(any(PreparationEvent.class)));
            }
        });

        Channel channel = channelFactory.newChannel(pipeline);
        ChannelFuture handlerFuture = handler.getHandlerFuture();
        fireChannelConnected(channel, new LocalAddress("test"));

        assertTrue(handlerFuture.isDone());
        assertFalse(handlerFuture.isSuccess());

        context.assertIsSatisfied();
    }

    @Test
    public void shouldConsumeUpstreamMessageEvent() throws Exception {

        context.checking(new Expectations() {
            {
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(any(PreparationEvent.class)));
            }
        });

        Channel channel = channelFactory.newChannel(pipeline);
        ChannelFuture handlerFuture = handler.getHandlerFuture();
        fireMessageReceived(channel, new Object());

        assertTrue(handlerFuture.isDone());
        assertFalse(handlerFuture.isSuccess());

        context.assertIsSatisfied();
    }

    @Test
    public void shouldConsumeUpstreamDisconnectedEvent() throws Exception {

        context.checking(new Expectations() {
            {
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(any(PreparationEvent.class)));
            }
        });

        Channel channel = channelFactory.newChannel(pipeline);
        ChannelFuture handlerFuture = handler.getHandlerFuture();
        fireChannelDisconnected(channel);

        assertTrue(handlerFuture.isDone());
        assertFalse(handlerFuture.isSuccess());

        context.assertIsSatisfied();
    }

    @Test
    public void shouldConsumeUpstreamUnboundEvent() throws Exception {

        context.checking(new Expectations() {
            {
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(any(PreparationEvent.class)));
            }
        });

        Channel channel = channelFactory.newChannel(pipeline);
        ChannelFuture handlerFuture = handler.getHandlerFuture();
        fireChannelUnbound(channel);

        assertTrue(handlerFuture.isSuccess());

        context.assertIsSatisfied();
    }

    @Test
    public void shouldConsumeUpstreamClosedEvent() throws Exception {

        context.checking(new Expectations() {
            {
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(any(PreparationEvent.class)));
            }
        });

        Channel channel = channelFactory.newChannel(pipeline);
        ChannelFuture handlerFuture = handler.getHandlerFuture();
        fireChannelClosed(channel);

        assertTrue(handlerFuture.isDone());
        assertFalse(handlerFuture.isSuccess());

        context.assertIsSatisfied();
    }

    @Test
    public void shouldPropagateUpstreamConnectedEventAfterFutureDone() throws Exception {

        context.checking(new Expectations() {
            {
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(any(PreparationEvent.class)));
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)),
                        with(channelState(same(BOUND), aNull(Object.class))));
            }
        });

        Channel channel = channelFactory.newChannel(pipeline);
        fireChannelUnbound(channel);
        fireChannelUnbound(channel);

        context.assertIsSatisfied();
    }
}
