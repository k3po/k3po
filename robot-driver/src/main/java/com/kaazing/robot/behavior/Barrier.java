/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.behavior;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.Channels;

public class Barrier {

    private final ChannelFuture future;

    public Barrier() {
        future = Channels.future(null);
    }

    public ChannelFuture getFuture() {
        return future;
    }
}
