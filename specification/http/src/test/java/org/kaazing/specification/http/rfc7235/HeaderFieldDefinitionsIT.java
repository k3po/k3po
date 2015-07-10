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
package org.kaazing.specification.http.rfc7235;

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
 * Test to validate behavior as specified in <a href="https://tools.ietf.org/html/rfc7235#section-4">RFC 7235 section 4:
 * Header Field Definitions</a>.
 */
public class HeaderFieldDefinitionsIT {

    private final K3poRule k3po = new K3poRule().setScriptRoot("org/kaazing/specification/http/rfc7235/headers");

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @Test
    @Specification({
        "invalid.user/response",
        "invalid.user/request" })
    public void shouldRespond401ToInvalidUser() throws Exception {
        k3po.finish();
    }

    @Ignore("TODO")
    @Test
    public void proxyMustNotModifyWWWAuthenticateHeader() {

    }

    @Ignore("TODO")
    @Test
    public void proxyMustNotAlterAuthenticationHeader() {

    }

    @Ignore("TODO")
    @Test
    public void secureProxyShouldSend407ToAnyUnAuthorizedRequest() {
        // The "Proxy-Authenticate" header field consists of at least one
        // challenge that indicates the authentication scheme(s) and parameters
        // applicable to the proxy for this effective request URI (Section 5.5
        // of [RFC7230]). A proxy MUST send at least one Proxy-Authenticate
        // header field in each 407 (Proxy Authentication Required) response
        // that it generates.
    }

    @Ignore("TODO")
    @Test
    public void clientMaySendProxyAuthorizationHeaderInResponseTo407() {
        // The "Proxy-Authorization" header field allows the client to identify
        // itself (or its user) to a proxy that requires authentication. Its
        // value consists of credentials containing the authentication
        // information of the client for the proxy and/or realm of the resource
        // being requested.
    }

}

