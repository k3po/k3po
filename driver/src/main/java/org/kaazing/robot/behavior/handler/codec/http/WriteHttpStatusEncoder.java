/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.behavior.handler.codec.http;

import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

import org.kaazing.robot.behavior.handler.codec.MessageEncoder;

public class WriteHttpStatusEncoder implements HttpMessageContributingEncoder {

    private final MessageEncoder codeEncoder;
    private final MessageEncoder reasonEncoder;

    public WriteHttpStatusEncoder(MessageEncoder codeEncoder, MessageEncoder reasonEncoder) {
        this.codeEncoder = codeEncoder;
        this.reasonEncoder = reasonEncoder;
    }

    @Override
    public void encode(HttpMessage message) {
        if (message instanceof HttpResponse) {
            HttpResponse response = (HttpResponse) message;
            HttpResponseStatus status = new HttpResponseStatus(Integer.parseInt(codeEncoder.encodeToString()),
                    reasonEncoder.encodeToString());
            response.setStatus(status);
        } else {
            throw new IllegalStateException("Can not write status onto a non-http response object");
        }
    }

    @Override
    public String toString() {
        return String.format("write http status encoder with %s %s", codeEncoder, reasonEncoder);
    }

}
