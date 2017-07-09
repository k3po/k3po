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
package org.kaazing.k3po.driver.internal.behavior.handler.event;

import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
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
import static org.jboss.netty.util.CharsetUtil.UTF_8;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kaazing.k3po.driver.internal.behavior.handler.codec.Maskers.newMasker;
import static org.kaazing.k3po.lang.internal.RegionInfo.newSequential;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import org.jboss.netty.buffer.ChannelBuffer;
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
import org.kaazing.k3po.driver.internal.behavior.handler.TestChannelEvent;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.Masker;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.MessageDecoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.ReadByteArrayBytesDecoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.ReadExactBytesDecoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.ReadExactTextDecoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.ReadExpressionDecoder;
import org.kaazing.k3po.driver.internal.behavior.handler.codec.ReadVariableLengthBytesDecoder;
import org.kaazing.k3po.driver.internal.behavior.handler.prepare.PreparationEvent;
import org.kaazing.k3po.driver.internal.jmock.Expectations;
import org.kaazing.k3po.driver.internal.jmock.Mockery;
import org.kaazing.k3po.lang.internal.RegionInfo;
import org.kaazing.k3po.lang.internal.el.ExpressionContext;
import org.kaazing.k3po.lang.internal.el.ExpressionFactoryUtils;

@RunWith(Parameterized.class)
public class ReadHandlerTest {

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
    private ChannelPipeline pipeline;
    private ChannelFactory channelFactory;
    private boolean blockChannelOpen = true;
    private ExpressionContext environment;
    private ReadHandler handler;
    private Masker masker;

    public ReadHandlerTest(byte[] maskingKey) {
        this.maskingKey = maskingKey;
    }

    @Before
    public void setUp() throws Exception {
        context = new Mockery() {
            {
                setThrowFirstErrorOnAssertIsSatisfied(true);
                setThreadingPolicy(new Synchroniser());
            }
        };

        upstream = context.mock(ChannelUpstreamHandler.class);

        ExpressionFactory expressionFactory = ExpressionFactoryUtils.newExpressionFactory();
        environment = new ExpressionContext();

        List<MessageDecoder> decoders = new ArrayList<>();
        RegionInfo regionInfo = newSequential(0, 0);
        decoders.add(new ReadExactTextDecoder(regionInfo, "Hello", UTF_8));
        decoders.add(new ReadExactBytesDecoder(regionInfo, new byte[] { 0x01, 0x02, 0x03 }));
        ValueExpression expression = expressionFactory.createValueExpression(environment, "${variable}", byte[].class);
        decoders.add(new ReadExpressionDecoder(regionInfo, expression, environment));
        decoders.add(new ReadByteArrayBytesDecoder(regionInfo, 3));

        // TODO: Add when Regex's work
        // decoders.add(new ReadRegexDecoder(NamedGroupPattern.compile("Hello\n"), UTF_8, environment));
        expression = expressionFactory.createValueExpression(environment, "${variable2}", Integer.class);
        decoders.add(new ReadVariableLengthBytesDecoder(regionInfo, expression, environment));
        decoders.add(new ReadExactTextDecoder(regionInfo, "The last decoder", UTF_8));

        masker = newMasker(maskingKey);
        handler = new ReadHandler(decoders, newMasker(maskingKey));

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
        fireExceptionCaught(channel, new Exception());

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
    public void shouldConsumeUpstreamMessageEventWithMatchingBytesFragmented() throws Exception {

        context.checking(new Expectations() {
            {
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(any(PreparationEvent.class)));
            }
        });
        environment.getELResolver().setValue(environment, null, "variable", new byte[] { 0x01, 0x02, 0x03 });
        environment.getELResolver().setValue(environment, null, "variable2", 3);

        // expression.setValue(environment, new byte[] { 0x01, 0x02, 0x03 });
        // expression.setValue(environment, 3);

        Channel channel = channelFactory.newChannel(pipeline);
        ChannelFuture handlerFuture = handler.getHandlerFuture();

        fireMessageReceived(channel, masker.applyMask(copiedBuffer("Hello", UTF_8)));
        assertFalse(handlerFuture.isDone());

        fireMessageReceived(channel, masker.applyMask(copiedBuffer(new byte[] { 0x01, 0x02, 0x03 })));
        assertFalse(handlerFuture.isDone());

        fireMessageReceived(channel, masker.applyMask(copiedBuffer(new byte[] { 0x01, 0x02, 0x03 })));
        assertFalse(handlerFuture.isDone());

        // TODO: Add when Regex's work
        // fireMessageReceived(channel, masker.applyMask(copiedBuffer("Hello\n", UTF_8)));
        // assertFalse(handlerFuture.isDone());

        fireMessageReceived(channel, masker.applyMask(copiedBuffer(new byte[] { 0x01, 0x02, 0x03 })));
        assertFalse(handlerFuture.isDone());

        fireMessageReceived(channel, masker.applyMask(copiedBuffer(new byte[] { 0x01, 0x02, 0x03 })));
        assertFalse(handlerFuture.isDone());

        fireMessageReceived(channel, masker.applyMask(copiedBuffer("The last decoder", UTF_8)));

        assertTrue(handlerFuture.isSuccess());

        context.assertIsSatisfied();
    }

