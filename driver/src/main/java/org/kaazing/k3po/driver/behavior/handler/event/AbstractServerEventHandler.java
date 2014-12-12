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

import static org.kaazing.k3po.driver.behavior.handler.event.AbstractEventHandler.ChannelEventKind.BOUND;
import static org.kaazing.k3po.driver.behavior.handler.event.AbstractEventHandler.ChannelEventKind.CHILD_CLOSED;
import static org.kaazing.k3po.driver.behavior.handler.event.AbstractEventHandler.ChannelEventKind.CHILD_OPEN;
import static org.kaazing.k3po.driver.behavior.handler.event.AbstractEventHandler.ChannelEventKind.CLOSED;
import static org.kaazing.k3po.driver.behavior.handler.event.AbstractEventHandler.ChannelEventKind.OPEN;
import static org.kaazing.k3po.driver.behavior.handler.event.AbstractEventHandler.ChannelEventKind.UNBOUND;
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
