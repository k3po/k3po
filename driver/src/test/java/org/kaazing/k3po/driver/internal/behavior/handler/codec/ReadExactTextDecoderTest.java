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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.local.DefaultLocalClientChannelFactory;
import org.junit.Before;
import org.junit.Test;
import org.kaazing.k3po.driver.internal.behavior.ScriptProgressException;

public class ReadExactTextDecoderTest {

    @Before
    public void setUp() {
        ChannelPipeline pipeline = pipeline(new SimpleChannelHandler());
        ChannelFactory channelFactory = new DefaultLocalClientChannelFactory();
        channelFactory.newChannel(pipeline);
        pipeline.getContext(SimpleChannelHandler.class);
    }

    @Test
    public void completeMatchOK() throws Exception {
        MessageDecoder decoder = new ReadExactTextDecoder("Hello", UTF_8);
        ChannelBuffer remainingBuffer = decoder.decode(copiedBuffer("Hello", UTF_8));
        assertNotNull(remainingBuffer);
        assertEquals(0, remainingBuffer.readableBytes());
    }

    @Test(
            expected = ScriptProgressException.class)
    public void NoMatchOK() throws Exception {
        MessageDecoder decoder = new ReadExactTextDecoder("Hello", UTF_8);
        decoder.decode(copiedBuffer("Goodbye", UTF_8));
    }

    @Test
    public void fragmentedMatchOK() throws Exception {
        MessageDecoder decoder = new ReadExactTextDecoder("Hello", UTF_8);
        ChannelBuffer remainingBuffer = decoder.decode(copiedBuffer("He", UTF_8));
        assertNull(remainingBuffer);
        remainingBuffer = decoder.decode(copiedBuffer("llo", UTF_8));
        assertNotNull(remainingBuffer);
        assertEquals(0, remainingBuffer.readableBytes());
    }

    @Test(
            expected = ScriptProgressException.class)
    public void onlyPartialMatchOK() throws Exception {
        MessageDecoder decoder = new ReadExactTextDecoder("Hello", UTF_8);
        ChannelBuffer remainingBuffer = decoder.decode(copiedBuffer("He", UTF_8));
        assertNull(remainingBuffer);
        decoder.decode(copiedBuffer("Goodbye", UTF_8));
    }

    @Test
    public void completeMatchWithBytesLeftOverOK() throws Exception {
        MessageDecoder decoder = new ReadExactTextDecoder("Hello", UTF_8);
        ChannelBuffer remainingBuffer = decoder.decode(copiedBuffer("Hello MessageDecoder", UTF_8));
        assertNotNull(remainingBuffer);
        assertEquals(copiedBuffer(" MessageDecoder", UTF_8), remainingBuffer);
    }
}
