/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.behavior.handler.codec;

import org.jboss.netty.buffer.ChannelBuffer;

import com.kaazing.robot.lang.el.ExpressionContext;

public class ReadByteLengthBytesDecoder extends ReadFixedLengthBytesDecoder<Byte> {

    public ReadByteLengthBytesDecoder(ExpressionContext environment, String captureName) {
        super(Byte.SIZE / Byte.SIZE, environment, captureName);
    }

    // Read the data into a Byte
    @Override
    public Byte readBuffer(ChannelBuffer buffer) {
        return buffer.readByte();
    }
}
