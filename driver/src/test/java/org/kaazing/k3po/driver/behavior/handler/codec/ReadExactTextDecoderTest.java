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

package org.kaazing.k3po.driver.behavior.handler.codec;

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
import org.kaazing.k3po.driver.behavior.ScriptProgressException;
import org.kaazing.k3po.driver.behavior.handler.codec.MessageDecoder;
import org.kaazing.k3po.driver.behavior.handler.codec.ReadExactTextDecoder;

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

    @Test(expected = ScriptProgressException.class)
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

    @Test(expected = ScriptProgressException.class)
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
