/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.behavior.handler.codec;

import org.jboss.netty.buffer.ChannelBuffer;


public interface MessageEncoder {

    ChannelBuffer encode();
    String encodeToString();

}
