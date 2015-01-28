/**
 * Copyright (c) 2007-2014 Kaazing Corporation. All rights reserved.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kaazing.specification.http;

import org.junit.Ignore;
import org.junit.Test;

/**
 * rfc7230#section-5.4
 *
 */
public class HostIT {

    @Test
    @Ignore("Not Implemented")
    public void upstreamShouldAcceptRequestWithEmptyHostHeader() {
        // host:
        // occurs when host is missing or null
    }

    @Test
    @Ignore("Not Implemented")
    public void clientHostHeaderShouldFollowRequestLine() {
        // in order
    }

    @Test
    @Ignore("Not Implemented")
    public void proxyShouldRewriteHostHeader() {
        // When a proxy receives a request with an absolute-form of
        // request-target, the proxy MUST ignore the received Host header field
        // (if any) and instead replace it with the host information of the
        // request-target. A proxy that forwards such a request MUST generate a
        // new Host field-value based on the received request-target rather than
        // forward the received Host field-value.
    }

    @Test
    @Ignore("Not Implemented")
    public void upstreamMustRejectRequestMissingHostHeader() {
        // 400
    }

    @Test
    @Ignore("Not Implemented")
    public void upstreamMustRejectRequestIfHostHeaderDoesNotMatchURI() {
        // 400
    }

    @Test
    @Ignore("Not Implemented")
    public void upstreamMustRejectRequestIfHostHeaderOccursMoreThanOnce() {
        // 400
    }
}
