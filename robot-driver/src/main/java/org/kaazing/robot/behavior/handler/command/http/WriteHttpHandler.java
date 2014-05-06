/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.behavior.handler.command.http;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

import org.kaazing.robot.behavior.handler.codec.http.HttpMessageContributingEncoder;
import org.kaazing.robot.behavior.handler.command.AbstractCommandHandler;

public class WriteHttpHandler extends AbstractCommandHandler {

    private final HttpMessageContributingEncoder httpEncoder;
    private final HttpMessage httpMessage;
    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(WriteHttpHandler.class);

    public WriteHttpHandler(HttpMessage httpMessage, HttpMessageContributingEncoder httpEncoder) {
        this.httpMessage = httpMessage;
        this.httpEncoder = httpEncoder;
    }

    @Override
    protected void invokeCommand(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info(String.format("Invoking write http handler with: %s", httpEncoder));
        httpEncoder.encode(httpMessage);
        getHandlerFuture().setSuccess();
    }


}
