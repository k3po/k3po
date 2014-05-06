/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.netty.jmock;

import org.hamcrest.Matcher;
import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;

// TODO: move to top-level netty.jmock project
public class Expectations extends org.jmock.Expectations {

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

    public static final Matcher<ChannelStateEvent> channelState(Matcher<ChannelState> expectedState,
            Matcher<Object> expectedValue) {
        return new ChannelStateEventChannelStateMatcher(expectedState, expectedValue);
    }
}
