/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.behavior.handler.codec.http;

import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.jboss.netty.util.CharsetUtil.UTF_8;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

import org.kaazing.robot.behavior.handler.codec.MessageDecoder;
import org.kaazing.robot.behavior.handler.codec.MessageMismatchException;

public class ReadHttpStatusDecoder implements HttpMessageContributingDecoder {

    private final MessageDecoder codeDecoder;
    private final MessageDecoder reasonDecoder;

    public ReadHttpStatusDecoder(MessageDecoder codeDecoder, MessageDecoder reasonDecoder) {
        this.codeDecoder = codeDecoder;
        this.reasonDecoder = reasonDecoder;
    }

    @Override
    public void decode(HttpMessage message) throws Exception {
        HttpResponse response;
        if (message instanceof HttpResponse) {
            response = (HttpResponse) message;

        } else {
            throw new MessageMismatchException("Can not match a http status on a http request", codeDecoder + ","
                    + reasonDecoder, null);
        }

        HttpResponseStatus status = response.getStatus();
        String code = Integer.toString(status.getCode());
        String reason = status.getReasonPhrase();

        ChannelBuffer buffer = copiedBuffer(code, UTF_8);
        codeDecoder.decode(buffer);
        buffer = copiedBuffer(reason, UTF_8);
        reasonDecoder.decode(buffer);
    }

    @Override
    public String toString() {
        return String.format("read http status decoder with: %s, %s", codeDecoder, reasonDecoder);
    }
}
