/**
 * Copyright 2007-2015, Kaazing Corporation. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaazing.k3po.driver.internal.behavior.handler.command;

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
import org.kaazing.k3po.driver.internal.behavior.handler.ExecutionHandler;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.ChannelDecoder;
import org.kaazing.k3po.driver.internal.behavior.handler.prepare.PreparationEvent;
import org.kaazing.k3po.driver.internal.jmock.Expectations;
import org.kaazing.k3po.driver.internal.jmock.Mockery;
import org.kaazing.k3po.driver.internal.netty.channel.SimpleChannelHandler;

public class ReadConfigHandlerTest {

    private Mockery context;
    private ChannelUpstreamHandler upstream;
    private ChannelDownstreamHandler downstream;
    private ChannelPipeline pipeline;
    private ChannelFactory channelFactory;
    private ReadConfigHandler handler;
    private ExecutionHandler execution;
    private ChannelDecoder decoder;

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

        decoder = context.mock(ChannelDecoder.class);

        handler = new ReadConfigHandler(decoder);

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
    public void shouldDecodeConfigOnPipelineFutureSuccess() throws Exception {

        context.checking(new Expectations() {
            {
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(any(PreparationEvent.class)));
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(channelState(OPEN, TRUE)));
                oneOf(decoder).decode(with(any(Channel.class)));
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
    public void shouldNotDecodeConfigOnPipelineFutureFailure() throws Exception {

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
