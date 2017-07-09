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

import static java.util.Objects.requireNonNull;
import static org.jboss.netty.channel.Channels.succeededFuture;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;

final class UpstreamReadAbortEvent implements ReadAbortEvent {

    private final Channel channel;

    UpstreamReadAbortEvent(
            Channel channel) {
        requireNonNull(channel);
        this.channel = channel;
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public ChannelFuture getFuture() {
        return succeededFuture(channel);
    }

    @Override
    public String toString() {
        String channelString = getChannel().toString();
        StringBuilder buf = new StringBuilder(channelString.length() + 64);
        buf.append(channelString);
        buf.append(" READ_ABORTED");
        return buf.toString();
    }

}
