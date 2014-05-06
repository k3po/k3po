/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.netty.jmock;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.ChannelStateEvent;

class ChannelStateEventChannelStateMatcher extends BaseMatcher<ChannelStateEvent> {

    private final Matcher<ChannelState> expectedState;
    private final Matcher<Object> expectedValue;

    ChannelStateEventChannelStateMatcher(Matcher<ChannelState> expectedState, Matcher<Object> expectedValue) {
        this.expectedState = expectedState;
        this.expectedValue = expectedValue;
    }

    @Override
    public boolean matches(Object obj) {
        return (obj instanceof ChannelStateEvent) && matches((ChannelStateEvent) obj);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("channelState ").appendValue(expectedState);
        description.appendText("[value ").appendValue(expectedValue).appendText("]");
    }

    private boolean matches(ChannelStateEvent evt) {
        ChannelState state = evt.getState();
        Object value = evt.getValue();
        return expectedState.matches(state) && expectedValue.matches(value);
    }
}
