/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.behavior.handler.codec.http;

import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.handler.codec.http.HttpVersion;

import com.kaazing.robot.behavior.handler.codec.MessageEncoder;

public class WriteHttpVersionEncoder implements HttpMessageContributingEncoder {

    private MessageEncoder versionEncoder;

    public WriteHttpVersionEncoder(MessageEncoder versionEncoder) {
        this.versionEncoder = versionEncoder;
    }

    @Override
    public void encode(HttpMessage message) {
        String versionString = versionEncoder.encodeToString();
        HttpVersion oldVersion = message.getProtocolVersion();
        HttpVersion httpVersion = new HttpVersion(versionString, oldVersion.isKeepAliveDefault());
        message.setProtocolVersion(httpVersion);
    }

    @Override
    public String toString() {
        return String.format("write http version encoder with %s", versionEncoder);
    }

}
