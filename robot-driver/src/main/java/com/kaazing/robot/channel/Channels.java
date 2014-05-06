/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.channel;

import static org.jboss.netty.channel.Channels.future;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;

import com.kaazing.robot.behavior.handler.prepare.UpstreamPreparationEvent;

public final class Channels {

    public static ChannelFuture prepare(Channel channel) {
        ChannelPipeline pipeline = channel.getPipeline();
        ChannelFuture future = future(channel);
        pipeline.sendUpstream(new UpstreamPreparationEvent(channel, future));
        return future;
    }

    private Channels() {
        // utility class, no instances
    }
}
