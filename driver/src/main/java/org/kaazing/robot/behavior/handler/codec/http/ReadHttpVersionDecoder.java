/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.behavior.handler.codec.http;

import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.jboss.netty.util.CharsetUtil.UTF_8;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.handler.codec.http.HttpVersion;

import org.kaazing.robot.behavior.handler.codec.MessageDecoder;

public class ReadHttpVersionDecoder implements HttpMessageContributingDecoder {

    private MessageDecoder versionDecoder;

    public ReadHttpVersionDecoder(MessageDecoder versionDecoder) {
        this.versionDecoder = versionDecoder;
    }

    @Override
    public void decode(HttpMessage message) throws Exception {
        HttpVersion version = message.getProtocolVersion();
        ChannelBuffer buffer = copiedBuffer(version.getText(), UTF_8);
        versionDecoder.decode(buffer);
    }

    @Override
    public String toString() {
        return String.format("read http version decoder with: %s", versionDecoder);
    }
}
