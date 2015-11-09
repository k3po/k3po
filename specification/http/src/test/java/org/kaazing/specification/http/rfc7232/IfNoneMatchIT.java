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
 * RFC-7232, section 3.2 "If-None-Match"
 */
public class IfNoneMatchIT {
    private final K3poRule k3po =
            new K3poRule().setScriptRoot("org/kaazing/specification/http/rfc7232/preconditions/if.none.match");
    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @Test
    @Specification({
        "multiple.etags.delete.status.400/request",
        "multiple.etags.delete.status.400/response" })
    public void shouldResultInBadRequestResponseWithDeleteAndMutipleETags() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "multiple.etags.get.status.200/request",
        "multiple.etags.get.status.200/response" })
    public void shouldResultInOKResponseWithGetAndMutipleETags() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "multiple.etags.head.status.200/request",
        "multiple.etags.head.status.200/response" })
    public void shouldResultInOKResponseWithHeadAndMutipleETags() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "multiple.etags.post.status.400/request",
        "multiple.etags.post.status.400/response" })
    public void shouldResultInBadRequestResponseWithPostAndMutipleETags() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "multiple.etags.put.status.400/request",
        "multiple.etags.put.status.400/response" })
    public void shouldResultInBadRequestResponseWithPutAndMutipleETags() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "multiple.etags.get.status.304/request",
        "multiple.etags.get.status.304/response" })
    public void shouldResultInNotModifiedResponseWithGetAndMutipleETags() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "multiple.etags.head.status.304/request",
        "multiple.etags.head.status.304/response" })
    public void shouldResultInNotModifiedResponseWithHeadAndMutipleETags() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "single.etag.delete.status.400/request",
        "single.etag.delete.status.400/response" })
    public void shouldResultBadRequestResponseWithDeleteAndSingleETag() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "single.etag.get.status.200/request",
        "single.etag.get.status.200/response" })
    public void shouldResultInOKResponseWithGetAndSingleETag() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "single.etag.get.status.304/request",
        "single.etag.get.status.304/response" })
    public void shouldResultInNotModifiedResponseWithGetAndSingleETag() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "single.etag.head.status.200/request",
        "single.etag.head.status.200/response" })
    public void shouldResultInOKResponseWithHeadAndSingleETag() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "single.etag.head.status.304/request",
        "single.etag.head.status.304/response" })
    public void shouldResultInNotModifiedResponseWithHeadAndSingleETag() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "single.etag.post.status.400/request",
        "single.etag.post.status.400/response" })
    public void shouldResultBadRequestResponseWithPostAndSingleETag() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "single.etag.put.status.400/request",
        "single.etag.put.status.400/response" })
    public void shouldResultBadRequestResponseWithPutAndSingleETag() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "wildcard.delete.status.412/request",
        "wildcard.delete.status.412/response" })
    public void shouldResultInPreconditionFailedResponseWithDeleteAndWildcard() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "wildcard.get.status.304/request",
        "wildcard.get.status.304/response" })
    public void shouldResultInNotModifiedResponseWithGetAndWildcard() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "wildcard.head.status.304/request",
        "wildcard.head.status.304/response" })
    public void shouldResultInNotModifiedResponseWithHeadAndWildcard() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "wildcard.post.status.412/request",
        "wildcard.post.status.412/response" })
    public void shouldResultInPreconditionFailedResponseWithPostAndWildcard() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "wildcard.put.status.412/request",
        "wildcard.put.status.412/response" })
    public void shouldResultInPreconditionFailedResponseWithPutAndWildcard() throws Exception {
        k3po.finish();
    }
}
