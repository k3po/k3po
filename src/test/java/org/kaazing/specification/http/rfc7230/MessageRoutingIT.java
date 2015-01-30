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
package org.kaazing.specification.http.rfc7230;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Test to validate behavior as specified in <a href="https://tools.ietf.org/html/rfc7230#section-5">RFC 7230 section 5:
 * Message Routing Host</a>.
 */
public class MessageRoutingIT {

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-5.4">RFC 7230 section 5.4: Host</a>.
     */
    @Test
    @Ignore("Not Implemented")
    public void inboundShouldAcceptRequestWithEmptyHostHeader() {

    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-5.4">RFC 7230 section 5.4: Host</a>.
     */
    @Test
    @Ignore("Not Implemented")
    public void outboundHostHeaderShouldFollowRequestLine() {

    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-5.4">RFC 7230 section 5.4: Host</a>. <blockquote> When a
     * proxy receives a request with an absolute-form of request-target, the proxy MUST ignore the received Host header
     * field (if any) and instead replace it with the host information of the request-target. A proxy that forwards such
     * a request MUST generate a new Host field-value based on the received request-target rather than forward the
     * received Host field-value. </blockquote>
     */
    @Test
    @Ignore("Not Implemented")
    public void proxyShouldRewriteHostHeader() {

    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-5.4">RFC 7230 section 5.4: Host</a>.
     */
    @Test
    @Ignore("Not Implemented")
    public void inboundMustRejectRequestWith400IfMissingHostHeader() {

    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-5.4">RFC 7230 section 5.4: Host</a>.
     */
    @Test
    @Ignore("Not Implemented")
    public void inboundMustRejectRequestWith400IfHostHeaderDoesNotMatchURI() {

    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-5.4">RFC 7230 section 5.4: Host</a>.
     */
    @Test
    @Ignore("Not Implemented")
    public void inboundMustRejectRequestWith400IfHostHeaderOccursMoreThanOnce() {

    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-5.7">RFC 7230 section 5.7: Message Forwarding</a>.
     */
    @Test
    @Ignore("Not Implemented")
    public void proxyMustAttachAppropriateViaHeader() {

    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-5.7">RFC 7230 section 5.7: Message Forwarding</a>.
     */
    @Test
    @Ignore("Not Implemented")
    public void proxyMustAttachAppropriateViaHeadersEvenWhenOthers() {

    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-5.7">RFC 7230 section 5.7: Message Forwarding</a>.
     */
    @Test
    @Ignore("Not Implemented")
    public void gatewayMustAttachAppropriateViaHeaderOnRequestAndMayAttachOnResponse() {

    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-5.7">RFC 7230 section 5.7: Message Forwarding</a>.
     */
    @Test
    @Ignore("Not Implemented")
    public void firewallIntermediaryShouldReplaceHostInViaHeaderWithPseudonym() {

    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-5.7">RFC 7230 section 5.7: Message Forwarding</a>.
     */
    @Test
    @Ignore("Not Implemented")
    public void proxyMustNotTransformThePayloadOfARequestThatContainsANoTransformCacheControl() {
        // A proxy MUST NOT transform the payload (Section 3.3 of [RFC7231]) of
        // a message that contains a no-transform cache-control directive
        // (Section 5.2 of [RFC7234]).
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-5.7">RFC 7230 section 5.7: Message Forwarding</a>.
     */
    @Test
    @Ignore("Not Implemented")
    public void proxyMustNotTransformThePayloadOfAResponseThatContainsANoTransformCacheControl() {
        // A proxy MUST NOT transform the payload (Section 3.3 of [RFC7231]) of
        // a message that contains a no-transform cache-control directive
        // (Section 5.2 of [RFC7234]).
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-5.7">RFC 7230 section 5.7: Message Forwarding</a>.
     */
    @Test
    @Ignore("Not Implemented")
    public void proxyMustNotModifyQueryOrAbsolutePathOfRequest() {
        // A proxy MUST NOT modify the "absolute-path" and "query" parts of the
        // received request-target when forwarding it to the next inbound
        // server, except as noted above to replace an empty path with "/" or
        // "*".
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-5.3">RFC 7230 section 5.3: Request Target</a>.
     */
    @Test
    @Ignore("Not Implemented")
    public void inboundMustAcceptOriginForm() {
        // origin-form = absolute-path [ "?" query ]
        // GET /where?q=now HTTP/1.1
        // Host: www.example.org
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-5.3">RFC 7230 section 5.3: Request Target</a>.
     */
    @Test
    @Ignore("Not Implemented")
    public void inboundMustAcceptAbsoluteForm() {
        // GET http://www.example.org/pub/WWW/TheProject.html HTTP/1.1
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-5.3">RFC 7230 section 5.3: Request Target</a>.
     */
    @Test
    @Ignore("Not Implemented")
    public void intermediaryMustAcceptAuthorityFormConnectRequest() {
        // CONNECT www.example.com:80 HTTP/1.1
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-5.3">RFC 7230 section 5.3: Request Target</a>.
     */
    @Test
    @Ignore("Not Implemented")
    public void inboundMustAcceptAsterickFormOptionsRequest() {
        // OPTIONS * HTTP/1.1
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-5.3">RFC 7230 section 5.3: Request Target</a>.
     */
    @Test
    @Ignore("Not Implemented")
    public void lastProxyMustConvertOptionsInAbsoluteFormToAsterickForm() {
        // OPTIONS * HTTP/1.1
        // Host: www.example.org:8001
    }

}
