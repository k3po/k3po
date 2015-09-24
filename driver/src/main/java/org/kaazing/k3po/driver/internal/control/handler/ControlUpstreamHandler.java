/*
 * Copyright 2014, Kaazing Corporation. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kaazing.k3po.driver.internal.control.handler;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.kaazing.k3po.driver.internal.control.ControlMessage;

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
        case NOTIFY:
            notifyReceived(ctx, e);
            break;
        case AWAIT:
            awaitReceived(ctx, e);
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

    public void notifyReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        super.messageReceived(ctx, e);
    }

    public void awaitReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
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
