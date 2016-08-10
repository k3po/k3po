/**
 * Copyright 2007-2015, Kaazing Corporation. All rights reserved.
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
package org.kaazing.specification.http.rfc7230;

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
 * Test to validate behavior as specified in <a href="https://tools.ietf.org/html/rfc7230#section-2">RFC 7230 section 2:
 * Architecture</a>.
 */
public class ArchitectureIT {

    private final K3poRule k3po = new K3poRule().setScriptRoot("org/kaazing/specification/http/rfc7230/architecture");

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-2.6">RFC 7230 section 2.6: Protocol Versioning</a>.
     * @throws Exception when K3PO is not started
     */
    @Test
    @Specification({
        "outbound.must.send.version/request",
        "outbound.must.send.version/response" })
    public void outboundMustSendVersion() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-2.6">RFC 7230 section 2.6: Protocol Versioning</a>.
     * @throws Exception when K3PO is not started
     */
    @Test
    @Specification({
        "inbound.must.send.version/request",
        "inbound.must.send.version/response" })
    public void inboundMustSendVersion() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-2.6">RFC 7230 section 2.6: Protocol Versioning</a>.
     * @throws Exception
     */
    @Test
    @Specification({
        "response.must.be.400.on.invalid.version/request",
        "response.must.be.400.on.invalid.version/response" })
    public void inboundMustSend400OnInvalidVersion() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-2.6">RFC 7230 section 2.6: Protocol Versioning</a>.
     * @throws Exception
     */
    @Test
    @Specification({
        "inbound.must.reply.with.version.one.dot.one.when.received.higher.minor.version/request",
        "inbound.must.reply.with.version.one.dot.one.when.received.higher.minor.version/response" })
    public void inboundMustReplyWithVersionOneDotOneWhenReceivedHigherMinorVersion() throws Exception {
        // return response with 1.1
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-2.6">RFC 7230 section 2.6: Protocol Versioning</a>.
     * @throws Exception
     */
    @Test
    @Specification({
        "origin.server.should.send.505.on.major.version.not.equal.to.one/request",
        "origin.server.should.send.505.on.major.version.not.equal.to.one/response" })
    public void originServerShouldSend505OnMajorVersionNotEqualToOne() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-2.7">RFC 7230 section 2.7: Uniform Resource
     * Identifiers</a>.
     * @throws Exception
     */
    @Test
    @Specification({
        "client.must.send.host.identifier/request",
        "client.must.send.host.identifier/response" })
    public void clientMustSendHostIdentifier() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-2.7">RFC 7230 section 2.7: Uniform Resource
     * Identifiers</a>.
     * @throws Exception
     */
    @Specification({
        "inbound.must.reject.requests.missing.host.identifier/request",
        "inbound.must.reject.requests.missing.host.identifier/response" })
    @Test
    public void inboundMustRejectRequestsMissingHostIdentifier() throws Exception {
        // 400 Bad Request
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-2.7">RFC 7230 section 2.7: Uniform Resource
     * Identifiers</a>.
     *
     * A sender MUST NOT generate the userinfo subcomponent (and its "@" delimiter) when an "http" URI reference is
     * generated within a message as a request target or header field value. Before making use of an "http" URI
     * reference received from an untrusted source, a recipient SHOULD parse for userinfo and treat its presence as an
     * error; it is likely being used to obscure the authority for the sake of phishing attacks.
     *
     * @throws Exception
     */
    @Test
    @Specification({
        "inbound.must.reject.requests.with.user.info.on.uri/request",
        "inbound.must.reject.requests.with.user.info.on.uri/response" })
    public void inboundMustRejectRequestWithUserInfoOnURI() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-2.7">RFC 7230 section 2.7: Uniform Resource
     * Identifiers</a>.
     * @throws Exception
     */
    @Test
    @Specification({
        "inbound.should.allow.requests.with.percent.chars.in.uri/request",
        "inbound.should.allow.requests.with.percent.chars.in.uri/response" })
    public void inboundShouldAllowRequestsWithPercentCharsInURI() throws Exception {
        // equivalent %chars to normal chars ?
        k3po.finish();
    }
}
