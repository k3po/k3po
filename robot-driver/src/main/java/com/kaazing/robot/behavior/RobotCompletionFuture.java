/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.behavior;

import org.jboss.netty.channel.ChannelFuture;

public interface RobotCompletionFuture extends ChannelFuture {
    String getObservedScript();
}
