/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.netty.jmock;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.jboss.netty.channel.MessageEvent;

class MessageEventMessageMatcher extends BaseMatcher<MessageEvent> {

    private final Matcher<Object> expected;

    MessageEventMessageMatcher(Matcher<Object> expected) {
        this.expected = expected;
    }

    @Override
    public boolean matches(Object obj) {
        return (obj instanceof MessageEvent) && matches((MessageEvent) obj);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("message ").appendValue(expected);
    }

    private boolean matches(MessageEvent evt) {
        Object message = evt.getMessage();
        return expected.matches(message);
    }
}
