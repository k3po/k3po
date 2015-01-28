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
 * rfc7230#section-5.7
 *
 */
public class MessageForwardingIT {

    @Test
    @Ignore("Not Implemented")
    public void proxyMustAttachAppropriateViaHeader() {

    }

    @Test
    @Ignore("Not Implemented")
    public void proxyMustAttachAppropriateViaHeadersEvenWhenOthers() {

    }

    @Test
    @Ignore("Not Implemented")
    public void gatewayMustAttachAppropriateViaHeaderOnRequestAndMayAttachOnResponse() {

    }

    @Test
    @Ignore("Not Implemented")
    public void firewallIntermediaryShouldReplaceHostInViaHeaderWithPseudonym() {

    }

    @Test
    @Ignore("Not Implemented")
    public void proxyMustNotTransformThePayloadOfARequestThatContainsANoTransformCacheControl() {
        // A proxy MUST NOT transform the payload (Section 3.3 of [RFC7231]) of
        // a message that contains a no-transform cache-control directive
        // (Section 5.2 of [RFC7234]).
    }

    @Test
    @Ignore("Not Implemented")
    public void proxyMustNotTransformThePayloadOfAResponseThatContainsANoTransformCacheControl() {
        // A proxy MUST NOT transform the payload (Section 3.3 of [RFC7231]) of
        // a message that contains a no-transform cache-control directive
        // (Section 5.2 of [RFC7234]).
    }

    @Test
    @Ignore("Not Implemented")
    public void proxyMustNotModifyQueryOrAbsolutePathOfRequest() {
        // A proxy MUST NOT modify the "absolute-path" and "query" parts of the
        // received request-target when forwarding it to the next inbound
        // server, except as noted above to replace an empty path with "/" or
        // "*".
    }

}
