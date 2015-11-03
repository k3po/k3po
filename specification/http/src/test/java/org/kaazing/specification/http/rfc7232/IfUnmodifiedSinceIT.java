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
 * RFC-7232, section 3.4 "If-Unmodified-Since"
 */
public class IfUnmodifiedSinceIT {
    private final K3poRule k3po =
            new K3poRule().setScriptRoot("org/kaazing/specification/http/rfc7232/preconditions/if.unmodified.since");
    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @Test
    @Specification({
        "condition.failed.delete.status.412/request",
        "condition.failed.delete.status.412/response" })
    public void shouldResultInPreconditionFailedResponseWithDeleteAndConditionFailed() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "condition.failed.post.status.412/request",
        "condition.failed.post.status.412/response" })
    public void shouldResultInPreconditionFailedResponseWithPostAndConditionFailed() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "condition.failed.put.status.412/request",
        "condition.failed.put.status.412/response" })
    public void shouldResultInPreconditionFailedResponseWithPutAndConditionFailed() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "condition.passed.delete.status.200/request",
        "condition.passed.delete.status.200/response" })
    public void shouldResultInSuccessfulResponseWithDeleteAndConditionPassed() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "condition.passed.post.status.200/request",
        "condition.passed.post.status.200/response" })
    public void shouldResultInSuccessfulResponseWithPostAndConditionPassed() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "condition.passed.put.status.200/request",
        "condition.passed.put.status.200/response" })
    public void shouldResultInSuccessfulResponseWithPutAndConditionPassed() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "ignored.with.get/request",
        "ignored.with.get/response" })
    public void shouldIgnoreIfUnmodifiedSinceHeaderWithGet() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "ignored.with.head/request",
        "ignored.with.head/response" })
    public void shouldIgnoreIfUnmodifiedSinceHeaderWithHead() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "ignored.with.delete.and.invalid.http.date/request",
        "ignored.with.delete.and.invalid.http.date/response" })
    public void shouldIgnoreIfUnmodifiedSinceHeaderWithInvalidDateInDelete() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "ignored.with.post.and.invalid.http.date/request",
        "ignored.with.post.and.invalid.http.date/response" })
    public void shouldIgnoreIfUnmodifiedSinceHeaderWithInvalidDateInPost() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "ignored.with.put.and.invalid.http.date/request",
        "ignored.with.put.and.invalid.http.date/response" })
    public void shouldIgnoreIfUnmodifiedSinceHeaderWithInvalidDateInPut() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "ignored.with.delete.and.if.match/request",
        "ignored.with.delete.and.if.match/response" })
    public void shouldIgnoreIfUnmodifiedSinceHeaderAsDeleteAlsoContainsIfMatchHeader() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "ignored.with.post.and.if.match/request",
        "ignored.with.post.and.if.match/response" })
    public void shouldIgnoreIfUnmodifiedSinceHeaderAsPostAlsoContainsIfMatchHeader() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "ignored.with.put.and.if.match/request",
        "ignored.with.put.and.if.match/response" })
    public void shouldIgnoreIfUnmodifiedSinceHeaderAsPutAlsoContainsIfMatchHeader() throws Exception {
        k3po.finish();
    }
}
