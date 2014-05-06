/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.behavior.handler.codec;

import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.jboss.netty.channel.Channels.pipeline;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.local.DefaultLocalClientChannelFactory;
import org.junit.Before;
import org.junit.Test;

public class ReadExactBytesDecoderTest {

    // TODO: Can we just remove the context from the Decoder and thus not need this.
    private ChannelHandlerContext context;


    @Before
    public void setUp() {
        ChannelPipeline pipeline = pipeline(new SimpleChannelHandler());
        ChannelFactory channelFactory = new DefaultLocalClientChannelFactory();
        channelFactory.newChannel(pipeline);
        context = pipeline.getContext(SimpleChannelHandler.class);
    }

    @Test
    public void completeMatchOK() throws Exception {
        MessageDecoder decoder = new ReadExactBytesDecoder(new byte[] { 0x01, 0x02, 0x03 });
        ChannelBuffer remainingBuffer = decoder.decode(copiedBuffer(new byte[] { 0x01, 0x02, 0x03 }));
        assertNotNull(remainingBuffer);
        assertEquals(0, remainingBuffer.readableBytes());
    }

    @Test(expected = MessageMismatchException.class)
    public void NoMatchOK() throws Exception {
        MessageDecoder decoder = new ReadExactBytesDecoder(new byte[] { 0x01, 0x02, 0x03 });
        decoder.decode(copiedBuffer(new byte[] { 0x01, 0x02, 0x04 }));
    }

    @Test
    public void fragmentedMatchOK() throws Exception {
        MessageDecoder decoder = new ReadExactBytesDecoder(new byte[] { 0x01, 0x02, 0x03 });
        ChannelBuffer remainingBuffer = decoder.decode(copiedBuffer(new byte[] { 0x01, 0x02 }));
        assertNull(remainingBuffer);
        remainingBuffer = decoder.decode(copiedBuffer(new byte[] { 0x03 }));
        assertNotNull(remainingBuffer);
        assertEquals(0, remainingBuffer.readableBytes());
    }

    @Test(expected = MessageMismatchException.class)
    public void onlyPartialMatchOK() throws Exception {
        MessageDecoder decoder = new ReadExactBytesDecoder(new byte[] { 0x01, 0x02, 0x03 });
        ChannelBuffer remainingBuffer = decoder.decode(copiedBuffer(new byte[] { 0x01, 0x02 }));
        assertNull(remainingBuffer);
        decoder.decode(copiedBuffer(new byte[] { 0x04 }));
    }

    @Test
    public void completeMatchWithBytesLeftOverOK() throws Exception {
        MessageDecoder decoder = new ReadExactBytesDecoder(new byte[] { 0x01, 0x02, 0x03 });
        ChannelBuffer remainingBuffer = decoder.decode(copiedBuffer(new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05 }));
        assertNotNull(remainingBuffer);
        assertEquals(copiedBuffer(new byte[] { 0x04, 0x05 }), remainingBuffer);
    }
}
