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

import org.kaazing.robot.lang.el.ExpressionContext;
import org.kaazing.robot.lang.el.ExpressionFactoryUtils;

public class ReadLongLengthBytesDecoderTest {

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
    public void completeMatchWithCaptureOK() throws Exception {
        MessageDecoder decoder = new ReadLongLengthBytesDecoder(environment, "var");
        ChannelBuffer remainingBuffer =
                decoder.decode(copiedBuffer(new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x05 }));
        assertNotNull(remainingBuffer);
        assertEquals(0, remainingBuffer.readableBytes());
        ValueExpression expression = expressionFactory.createValueExpression(environment, "${var}", Long.class);
        Long actual = (Long) expression.getValue(environment);
        assertEquals(new Long(5), actual);
    }

    @Test
    public void noMatchWithCaptureOK() throws Exception {
        MessageDecoder decoder = new ReadLongLengthBytesDecoder(environment, "var");
        ChannelBuffer remainingBuffer = decoder.decode(copiedBuffer(new byte[] { 0x00, 0x00, 0x00 }));

        assertNull(remainingBuffer);
        ValueExpression expression = expressionFactory.createValueExpression(environment, "${var}", Long.class);

        thrown.expect(PropertyNotFoundException.class);
        expression.getValue(environment);
    }

    @Test
    public void fragmentedMatchWithCaptureOK() throws Exception {
        MessageDecoder decoder = new ReadLongLengthBytesDecoder(environment, "var");
        ChannelBuffer remainingBuffer = decoder.decode(copiedBuffer(new byte[] { 0x00, 0x00 }));
        assertNull(remainingBuffer);
        ValueExpression expression = expressionFactory.createValueExpression(environment, "${var}", Long.class);

        try {
            expression.getValue(environment);
        } catch (PropertyNotFoundException p) {
            // OK
        }

        remainingBuffer = decoder.decode(copiedBuffer(new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x05 }));
        assertNotNull(remainingBuffer);
        assertEquals(0, remainingBuffer.readableBytes());

        Long actual = (Long) expression.getValue(environment);
        assertEquals(new Long(5), actual);
    }

    @Test
    public void completeMatchWithBytesLeftOverWithCapturerOK() throws Exception {
        MessageDecoder decoder = new ReadLongLengthBytesDecoder(environment, "var");
        ChannelBuffer remainingBuffer =
                decoder.decode(copiedBuffer(new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x05, 0x05 }));
        assertNotNull(remainingBuffer);
        assertEquals(copiedBuffer(new byte[] { 0x05 }), remainingBuffer);
        ValueExpression expression = expressionFactory.createValueExpression(environment, "${var}", Long.class);
        Long actual = (Long) expression.getValue(environment);
        assertEquals(new Long(5), actual);
    }
}
