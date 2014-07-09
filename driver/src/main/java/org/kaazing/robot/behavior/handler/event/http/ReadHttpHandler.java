/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.behavior.handler.event.http;

import static java.util.EnumSet.of;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.kaazing.robot.behavior.handler.codec.http.HttpMessageContributingDecoder;
import org.kaazing.robot.behavior.handler.event.AbstractEventHandler;

public class ReadHttpHandler extends AbstractEventHandler {

    private final HttpMessageContributingDecoder decoder;
    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(ReadHttpHandler.class);

    public ReadHttpHandler(HttpMessageContributingDecoder decoder) {
        super(of(ChannelEventKind.MESSAGE));
        this.decoder = decoder;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        LOGGER.debug(String.format("Message Received with: %s", this.toString()));
        Object message = e.getMessage();
        if (message instanceof HttpMessage) {

            HttpMessage httpMessage = (HttpMessage) message;
            ChannelFuture handlerFuture = getHandlerFuture();
            assert handlerFuture != null;

            try {
                decoder.decode(httpMessage);
                handlerFuture.setSuccess();
            } catch (Exception mme) {
                handlerFuture.setFailure(mme);
            }
            super.messageReceived(ctx, e);
        }
    }

    @Override
    public String toString() {
        return String.format("read http handler with: %s", decoder);
    }
}
