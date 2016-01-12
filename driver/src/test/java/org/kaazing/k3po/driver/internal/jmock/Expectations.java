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

import java.util.List;

import org.hamcrest.Matcher;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.jmock.lib.action.CustomAction;

public class Expectations extends org.jmock.Expectations {

    public static Matcher<MessageEvent> response(Object expected) {
        return new HttpResponseMatcher(expected);
    }

    public static Matcher<MessageEvent> message(Object expected) {
        return message(equal(expected));
    }

    public static Matcher<MessageEvent> message(Matcher<Object> expected) {
        return new MessageEventMessageMatcher(expected);
    }

    public static Matcher<ChannelStateEvent> channelState(ChannelState expectedState, Object expectedValue) {
        return channelState(same(expectedState), equal(expectedValue));
    }

    public static Matcher<ChannelStateEvent> channelState(ChannelState expected) {
        return channelState(same(expected), any(Object.class));
    }

    public static Matcher<ChannelStateEvent> channelState(Matcher<ChannelState> expectedState,
            Matcher<Object> expectedValue) {
        return new ChannelStateEventChannelStateMatcher(expectedState, expectedValue);
    }

    public static Action callChannelFutureListener(final ChannelFuture future) {
        return new CustomAction("callChannelFutureListener") {

            @Override
            public Object invoke(Invocation invocation) throws Throwable {
                ((ChannelFutureListener) invocation.getParameter(0)).operationComplete(future);
                return null;
            }

        };
    }

    public static <T> Action saveParameter(final int parameter, final List<T> list) {
        return new CustomAction("save") {

            @SuppressWarnings("unchecked")
            @Override
            public Object invoke(Invocation invocation) throws Throwable {
                list.add((T) invocation.getParameter(parameter));
                return null;
            }

        };
    }
}
