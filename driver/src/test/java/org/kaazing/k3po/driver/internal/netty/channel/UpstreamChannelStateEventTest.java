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
package org.kaazing.k3po.driver.internal.netty.channel;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.UpstreamChannelStateEvent;

/**
 * Convenience class that simply overRides toString to not print out the channel
 * to facilitate easier testing (no regex's needed, simple string comparison)
 *
 */
public class UpstreamChannelStateEventTest extends UpstreamChannelStateEvent {

    public UpstreamChannelStateEventTest(Channel channel, ChannelState state, Object value) {
        super(channel, state, value);
    }

    /*
     * Copied from UpstreamChannelStateEvent just removing the channel from the
     * string
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(64);
        switch (getState()) {
        case OPEN:
            if (Boolean.TRUE.equals(getValue())) {
                buf.append("OPEN");
            }
            else {
                buf.append("CLOSED");
            }
            break;
        case BOUND:
            if (getValue() != null) {
                buf.append("BOUND: ");
                buf.append(getValue());
            }
            else {
                buf.append("UNBOUND");
            }
            break;
        case CONNECTED:
            if (getValue() != null) {
                buf.append("CONNECTED: ");
                buf.append(getValue());
            }
            else {
                buf.append("DISCONNECTED");
            }
            break;
        case INTEREST_OPS:
            buf.append("INTEREST_CHANGED");
            break;
        default:
            buf.append(getState().name());
            buf.append(": ");
            buf.append(getValue());
        }
        return buf.toString();
    }
}
