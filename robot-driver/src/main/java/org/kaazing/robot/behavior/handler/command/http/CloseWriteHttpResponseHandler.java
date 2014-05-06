/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.behavior.handler.command.http;

import static org.kaazing.robot.behavior.handler.codec.HttpUtils.END_OF_HTTP_MESSAGE_BUFFER;
import static org.jboss.netty.channel.Channels.write;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

import org.kaazing.robot.behavior.handler.command.AbstractCommandHandler;

public class CloseWriteHttpResponseHandler extends AbstractCommandHandler {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(CloseWriteHttpRequestHandler.class);

    public CloseWriteHttpResponseHandler() {
    }

    @Override
    protected void invokeCommand(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("Invoking close http write response");
        write(ctx, getHandlerFuture(), END_OF_HTTP_MESSAGE_BUFFER);
    }

}
