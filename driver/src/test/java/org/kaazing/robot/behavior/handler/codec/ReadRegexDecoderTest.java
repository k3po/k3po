/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.behavior.handler.codec;

import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.jboss.netty.channel.Channels.pipeline;
import static org.jboss.netty.util.CharsetUtil.UTF_8;
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
import org.kaazing.robot.lang.el.ExpressionContext;
import org.kaazing.robot.lang.el.ExpressionFactoryUtils;
import org.kaazing.robot.lang.regex.NamedGroupPattern;

public class ReadRegexDecoderTest {

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
        NamedGroupPattern pattern = NamedGroupPattern.compile("/H.*o\\n/");
        MessageDecoder decoder = new ReadRegexDecoder(pattern, UTF_8, environment);

        ChannelBuffer remainingBuffer = decoder.decode(copiedBuffer("Hello\n", UTF_8));

        assertEquals(0, remainingBuffer.readableBytes());
    }

    @Test
    public void completeMatchWithCaptureOK() throws Exception {
        NamedGroupPattern pattern = NamedGroupPattern.compile("/(?<var>H.*o)\\n/");
        MessageDecoder decoder = new ReadRegexDecoder(pattern, UTF_8, environment);

        ChannelBuffer remainingBuffer = decoder.decode(copiedBuffer("Hello\n", UTF_8));
        assertEquals(0, remainingBuffer.readableBytes());

        ValueExpression expression = expressionFactory.createValueExpression(environment, "${var}", byte[].class);
        assertArrayEquals("Hello".getBytes(UTF_8), (byte[]) expression.getValue(environment));
    }

    @Test(expected = MessageMismatchException.class)
    public void noMatchOK() throws Exception {
        NamedGroupPattern pattern = NamedGroupPattern.compile("/H.*o\\n/");
        MessageDecoder decoder = new ReadRegexDecoder(pattern, UTF_8, environment);

        decoder.decode(copiedBuffer("Hellf\n", UTF_8));
    }

    @Test(expected = MessageMismatchException.class)
    public void noMatchWithCaptureOK() throws Exception {
        NamedGroupPattern pattern = NamedGroupPattern.compile("/(?<var>H.*o)\\n/");
        MessageDecoder decoder = new ReadRegexDecoder(pattern, UTF_8, environment);

        decoder.decode(copiedBuffer("Hellf\n", UTF_8));
    }

    @Test
    public void fragmentedMatchOK() throws Exception {
        NamedGroupPattern pattern = NamedGroupPattern.compile("/H.*o\\n/");
        MessageDecoder decoder = new ReadRegexDecoder(pattern, UTF_8, environment);

        ChannelBuffer remainingBuffer = decoder.decode(copiedBuffer("Hel", UTF_8));
        assertNull(remainingBuffer);

        remainingBuffer = decoder.decode(copiedBuffer("o\n", UTF_8));
        assertEquals(0, remainingBuffer.readableBytes());
    }

    @Test
    public void fragmentedMatchWithCaptureOK() throws Exception {
        NamedGroupPattern pattern = NamedGroupPattern.compile("/(?<var>H.*o)\\n/");
        MessageDecoder decoder = new ReadRegexDecoder(pattern, UTF_8, environment);

        ChannelBuffer remainingBuffer = decoder.decode(copiedBuffer("Hel", UTF_8));
        assertNull(remainingBuffer);
        ValueExpression expression = expressionFactory.createValueExpression(environment, "${var}", byte[].class);
        try {
            expression.getValue(environment);
        } catch (PropertyNotFoundException p) {
            // OK
        }

        remainingBuffer = decoder.decode(copiedBuffer("o\n", UTF_8));
        assertEquals(0, remainingBuffer.readableBytes());
        assertArrayEquals("Helo".getBytes(UTF_8), (byte[]) expression.getValue(environment));

    }

    @Test
    public void completeMatchWithBytesLeftOverOK() throws Exception {
        NamedGroupPattern pattern = NamedGroupPattern.compile("/H.*o\\n/");
        MessageDecoder decoder = new ReadRegexDecoder(pattern, UTF_8, environment);

        ChannelBuffer remainingBuffer = decoder.decode(copiedBuffer("Hello\nWorld", UTF_8));
        assertNotNull(remainingBuffer);
        assertEquals(copiedBuffer("World", UTF_8), remainingBuffer);
    }

    @Test
    public void completeMatchWithBytesLeftOverWithCapturerOK() throws Exception {
        
        NamedGroupPattern pattern = NamedGroupPattern.compile("/(?<var>H.*o)\\n/");
        MessageDecoder decoder = new ReadRegexDecoder(pattern, UTF_8, environment);

        ChannelBuffer remainingBuffer = decoder.decode(copiedBuffer("Hello\nWorld", UTF_8));
        assertNotNull(remainingBuffer);
        assertEquals(copiedBuffer("World", UTF_8), remainingBuffer);

        ValueExpression expression = expressionFactory.createValueExpression(environment, "${var}", byte[].class);
        assertArrayEquals("Hello".getBytes(UTF_8), (byte[]) expression.getValue(environment));
    }
}
