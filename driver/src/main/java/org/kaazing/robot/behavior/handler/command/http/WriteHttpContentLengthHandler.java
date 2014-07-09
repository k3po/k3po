/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.behavior.handler.command.http;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMessage;

import org.kaazing.robot.behavior.handler.command.AbstractCommandHandler;

public class WriteHttpContentLengthHandler extends AbstractCommandHandler {

    private HttpMessage httpMessage;

    public WriteHttpContentLengthHandler(HttpMessage httpMessage) {
        this.httpMessage = httpMessage;
    }

    @Override
    protected void invokeCommand(ChannelHandlerContext ctx) throws Exception {
        HttpHeaders.setContentLength(httpMessage, 0);
        getHandlerFuture().setSuccess();
    }
}
