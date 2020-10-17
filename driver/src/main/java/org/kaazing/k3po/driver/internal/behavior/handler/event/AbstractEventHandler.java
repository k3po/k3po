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
package org.kaazing.k3po.driver.internal.behavior.handler.event;

import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static java.util.EnumSet.complementOf;
import static java.util.EnumSet.of;
import static org.kaazing.k3po.driver.internal.behavior.handler.event.AbstractEventHandler.ChannelEventKind.BOUND;
import static org.kaazing.k3po.driver.internal.behavior.handler.event.AbstractEventHandler.ChannelEventKind.CHILD_CLOSED;
import static org.kaazing.k3po.driver.internal.behavior.handler.event.AbstractEventHandler.ChannelEventKind.CHILD_OPEN;
import static org.kaazing.k3po.driver.internal.behavior.handler.event.AbstractEventHandler.ChannelEventKind.CLOSED;
import static org.kaazing.k3po.driver.internal.behavior.handler.event.AbstractEventHandler.ChannelEventKind.CONNECTED;
import static org.kaazing.k3po.driver.internal.behavior.handler.event.AbstractEventHandler.ChannelEventKind.DISCONNECTED;
import static org.kaazing.k3po.driver.internal.behavior.handler.event.AbstractEventHandler.ChannelEventKind.EXCEPTION;
import static org.kaazing.k3po.driver.internal.behavior.handler.event.AbstractEventHandler.ChannelEventKind.FLUSHED;
import static org.kaazing.k3po.driver.internal.behavior.handler.event.AbstractEventHandler.ChannelEventKind.IDLE_STATE;
import static org.kaazing.k3po.driver.internal.behavior.handler.event.AbstractEventHandler.ChannelEventKind.INPUT_SHUTDOWN;
import static org.kaazing.k3po.driver.internal.behavior.handler.event.AbstractEventHandler.ChannelEventKind.INTEREST_OPS;
import static org.kaazing.k3po.driver.internal.behavior.handler.event.AbstractEventHandler.ChannelEventKind.MESSAGE;
import static org.kaazing.k3po.driver.internal.behavior.handler.event.AbstractEventHandler.ChannelEventKind.OPEN;
import static org.kaazing.k3po.driver.internal.behavior.handler.event.AbstractEventHandler.ChannelEventKind.OUTPUT_SHUTDOWN;
import static org.kaazing.k3po.driver.internal.behavior.handler.event.AbstractEventHandler.ChannelEventKind.READ_ABORTED;
import static org.kaazing.k3po.driver.internal.behavior.handler.event.AbstractEventHandler.ChannelEventKind.READ_ADVISED;
import static org.kaazing.k3po.driver.internal.behavior.handler.event.AbstractEventHandler.ChannelEventKind.UNBOUND;
import static org.kaazing.k3po.driver.internal.behavior.handler.event.AbstractEventHandler.ChannelEventKind.UNKNOWN;
import static org.kaazing.k3po.driver.internal.behavior.handler.event.AbstractEventHandler.ChannelEventKind.WRITE_ABORTED;
import static org.kaazing.k3po.driver.internal.behavior.handler.event.AbstractEventHandler.ChannelEventKind.WRITE_ADVISED;
import static org.kaazing.k3po.driver.internal.behavior.handler.event.AbstractEventHandler.ChannelEventKind.WRITE_COMPLETED;

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
import org.kaazing.k3po.driver.internal.behavior.ScriptProgressException;
import org.kaazing.k3po.driver.internal.behavior.handler.ExecutionHandler;
import org.kaazing.k3po.driver.internal.netty.channel.FlushEvent;
import org.kaazing.k3po.driver.internal.netty.channel.ReadAbortEvent;
import org.kaazing.k3po.driver.internal.netty.channel.ReadAdviseEvent;
import org.kaazing.k3po.driver.internal.netty.channel.ShutdownInputEvent;
import org.kaazing.k3po.driver.internal.netty.channel.ShutdownOutputEvent;
import org.kaazing.k3po.driver.internal.netty.channel.WriteAbortEvent;
import org.kaazing.k3po.driver.internal.netty.channel.WriteAdviseEvent;

public abstract class AbstractEventHandler extends ExecutionHandler {

    protected static final EnumSet<ChannelEventKind> DEFAULT_INTERESTED_EVENTS =
            complementOf(of(CHILD_OPEN, CHILD_CLOSED, WRITE_COMPLETED, INTEREST_OPS, EXCEPTION, IDLE_STATE,
                            OUTPUT_SHUTDOWN, FLUSHED, UNKNOWN));

    public enum ChannelEventKind {
        CHILD_OPEN, CHILD_CLOSED, OPEN, BOUND, CONNECTED, MESSAGE, WRITE_COMPLETED, DISCONNECTED, UNBOUND, CLOSED, EXCEPTION,
        INTEREST_OPS, IDLE_STATE, INPUT_SHUTDOWN, OUTPUT_SHUTDOWN, FLUSHED, UNKNOWN, READ_ABORTED, WRITE_ABORTED,
        READ_ADVISED, WRITE_ADVISED
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
            super.handleUpstream1(ctx, evt);
        }
    }

    protected void handleUnexpectedEvent(ChannelHandlerContext ctx, ChannelEvent evt) {
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
            case READ_ABORTED:
                throw new ScriptProgressException(getRegionInfo(), "read aborted");
            case WRITE_ABORTED:
                throw new ScriptProgressException(getRegionInfo(), "write aborted");
            case READ_ADVISED:
                throw new ScriptProgressException(getRegionInfo(), "read advised ...");
            case WRITE_ADVISED:
                throw new ScriptProgressException(getRegionInfo(), "write advised ...");
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
        if (evt instanceof ReadAdviseEvent) {
            return READ_ADVISED;
        }

        if (evt instanceof WriteAdviseEvent) {
            return WRITE_ADVISED;
        }

        if (evt instanceof ReadAbortEvent) {
            return READ_ABORTED;
        }

        if (evt instanceof WriteAbortEvent) {
            return WRITE_ABORTED;
        }

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
