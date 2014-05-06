/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.behavior.handler.codec;

import org.jboss.netty.buffer.ChannelBuffer;

import org.kaazing.robot.lang.el.ExpressionContext;

public class ReadIntLengthBytesDecoder extends ReadFixedLengthBytesDecoder<Integer> {

    public ReadIntLengthBytesDecoder(ExpressionContext environment, String captureName) {
        super(Integer.SIZE / Byte.SIZE, environment, captureName);
    }

    // Read the data into an Integer
    @Override
    public Integer readBuffer(ChannelBuffer buffer) {
        return buffer.readInt();
    }
}
