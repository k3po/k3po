/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.buffer;

import static java.lang.String.format;
import static org.junit.Assert.assertTrue;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferIndexFinder;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.util.CharsetUtil;
import org.junit.Test;

public class FastIndexFinderTest {

    @Test
    public void findStringIndexNoMatch() throws Exception {

        String pattern = "Hello, World!";
        ChannelBuffer expected = ChannelBuffers.wrappedBuffer(pattern.getBytes(CharsetUtil.UTF_8));
        ChannelBufferIndexFinder kmp = new FastIndexFinder(expected);

        String text = "foo";
        ChannelBuffer observed = ChannelBuffers.wrappedBuffer(text.getBytes(CharsetUtil.UTF_8));
        int idx = observed.bytesBefore(kmp);
        assertTrue(format("Expected index -1, got %d", idx), idx == -1);
    }

    @Test
    // see http://jira.kaazing.wan/NR-20
            public
            void findStringIndexNoMatchPrefix() throws Exception {

        String pattern = "HTTP/1.1 20002303";
        ChannelBuffer expected = ChannelBuffers.wrappedBuffer(pattern.getBytes(CharsetUtil.UTF_8));
        ChannelBufferIndexFinder kmp = new FastIndexFinder(expected);

        String text = "HTTP/1.1 200";
        ChannelBuffer observed = ChannelBuffers.wrappedBuffer(text.getBytes(CharsetUtil.UTF_8));
        int idx = observed.bytesBefore(kmp);
        assertTrue(format("Expected index -1, got %d", idx), idx == -1);
    }

    @Test
    public void findStringIndexPartial() throws Exception {

        String pattern = "Wor";
        ChannelBuffer expected = ChannelBuffers.wrappedBuffer(pattern.getBytes(CharsetUtil.UTF_8));
        ChannelBufferIndexFinder kmp = new FastIndexFinder(expected);

        String text = "Hello, World!";
        ChannelBuffer observed = ChannelBuffers.wrappedBuffer(text.getBytes(CharsetUtil.UTF_8));
        int idx = observed.bytesBefore(kmp);
        assertTrue(format("Expected index 7, got %d", idx), idx == 7);
    }

    @Test
    public void findStringIndexFull() throws Exception {

        String pattern = "Hello, World!";
        ChannelBuffer expected = ChannelBuffers.wrappedBuffer(pattern.getBytes(CharsetUtil.UTF_8));
        ChannelBufferIndexFinder kmp = new FastIndexFinder(expected);

        String text = pattern;
        ChannelBuffer observed = ChannelBuffers.wrappedBuffer(text.getBytes(CharsetUtil.UTF_8));
        int idx = observed.bytesBefore(kmp);
        assertTrue(format("Expected index 0, got %d", idx), idx == 0);
    }

    @Test
    public void findHexIndexNoMatch() throws Exception {

        byte[] pattern =
                new byte[] { (byte) 0x48, (byte) 0x65, (byte) 0x6c, (byte) 0x6c, (byte) 0x6f, (byte) 0x2c, (byte) 0x20,
                        (byte) 0x57, (byte) 0x6f, (byte) 0x72, (byte) 0x6c, (byte) 0x64, (byte) 0x21 };

        ChannelBuffer expected = ChannelBuffers.wrappedBuffer(pattern);
        ChannelBufferIndexFinder kmp = new FastIndexFinder(expected);

        byte[] text = new byte[] { (byte) 0x66, (byte) 06f, (byte) 0x6f };
        ChannelBuffer observed = ChannelBuffers.wrappedBuffer(text);
        int idx = observed.bytesBefore(kmp);
        assertTrue(format("Expected index -1, got %d", idx), idx == -1);
    }

    @Test
    public void findHexIndexPartial() throws Exception {

        byte[] pattern = new byte[] { (byte) 0x57, (byte) 0x6f, (byte) 0x72 };
        ChannelBuffer expected = ChannelBuffers.wrappedBuffer(pattern);
        ChannelBufferIndexFinder kmp = new FastIndexFinder(expected);

        byte[] text =
                new byte[] { (byte) 0x48, (byte) 0x65, (byte) 0x6c, (byte) 0x6c, (byte) 0x6f, (byte) 0x2c, (byte) 0x20,
                        (byte) 0x57, (byte) 0x6f, (byte) 0x72, (byte) 0x6c, (byte) 0x64, (byte) 0x21 };
        ChannelBuffer observed = ChannelBuffers.wrappedBuffer(text);
        int idx = observed.bytesBefore(kmp);
        assertTrue(format("Expected index 7, got %d", idx), idx == 7);
    }

    @Test
    public void findHexIndexFull() throws Exception {

        byte[] pattern =
                new byte[] { (byte) 0x48, (byte) 0x65, (byte) 0x6c, (byte) 0x6c, (byte) 0x6f, (byte) 0x2c, (byte) 0x20,
                        (byte) 0x57, (byte) 0x6f, (byte) 0x72, (byte) 0x6c, (byte) 0x64, (byte) 0x21 };
        ChannelBuffer expected = ChannelBuffers.wrappedBuffer(pattern);
        ChannelBufferIndexFinder kmp = new FastIndexFinder(expected);

        byte[] text = pattern;
        ChannelBuffer observed = ChannelBuffers.wrappedBuffer(text);
        int idx = observed.bytesBefore(kmp);
        assertTrue(format("Expected index 0, got %d", idx), idx == 0);
    }

    @Test
    public void findStringInHexIndex() throws Exception {

        String pattern = "Wor";
        ChannelBuffer expected = ChannelBuffers.wrappedBuffer(pattern.getBytes(CharsetUtil.UTF_8));
        ChannelBufferIndexFinder kmp = new FastIndexFinder(expected);

        byte[] text =
                new byte[] { (byte) 0x48, (byte) 0x65, (byte) 0x6c, (byte) 0x6c, (byte) 0x6f, (byte) 0x2c, (byte) 0x20,
                        (byte) 0x57, (byte) 0x6f, (byte) 0x72, (byte) 0x6c, (byte) 0x64, (byte) 0x21 };
        ChannelBuffer observed = ChannelBuffers.wrappedBuffer(text);
        int idx = observed.bytesBefore(kmp);
        assertTrue(format("Expected index 7, got %d", idx), idx == 7);
    }

    @Test
    public void findHexInStringIndex() throws Exception {

        byte[] pattern = new byte[] { (byte) 0x57, (byte) 0x6f, (byte) 0x72 };
        ChannelBuffer expected = ChannelBuffers.wrappedBuffer(pattern);
        ChannelBufferIndexFinder kmp = new FastIndexFinder(expected);

        String text = "Hello, World!";
        ChannelBuffer observed = ChannelBuffers.wrappedBuffer(text.getBytes(CharsetUtil.UTF_8));
        int idx = observed.bytesBefore(kmp);
        assertTrue(format("Expected index 7, got %d", idx), idx == 7);
    }
}
