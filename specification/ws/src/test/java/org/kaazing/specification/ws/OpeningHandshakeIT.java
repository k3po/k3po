/*
 * Copyright 2014, Kaazing Corporation. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kaazing.specification.ws;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.rules.RuleChain.outerRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.kaazing.k3po.junit.annotation.Specification;
import org.kaazing.k3po.junit.rules.K3poRule;

/**
 * RFC-6455, section 4.1 "Client-Side Requirements"
 * RFC-6455, section 4.2 "Server-Side Requirements"
 */
public class OpeningHandshakeIT {

    private final K3poRule k3po = new K3poRule().setScriptRoot("org/kaazing/specification/ws/opening");

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    // TODO:
    // proxy => HTTP CONNECT w/ optional authorization, auto-configuration via ws://, wss://
    // TLS (not SSL) w/ SNI for wss://

    @Test
    @Specification({
        "connection.established/handshake.request",
        "connection.established/handshake.response" })
    public void shouldEstablishConnection() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "request.header.cookie/handshake.request",
        "request.header.cookie/handshake.response" })
    public void shouldEstablishConnectionWithCookieRequestHeader() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "request.headers.random.case/handshake.request",
        "request.headers.random.case/handshake.response" })
    public void shouldEstablishConnectionWithRandomCaseRequestHeaders() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "response.headers.random.case/handshake.request",
        "response.headers.random.case/handshake.response" })
    public void shouldEstablishConnectionWithRandomCaseResponseHeaders() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "request.header.origin/handshake.request",
        "request.header.origin/handshake.response" })
    public void shouldEstablishConnectionWithRequestHeaderOrigin() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "request.header.sec.websocket.protocol/handshake.request",
        "request.header.sec.websocket.protocol/handshake.response" })
    public void shouldEstablishConnectionWithRequestHeaderSecWebSocketProtocol() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "request.header.sec.websocket.extensions/handshake.request",
        "request.header.sec.websocket.extensions/handshake.response" })
    public void shouldEstablishConnectionWithRequestHeaderSecWebSocketExtensions() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "response.header.sec.websocket.extensions.partial.agreement/handshake.request",
        "response.header.sec.websocket.extensions.partial.agreement/handshake.response" })
    public void shouldEstablishConnectionWithSomeExtensionsNegotiated() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "response.header.sec.websocket.extensions.reordered/handshake.request",
        "response.header.sec.websocket.extensions.reordered/handshake.response" })
    public void shouldEstablishConnectionWhenOrderOfExtensionsNegotiatedChanged() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "request.method.not.get/handshake.request",
        "request.method.not.get/handshake.response" })
    public void shouldFailHandshakeWhenMethodNotGet() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "request.version.not.http.1.1/handshake.request",
        "request.version.not.http.1.1/handshake.response" })
    public void shouldFailHandshakeWhenVersionNotHttp11() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "request.header.host.missing/handshake.request",
        "request.header.host.missing/handshake.response" })
    public void shouldFailHandshakeWhenRequestHeaderHostMissing() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "request.header.upgrade.missing/handshake.request",
        "request.header.upgrade.missing/handshake.response" })
    public void shouldFailHandshakeWhenRequestHeaderUpgradeMissing() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "request.header.upgrade.not.websocket/handshake.request",
        "request.header.upgrade.not.websocket/handshake.response" })
    public void shouldFailHandshakeWhenRequestHeaderUpgradeNotWebSocket() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "request.header.connection.missing/handshake.request",
        "request.header.connection.missing/handshake.response" })
    public void shouldFailHandshakeWhenRequestHeaderConnectionMissing() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "request.header.connection.not.upgrade/handshake.request",
        "request.header.connection.not.upgrade/handshake.response" })
    public void shouldFailHandshakeWhenRequestHeaderConnectionNotUpgrade() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "request.header.sec.websocket.key.missing/handshake.request",
        "request.header.sec.websocket.key.missing/handshake.response" })
    public void shouldFailHandshakeWhenRequestHeaderSecWebSocketKeyMissing() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "request.header.sec.websocket.key.not.16bytes.base64/handshake.request",
        "request.header.sec.websocket.key.not.16bytes.base64/handshake.response" })
    public void shouldFailHandshakeWhenRequestHeaderSecWebSocketKeyNot16BytesBase64() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "request.header.sec.websocket.version.not.13/handshake.request",
        "request.header.sec.websocket.version.not.13/handshake.response" })
    public void shouldFailHandshakeWhenRequestHeaderSecWebSocketVersionNot13() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "response.header.connection.not.upgrade/handshake.request",
        "response.header.connection.not.upgrade/handshake.response" })
    public void shouldFailConnectionWhenResponseHeaderConnectionNotUpgrade() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "response.header.connection.missing/handshake.request",
        "response.header.connection.missing/handshake.response" })
    public void shouldFailConnectionWhenResponseHeaderConnectionMissing() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "response.header.upgrade.not.websocket/handshake.request",
        "response.header.upgrade.not.websocket/handshake.response" })
    public void shouldFailConnectionWhenResponseHeaderUpgradeNotWebSocket() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "response.header.upgrade.missing/handshake.request",
        "response.header.upgrade.missing/handshake.response" })
    public void shouldFailConnectionWhenResponseHeaderUpgradeMissing() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "response.header.sec.websocket.accept.not.hashed/handshake.request",
        "response.header.sec.websocket.accept.not.hashed/handshake.response" })
    public void shouldFailConnectionWhenResponseHeaderSecWebSocketAcceptNotHashed() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "response.header.sec.websocket.accept.missing/handshake.request",
        "response.header.sec.websocket.accept.missing/handshake.response" })
    public void shouldFailConnectionWhenResponseHeaderSecWebSocketAcceptMissing() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "response.header.sec.websocket.extensions.not.negotiated/handshake.request",
        "response.header.sec.websocket.extensions.not.negotiated/handshake.response" })
    public void shouldFailConnectionWhenResponseHeaderSecWebSocketExtensionsNotNegotiated() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "response.header.sec.websocket.protocol.not.negotiated/handshake.request",
        "response.header.sec.websocket.protocol.not.negotiated/handshake.response" })
    public void shouldFailConnectionWhenResponseHeaderSecWebSocketProtocolNotNegotiated() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "multiple.connections.established/handshake.requests",
        "multiple.connections.established/handshake.responses" })
    public void shouldEstablishMultipleConnections() throws Exception {
        k3po.finish();
    }
}
