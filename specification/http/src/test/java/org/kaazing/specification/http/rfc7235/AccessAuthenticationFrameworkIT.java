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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.kaazing.k3po.junit.annotation.Specification;
import org.kaazing.k3po.junit.rules.K3poRule;


/**
 * Test to validate behavior as specified in <a href="https://tools.ietf.org/html/rfc7235#section-2">RFC 7235 section 2:
 * Access Authentication Framework</a>.
 */
public class AccessAuthenticationFrameworkIT {

    private final K3poRule k3po = new K3poRule().setScriptRoot("org/kaazing/specification/http/rfc7235/framework");

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @Test
    @Specification({
        "forbidden/request",
        "forbidden/response" })
    public void shouldRespondWithForbiddenStatusCode() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "invalid.then.valid.credentials/request",
        "invalid.then.valid.credentials/response" })
    public void shouldRespondWithUnauthorizedStatusCodeWithInvalidCredentials() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "missing.then.valid.credentials/request",
        "missing.then.valid.credentials/response" })
    public void shouldRespondWithUnauthorizedStatusCodeWithMissingCredentials() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "partial.then.valid.credentials/request",
        "partial.then.valid.credentials/response" })
    public void shouldRespondWithUnauthorizedStatusCodeWithPartialCredentials() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "proxy.authentication/request",
        "proxy.authentication/response"
    })
    public void shouldPassWithProxyAuthentication() throws Exception {
        k3po.finish();
    }

}

