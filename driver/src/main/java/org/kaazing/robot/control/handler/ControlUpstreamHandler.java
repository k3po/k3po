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

package org.kaazing.robot.control.handler;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

import org.kaazing.robot.control.ControlMessage;

public class ControlUpstreamHandler extends SimpleChannelUpstreamHandler {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(ControlUpstreamHandler.class);

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        ControlMessage message = (ControlMessage) e.getMessage();

        switch (message.getKind()) {
        case PREPARE:
            prepareReceived(ctx, e);
            break;
        case START:
            startReceived(ctx, e);
            break;
        case ABORT:
            abortReceived(ctx, e);
            break;
        default:
            throw new IllegalArgumentException(String.format("Unexpected control message: %s", message.getKind()));
        }

    }

    public void prepareReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        super.messageReceived(ctx, e);
    }

    public void startReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        super.messageReceived(ctx, e);
    }

    public void abortReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        super.messageReceived(ctx, e);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        String msg = "Control channel caught exception event: ";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(msg, e.getCause());
        } else {
            LOGGER.info(msg + e.getCause());
        }
    }

}
