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

package org.kaazing.robot.driver.behavior.handler.command;

import static java.lang.Boolean.TRUE;
import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.jboss.netty.buffer.ChannelBuffers.wrappedBuffer;
import static org.jboss.netty.channel.ChannelState.OPEN;
import static org.jboss.netty.channel.Channels.pipeline;
import static org.jboss.netty.util.CharsetUtil.UTF_8;
import static org.junit.Assert.assertFalse;

import java.nio.channels.NotYetConnectedException;
import java.util.ArrayList;
import java.util.List;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.WriteCompletionEvent;
import org.jboss.netty.channel.local.DefaultLocalClientChannelFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;

import org.kaazing.robot.driver.jmock.Expectations;
import org.kaazing.robot.driver.jmock.Mockery;
import org.kaazing.robot.driver.behavior.handler.ExecutionHandler;
import org.kaazing.robot.driver.behavior.handler.codec.MessageEncoder;
import org.kaazing.robot.driver.behavior.handler.codec.WriteBytesEncoder;
import org.kaazing.robot.driver.behavior.handler.codec.WriteExpressionEncoder;
import org.kaazing.robot.driver.behavior.handler.codec.WriteTextEncoder;
import org.kaazing.robot.driver.behavior.handler.prepare.PreparationEvent;
import org.kaazing.robot.lang.el.ExpressionContext;

public class WriteHandlerTest {

    private Mockery context;
    private ChannelUpstreamHandler upstream;
    private ChannelDownstreamHandler downstream;
    private ChannelPipeline pipeline;
    private ChannelFactory channelFactory;
    private WriteHandler handler;
    private ExecutionHandler execution;
    private ExpressionContext environment;
    private ValueExpression expression;

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

        List<MessageEncoder> encoders = new ArrayList<MessageEncoder>();
        encoders.add(new WriteBytesEncoder(new byte[] { 0x01, 0x02, 0x03 }));
        encoders.add(new WriteTextEncoder("Hello, world", UTF_8));

        ExpressionFactory expressionFactory = ExpressionFactory.newInstance();
        environment = new ExpressionContext();
        expression = expressionFactory.createValueExpression(environment, "${variable}", byte[].class);
        encoders.add(new WriteExpressionEncoder(expression, environment));

        handler = new WriteHandler(encoders);

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

    @Ignore("DPW to fix : Unexpected exception, expected<java.nio.channels.NotYetConnectedException> but was<java.util.ConcurrentModificationException>")
    @Test(expected = NotYetConnectedException.class, timeout = 2000)
    public void shouldPropagateDownstreamMessageOnPipelineFutureSuccess() throws Exception {

        final ChannelBuffer[] expectedArr = new ChannelBuffer[3];
        expectedArr[0] = wrappedBuffer(new byte[] { 0x01, 0x02, 0x03 });
        expectedArr[1] = copiedBuffer("Hello, world", UTF_8);
        expectedArr[2] = wrappedBuffer(new byte[] { 0x01, 0x02, 0x03 });
        final ChannelBuffer expected = wrappedBuffer(expectedArr);

        context.checking(new Expectations() {
            {
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(any(PreparationEvent.class)));
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(channelState(OPEN, TRUE)));
                oneOf(downstream).handleDownstream(with(any(ChannelHandlerContext.class)), with(message(expected)));
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(any(WriteCompletionEvent.class)));
            }
        });

        expression.setValue(environment, new byte[] { 0x01, 0x02, 0x03 });
        channelFactory.newChannel(pipeline);
        ChannelFuture executionFuture = execution.getHandlerFuture();
        executionFuture.setSuccess();

        ChannelFuture handlerFuture = handler.getHandlerFuture();
        handlerFuture.sync();

        context.assertIsSatisfied();
    }

    @Ignore("to fix dpw# the Mockery is not thread-safe: use a Synchroniser to ensure thread safety")
    @Test(timeout = 2000)
    public void shouldNotPropagateDownstreamMessageOnPipelineFutureFailure() throws Exception {

        context.checking(new Expectations() {
            {
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(any(PreparationEvent.class)));
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(channelState(OPEN, TRUE)));
            }
        });
        expression.setValue(environment, new byte[] { 0x01, 0x02, 0x03 });
        channelFactory.newChannel(pipeline);
        ChannelFuture executionFuture = execution.getHandlerFuture();
        executionFuture.setFailure(new ChannelException("pipeline already failed"));

        ChannelFuture handlerFuture = handler.getHandlerFuture();
        assertFalse(handlerFuture.isDone());

        context.assertIsSatisfied();
    }
}