    @Test
    public void shouldConsumeUpstreamMessageEventWithMatchingBytes() throws Exception {

        context.checking(new Expectations() {
            {
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(any(PreparationEvent.class)));
            }
        });
        environment.getELResolver().setValue(environment, null, "variable", new byte[] { 0x01, 0x02, 0x03 });
        environment.getELResolver().setValue(environment, null, "variable2", 3);

        // expression.setValue(environment, new byte[] { 0x01, 0x02, 0x03 });
        // expression.setValue(environment, 3);

        Channel channel = channelFactory.newChannel(pipeline);
        ChannelFuture handlerFuture = handler.getHandlerFuture();

        ChannelBuffer text = copiedBuffer("Hello", UTF_8);
        ChannelBuffer bytes =
                copiedBuffer(new byte[] { 0x01, 0x02, 0x03, 0x01, 0x02, 0x03, 0x01, 0x02, 0x03, 0x01, 0x02, 0x03 });
        ChannelBuffer last = copiedBuffer("The last decoder", UTF_8);

        // TODO: Add Regex's when they are working

        fireMessageReceived(channel, masker.applyMask(copiedBuffer(text, bytes, last)));

        assertTrue(handlerFuture.isSuccess());

        context.assertIsSatisfied();
    }

    @Test
    public void shouldConsumeUpstreamMessageEventWithNonMatchingFirstDecoderBytes() throws Exception {

        context.checking(new Expectations() {
            {
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(any(PreparationEvent.class)));
            }
        });

        Channel channel = channelFactory.newChannel(pipeline);
        ChannelFuture handlerFuture = handler.getHandlerFuture();
        fireMessageReceived(channel, masker.applyMask(copiedBuffer("Goodbye", UTF_8)));

        assertTrue(handlerFuture.isDone());
        assertFalse(handlerFuture.isSuccess());

        context.assertIsSatisfied();
    }

    @Test
    public void shouldConsumeUpstreamMessageEventWithNonMatchingMiddleDecoderBytes() throws Exception {

        context.checking(new Expectations() {
            {
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(any(PreparationEvent.class)));
            }
        });

        environment.getELResolver().setValue(environment, null, "variable", new byte[] { 0x01, 0x02, 0x03 });
        environment.getELResolver().setValue(environment, null, "variable2", 3);

        // expression.setValue(environment, new byte[] { 0x01, 0x02, 0x03 });
        // expression.setValue(environment, 3);

        Channel channel = channelFactory.newChannel(pipeline);
        ChannelFuture handlerFuture = handler.getHandlerFuture();

        fireMessageReceived(channel, masker.applyMask(copiedBuffer("Hello", UTF_8)));
        assertFalse(handlerFuture.isDone());

        fireMessageReceived(channel, masker.applyMask(copiedBuffer(new byte[] { 0x01, 0x02, 0x03 })));
        assertFalse(handlerFuture.isDone());

        fireMessageReceived(channel, masker.applyMask(copiedBuffer(new byte[] { 0x01, 0x02, 0x04 })));
        assertTrue(handlerFuture.isDone());
        assertFalse(handlerFuture.isSuccess());

        context.assertIsSatisfied();
    }

    @Test
    public void shouldConsumeUpstreamMessageEventWithNonMatchingLastDecoderBytes() throws Exception {

        context.checking(new Expectations() {
            {
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(any(PreparationEvent.class)));
            }
        });

        environment.getELResolver().setValue(environment, null, "variable", new byte[] { 0x01, 0x02, 0x03 });
        environment.getELResolver().setValue(environment, null, "variable2", 3);

        // expression.setValue(environment, new byte[] { 0x01, 0x02, 0x03 });
        // expression.setValue(environment, 3);

        Channel channel = channelFactory.newChannel(pipeline);
        ChannelFuture handlerFuture = handler.getHandlerFuture();

        fireMessageReceived(channel, masker.applyMask(copiedBuffer("Hello", UTF_8)));
        assertFalse(handlerFuture.isDone());

        fireMessageReceived(channel, masker.applyMask(copiedBuffer(new byte[] { 0x01, 0x02, 0x03 })));
        assertFalse(handlerFuture.isDone());

        fireMessageReceived(channel, masker.applyMask(copiedBuffer(new byte[] { 0x01, 0x02, 0x03 })));
        assertFalse(handlerFuture.isDone());

        // TODO: Add when Regex's work
        // fireMessageReceived(channel, masker.applyMask(copiedBuffer("Hello\n", UTF_8)));
        // assertFalse(handlerFuture.isDone());

        fireMessageReceived(channel, masker.applyMask(copiedBuffer(new byte[] { 0x01, 0x02, 0x03 })));
        assertFalse(handlerFuture.isDone());

        // Expecting 4 bytes ... only sending three.
        fireMessageReceived(channel, masker.applyMask(copiedBuffer(new byte[] { 0x01, 0x02, 0x03 })));
        assertFalse(handlerFuture.isDone());

        fireMessageReceived(channel, masker.applyMask(copiedBuffer("The first decoder", UTF_8)));
        assertTrue(handlerFuture.isDone());
        assertFalse(handlerFuture.isSuccess());

        context.assertIsSatisfied();
    }

    @Test
    public void shouldPropagateUpstreamMessageEventWhenMatchingBytesLeaveRemainingBytes() throws Exception {

        ChannelBuffer text = copiedBuffer("Hello", UTF_8);
        ChannelBuffer bytes =
                copiedBuffer(new byte[] { 0x01, 0x02, 0x03, 0x01, 0x02, 0x03, 0x01, 0x02, 0x03, 0x01, 0x02, 0x03 });
        ChannelBuffer last = copiedBuffer("The last decoder. But with remaining bytes.", UTF_8);
        // TODO: Add Regex's when they are working
        ChannelBuffer buffer = copiedBuffer(text, bytes, last);
        ChannelBuffer maskedBuffer = masker.applyMask(buffer);
        final ChannelBuffer maskedRemainingBuffer = copiedBuffer(maskedBuffer);
        int remainingBytes = copiedBuffer(". But with remaining bytes.", UTF_8).readableBytes();
        maskedRemainingBuffer.readerIndex(maskedRemainingBuffer.writerIndex() - remainingBytes);

        context.checking(new Expectations() {
            {
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(any(PreparationEvent.class)));
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)),
                        with(message(maskedRemainingBuffer)));
            }
        });
        environment.getELResolver().setValue(environment, null, "variable", new byte[] { 0x01, 0x02, 0x03 });
        environment.getELResolver().setValue(environment, null, "variable2", 3);

        // expression.setValue(environment, new byte[] { 0x01, 0x02, 0x03 });
        // expression.setValue(environment, 3);

        Channel channel = channelFactory.newChannel(pipeline);
        ChannelFuture handlerFuture = handler.getHandlerFuture();

        fireMessageReceived(channel, maskedBuffer);

        assertTrue(handlerFuture.isSuccess());

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

        assertTrue(handlerFuture.isDone());
        assertFalse(handlerFuture.isSuccess());

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
    public void shouldPropagateUpstreamBoundEventAfterFutureDone() throws Exception {

        context.checking(new Expectations() {
            {
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(any(PreparationEvent.class)));
                oneOf(upstream).handleUpstream(with(any(ChannelHandlerContext.class)), with(channelState(BOUND)));
            }
        });

        Channel channel = channelFactory.newChannel(pipeline);
        fireChannelBound(channel, new LocalAddress("test"));
        fireChannelBound(channel, new LocalAddress("test"));

        context.assertIsSatisfied();
    }
}
