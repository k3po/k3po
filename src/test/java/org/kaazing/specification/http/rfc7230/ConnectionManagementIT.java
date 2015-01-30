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
 * Test to validate behavior as specified in <a href="https://tools.ietf.org/html/rfc7230#section-6">RFC 7230 section 6:
 * Connection Management</a>.
 */
public class ConnectionManagementIT {

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-6.1">RFC 7230 section 6.1: Connection</a>.
     */
    @Test
    @Ignore("Not Implemented")
    public void intermediaryMustRemoveConnectionHeaderIfMatchesConnectionOptionHeader() {

    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-6.1">RFC 7230 section 6.1: Connection</a>.
     */
    @Test
    @Ignore("Not Implemented")
    public void mustCloseConnectionAfterRequestWithConnectionClose() {

    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-6.1">RFC 7230 section 6.1: Connection</a>.
     */
    @Test
    @Ignore("Not Implemented")
    public void mustCloseConnectionAfterResponseWithConnectionClose() {

    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-6.3">RFC 7230 section 6.3: Persistence</a>.
     */
    @Test
    @Ignore("Not Implemented")
    public void underlyingConnectionShouldByDefaultPersistBetweenMultipleRequestResponses() {

    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-6.3.1">RFC 7230 section 6.3.1: Retrying Requests</a>.
     */
    @Test
    @Ignore("Not Implemented")
    public void proxyMustNotRetryNonIdempotentRequests() {

    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-6.3.2">RFC 7230 section 6.3.2: Pipelining</a>.
     * <p>
     * A client can send multiple requests with out getting a response, but a server must send responses back it order
     * </p>
     */
    @Test
    @Ignore("Not Implemented")
    public void serverShouldAcceptHttpPipelining() {

    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-6.3.2">RFC 7230 section 6.3.2: Pipelining</a>.
     * <p>
     * If client is pipelining requests and the connection closes from underneath the client should retry requests, and
     * the first retry must not be pipelined with other requests
     * </p>
     */
    @Test
    @Ignore("Not Implemented")
    public void clientWithPipeliningMustNotRetryPipeliningImmediatelyAfterFailure() {

    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-6.6">RFC 7230 section 6.6: Tear-down</a>. <blockquote> A
     * server that receives a "close" connection option MUST initiate a close of the connection (see below) after it
     * sends the final response to the request that contained "close". The server SHOULD send a "close" connection
     * option in its final response on that connection. The server MUST NOT process any further requests received on
     * that connection. </blockquote>
     */
    @Test
    @Ignore("Not Implemented")
    public void serverMustCloseItsHalfOfConnectionAfterSendingResponseIfItReceivesAClose() {

    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-6.6">RFC 7230 section 6.6: Tear-down</a>. <blockquote> A
     * client that receives a "close" connection option MUST cease sending requests on that connection and close the
     * connection after reading the response message containing the "close"; if additional pipelined requests had been
     * sent on the connection, the client SHOULD NOT assume that they will be processed by the server. </blockquote>
     */
    @Test
    @Ignore("Not Implemented")
    public void clientMustStopPipeliningRequestsIfItReceivesACloseInAResponse() {

    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-6.7">RFC 7230 section 6.7: Upgrade</a>. <blockquote> A
     * server that sends a 101 (Switching Protocols) response MUST send an Upgrade header field to indicate the new
     * protocol(s) to which the connection is being switched; if multiple protocol layers are being switched, the sender
     * MUST list the protocols in layer-ascending order. </blockquote>
     */
    @Test
    @Ignore("Not Implemented")
    public void serverGettingUpgradeRequestMustRespondWithUpgradeHeader() {

    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-6.7">RFC 7230 section 6.7: Upgrade</a>. <blockquote> A
     * server that sends a 426 (Upgrade Required) response MUST send an Upgrade header field to indicate the acceptable
     * protocols, in order of descending preference. </blockquote>
     */
    @Test
    @Ignore("Not Implemented")
    public void serverThatSendsUpgradeRequiredMustIncludeUpgradeHeader() {

    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-6.7">RFC 7230 section 6.7: Upgrade</a>. <blockquote> if
     * the Upgrade header field is received in a GET request and the server decides to switch protocols, it first
     * responds with a 101 (Switching Protocols) message in HTTP/1.1 and then immediately follows that with the new
     * protocol's equivalent of a response to a GET on the target resource. </blockquote>
     */
    @Test
    @Ignore("Not Implemented")
    public void serverThatIsUpgradingMustSendAResponse101() {

    }
}
