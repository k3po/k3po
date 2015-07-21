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

package org.kaazing.specification.wse;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.rules.RuleChain.outerRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.kaazing.k3po.junit.annotation.Specification;
import org.kaazing.k3po.junit.rules.K3poRule;

public class OpeningIT {
    private final K3poRule k3po = new K3poRule()
            .setScriptRoot("org/kaazing/specification/wse/opening");

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    // TODO:
    // Upstream/Downstream URL Scheme other than HTTPS when original handshake
    // URL uses HTTPS

    @Test
    @Specification({
        "connection.established/handshake.request",
        "connection.established/handshake.response" })
    public void shouldEstablishConnection() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "request.header.origin/handshake.request",
        "request.header.origin/handshake.response" })
    public void shouldEstablishConnectionWithRequestHeaderOrigin()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "request.header.x.websocket.protocol/handshake.request",
        "request.header.x.websocket.protocol/handshake.response" })
    public void shouldEstablishConnectionWithRequestHeaderXWebSocketProtocol()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "request.header.x.websocket.extensions/handshake.request",
        "request.header.x.websocket.extensions/handshake.response" })
    public void shouldEstablishConnectionWithRequestHeaderXWebSocketExtensions()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "request.with.body/handshake.request",
        "request.with.body/handshake.response" })
    public void shouldEstablishConnectionWithNonEmptyRequestBody()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "response.body.has.upstream.with.different.port/handshake.request",
        "response.body.has.upstream.with.different.port/handshake.response" })
    public void shouldEstablishConnectionWhenResponseBodyHasUpstreamWithDifferentPort()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "response.body.has.downstream.with.different.port/handshake.request",
        "response.body.has.downstream.with.different.port/handshake.response" })
    public void shouldEstablishConnectionWhenResponseBodyHasDownstreamWithDifferentPort()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "request.method.not.post/handshake.request",
        "request.method.not.post/handshake.response" })
    public void shouldFailHandshakeWhenRequestMethodNotPost() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "request.header.x.sequence.number.missing/handshake.request",
        "request.header.x.sequence.number.missing/handshake.response" })
    public void shouldFailHandshakeWhenRequestHeaderXSequenceNoIsMissing() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "request.header.x.sequence.number.negative/handshake.request",
        "request.header.x.sequence.number.negative/handshake.response" })
    public void shouldFailHandshakeWhenRequestHeaderXSequenceNoIsNegative() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "request.header.x.sequence.number.non.integer/handshake.request",
        "request.header.x.sequence.number.non.integer/handshake.response" })
    public void shouldFailHandshakeWhenRequestHeaderXSequenceNoIsNotInteger() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "request.header.x.sequence.number.out.of.range/handshake.request",
        "request.header.x.sequence.number.out.of.range/handshake.response" })
    public void shouldFailHandshakeWhenRequestHeaderXSequenceNoIsOutOfRange() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "request.header.x.websocket.version.missing/handshake.request",
        "request.header.x.websocket.version.missing/handshake.response" })
    public void shouldFailHandshakeWhenRequestHeaderXWebSocketVersionMissing()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "request.header.x.websocket.version.not.wseb-1.0/handshake.request",
        "request.header.x.websocket.version.not.wseb-1.0/handshake.response" })
    public void shouldFailHandshakeWhenRequestHeaderXWebSocketVersionNotWseb10()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "request.header.x.accept.commands.not.ping/handshake.request",
        "request.header.x.accept.commands.not.ping/handshake.response" })
    public void shouldFailHandshakeWhenHeaderXAcceptCommandsNotPing()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "response.status.code.not.201/handshake.request",
        "response.status.code.not.201/handshake.response" })
    public void shouldFailConnectionWhenResponseStatusCodeNot201()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "response.header.content.type.missing/handshake.request",
        "response.header.content.type.missing/handshake.response" })
    public void shouldFailConnectionWhenResponseHeaderContentTypeIsMissing()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "response.header.content.type.not.text.plain.charset.utf-8/handshake.request",
        "response.header.content.type.not.text.plain.charset.utf-8/handshake.response" })
    public void shouldFailConnectionWhenResponseHeaderContentTypeNotTextPlainCharsetUTF8()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "response.header.x.websocket.version.not.matching/handshake.request",
        "response.header.x.websocket.version.not.matching/handshake.response" })
    public void shouldFailConnectionWhenResponseHeaderXWebSocketVersionNotMatching()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "response.header.x.websocket.protocol.not.negotiated/handshake.request",
        "response.header.x.websocket.protocol.not.negotiated/handshake.response" })
    public void shouldFailConnectionWhenXWebSocketProtocolNotNegotiated()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "response.header.x.websocket.extensions.not.negotiated/handshake.request",
        "response.header.x.websocket.extensions.not.negotiated/handshake.response" })
    public void shouldFailConnectionWhenXWebSocketExtensionsNotNegotiated()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "response.body.with.no.downstream/handshake.request",
        "response.body.with.no.downstream/handshake.response" })
    public void shouldFailConnectionWhenResponseBodyHasNoDownstream()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "response.body.has.upstream.with.scheme.not.http.or.https/handshake.request",
        "response.body.has.upstream.with.scheme.not.http.or.https/handshake.response" })
    public void shouldFailConnectionWhenResponseBodyHasUpstreamWithSchemeNotHttpOrHttps()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "response.body.has.upstream.with.different.host/handshake.request",
        "response.body.has.upstream.with.different.host/handshake.response" })
    public void shouldFailConnectionWhenResponseBodyHasUpstreamWithDifferentHost()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "response.body.has.upstream.with.different.path.prefix/handshake.request",
        "response.body.has.upstream.with.different.path.prefix/handshake.response" })
    public void shouldFailConnectionWhenResponseBodyHasUpstreamWithDifferentPathPrefix()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "response.body.has.downstream.with.scheme.not.http.or.https/handshake.request",
        "response.body.has.downstream.with.scheme.not.http.or.https/handshake.response" })
    public void shouldFailConnectionWhenResponseBodyHasDownstreamWithSchemeNotHttpOrHttps()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "response.body.has.downstream.with.different.host/handshake.request",
        "response.body.has.downstream.with.different.host/handshake.response" })
    public void shouldFailConnectionWhenResponseBodyHasDownstreamWithDifferentHost()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "response.body.has.downstream.with.different.path.prefix/handshake.request",
        "response.body.has.downstream.with.different.path.prefix/handshake.response" })
    public void shouldFailConnectionWhenResponseBodyHasDownstreamWithDifferentPathPrefix()
            throws Exception {
        k3po.finish();
    }
}
