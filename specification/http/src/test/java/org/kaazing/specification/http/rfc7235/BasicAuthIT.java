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
 * Test to validate behavior as specified in <a href="https://tools.ietf.org/html/rfc7235#section-2.1">RFC 7235 section 2:1</a>, 
 * <a href="https://tools.ietf.org/html/rfc7235#section-2.2">RFC 7235 section 2:2</a>, <a href="https://tools.ietf.org/html/rfc7235#section-3.1">RFC 7235 section 3:1</a>, 
 * <a href="https://tools.ietf.org/html/rfc7235#section-4.1">RFC 7235 section 4:1</a>, and <a href="https://tools.ietf.org/html/rfc7235#section-4.2">RFC 7235 section 4:2</a>.
 */
public class BasicAuthIT {

    private final K3poRule k3po = new K3poRule()
        .setScriptRoot("org/kaazing/specification/http/rfc7235/basic");

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @Test
    @Specification({"authorized/invalid.then.valid.credentials/request",
        "authorized/invalid.then.valid.credentials/response" })
    public void authorizedInvalidThenValidCredentials() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({"authorized/missing.then.valid.credentials/request",
        "authorized/missing.then.valid.credentials/response" })
    public void authorizedMissingThenValidCredentials() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({"authorized/valid.credentials/request",
        "authorized/valid.credentials/response" })
    public void authorizedValidCredentials() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({"forbidden/response",
        "forbidden/request" })
    public void forbidden() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({"unauthorized/invalid.username.valid.password/response",
        "unauthorized/invalid.username.valid.password/request" })
    public void unauthorizedInvalidUsernameValidPassword() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({"unauthorized/no.credentials/response",
        "unauthorized/no.credentials/request" })
    public void unauthorizedNoCredentials() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({"unauthorized/unknown.user/response",
        "unauthorized/unknown.user/request" })
    public void unauthorizedUnknownUser() throws Exception {
        k3po.finish();
    }
    
    @Test
    @Specification({"unauthorized/valid.username.invalid.password/response",
        "unauthorized/valid.username.invalid.password/request" })
    public void unauthorizedValidUsernameInvalidPassword() throws Exception {
        k3po.finish();
    }

}

