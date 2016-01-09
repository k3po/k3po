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
package org.kaazing.k3po.driver.internal.jmock;

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
