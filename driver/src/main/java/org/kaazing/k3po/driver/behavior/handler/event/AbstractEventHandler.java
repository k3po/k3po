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

package org.kaazing.k3po.driver.behavior.handler.event;

import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static java.util.EnumSet.complementOf;
import static java.util.EnumSet.of;
import static org.kaazing.k3po.driver.behavior.handler.event.AbstractEventHandler.ChannelEventKind.BOUND;
import static org.kaazing.k3po.driver.behavior.handler.event.AbstractEventHandler.ChannelEventKind.CHILD_CLOSED;
import static org.kaazing.k3po.driver.behavior.handler.event.AbstractEventHandler.ChannelEventKind.CHILD_OPEN;
import static org.kaazing.k3po.driver.behavior.handler.event.AbstractEventHandler.ChannelEventKind.CLOSED;
import static org.kaazing.k3po.driver.behavior.handler.event.AbstractEventHandler.ChannelEventKind.CONNECTED;
import static org.kaazing.k3po.driver.behavior.handler.event.AbstractEventHandler.ChannelEventKind.DISCONNECTED;
import static org.kaazing.k3po.driver.behavior.handler.event.AbstractEventHandler.ChannelEventKind.EXCEPTION;
import static org.kaazing.k3po.driver.behavior.handler.event.AbstractEventHandler.ChannelEventKind.FLUSHED;
import static org.kaazing.k3po.driver.behavior.handler.event.AbstractEventHandler.ChannelEventKind.IDLE_STATE;
import static org.kaazing.k3po.driver.behavior.handler.event.AbstractEventHandler.ChannelEventKind.INPUT_SHUTDOWN;
import static org.kaazing.k3po.driver.behavior.handler.event.AbstractEventHandler.ChannelEventKind.INTEREST_OPS;
import static org.kaazing.k3po.driver.behavior.handler.event.AbstractEventHandler.ChannelEventKind.MESSAGE;
import static org.kaazing.k3po.driver.behavior.handler.event.AbstractEventHandler.ChannelEventKind.OPEN;
import static org.kaazing.k3po.driver.behavior.handler.event.AbstractEventHandler.ChannelEventKind.OUTPUT_SHUTDOWN;
import static org.kaazing.k3po.driver.behavior.handler.event.AbstractEventHandler.ChannelEventKind.UNBOUND;
import static org.kaazing.k3po.driver.behavior.handler.event.AbstractEventHandler.ChannelEventKind.UNKNOWN;
import static org.kaazing.k3po.driver.behavior.handler.event.AbstractEventHandler.ChannelEventKind.WRITE_COMPLETED;

import java.util.EnumSet;
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
import org.kaazing.k3po.driver.behavior.ScriptProgressException;
import org.kaazing.k3po.driver.behavior.handler.ExecutionHandler;
import org.kaazing.k3po.driver.netty.channel.FlushEvent;
import org.kaazing.k3po.driver.netty.channel.ShutdownInputEvent;
import org.kaazing.k3po.driver.netty.channel.ShutdownOutputEvent;

public abstract class AbstractEventHandler extends ExecutionHandler {

    protected static final EnumSet<ChannelEventKind> DEFAULT_INTERESTED_EVENTS =
            complementOf(of(CHILD_OPEN, CHILD_CLOSED, WRITE_COMPLETED, INTEREST_OPS, EXCEPTION, IDLE_STATE,
                            OUTPUT_SHUTDOWN, UNKNOWN));

    public static enum ChannelEventKind {
        CHILD_OPEN, CHILD_CLOSED, OPEN, BOUND, CONNECTED, MESSAGE, WRITE_COMPLETED, DISCONNECTED, UNBOUND, CLOSED, EXCEPTION,
        INTEREST_OPS, IDLE_STATE, INPUT_SHUTDOWN, OUTPUT_SHUTDOWN, FLUSHED, UNKNOWN
    };

    private final Set<ChannelEventKind> interestEvents;
    private final Set<ChannelEventKind> expectedEvents;

    protected AbstractEventHandler(Set<ChannelEventKind> expectedEvents) {
        this(DEFAULT_INTERESTED_EVENTS,
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

        assert handlerFuture != null;
        if (handlerFuture.isDone() || !interestEvents.contains(eventAsKind)) {
            // Skip events not deemed interesting, such as write
            // completion events

            ctx.sendUpstream(evt);

        } else if (!expectedEvents.contains(eventAsKind)) {
            handleUnexpectedEvent(ctx, evt);
        } else {
            ChannelFuture pipelineFuture = getPipelineFuture();
            if (!pipelineFuture.isSuccess()) {
                // expected event arrived too early
                Exception exception = new ScriptProgressException(getRegionInfo(), format("%s", this));
                handlerFuture.setFailure(exception.fillInStackTrace());
            } else {
                super.handleUpstream1(ctx, evt);
            }
        }
    }

    protected void handleUnexpectedEvent(ChannelHandlerContext ctx, ChannelEvent evt) throws Exception {
        ChannelEventKind eventAsKind = asEventKind(evt);

        // Treat interesting but unexpected events as failure
        try {
            switch (eventAsKind) {
            case OPEN:
                throw new ScriptProgressException(getRegionInfo(), "opened");
            case BOUND:
                throw new ScriptProgressException(getRegionInfo(), "bound");
            case CONNECTED:
                throw new ScriptProgressException(getRegionInfo(), "connected");
            case DISCONNECTED:
                throw new ScriptProgressException(getRegionInfo(), "disconnected");
            case UNBOUND:
                throw new ScriptProgressException(getRegionInfo(), "unbound");
            case CLOSED:
                throw new ScriptProgressException(getRegionInfo(), "closed");
            case FLUSHED:
                throw new ScriptProgressException(getRegionInfo(), "flushed");
            case MESSAGE:
                throw new ScriptProgressException(getRegionInfo(), "read ...");
            case INPUT_SHUTDOWN:
                throw new ScriptProgressException(getRegionInfo(), "read closed");
            case OUTPUT_SHUTDOWN:
                throw new ScriptProgressException(getRegionInfo(), "write closed");
            case CHILD_OPEN:
                throw new ScriptProgressException(getRegionInfo(), "child opened");
            case CHILD_CLOSED:
                throw new ScriptProgressException(getRegionInfo(), "child closed");
            case EXCEPTION:
            default:
                String message = format("Unexpected event |%s| for handler %s", eventAsKind, getClass());
                ChannelException exception = new ChannelException(message);
                exception.fillInStackTrace();
                if (evt instanceof ExceptionEvent) {
                    Throwable cause = ((ExceptionEvent) evt).getCause();
                    exception.initCause(cause);
                }
                getHandlerFuture().setFailure(exception);
                break;
            }
        }
        catch (ScriptProgressException e) {
            getHandlerFuture().setFailure(e);
        }
    }

    private static ChannelEventKind asEventKind(ChannelEvent evt) {
        if (evt instanceof ShutdownInputEvent) {
            return INPUT_SHUTDOWN;
        }

        if (evt instanceof ShutdownOutputEvent) {
            return OUTPUT_SHUTDOWN;
        }

        if (evt instanceof FlushEvent) {
            return FLUSHED;
        }

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
