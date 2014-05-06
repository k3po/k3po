/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.behavior.handler.codec;

import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.jboss.netty.channel.Channels.pipeline;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.local.DefaultLocalClientChannelFactory;
import org.junit.Before;
import org.junit.Test;

import org.kaazing.robot.lang.el.ExpressionContext;
import org.kaazing.robot.lang.el.ExpressionFactoryUtils;

public class ReadExpressionDecoderTest {

    // TODO: Can we just remove the context from the Decoder and thus not need this.
    private ChannelHandlerContext context;
    private ExpressionFactory expressionFactory;
    private ExpressionContext environment;

    @Before
    public void setUp() {
        ChannelPipeline pipeline = pipeline(new SimpleChannelHandler());
        ChannelFactory channelFactory = new DefaultLocalClientChannelFactory();
        channelFactory.newChannel(pipeline);
        context = pipeline.getContext(SimpleChannelHandler.class);
        expressionFactory = ExpressionFactoryUtils.newExpressionFactory();
        environment = new ExpressionContext();
    }

    @Test
    public void completeMatchOK() throws Exception {
        ValueExpression expression = expressionFactory.createValueExpression(environment, "${variable}", byte[].class);
        MessageDecoder decoder = new ReadExpressionDecoder(expression, environment);

        environment.getELResolver().setValue(environment, null, "variable", new byte[] { 0x01, 0x02, 0x03 });

        ChannelBuffer remainingBuffer = decoder.decode(copiedBuffer(new byte[] { 0x01, 0x02, 0x03 }));
        assertNotNull(remainingBuffer);
        assertEquals(0, remainingBuffer.readableBytes());
    }

    @Test(expected = MessageMismatchException.class)
    public void noMatchOK() throws Exception {
        ValueExpression expression = expressionFactory.createValueExpression(environment, "${variable}", byte[].class);
        MessageDecoder decoder = new ReadExpressionDecoder(expression, environment);

        environment.getELResolver().setValue(environment, null, "variable", new byte[] { 0x01, 0x02, 0x03 });

        decoder.decode(copiedBuffer(new byte[] { 0x01, 0x02, 0x04 }));
    }

    @Test
    public void fragmentedMatchOK() throws Exception {
        ValueExpression expression = expressionFactory.createValueExpression(environment, "${variable}", byte[].class);
        MessageDecoder decoder = new ReadExpressionDecoder(expression, environment);

        environment.getELResolver().setValue(environment, null, "variable", new byte[] { 0x01, 0x02, 0x03 });

        ChannelBuffer remainingBuffer = decoder.decode(copiedBuffer(new byte[] { 0x01, 0x02 }));
        assertNull(remainingBuffer);

        remainingBuffer = decoder.decode(copiedBuffer(new byte[] { 0x03 }));
        assertNotNull(remainingBuffer);
        assertEquals(0, remainingBuffer.readableBytes());
    }

    @Test(expected = MessageMismatchException.class)
    public void onlyPartialMatchOK() throws Exception {
        ValueExpression expression = expressionFactory.createValueExpression(environment, "${variable}", byte[].class);
        MessageDecoder decoder = new ReadExpressionDecoder(expression, environment);

        environment.getELResolver().setValue(environment, null, "variable", new byte[] { 0x01, 0x02, 0x03 });

        ChannelBuffer remainingBuffer = decoder.decode(copiedBuffer(new byte[] { 0x01, 0x02 }));
        assertNull(remainingBuffer);

        decoder.decode(copiedBuffer(new byte[] { 0x04 }));
    }

    @Test
    public void completeMatchWithBytesLeftOverOK() throws Exception {
        ValueExpression expression = expressionFactory.createValueExpression(environment, "${variable}", byte[].class);
        MessageDecoder decoder = new ReadExpressionDecoder(expression, environment);

        environment.getELResolver().setValue(environment, null, "variable", new byte[] { 0x01, 0x02, 0x03 });

        ChannelBuffer remainingBuffer = decoder.decode(copiedBuffer(new byte[] { 0x01, 0x02, 0x03, 0x04 }));
        assertNotNull(remainingBuffer);
        assertEquals(copiedBuffer(new byte[] { 0x04 }), remainingBuffer);
    }
}
