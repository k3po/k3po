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

/**
 *  Test to validate behavior as specified in <a href="https://tools.ietf.org/html/rfc7232">RFC 7232:
 *   Hypertext Transfer Protocol (HTTP/1.1): Conditional Requests</a>.
 */
package org.kaazing.specification.http.rfc7232;

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
 * Test to validate behavior as specified in <a href="https://tools.ietf.org/html/rfc7232">RFC 7232</a>.
 */
public class HeaderFieldsIT {

    private final K3poRule k3po = new K3poRule().setScriptRoot("org/kaazing/specification/http/rfc7232/headers");

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @Test
    @Specification({
        "get.request.with.invalid.if.match.header/request",
        "get.request.with.invalid.if.match.header/response" })
    public void shouldFailWithInvalidIfMatchHeader() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "get.request.with.valid.if.match.header/request",
        "get.request.with.valid.if.match.header/response" })
    public void shouldPassWithValidIfMatchHeader() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "put.request.with.invalid.if.none.match.header/request",
        "put.request.with.invalid.if.none.match.header/response" })
    public void shouldFailWithPutRequestAndInvalidIfMatchHeader() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "get.request.with.valid.if.none.match.header/request",
        "get.request.with.valid.if.none.match.header/response" })
    public void shouldPassWithValidIfNoneMatchHeader() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "get.request.with.if.modified.since.header/request",
        "get.request.with.if.modified.since.header/response" })
    public void shouldPassWithIfModifiedSinceHeader() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "get.request.with.if.unmodified.since.header/request",
        "get.request.with.if.unmodified.since.header/response" })
    public void shouldPassWithIfUnmodifiedSinceHeader() throws Exception {
        k3po.finish();
    }

}
