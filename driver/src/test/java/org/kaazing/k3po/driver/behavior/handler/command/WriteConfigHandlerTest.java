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

package org.kaazing.k3po.driver.behavior.handler.command;

import static java.lang.Boolean.TRUE;
import static org.jboss.netty.channel.ChannelState.OPEN;
import static org.jboss.netty.channel.Channels.pipeline;
import static org.junit.Assert.assertFalse;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.local.DefaultLocalClientChannelFactory;
import org.junit.Before;
import org.junit.Test;
import org.kaazing.k3po.driver.behavior.handler.ExecutionHandler;
import org.kaazing.k3po.driver.behavior.handler.codec.ConfigEncoder;
import org.kaazing.k3po.driver.behavior.handler.command.WriteConfigHandler;
import org.kaazing.k3po.driver.behavior.handler.prepare.PreparationEvent;
import org.kaazing.k3po.driver.jmock.Expectations;
import org.kaazing.k3po.driver.jmock.Mockery;
import org.kaazing.k3po.driver.netty.channel.SimpleChannelHandler;

public class WriteConfigHandlerTest {

    private Mockery context;
    private ChannelUpstreamHandler upstream;
    private ChannelDownstreamHandler downstream;
    private ChannelPipeline pipeline;
    private ChannelFactory channelFactory;
    private WriteConfigHandler handler;
    private ExecutionHandler execution;
    private ConfigEncoder encoder;

    @Before
    public void setUp() throws Exception {
        context = new Mockery() {
            {
                setThrowFirstErrorOnAssertIsSatisfied(true);
            }
        };

        upstream = context.mock(ChannelUpstreamHandler.class);
        downstream = context.mock(ChannelDownstreamHandler.class);

        execution = new ExecutionHandler();

        encoder = context.mock(ConfigEncoder.class);

        handler = new WriteConfigHandler(encoder);

        pipeline = pipeline(new SimpleChannelHandler() {
            @Override
            public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent evt) throws Exception {
                downstream.handleDownstream(ctx, evt);
                super.handleDownstream(ctx, evt);
            }
        }, execution, handler, new SimpleChannelHandler() {
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
    public void shouldEncodeConfigOnPipelineFutureSuccess() throws Exception {

        context.checking(new Expectations() {
            {
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(any(PreparationEvent.class)));
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(channelState(OPEN, TRUE)));
                oneOf(encoder).encode(with(any(Channel.class)));
            }
        });

        channelFactory.newChannel(pipeline);
        ChannelFuture executionFuture = execution.getHandlerFuture();
        executionFuture.setSuccess();

        ChannelFuture handlerFuture = handler.getHandlerFuture();
        handlerFuture.sync();

        context.assertIsSatisfied();
    }

    @Test
    public void shouldNotEncodeConfigOnPipelineFutureFailure() throws Exception {

        context.checking(new Expectations() {
            {
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(any(PreparationEvent.class)));
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(channelState(OPEN, TRUE)));
            }
        });

        channelFactory.newChannel(pipeline);
        ChannelFuture executionFuture = execution.getHandlerFuture();
        executionFuture.setFailure(new ChannelException("pipeline already failed"));

        ChannelFuture handlerFuture = handler.getHandlerFuture();
        assertFalse(handlerFuture.isDone());

        context.assertIsSatisfied();
    }
}
