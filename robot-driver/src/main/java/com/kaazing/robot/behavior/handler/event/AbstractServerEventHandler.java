/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.behavior.handler.event;

import static com.kaazing.robot.behavior.handler.event.AbstractEventHandler.ChannelEventKind.BOUND;
import static com.kaazing.robot.behavior.handler.event.AbstractEventHandler.ChannelEventKind.CHILD_CLOSED;
import static com.kaazing.robot.behavior.handler.event.AbstractEventHandler.ChannelEventKind.CHILD_OPEN;
import static com.kaazing.robot.behavior.handler.event.AbstractEventHandler.ChannelEventKind.CLOSED;
import static com.kaazing.robot.behavior.handler.event.AbstractEventHandler.ChannelEventKind.OPEN;
import static com.kaazing.robot.behavior.handler.event.AbstractEventHandler.ChannelEventKind.UNBOUND;
import static java.util.EnumSet.of;

import java.util.Set;

public abstract class AbstractServerEventHandler extends AbstractEventHandler {

    public AbstractServerEventHandler(Set<ChannelEventKind> expectedEvents) {
        super(of(OPEN, BOUND, CHILD_OPEN, CHILD_CLOSED, UNBOUND, CLOSED), expectedEvents);
    }

    public AbstractServerEventHandler(Set<ChannelEventKind> interestingEvents, Set<ChannelEventKind> expectedEvents) {
        super(interestingEvents, expectedEvents);
    }
}
