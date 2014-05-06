/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.behavior.handler.event.http;

import static java.util.EnumSet.of;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpMessage;

import com.kaazing.robot.behavior.handler.event.AbstractEventHandler;

public class EndOfReadHttpHeadersHandler extends AbstractEventHandler {

    public EndOfReadHttpHeadersHandler() {
        super(of(ChannelEventKind.MESSAGE));
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        Object message = e.getMessage();
        assert message instanceof HttpMessage;

        ChannelFuture handlerFuture = getHandlerFuture();
        assert handlerFuture != null;

        handlerFuture.setSuccess();
    }
}
