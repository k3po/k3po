/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.behavior.handler.codec;

import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;

import java.nio.charset.Charset;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

public class WriteTextEncoder implements MessageEncoder {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(WriteTextEncoder.class);

    private final String text;
    private final Charset charset;

    public WriteTextEncoder(String text, Charset charset) {
        this.text = text;
        this.charset = charset;
    }

    @Override
    public ChannelBuffer encode() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("encode text: " + text);
        }
        return copiedBuffer(text, charset);
    }

    @Override
    public String encodeToString() {
        return text;
    }
}
