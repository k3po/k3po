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

package org.kaazing.robot.driver.netty.channel.http;

import static java.util.Objects.requireNonNull;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;

final class DownstreamHttpCompleteContentEvent implements HttpContentCompleteEvent {

    private final Channel channel;
    private final ChannelFuture future;

    DownstreamHttpCompleteContentEvent(
            Channel channel,
            ChannelFuture future) {
        requireNonNull(channel);
        requireNonNull(future);
        this.channel = channel;
        this.future = future;
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public ChannelFuture getFuture() {
        return future;
    }

    @Override
    public String toString() {
        String channelString = getChannel().toString();
        StringBuilder buf = new StringBuilder(channelString.length() + 64);
        buf.append(channelString);
        buf.append(" COMPLETE CONTENT REQUEST");
        return buf.toString();
    }

}
