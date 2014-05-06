/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.behavior.handler.command.http;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

import com.kaazing.robot.behavior.handler.command.AbstractCommandHandler;

public class EndOfWriteHttpHeadersHandler extends AbstractCommandHandler {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(EndOfWriteHttpHeadersHandler.class);
    private HttpMessage httpMessage;

    public EndOfWriteHttpHeadersHandler(HttpMessage httpMessage) {
        this.httpMessage = httpMessage;
    }

    @Override
    protected void invokeCommand(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("Invoking end of write http headers");
        Channels.write(ctx.getChannel(), httpMessage);
        getHandlerFuture().setSuccess();
    }

}
