/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.behavior.handler.codec.http;

import java.net.URI;

import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.handler.codec.http.HttpRequest;

import com.kaazing.robot.behavior.handler.codec.MessageEncoder;

public class WriteHttpParameterEncoder implements HttpMessageContributingEncoder {

    private final MessageEncoder keyEncoder;
    private final MessageEncoder valueEncoder;

    public WriteHttpParameterEncoder(MessageEncoder keyEncoder, MessageEncoder valueEncoder) {
        this.keyEncoder = keyEncoder;
        this.valueEncoder = valueEncoder;
    }

    @Override
    public void encode(HttpMessage message) throws Exception {
        if (message instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) message;
            URI oldURI = URI.create(request.getUri());
            String authority = oldURI.getAuthority();
            String fragment = oldURI.getFragment();
            String path = oldURI.getPath();
            String scheme = oldURI.getScheme();
            String query = oldURI.getQuery();
            if (query == null) {
                query = String.format("%s=%s", keyEncoder.encodeToString(), valueEncoder.encodeToString());
            } else {
                query = String.format("%s&%s=%s", query, keyEncoder.encodeToString(), valueEncoder.encodeToString());
            }
            URI newURI = new URI(scheme, authority, path, query, fragment);
            request.setUri(newURI.toString());
        } else {
            throw new IllegalStateException("Can not write method onto a non-http request object");
        }
    }

    @Override
    public String toString() {
        return String.format("write http parameter encoder with %s %s", keyEncoder, valueEncoder);
    }

}
