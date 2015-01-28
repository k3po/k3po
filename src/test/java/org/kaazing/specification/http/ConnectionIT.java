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
 * rfc7230#section-6
 *
 */
public class ConnectionIT {

    @Test
    @Ignore("Not Implemented")
    public void intermediaryMustRemoveConnectionHeaderIfMatchesConnectionOptionHeader() {
        // When a header field aside from Connection is used to supply control
        // information for or about the current connection, the sender MUST list
        // the corresponding field-name within the Connection header field. A
        // proxy or gateway MUST parse a received Connection header field before
        // a message is forwarded and, for each connection-option in this field,
        // remove any header field(s) from the message with the same name as the
        // connection-option, and then remove the Connection header field itself
        // (or replace it with the intermediary's own connection options for the
        // forwarded message).
        //
        // Hence, the Connection header field provides a declarative way of
        // distinguishing header fields that are only intended for the immediate
        // recipient ("hop-by-hop") from those fields that are intended for all
        // recipients on the chain ("end-to-end"), enabling the message to be
        // self-descriptive and allowing future connection-specific extensions
        // to be deployed without fear that they will be blindly forwarded by
        // older intermediaries.
    }

    @Test
    @Ignore("Not Implemented")
    public void mustCloseConnectionAfterRequestWithConnectionClose() {

    }

    @Test
    @Ignore("Not Implemented")
    public void mustCloseConnectionAfterResponseWithConnectionClose() {

    }

    @Test
    @Ignore("Not Implemented")
    public void underlyingConnectionShouldByDefaultPersistBetweenMultipleRequestResponses() {

    }

    @Test
    @Ignore("Not Implemented")
    public void proxyMustNotRetryNonIdempotentRequests() {
        // . A proxy MUST NOT automatically retry
        // non-idempotent requests.

        // POST =====> P ====> Tcp dies underneath
        // send error or something back straight close, not in spec but
        // recomended
        // 10.5.3 502 Bad Gateway or 504 if timed-out
        // The server, while acting as a gateway or proxy, received an invalid
        // response from the upstream server it accessed in attempting to
        // fulfill the request.

    }

    @Test
    @Ignore("Not Implemented")
    public void serverShouldAcceptHttpPipelining() {
        // A client can send multiple requests with out getting a response,
        // responses
        // must come back in order
    }

    @Test
    @Ignore("Not Implemented")
    public void clientWithPipeliningMustNotRetryPipeliningImmediatelyAfterFailure() {
        // If pipeline and then connection closes, client retries requests
        // without pipelining
    }

    @Test
    @Ignore("Not Implemented")
    public void serverMustCloseItsHalfOfConnectionAfterSendingResponseIfItReceivesAClose() {
        // A server that receives a "close" connection option MUST initiate a
        // close of the connection (see below) after it sends the final response
        // to the request that contained "close". The server SHOULD send a
        // "close" connection option in its final response on that connection.
        // The server MUST NOT process any further requests received on that
        // connection.
    }

    @Test
    @Ignore("Not Implemented")
    public void clientMustStopPipeliningRequestsIfItReceivesACloseInAResponse() {
        // A client that receives a "close" connection option MUST cease sending
        // requests on that connection and close the connection after reading
        // the response message containing the "close"; if additional pipelined
        // requests had been sent on the connection, the client SHOULD NOT
        // assume that they will be processed by the server.
    }

    @Test
    @Ignore("Not Implemented")
    public void serverGettingUpgradeRequestMustRespondWithUpgradeHeader() {
        // A server that sends a 101 (Switching Protocols) response MUST send an
        // Upgrade header field to indicate the new protocol(s) to which the
        // connection is being switched; if multiple protocol layers are being
        // switched, the sender MUST list the protocols in layer-ascending
        // order.
    }

    @Test
    @Ignore("Not Implemented")
    public void serverThatSendsUpgradeRequiredMustIncludeUpgradeHeader() {
        // A server that sends a 426 (Upgrade Required) response MUST send an
        // Upgrade header field to indicate the acceptable protocols, in order
        // of descending preference.
    }

    @Test
    @Ignore("Not Implemented")
    public void serverThatIsUpgradingMustSendAResponse101() {
        // if the Upgrade header field is received in a GET request
        // and the server decides to switch protocols, it first responds with a
        // 101 (Switching Protocols) message in HTTP/1.1 and then immediately
        // follows that with the new protocol's equivalent of a response to a
        // GET on the target resource.
    }
}
