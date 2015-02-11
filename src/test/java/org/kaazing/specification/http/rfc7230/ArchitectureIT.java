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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Ignore;
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

    @Rule
    public final K3poRule k3po = new K3poRule().setScriptRoot("org/kaazing/specification/http/rfc7230/architecture");

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

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
        k3po.join();
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
        k3po.join();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-2.6">RFC 7230 section 2.6: Protocol Versioning</a>.
     * @throws Exception
     */
    @Test
    @Specification({
            "response.must.be.505.on.invalid.version/request",
            "response.must.be.505.on.invalid.version/response" })
    public void inboundMustSend505OnInvalidVersion() throws Exception {
        // remember no lowercase
        k3po.join();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-2.6">RFC 7230 section 2.6: Protocol Versioning</a>.
     * @throws Exception
     */
    @Test
    @Specification({
        "inbound.must.reply.with.version.one.dot.one.when.received.higher.minor.version/request",
        "inbound.must.reply.with.version.one.dot.one.when.received.higher.minor.version/response" })
    public void inboundMustReplyWithVersionOneDotOneWhenReceivedHigherMinorVersion()
            throws Exception {
        // return response with 1.1
        k3po.join();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-2.6">RFC 7230 section 2.6: Protocol Versioning</a>.
     * @throws Exception
     */
    @Test
    @Ignore("Not Implemented")
    public void originServerShouldSend505OnMajorVersionNotEqualToOne() throws Exception {
        k3po.join();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-2.7">RFC 7230 section 2.7: Uniform Resource
     * Identifiers</a>.
     * @throws Exception
     */
    @Test
    @Ignore("Not Implemented")
    public void clientMustSendHostIdentifier() throws Exception {
        k3po.join();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-2.7">RFC 7230 section 2.7: Uniform Resource
     * Identifiers</a>.
     * @throws Exception
     */
    @Test
    @Ignore("Not Implemented")
    public void inboundMustRejectRequestsMissingHostIdentifier() throws Exception {
        // 400 Bad Request
        k3po.join();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-2.7">RFC 7230 section 2.7: Uniform Resource
     * Identifiers</a>.
     * @throws Exception
     */
    @Test
    @Ignore("Not Implemented")
    public void inboundMustRejectRequestsWithInvalidHostIdentifier() throws Exception {
        // 400 Bad Request
        k3po.join();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-2.7">RFC 7230 section 2.7: Uniform Resource
     * Identifiers</a>.
     * @throws Exception
     */
    @Test
    @Ignore("Not Implemented")
    public void inboundMustRejectRequestWithUserInfoOnURI() throws Exception {
        // ex http://localhost:8000@username:password
        k3po.join();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-2.7">RFC 7230 section 2.7: Uniform Resource
     * Identifiers</a>.
     * @throws Exception
     */
    @Test
    @Ignore("Not Implemented")
    public void inboundShouldAllowRequestsWithPercentCharsInURI() throws Exception {
        // equivalent %chars to normal chars ?
        k3po.join();
    }
}
