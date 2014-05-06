/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.behavior.handler.codec;

import org.jboss.netty.buffer.ChannelBuffer;

import com.kaazing.robot.lang.el.ExpressionContext;

public class ReadLongLengthBytesDecoder extends ReadFixedLengthBytesDecoder<Long> {

    public ReadLongLengthBytesDecoder(ExpressionContext environment, String captureName) {
        super(Long.SIZE / Byte.SIZE, environment, captureName);
    }

    // Read the data into a Long
    @Override
    public Long readBuffer(ChannelBuffer buffer) {
        return buffer.readLong();
    }
}
