/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.behavior.handler.codec.http;

import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.jboss.netty.util.CharsetUtil.UTF_8;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;

import com.kaazing.robot.behavior.handler.codec.MessageDecoder;
import com.kaazing.robot.behavior.handler.codec.MessageMismatchException;

public class ReadHttpMethodDecoder implements HttpMessageContributingDecoder {

    private MessageDecoder methodValueDecoder;

    public ReadHttpMethodDecoder(MessageDecoder methodValueDecoder) {
        this.methodValueDecoder = methodValueDecoder;
    }

    @Override
    public void decode(HttpMessage message) throws Exception {
        HttpRequest request = null;
        if (message instanceof HttpRequest) {
            request = (HttpRequest) message;
        } else {
            throw new MessageMismatchException("Can not match a request method on a http response", methodValueDecoder,
                    null);
        }
        HttpMethod method = request.getMethod();
        ChannelBuffer buffer = copiedBuffer(method.getName(), UTF_8);
        methodValueDecoder.decode(buffer);
    }

    @Override
    public String toString() {
        return String.format("read http method decoder with: %s", methodValueDecoder);
    }

}
