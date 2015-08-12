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

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.rules.RuleChain.outerRule;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.kaazing.k3po.junit.annotation.Specification;
import org.kaazing.k3po.junit.rules.K3poRule;

/**
 * Test to validate behavior as specified in <a href="https://tools.ietf.org/html/rfc7230#section-6">RFC 7230 section 6:
 * Connection Management</a>.
 */
public class ConnectionManagementIT {

    private final K3poRule k3po = new K3poRule().setScriptRoot("org/kaazing/specification/http/rfc7230/connection.management");

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-6.1">RFC 7230 section 6.1: Connection</a>.
     *
     * In order to avoid confusing downstream recipients, a proxy or gateway MUST remove or replace any received
     * connection options before forwarding the message.
     *
     * @throws Exception when K3PO is not started
     */
    @Test
    @Specification({
        "intermediary.must.remove.connection.header.on.forward.request/client",
        "intermediary.must.remove.connection.header.on.forward.request/intermediary",
        "intermediary.must.remove.connection.header.on.forward.request/backend" })
    public void intermediaryMustRemoveConnectionHeaderOnForwardRequest() throws Exception {
        k3po.finish();
    }


    @Test
    @Specification({
            "reverse.proxy.connection.established/client",
            "reverse.proxy.connection.established/proxy",
            "reverse.proxy.connection.established/server" })
    public void reverseProxyConnectionEstablished() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-6.1">RFC 7230 section 6.1: Connection</a>.
     *
     * The "close" connection option is defined for a sender to signal that this connection will be closed after
     * completion of the response. For example,
     *
     * Connection: close
     *
     * in either the request or the response header fields indicates that the sender is going to close the connection
     * after the current request/response is complete (Section 6.6).
     *
     * @throws Exception when K3PO is not started
     */
    @Test
    @Specification({
        "client.must.close.connection.after.request.with.connection.close/request",
        "client.must.close.connection.after.request.with.connection.close/response" })
    public void clientMustCloseConnectionAfterRequestWithConnectionClose() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-6.1">RFC 7230 section 6.1: Connection</a>.
     *
     * The "close" connection option is defined for a sender to signal that this connection will be closed after
     * completion of the response. For example,
     *
     * Connection: close
     *
     * in either the request or the response header fields indicates that the sender is going to close the connection
     * after the current request/response is complete (Section 6.6).
     *
     * @throws Exception when K3PO is not started
     */
    @Test
    @Specification({
        "server.must.close.connection.after.response.with.connection.close/request",
        "server.must.close.connection.after.response.with.connection.close/response" })
    public void serverMustCloseConnectionAfterResponseWithConnectionClose() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-6.3">RFC 7230 section 6.3: Persistence</a>.
     *
     * HTTP implementations SHOULD support persistent connections.
     *
     * @throws Exception when K3PO is not started
     */
    @Test
    @Specification({
        "connections.should.persist.by.default/client",
        "connections.should.persist.by.default/backend" })
    public void connectionsShouldPersistByDefault() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-6.3.1">RFC 7230 section 6.3.1: Retrying Requests</a>.
     *
     * A proxy MUST NOT automatically retry non-idempotent requests.
     *
     * @throws Exception when K3PO is not started
     */
    @Test
    @Specification({
        "proxy.must.not.retry.non.idempotent.requests/client",
        "proxy.must.not.retry.non.idempotent.requests/proxy",
        "proxy.must.not.retry.non.idempotent.requests/server" })
    public void proxyMustNotRetryNonIdempotentRequests() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-6.3.2">RFC 7230 section 6.3.2: Pipelining</a>.
     * <p>
     * A client can send multiple requests with out getting a response, but a server must send responses back it order
     * </p>
     * @throws Exception when K3PO is not started
     */
    @Test
    @Specification({
        "server.should.accept.http.pipelining/request",
        "server.should.accept.http.pipelining/response" })
    public void serverShouldAcceptHttpPipelining() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-6.3.2">RFC 7230 section 6.3.2: Pipelining</a>.
     * <p>
     * If client is pipelining requests and the connection closes from underneath the client should retry requests, and
     * the first retry must not be pipelined with other requests
     * </p>
     *
     * @throws Exception when K3PO is not started
     */
    @Test
    @Specification({
        "client.with.pipelining.must.not.retry.pipelining.immediately.after.failure/request",
        "client.with.pipelining.must.not.retry.pipelining.immediately.after.failure/response" })
    @Ignore("Requires enhancement k3po/132")
    public void clientWithPipeliningMustNotRetryPipeliningImmediatelyAfterFailure() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-6.6">RFC 7230 section 6.6: Tear-down</a>. <blockquote> A
     * server that receives a "close" connection option MUST initiate a close of the connection (see below) after it
     * sends the final response to the request that contained "close". The server SHOULD send a "close" connection
     * option in its final response on that connection. The server MUST NOT process any further requests received on
     * that connection. </blockquote>
     *
     * @throws Exception when K3PO is not started
     */
    @Test
    @Specification({
        "server.must.close.its.half.of.connection.after.sending.response.if.it.receives.a.close/request",
        "server.must.close.its.half.of.connection.after.sending.response.if.it.receives.a.close/response" })
    @Ignore("Requires enhancement k3po/132")
    public void serverMustCloseItsHalfOfConnectionAfterSendingResponseIfItReceivesAClose() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-6.6">RFC 7230 section 6.6: Tear-down</a>. <blockquote> A
     * client that receives a "close" connection option MUST cease sending requests on that connection and close the
     * connection after reading the response message containing the "close"; if additional pipelined requests had been
     * sent on the connection, the client SHOULD NOT assume that they will be processed by the server. </blockquote>
     *
     * @throws Exception when K3PO is not started
     */
    @Test
    @Specification({
        "client.must.stop.pipelining.requests.if.it.receives.a.close.in.a.response/request",
        "client.must.stop.pipelining.requests.if.it.receives.a.close.in.a.response/response" })
    @Ignore("Requires enhancement k3po/132")
    public void clientMustStopPipeliningRequestsIfItReceivesACloseInAResponse() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-6.7">RFC 7230 section 6.7: Upgrade</a>. <blockquote> A
     * server that sends a 101 (Switching Protocols) response MUST send an Upgrade header field to indicate the new
     * protocol(s) to which the connection is being switched; if multiple protocol layers are being switched, the sender
     * MUST list the protocols in layer-ascending order. </blockquote>
     *
     * @throws Exception when K3PO is not started
     */
    @Test
    @Specification({
        "server.getting.upgrade.request.must.respond.with.upgrade.header/request",
        "server.getting.upgrade.request.must.respond.with.upgrade.header/response" })
    public void serverGettingUpgradeRequestMustRespondWithUpgradeHeader() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-6.7">RFC 7230 section 6.7: Upgrade</a>. <blockquote> A
     * server that sends a 426 (Upgrade Required) response MUST send an Upgrade header field to indicate the acceptable
     * protocols, in order of descending preference. </blockquote>
     *
     * @throws Exception when K3PO is not started
     */
    @Test
    @Specification({
        "server.that.sends.upgrade.required.must.include.upgrade.header/request",
        "server.that.sends.upgrade.required.must.include.upgrade.header/response" })
    public void serverThatSendsUpgradeRequiredMustIncludeUpgradeHeader() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-6.7">RFC 7230 section 6.7: Upgrade</a>.
     *
     * <blockquote> if the Upgrade header field is received in a GET request and the server decides to switch protocols,
     * it first responds with a 101 (Switching Protocols) message in HTTP/1.1 and then immediately follows that with the
     * new protocol's equivalent of a response to a GET on the target resource. </blockquote>
     *
     * @throws Exception when K3PO is not started
     */
    @Test
    @Specification({
        "server.that.is.upgrading.must.send.a.101.response/request",
        "server.that.is.upgrading.must.send.a.101.response/response" })
    public void serverThatIsUpgradingMustSendA100Response() throws Exception {
        k3po.finish();
    }
}
