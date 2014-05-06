/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.behavior.handler;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;

public class TestChannelEvent implements ChannelEvent {

    private final Channel channel;
    private final ChannelFuture future;

    public TestChannelEvent(Channel channel, ChannelFuture future) {
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
}
