/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.behavior.handler.codec;

import org.jboss.netty.buffer.ChannelBuffer;

import org.kaazing.robot.lang.el.ExpressionContext;

public class ReadByteArrayBytesDecoder extends ReadFixedLengthBytesDecoder<byte[]> {

    public ReadByteArrayBytesDecoder(final int length) {
        super(length);
    }

    public ReadByteArrayBytesDecoder(final int length, final ExpressionContext environment, final String captureName) {
        super(length, environment, captureName);
    }

    // Read the data into an array of bytes
    @Override
    public byte[] readBuffer(final ChannelBuffer buffer) {
        int len = getLength();
        byte[] matched = new byte[len];
        buffer.readBytes(matched, 0, len);
        return matched;
    }
}
