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
import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.jboss.netty.buffer.ChannelBuffers.wrappedBuffer;
import static org.jboss.netty.channel.ChannelState.OPEN;
import static org.jboss.netty.channel.Channels.fireWriteComplete;
import static org.jboss.netty.channel.Channels.pipeline;
import static org.jboss.netty.util.CharsetUtil.UTF_8;
import static org.junit.Assert.assertFalse;
import static org.kaazing.k3po.driver.behavior.handler.codec.Maskers.newMasker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.WriteCompletionEvent;
import org.jboss.netty.channel.local.DefaultLocalClientChannelFactory;
import org.jmock.lib.concurrent.Synchroniser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kaazing.k3po.driver.behavior.handler.ExecutionHandler;
import org.kaazing.k3po.driver.behavior.handler.codec.Masker;
import org.kaazing.k3po.driver.behavior.handler.codec.MessageEncoder;
import org.kaazing.k3po.driver.behavior.handler.codec.WriteBytesEncoder;
import org.kaazing.k3po.driver.behavior.handler.codec.WriteExpressionEncoder;
import org.kaazing.k3po.driver.behavior.handler.codec.WriteTextEncoder;
import org.kaazing.k3po.driver.behavior.handler.prepare.PreparationEvent;
import org.kaazing.k3po.driver.jmock.Expectations;
import org.kaazing.k3po.driver.jmock.Mockery;
import org.kaazing.k3po.lang.el.ExpressionContext;

@RunWith(Parameterized.class)
public class WriteHandlerTest {

    @Parameters
    public static Iterable<byte[]> maskingKeys() {
        byte[] identityKey = new byte[] { 0x00, 0x00, 0x00, 0x00 };
        byte[] maskingKey = new byte[4];
        new Random().nextBytes(maskingKey);

        return asList(identityKey, maskingKey);
    }

    @Rule
    public TestRule timeout = new DisableOnDebug(new Timeout(1, SECONDS));

    private final byte[] maskingKey;

    private Mockery context;
    private ChannelUpstreamHandler upstream;
    private ChannelDownstreamHandler downstream;
    private ChannelPipeline pipeline;
    private ChannelFactory channelFactory;
    private WriteHandler handler;
    private ExecutionHandler execution;
    private ExpressionContext environment;
    private ValueExpression expression;
    private Masker masker;

    public WriteHandlerTest(byte[] maskingKey) {
        this.maskingKey = maskingKey;
    }

    @Before
    public void setUp() throws Exception {
        context = new Mockery() {
            {
                setThrowFirstErrorOnAssertIsSatisfied(true);
            }
        };
        context.setThreadingPolicy(new Synchroniser());

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

        masker = newMasker(maskingKey);
        handler = new WriteHandler(encoders, newMasker(maskingKey));

        pipeline = pipeline(new SimpleChannelHandler() {
            @Override
            public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent evt) throws Exception {
                downstream.handleDownstream(ctx, evt);
                super.handleDownstream(ctx, evt);
            }

            @Override
            public void writeRequested(ChannelHandlerContext ctx, MessageEvent e)
                    throws Exception {
                Object message = e.getMessage();
                e.getFuture().setSuccess();
                if (message instanceof ChannelBuffer) {
                    ChannelBuffer buf = (ChannelBuffer) message;
                    fireWriteComplete(ctx, buf.readableBytes());
                }
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
    public void shouldPropagateDownstreamMessageOnPipelineFutureSuccess() throws Exception {

        final ChannelBuffer[] expectedArr = new ChannelBuffer[3];
        expectedArr[0] = wrappedBuffer(new byte[] { 0x01, 0x02, 0x03 });
        expectedArr[1] = copiedBuffer("Hello, world", UTF_8);
        expectedArr[2] = wrappedBuffer(new byte[] { 0x01, 0x02, 0x03 });
        final ChannelBuffer expected = masker.applyMask(wrappedBuffer(expectedArr));

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

    @Test
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
