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
package org.kaazing.k3po.driver.internal.behavior.handler.codec;

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
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.local.DefaultLocalClientChannelFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.kaazing.k3po.driver.internal.behavior.ScriptProgressException;
import org.kaazing.k3po.lang.internal.el.ExpressionContext;
import org.kaazing.k3po.lang.internal.el.ExpressionFactoryUtils;
import org.kaazing.k3po.lang.internal.regex.NamedGroupPattern;

public class ReadRegexDecoderTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ExpressionContext environment;
    private ExpressionFactory expressionFactory;

    @Before
    public void setUp() {
        ChannelPipeline pipeline = pipeline(new SimpleChannelHandler());
        ChannelFactory channelFactory = new DefaultLocalClientChannelFactory();
        channelFactory.newChannel(pipeline);
        pipeline.getContext(SimpleChannelHandler.class);
        environment = new ExpressionContext();
        expressionFactory = ExpressionFactoryUtils.newExpressionFactory();
    }

    @Test
    public void completeMatchOK() throws Exception {
        NamedGroupPattern pattern = NamedGroupPattern.compile("H.*o\\n");
        MessageDecoder decoder = new ReadRegexDecoder(pattern, UTF_8, environment);

        ChannelBuffer remainingBuffer = decoder.decode(copiedBuffer("Hello\n", UTF_8));

        assertEquals(0, remainingBuffer.readableBytes());
    }

    @Test
    public void completeMatchWithCaptureOK() throws Exception {
        NamedGroupPattern pattern = NamedGroupPattern.compile("(?<var>H.*o)\\n");
        MessageDecoder decoder = new ReadRegexDecoder(pattern, UTF_8, environment);

        ChannelBuffer remainingBuffer = decoder.decode(copiedBuffer("Hello\n", UTF_8));
        assertEquals(0, remainingBuffer.readableBytes());

        ValueExpression expression = expressionFactory.createValueExpression(environment, "${var}", Object.class);
        assertEquals("Hello", expression.getValue(environment));
    }

    @Test(
            expected = ScriptProgressException.class)
    public void noMatchOK() throws Exception {
        NamedGroupPattern pattern = NamedGroupPattern.compile("H.*o\\n");
        MessageDecoder decoder = new ReadRegexDecoder(pattern, UTF_8, environment);

        decoder.decode(copiedBuffer("Hellf\n", UTF_8));
    }

    @Test(
            expected = ScriptProgressException.class)
    public void noMatchWithCaptureOK() throws Exception {
        NamedGroupPattern pattern = NamedGroupPattern.compile("(?<var>H.*o)\\n");
        MessageDecoder decoder = new ReadRegexDecoder(pattern, UTF_8, environment);

        decoder.decode(copiedBuffer("Hellf\n", UTF_8));
    }

    @Test
    public void fragmentedMatchOK() throws Exception {
        NamedGroupPattern pattern = NamedGroupPattern.compile("H.*o\\n");
        MessageDecoder decoder = new ReadRegexDecoder(pattern, UTF_8, environment);

        ChannelBuffer remainingBuffer = decoder.decode(copiedBuffer("Hel", UTF_8));
        assertNull(remainingBuffer);

        remainingBuffer = decoder.decode(copiedBuffer("o\n", UTF_8));
        assertEquals(0, remainingBuffer.readableBytes());
    }

    @Test
    public void fragmentedMatchWithCaptureOK() throws Exception {
        NamedGroupPattern pattern = NamedGroupPattern.compile("(?<var>H.*o)\\n");
        MessageDecoder decoder = new ReadRegexDecoder(pattern, UTF_8, environment);

        ChannelBuffer remainingBuffer = decoder.decode(copiedBuffer("Hel", UTF_8));
        assertNull(remainingBuffer);
        ValueExpression expression = expressionFactory.createValueExpression(environment, "${var}", Object.class);
        try {
            expression.getValue(environment);
        } catch (PropertyNotFoundException p) {
            // OK
        }

        remainingBuffer = decoder.decode(copiedBuffer("o\n", UTF_8));
        assertEquals(0, remainingBuffer.readableBytes());
        assertEquals("Helo", expression.getValue(environment));

    }

    @Test
    public void completeMatchWithBytesLeftOverOK() throws Exception {
        NamedGroupPattern pattern = NamedGroupPattern.compile("H.*o\\n");
        MessageDecoder decoder = new ReadRegexDecoder(pattern, UTF_8, environment);

        ChannelBuffer remainingBuffer = decoder.decode(copiedBuffer("Hello\nWorld", UTF_8));
        assertNotNull(remainingBuffer);
        assertEquals(copiedBuffer("World", UTF_8), remainingBuffer);
    }

    @Test
    public void completeMatchWithBytesLeftOverWithCapturerOK() throws Exception {

        NamedGroupPattern pattern = NamedGroupPattern.compile("(?<var>H.*o)\\n");
        MessageDecoder decoder = new ReadRegexDecoder(pattern, UTF_8, environment);

        ChannelBuffer remainingBuffer = decoder.decode(copiedBuffer("Hello\nWorld", UTF_8));
        assertNotNull(remainingBuffer);
        assertEquals(copiedBuffer("World", UTF_8), remainingBuffer);

        ValueExpression expression = expressionFactory.createValueExpression(environment, "${var}", Object.class);
        assertEquals("Hello", expression.getValue(environment));
    }
}
