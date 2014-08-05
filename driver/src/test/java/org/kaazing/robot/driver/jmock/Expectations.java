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

package org.kaazing.robot.driver.jmock;

import org.hamcrest.Matcher;
import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;

public class Expectations extends org.jmock.Expectations {

    public static final Matcher<MessageEvent> response(Object expected) {
        return new HttpResponseMatcher(expected);
    }

    public static final Matcher<MessageEvent> message(Object expected) {
        return message(equal(expected));
    }

    public static final Matcher<MessageEvent> message(Matcher<Object> expected) {
        return new MessageEventMessageMatcher(expected);
    }

    public static final Matcher<ChannelStateEvent> channelState(ChannelState expectedState, Object expectedValue) {
        return channelState(same(expectedState), equal(expectedValue));
    }

    public static final Matcher<ChannelStateEvent> channelState(ChannelState expected) {
        return channelState(same(expected), any(Object.class));
    }

    public static final Matcher<ChannelStateEvent> channelState(Matcher<ChannelState> expectedState, Matcher<Object> expectedValue) {
        return new ChannelStateEventChannelStateMatcher(expectedState, expectedValue);
    }
}
