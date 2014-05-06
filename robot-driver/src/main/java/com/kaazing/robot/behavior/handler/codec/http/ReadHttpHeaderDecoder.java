/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.behavior.handler.codec.http;

import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.jboss.netty.util.CharsetUtil.UTF_8;

import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

import com.kaazing.robot.behavior.handler.codec.MessageDecoder;
import com.kaazing.robot.behavior.handler.codec.MessageMismatchException;

public class ReadHttpHeaderDecoder implements HttpMessageContributingDecoder {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(ReadHttpHeaderDecoder.class);
    private String name;
    private MessageDecoder valueDecoder;

    public ReadHttpHeaderDecoder(String name, MessageDecoder valueDecoder) {
        this.name = name;
        this.valueDecoder = valueDecoder;
    }

    /**
     * Will remove the first matching header, ie. if there are multiple headers with same key, it will remove the first
     * one whose value also matches the expected value
     */
    @Override
    public void decode(HttpMessage message) throws Exception {
        List<String> headerValues = message.getHeaders(name);
        if (headerValues.isEmpty()) {
            new MessageMismatchException("Could not match non-existent header", name, null);
        }

        int firstMatchingHeader = -1;
        MessageMismatchException lastException = null;

        for (int i = 0; i < headerValues.size(); i++) {
            try {
                String currentHeaderValue = headerValues.get(i);
                ChannelBuffer copiedBuffer = copiedBuffer(currentHeaderValue, UTF_8);
                valueDecoder.decode(copiedBuffer);
                if (firstMatchingHeader > -1) {
                    LOGGER.warn(String.format(
                            "Multiple matching headers for read header %s, will remove first matching header", name));
                    // no need to throw this exception multiple times
                    break;
                } else {
                    firstMatchingHeader = i;
                }
            } catch (MessageMismatchException mme) {
                lastException = mme;
            }
        }

        if (firstMatchingHeader == -1) {
            assert lastException != null;
            throw lastException;
        }

        for (int i = 0; i < headerValues.size(); i++) {
            message.removeHeader(name);
        }

        for (int i = 0; i < headerValues.size(); i++) {
            if (i != firstMatchingHeader) {
                message.addHeader(name, headerValues.get(i));
            }
        }

        message.removeHeader(name);
    }

    @Override
    public String toString() {
        return String.format("read http header decoder with: %s, %s", name, valueDecoder);
    }
}
