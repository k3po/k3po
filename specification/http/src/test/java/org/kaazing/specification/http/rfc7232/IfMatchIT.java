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
 * RFC-7232, section 3.1 "If-Match"
 */
public class IfMatchIT {
    private final K3poRule k3po = new K3poRule().setScriptRoot("org/kaazing/specification/http/rfc7232/preconditions/if.match");
    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @Test
    @Specification({
        "multiple.etags.delete.status.200/request",
        "multiple.etags.delete.status.200/response" })
    public void shouldSucceedWithDeleteAndMatchingETagInTheList() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "multiple.etags.get.status.200/request",
        "multiple.etags.get.status.200/response" })
    public void shouldIgnoreIfMatchHeaderWithGetAndMatchingETagInTheList() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "multiple.etags.head.status.200/request",
        "multiple.etags.head.status.200/response" })
    public void shouldIgnoreIfMatchHeaderWithHeadAndMatchingETagInTheList() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "multiple.etags.post.status.200/request",
        "multiple.etags.post.status.200/response" })
    public void shouldSucceedWithPostAndMatchingETagInTheList() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "multiple.etags.put.status.200/request",
        "multiple.etags.put.status.200/response" })
    public void shouldSucceedWithPutAndMatchingETagInTheList() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "unexpected.etags.delete.status.412/request",
        "unexpected.etags.delete.status.412/response" })
    public void shouldCausePreconditionFailedWithDeleteAndNoMatchingETagsInTheList() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "unexpected.etags.get.status.200/request",
        "unexpected.etags.get.status.200/response" })
    public void shouldIgnoreIfMatchHeaderWithGetAndNoMatchingETagsInTheList() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "unexpected.etags.head.status.200/request",
        "unexpected.etags.head.status.200/response" })
    public void shouldIgnoreIfMatchHeaderWithHeadAndNoMatchingETagsInTheList() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "unexpected.etags.post.status.412/request",
        "unexpected.etags.post.status.412/response" })
    public void shouldCausePreconditionFailedWithPostAndNoMatchingETagsInTheList() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "unexpected.etags.put.status.412/request",
        "unexpected.etags.put.status.412/response" })
    public void shouldCausePreconditionFailedWithPutAndNoMatchingETagsInTheList() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "strong.etag.delete.status.200/request",
        "strong.etag.delete.status.200/response" })
    public void shouldSucceedWithDeleteAndMatchingStrongETag() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "strong.etag.get.status.200/request",
        "strong.etag.get.status.200/response" })
    public void shouldSucceedWithGetAndMatchingStrongETag() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "strong.etag.head.status.200/request",
        "strong.etag.head.status.200/response" })
    public void shouldSucceedWithHeadAndMatchingStrongETag() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "strong.etag.post.status.200/request",
        "strong.etag.post.status.200/response" })
    public void shouldSucceedWithPostAndMatchingStrongETag() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "strong.etag.put.status.200/request",
        "strong.etag.put.status.200/response" })
    public void shouldSucceedWithPutAndMatchingStrongETag() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "strong.unmatched.etag.delete.status.412/request",
        "strong.unmatched.etag.delete.status.412/response" })
    public void shouldCausePreconditionFailedWithDeleteAndUnmatchedStrongETag() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "strong.unmatched.etag.get.status.200/request",
        "strong.unmatched.etag.get.status.200/response" })
    public void shouldIgnoreIfMatchHeaderWithGetAndUnmatchedStrongETag() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "strong.unmatched.etag.head.status.200/request",
        "strong.unmatched.etag.head.status.200/response" })
    public void shouldIgnoreIfMatchHeaderWithHeadAndUnmatchedStrongETag() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "strong.unmatched.etag.post.status.412/request",
        "strong.unmatched.etag.post.status.412/response" })
    public void shouldCausePreconditionFailedWithPostAndUnmatchedStrongETag() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "strong.unmatched.etag.put.status.412/request",
        "strong.unmatched.etag.put.status.412/response" })
    public void shouldCausePreconditionFailedWithPutAndUnmatchedStrongETag() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "weak.etag.delete.status.412/request",
        "weak.etag.delete.status.412/response" })
    public void shouldCausePreconditionFailedWithDeleteAndWeakETag() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "weak.etag.get.status.200/request",
        "weak.etag.get.status.200/response" })
    public void shouldIgnoreIfMatchHeaderWithGetAndWeakETag() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "weak.etag.head.status.200/request",
        "weak.etag.head.status.200/response" })
    public void shouldIgnoreIfMatchHeaderWithHeadAndWeakETag() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "weak.etag.post.status.412/request",
        "weak.etag.post.status.412/response" })
    public void shouldCausePreconditionFailedWithPostAndWeakETag() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "weak.etag.put.status.412/request",
        "weak.etag.put.status.412/response" })
    public void shouldCausePreconditionFailedWithPutAndWeakETag() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "wildcard.etag.delete.status.200/request",
        "wildcard.etag.delete.status.200/response" })
    public void shouldSucceedWithDeleteAndWildcardForValidResource() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "wildcard.etag.get.status.200/request",
        "wildcard.etag.get.status.200/response" })
    public void shouldSucceedWithGetAndWildcardForValidResource() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "wildcard.etag.head.status.200/request",
        "wildcard.etag.head.status.200/response" })
    public void shouldSucceedWithHeadAndWildcardForValidResource() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "wildcard.etag.post.status.200/request",
        "wildcard.etag.post.status.200/response" })
    public void shouldSucceedWithPostAndWildcardForValidResource() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "wildcard.etag.put.status.200/request",
        "wildcard.etag.put.status.200/response" })
    public void shouldSucceedWithPutAndWildcardForValidResource() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "wildcard.etag.delete.status.412/request",
        "wildcard.etag.delete.status.412/response" })
    public void shouldCausePreconditionFailedWithDeleteAndWildcardForInvalidResource() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "wildcard.etag.post.status.412/request",
        "wildcard.etag.post.status.412/response" })
    public void shouldCausePreconditionFailedWithPostAndWildcardForInvalidResource() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "wildcard.etag.put.status.412/request",
        "wildcard.etag.put.status.412/response" })
    public void shouldCausePreconditionFailedWithPutAndWildcardForInvalidResource() throws Exception {
        k3po.finish();
    }

}
