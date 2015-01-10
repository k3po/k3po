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

public class OpeningHandshakeIT {
	private final K3poRule k3po = new K3poRule().setScriptRoot("org/kaazing/specification/wse/opening");

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);
    
    // TODO: 
    // Upstream/Downstream URL Scheme other than HTTPS when original handshake URL uses HTTPS
    
    
    @Test
    @Specification({
        "connection.established/handshake.request",
        "connection.established/handshake.response" })
    public void shouldEstablishConnection() throws Exception {
        k3po.join();
    }
    
    @Test
    @Specification({
        "request.header.websocket.protocol/handshake.request",
        "request.header.websocket.protocol/handshake.response" })
    public void shouldEstablishConnectionWithRequestHeaderWebSocketProtocol() throws Exception {
        k3po.join();
    }
    
    @Test
    @Specification({
        "request.header.websocket.extensions/handshake.request",
        "request.header.websocket.extensions/handshake.response" })
    public void shouldEstablishConnectionWithRequestHeaderWebSocketExtensions() throws Exception {
        k3po.join();
    }
    
    @Test
    @Specification({
        "request.with.body/handshake.request",
        "request.with.body/handshake.response" })
    public void shouldEstablishConnectionWithNonEmptyRequestBody() throws Exception {
    	k3po.join();
    }
    
    @Test
    @Specification({
    	"request.header.method.not.post/handshake.request",
    	"request.header.method.not.post/handshake.response" })
    public void shouldFailHandshakeWhenRequestMethodNotPost() throws Exception {
    	k3po.join();
    }
    
    @Test
    @Specification({
    	"request.header.websocket.version.missing/handshake.request",
    	"request.header.websocket.version.missing/handshake.response" })
    public void shouldFailHandshakeWhenRequestHeaderWebSocketVersionMissing() throws Exception {
    	k3po.join();
    }
    
    @Test
    @Specification({
    	"request.header.websocket.version.not.wseb-1.1/handshake.request",
    	"request.header.websocket.version.not.wseb-1.1/handshake.response" })
    public void shouldFailHandshakeWhenRequestHeaderWebSocketVersionNotWseb11() throws Exception {
    	k3po.join();
    }
    
    @Test
    @Specification({
    	"request.header.accept.commands.not.ping/handshake.request",
    	"request.header.accept.commands.not.ping/handshake.response" })
    public void shouldFailHandshakeWhenHeaderAcceptCommandsNotPing() throws Exception {
    	k3po.join();
    }
    
    @Test
    @Specification({
    	"response.status.code.not.201/handshake.request",
    	"response.status.code.not.201/handshake.response" })
    public void shouldFailConnectionWhenResponseStatusCodeNot201() throws Exception {
    	k3po.join();
    }
    
    @Test
    @Specification({
    	"response.header.content.type.missing/handshake.request",
    	"response.header.content.type.missing/handshake.response" })
    public void shouldFailConnectionWhenResponseHeaderContentTypeIsMissing() throws Exception {
    	k3po.join();
    }
    
    @Test
    @Specification({
    	"response.header.content.type.not.text.plain.charset.utf-8/handshake.request",
    	"response.header.content.type.not.text.plain.charset.utf-8/handshake.response" })
    public void shouldFailConnectionWhenResponseHeaderContentTypeNotTextPlainCharsetUTF8() throws Exception {
    	k3po.join();
    }
    
    @Test
    @Specification({
    	"response.header.websocket.version.not.matching/handshake.request",
    	"response.header.websocket.version.not.matching/handshake.response" })
    public void shouldFailConnectionWhenResponseHeaderWebSocketVersionNotMatching() throws Exception {
    	k3po.join();
    }
    
    @Test
    @Specification({
    	"response.header.websocket.protocol.not.negotiated/handshake.request",
    	"response.header.websocket.protocol.not.negotiated/handshake.response" })
    public void shouldFailConnectionWhenWebSocketProtocolNotNegotiated() throws Exception {
    	k3po.join();
    }
    
    @Test
    @Specification({
    	"handshake.response.body.containing.one.url/handshake.request",
    	"handshake.response.body.containing.one.url/handshake.response" })
    public void shouldFailConnectionWhenHandshakeResponseBodyContainsOneUrl() throws Exception {
    	k3po.join();
    }
    
    @Test
    @Specification({
    	"upstream.url.scheme.neither.http.nor.https/handshake.request",
    	"upstream.url.scheme.neither.http.nor.https/handshake.response" })
    public void shouldFailConnectionWhenUpstreamUrlSchemeNeitherHttpNorHttps() throws Exception {
    	k3po.join();
    }
    
    @Test
    @Specification({
    	"upstream.url.host.not.matching.websocket.url.host/handshake.request",
    	"upstream.url.host.not.matching.websocket.url.host/handshake.response" })
    public void shouldFailConnectionWhenUpstreamUrlHostNotMatchingWebSocketUrlHost() throws Exception {
    	k3po.join();
    }
    
    @Test
    @Specification({
    	"upstream.url.path.not.prefixed.by.websocket.url.path/handshake.request",
    	"upstream.url.path.not.prefixed.by.websocket.url.path/handshake.response" })
    public void shouldFailConnectionWhenUpstreamUrlPathNotPrefixedByWebSocketUrlPath() throws Exception {
    	k3po.join();
    }
    
    @Test
    @Specification({
    	"downstream.url.scheme.neither.http.nor.https/handshake.request",
    	"downstream.url.scheme.neither.http.nor.https/handshake.response" })
    public void shouldFailConnectionWhenDownstreamUrlSchemeNeitherHttpNorHttps() throws Exception {
    	k3po.join();
    }
    
    @Test
    @Specification({
    	"downstream.url.host.not.matching.websocket.url.host/handshake.request",
    	"downstream.url.host.not.matching.websocket.url.host/handshake.response" })
    public void shouldFailConnectionWhenDownstreamUrlHostNotMatchingWebSocketUrlHost() throws Exception {
    	k3po.join();
    }
    
    @Test
    @Specification({
    	"downstream.url.path.not.prefixed.by.websocket.url.path/handshake.request",
    	"downstream.url.path.not.prefixed.by.websocket.url.path/handshake.response" })
    public void shouldFailConnectionWhenDownstreamUrlPathNotPrefixedByWebSocketUrlPath() throws Exception {
    	k3po.join();
    }
    
}
