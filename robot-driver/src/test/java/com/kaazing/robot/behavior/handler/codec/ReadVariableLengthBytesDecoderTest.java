/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.behavior.handler.codec;

import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.jboss.netty.channel.Channels.pipeline;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.el.ExpressionFactory;
import javax.el.PropertyNotFoundException;
import javax.el.ValueExpression;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.local.DefaultLocalClientChannelFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.kaazing.robot.lang.el.ExpressionContext;
import com.kaazing.robot.lang.el.ExpressionFactoryUtils;

public class ReadVariableLengthBytesDecoderTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    // TODO: Can we just remove the context from the Decoder and thus not need this.
    private ChannelHandlerContext context;
    private ExpressionContext environment;
    private ExpressionFactory expressionFactory;


    @Before
    public void setUp() {
        ChannelPipeline pipeline = pipeline(new SimpleChannelHandler());
        ChannelFactory channelFactory = new DefaultLocalClientChannelFactory();
        channelFactory.newChannel(pipeline);
        context = pipeline.getContext(SimpleChannelHandler.class);
        environment = new ExpressionContext();
        expressionFactory = ExpressionFactoryUtils.newExpressionFactory();
    }

    @Test
    public void completeMatchOK() throws Exception {
        ValueExpression expression = expressionFactory.createValueExpression(environment, "${var}", Integer.class);
        MessageDecoder decoder = new ReadVariableLengthBytesDecoder(expression, environment);

        environment.getELResolver().setValue(environment, null, "var", new Integer(3));

        ChannelBuffer remainingBuffer = decoder.decode(copiedBuffer(new byte[] { 0x01, 0x02, 0x03 }));
        assertNotNull(remainingBuffer);
        assertEquals(0, remainingBuffer.readableBytes());
    }

    @Test
    public void completeMatchWithCaptureOK() throws Exception {
        ValueExpression expression = expressionFactory.createValueExpression(environment, "${var}", Integer.class);
        MessageDecoder decoder = new ReadVariableLengthBytesDecoder(expression, environment, "var2");

        environment.getELResolver().setValue(environment, null, "var", new Integer(3));

        ChannelBuffer remainingBuffer = decoder.decode(copiedBuffer(new byte[] { 0x01, 0x02, 0x03 }));
        assertNotNull(remainingBuffer);
        assertEquals(0, remainingBuffer.readableBytes());
        ValueExpression expression2 = expressionFactory.createValueExpression(environment, "${var2}", byte[].class);
        assertArrayEquals(new byte[] { 0x01, 0x02, 0x03 }, (byte[]) expression2.getValue(environment));
    }

    @Test
    public void noMatchOK() throws Exception {
        ValueExpression expression = expressionFactory.createValueExpression(environment, "${var}", Integer.class);
        MessageDecoder decoder = new ReadVariableLengthBytesDecoder(expression, environment);

        environment.getELResolver().setValue(environment, null, "var", new Integer(3));

        ChannelBuffer remainingBuffer = decoder.decode(copiedBuffer(new byte[] { 0x01, 0x02 }));
        assertNull(remainingBuffer);
    }

    @Test
    public void noMatchWithCaptureOK() throws Exception {
        ValueExpression expression = expressionFactory.createValueExpression(environment, "${var}", Integer.class);
        MessageDecoder decoder = new ReadVariableLengthBytesDecoder(expression, environment, "var2");

        environment.getELResolver().setValue(environment, null, "var", new Integer(3));

        ChannelBuffer remainingBuffer = decoder.decode(copiedBuffer(new byte[] { 0x01, 0x02 }));
        assertNull(remainingBuffer);
        ValueExpression expression2 = expressionFactory.createValueExpression(environment, "${var2}", byte[].class);

        thrown.expect(PropertyNotFoundException.class);
        expression2.getValue(environment);
    }

    @Test
    public void fragmentedMatchOK() throws Exception {
        ValueExpression expression = expressionFactory.createValueExpression(environment, "${var}", Integer.class);
        MessageDecoder decoder = new ReadVariableLengthBytesDecoder(expression, environment);

        environment.getELResolver().setValue(environment, null, "var", new Integer(3));

        ChannelBuffer remainingBuffer = decoder.decode(copiedBuffer(new byte[] { 0x01, 0x02 }));
        assertNull(remainingBuffer);

        remainingBuffer = decoder.decode(copiedBuffer(new byte[] { 0x03 }));
        assertNotNull(remainingBuffer);
        assertEquals(0, remainingBuffer.readableBytes());

    }

    @Test
    public void fragmentedMatchWithCaptureOK() throws Exception {
        ValueExpression expression = expressionFactory.createValueExpression(environment, "${var}", Integer.class);
        MessageDecoder decoder = new ReadVariableLengthBytesDecoder(expression, environment, "var2");

        environment.getELResolver().setValue(environment, null, "var", new Integer(3));

        ChannelBuffer remainingBuffer = decoder.decode(copiedBuffer(new byte[] { 0x01, 0x02 }));
        assertNull(remainingBuffer);
        ValueExpression expression2 = expressionFactory.createValueExpression(environment, "${var2}", byte[].class);

        try {
            expression2.getValue(environment);
        } catch (PropertyNotFoundException p) {
            // OK
        }

        remainingBuffer = decoder.decode(copiedBuffer(new byte[] { 0x03 }));
        assertNotNull(remainingBuffer);
        assertEquals(0, remainingBuffer.readableBytes());
        assertArrayEquals(new byte[] { 0x01, 0x02, 0x03 }, (byte[]) expression2.getValue(environment));
    }

    @Test
    public void completeMatchWithBytesLeftOverOK() throws Exception {
        ValueExpression expression = expressionFactory.createValueExpression(environment, "${var}", Integer.class);
        MessageDecoder decoder = new ReadVariableLengthBytesDecoder(expression, environment);

        environment.getELResolver().setValue(environment, null, "var", new Integer(3));

        ChannelBuffer remainingBuffer = decoder.decode(copiedBuffer(new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05 }));
        assertNotNull(remainingBuffer);
        assertEquals(copiedBuffer(new byte[] { 0x04, 0x05 }), remainingBuffer);
    }

    @Test
    public void completeMatchWithBytesLeftOverWithCapturerOK() throws Exception {
        ValueExpression expression = expressionFactory.createValueExpression(environment, "${var}", Integer.class);
        MessageDecoder decoder = new ReadVariableLengthBytesDecoder(expression, environment, "var2");

        environment.getELResolver().setValue(environment, null, "var", new Integer(3));

        ChannelBuffer remainingBuffer = decoder.decode(copiedBuffer(new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05 }));
        assertNotNull(remainingBuffer);
        assertEquals(copiedBuffer(new byte[] { 0x04, 0x05 }), remainingBuffer);
        ValueExpression expression2 = expressionFactory.createValueExpression(environment, "${var2}", byte[].class);
        assertArrayEquals(new byte[] { 0x01, 0x02, 0x03 }, (byte[]) expression2.getValue(environment));

    }
}
