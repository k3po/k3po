/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.behavior.handler.codec.http;

import org.jboss.netty.handler.codec.http.HttpMessage;

public interface HttpMessageContributingDecoder {

    void decode(HttpMessage message) throws Exception;
}
