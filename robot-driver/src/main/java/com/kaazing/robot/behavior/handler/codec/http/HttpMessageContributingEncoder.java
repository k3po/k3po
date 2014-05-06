/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.behavior.handler.codec.http;

import org.jboss.netty.handler.codec.http.HttpMessage;

public interface HttpMessageContributingEncoder {

    void encode(HttpMessage message) throws Exception;
}
