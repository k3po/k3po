/**
 * Copyright 2007-2015, Kaazing Corporation. All rights reserved.
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
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.kaazing.k3po.driver.internal.control.ControlMessage;
import org.kaazing.k3po.driver.internal.control.ErrorMessage;
import org.kaazing.k3po.lang.internal.parser.ScriptParseException;

public class ControlUpstreamHandler extends SimpleChannelUpstreamHandler {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(ControlUpstreamHandler.class);

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent evt) throws Exception {
        ControlMessage message = (ControlMessage) evt.getMessage();

        try {
            switch (message.getKind()) {
            case PREPARE:
                prepareReceived(ctx, evt);
                break;
            case START:
                startReceived(ctx, evt);
                break;
            case ABORT:
                abortReceived(ctx, evt);
                break;
            case NOTIFY:
                notifyReceived(ctx, evt);
                break;
            case AWAIT:
                awaitReceived(ctx, evt);
                break;
            case CLOSE:
                closeReceived(ctx, evt);
                break;
            default:
                throw new IllegalArgumentException(String.format("Unexpected control message: %s", message.getKind()));
            }
        } catch (Exception ex) {
            sendErrorMessage(ctx, ex);
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

    public void closeReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
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

    protected void sendErrorMessage(ChannelHandlerContext ctx, Throwable throwable) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setDescription(throwable.getMessage());

        if (throwable instanceof ScriptParseException) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error("Caught exception trying to parse script. Sending error to client", throwable);
            } else {
                LOGGER.error("Caught exception trying to parse script. Sending error to client. Due to " + throwable);
            }
            errorMessage.setSummary("Parse Error");
            Channels.write(ctx, Channels.future(null), errorMessage);
        } else {
            LOGGER.error("Internal error. Sending error to client", throwable);
            errorMessage.setSummary("Internal error");
            Channels.write(ctx, Channels.future(null), errorMessage);
        }
    }
    
    protected void sendErrorMessage(ChannelHandlerContext ctx, String description) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setSummary("Internal error");
        errorMessage.setDescription(description);
        if (LOGGER.isDebugEnabled())
            LOGGER.error("Sending error to client:" + description);
        Channels.write(ctx, Channels.future(null), errorMessage);
    }
}
