/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.behavior.handler.codec.http;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.HttpMessage;

public class WriteHttpContentLengthEncoder implements HttpMessageContributingEncoder {

    @Override
    public void encode(HttpMessage message) {
        message.setChunked(false);
        message.setContent(ChannelBuffers.EMPTY_BUFFER);
    }

    @Override
    public String toString() {
        return "write http content length encoder";
    }
}
