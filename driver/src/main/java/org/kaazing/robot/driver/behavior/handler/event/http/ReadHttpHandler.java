/*
 * Copyright (c) 2014 "Kaazing Corporation," (www.kaazing.com)
 *
 * This file is part of Robot.
 *
 * Robot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.kaazing.robot.driver.behavior.handler.event.http;

import static java.util.EnumSet.of;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.kaazing.robot.driver.behavior.handler.codec.http.HttpMessageContributingDecoder;
import org.kaazing.robot.driver.behavior.handler.event.AbstractEventHandler;

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
