/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.behavior.handler.codec.http;

import org.jboss.netty.handler.codec.http.HttpMessage;

import org.kaazing.robot.behavior.handler.codec.MessageEncoder;

public class WriteHttpHeaderEncoder implements HttpMessageContributingEncoder {

    private final MessageEncoder nameEncoder;
    private final MessageEncoder valueEncoder;

    public WriteHttpHeaderEncoder(MessageEncoder nameEncoder, MessageEncoder valueEncoder) {
        this.nameEncoder = nameEncoder;
        this.valueEncoder = valueEncoder;
    }

    @Override
    public void encode(HttpMessage message) {
        message.addHeader(nameEncoder.encodeToString(), valueEncoder.encodeToString());
    }

    @Override
    public String toString() {
        return String.format("write http header encoder with %s %s", nameEncoder, valueEncoder);
    }
}
