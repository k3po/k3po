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

package org.kaazing.robot.behavior.handler.event;

import static org.kaazing.robot.behavior.handler.event.AbstractEventHandler.ChannelEventKind.BOUND;
import static org.kaazing.robot.behavior.handler.event.AbstractEventHandler.ChannelEventKind.CHILD_CLOSED;
import static org.kaazing.robot.behavior.handler.event.AbstractEventHandler.ChannelEventKind.CHILD_OPEN;
import static org.kaazing.robot.behavior.handler.event.AbstractEventHandler.ChannelEventKind.CLOSED;
import static org.kaazing.robot.behavior.handler.event.AbstractEventHandler.ChannelEventKind.CONNECTED;
import static org.kaazing.robot.behavior.handler.event.AbstractEventHandler.ChannelEventKind.DISCONNECTED;
import static org.kaazing.robot.behavior.handler.event.AbstractEventHandler.ChannelEventKind.EXCEPTION;
import static org.kaazing.robot.behavior.handler.event.AbstractEventHandler.ChannelEventKind.IDLE_STATE;
import static org.kaazing.robot.behavior.handler.event.AbstractEventHandler.ChannelEventKind.INTEREST_OPS;
import static org.kaazing.robot.behavior.handler.event.AbstractEventHandler.ChannelEventKind.MESSAGE;
import static org.kaazing.robot.behavior.handler.event.AbstractEventHandler.ChannelEventKind.OPEN;
import static org.kaazing.robot.behavior.handler.event.AbstractEventHandler.ChannelEventKind.UNBOUND;
import static org.kaazing.robot.behavior.handler.event.AbstractEventHandler.ChannelEventKind.UNKNOWN;
import static org.kaazing.robot.behavior.handler.event.AbstractEventHandler.ChannelEventKind.WRITE_COMPLETED;
import static java.lang.Boolean.TRUE;
import static java.util.EnumSet.complementOf;
import static java.util.EnumSet.of;

import java.util.Set;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ChildChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.WriteCompletionEvent;
import org.jboss.netty.handler.timeout.IdleStateEvent;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

import org.kaazing.robot.behavior.handler.ExecutionHandler;

public abstract class AbstractEventHandler extends ExecutionHandler {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(AbstractEventHandler.class);

    public static enum ChannelEventKind {
        CHILD_OPEN, CHILD_CLOSED, OPEN, BOUND, CONNECTED, MESSAGE, WRITE_COMPLETED, DISCONNECTED, UNBOUND, CLOSED, EXCEPTION,
        INTEREST_OPS, IDLE_STATE, UNKNOWN
    };

    private final Set<ChannelEventKind> interestEvents;
    private final Set<ChannelEventKind> expectedEvents;

    protected AbstractEventHandler(Set<ChannelEventKind> expectedEvents) {
        this(complementOf(of(CHILD_OPEN, CHILD_CLOSED, WRITE_COMPLETED, INTEREST_OPS, EXCEPTION, IDLE_STATE, UNKNOWN)),
                expectedEvents);
    }

    protected AbstractEventHandler(Set<ChannelEventKind> interestEvents, Set<ChannelEventKind> expectedEvents) {
        this.interestEvents = interestEvents;
        this.expectedEvents = expectedEvents;
    }

    @Override
    protected void handleUpstream1(ChannelHandlerContext ctx, ChannelEvent evt) throws Exception {

        ChannelEventKind eventAsKind = asEventKind(evt);
        ChannelFuture handlerFuture = getHandlerFuture();

        boolean isLogDebugEnabled = LOGGER.isDebugEnabled();

        if (isLogDebugEnabled) {
            LOGGER.debug("handleUpstream1 event " + eventAsKind);
        }

        assert handlerFuture != null;
        if (handlerFuture.isDone() || !interestEvents.contains(eventAsKind)) {
            // Skip events not deemed interesting, such as write
            // completion events

            if (isLogDebugEnabled) {
                LOGGER.debug(eventAsKind + "event not interesting send upstream");
            }

            ctx.sendUpstream(evt);

        } else if (!expectedEvents.contains(eventAsKind)) {
            handleUnexpectedEvent(ctx, evt);
        } else {
            ChannelFuture pipelineFuture = getPipelineFuture();
            if (!pipelineFuture.isSuccess()) {
                LOGGER.error(getClass()
                        + String.format(
                                "  future is not success. setting handler future to failure done(%s), cannceled(%s), cause(%s)",
                                pipelineFuture.isDone(), pipelineFuture.isCancelled(), pipelineFuture.getCause()));

                handlerFuture.setFailure(new ChannelException("Expected event arrived too early").fillInStackTrace());

            } else {
                if (isLogDebugEnabled) {
                    LOGGER.debug(getClass() + " handler's pipelinefuture is a success. Sending event " + eventAsKind
                            + " to handler");
                }
                super.handleUpstream1(ctx, evt);
            }
        }
    }

    protected void handleUnexpectedEvent(ChannelHandlerContext ctx, ChannelEvent evt) throws Exception {
        ChannelEventKind eventAsKind = asEventKind(evt);

        // Treat interesting but unexpected events as failure
        String message = String.format("Unexpected event |%s| for handler %s", eventAsKind, getClass());
        LOGGER.error(message);
        switch (eventAsKind) {
        case EXCEPTION:
            Throwable cause = ((ExceptionEvent) evt).getCause();
            getHandlerFuture().setFailure(new ChannelException(message, cause).fillInStackTrace());
            break;
        default:
            getHandlerFuture().setFailure(new ChannelException(message).fillInStackTrace());
            break;
        }
    }

    private static ChannelEventKind asEventKind(ChannelEvent evt) {
        if (evt instanceof MessageEvent) {
            return MESSAGE;
        }

        if (evt instanceof WriteCompletionEvent) {
            return WRITE_COMPLETED;
        }

        if (evt instanceof ChannelStateEvent) {
            ChannelStateEvent cse = (ChannelStateEvent) evt;
            Object value = cse.getValue();
            switch (cse.getState()) {
            case OPEN:
                return TRUE.equals(value) ? OPEN : CLOSED;

            case BOUND:
                return value != null ? BOUND : UNBOUND;

            case CONNECTED:
                return value != null ? CONNECTED : DISCONNECTED;

            case INTEREST_OPS:
                return INTEREST_OPS;
            }
        }

        if (evt instanceof ChildChannelStateEvent) {
            ChildChannelStateEvent ccse = (ChildChannelStateEvent) evt;
            Channel child = ccse.getChildChannel();
            return child.isOpen() ? CHILD_OPEN : CHILD_CLOSED;
        }

        if (evt instanceof ExceptionEvent) {
            return EXCEPTION;
        }

        if (evt instanceof IdleStateEvent) {
            return IDLE_STATE;
        }

        return UNKNOWN;
    }
}
