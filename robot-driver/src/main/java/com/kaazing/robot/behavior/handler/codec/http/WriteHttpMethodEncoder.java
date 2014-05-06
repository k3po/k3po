/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.behavior.handler.codec.http;

import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;

import com.kaazing.robot.behavior.handler.codec.MessageEncoder;

public class WriteHttpMethodEncoder implements HttpMessageContributingEncoder {

    private final MessageEncoder methodEncoder;

    public WriteHttpMethodEncoder(MessageEncoder methodEncoder) {
        this.methodEncoder = methodEncoder;
    }

    @Override
    public void encode(HttpMessage message) {
        if (message instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) message;
            HttpMethod method = new HttpMethod(methodEncoder.encodeToString());
            request.setMethod(method);
        } else {
            throw new IllegalStateException("Can not write method onto a Http Response");
        }
    }

    @Override
    public String toString() {
        return String.format("write http method encoder with %s", methodEncoder);
    }

}
