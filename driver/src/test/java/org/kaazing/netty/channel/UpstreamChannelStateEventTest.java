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

package org.kaazing.netty.channel;

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
