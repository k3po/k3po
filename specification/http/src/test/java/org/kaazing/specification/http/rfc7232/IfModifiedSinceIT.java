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
 * RFC-7232, section 3.3 "If-Modified-Since"
 */
public class IfModifiedSinceIT {
    private final K3poRule k3po =
            new K3poRule().setScriptRoot("org/kaazing/specification/http/rfc7232/preconditions/if.modified.since");
    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @Test
    @Specification({
        "condition.failed.get.status.304/request",
        "condition.failed.get.status.304/response" })
    public void shouldResultInNotModifiedResponseWithGetAndConditionFailed() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "condition.failed.head.status.304/request",
        "condition.failed.head.status.304/response" })
    public void shouldResultInNotModifiedResponseWithHeadAndConditionFailed() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "condition.passed.get.status.200/request",
        "condition.passed.get.status.200/response" })
    public void shouldResultInOKResponseWithGetAndConditionPassed() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "condition.failed.head.status.200/request",
        "condition.failed.head.status.200/response" })
    public void shouldResultInNotModifiedResponseWithHeadAndConditionPassed() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "ignored.with.delete/request",
        "ignored.with.delete/response" })
    public void shouldIgnoreIfModifiedSinceHeaderWithDelete() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "ignored.with.post/request",
        "ignored.with.post/response" })
    public void shouldIgnoreIfModifiedSinceHeaderWithPost() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "ignored.with.put/request",
        "ignored.with.put/response" })
    public void shouldIgnoreIfModifiedSinceHeaderWithPut() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "ignored.with.get.and.if.none.match/request",
        "ignored.with.get.and.if.none.match/response" })
    public void shouldIgnoreIfModifiedSinceHeaderAsGetAlsoContainsIfNoneMatchHeader() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "ignored.with.head.and.if.none.match/request",
        "ignored.with.head.and.if.none.match/response" })
    public void shouldIgnoreIfModifiedSinceHeaderAsHeadAlsoContainsIfNoneMatchHeader() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "ignored.with.get.and.invalid.http.date/request",
        "ignored.with.get.and.invalid.http.date/response" })
    public void shouldIgnoreIfModifiedSinceHeaderWithInvalidDateInGet() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "ignored.with.head.and.invalid.http.date/request",
        "ignored.with.head.and.invalid.http.date/response" })
    public void shouldIgnoreIfModifiedSinceHeaderWithInvalidDateInHead() throws Exception {
        k3po.finish();
    }
}
