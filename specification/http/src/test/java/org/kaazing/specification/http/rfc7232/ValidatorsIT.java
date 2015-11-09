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
 * RFC-7232, section 2.2 "Last-Modified" and section 2.3 "Etag"
 */
public class ValidatorsIT {

    private final K3poRule k3po = new K3poRule().setScriptRoot("org/kaazing/specification/http/rfc7232/validators");

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @Test
    @Specification({
        "last.modified.in.get/request",
        "last.modified.in.get/response" })
    public void shouldReceiveLastModifiedInGetResponse() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "last.modified.in.head/request",
        "last.modified.in.head/response" })
    public void shouldReceiveLastModifiedInHeadResponse() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "last.modified.in.post/request",
        "last.modified.in.post/response" })
    public void shouldReceiveLastModifiedInPostResponse() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "last.modified.in.put/request",
        "last.modified.in.put/response" })
    public void shouldReceiveLastModifiedInPutResponse() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "last.modified.with.strong.etag.in.get/request",
        "last.modified.with.strong.etag.in.get/response" })
    public void shouldReceiveLastModifiedAndStrongETagInGetResponse() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "last.modified.with.strong.etag.in.head/request",
        "last.modified.with.strong.etag.in.head/response" })
    public void shouldReceiveLastModifiedAndStrongETagInHeadResponse() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "last.modified.with.strong.etag.in.post/request",
        "last.modified.with.strong.etag.in.post/response" })
    public void shouldReceiveLastModifiedAndStrongETagInPostResponse() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "last.modified.with.strong.etag.in.put/request",
        "last.modified.with.strong.etag.in.put/response" })
    public void shouldReceiveLastModifiedAndStrongETagInPutResponse() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "last.modified.with.weak.etag.in.get/request",
        "last.modified.with.weak.etag.in.get/response" })
    public void shouldReceiveLastModifiedAndWeakETagInGetResponse() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "last.modified.with.weak.etag.in.head/request",
        "last.modified.with.weak.etag.in.head/response" })
    public void shouldReceiveLastModifiedAndWeakETagInHeadResponse() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "last.modified.with.weak.etag.in.post/request",
        "last.modified.with.weak.etag.in.post/response" })
    public void shouldReceiveLastModifiedAndWeakETagInPostResponse() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "last.modified.with.weak.etag.in.put/request",
        "last.modified.with.weak.etag.in.put/response" })
    public void shouldReceiveLastModifiedAndWeakETagInPutResponse() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "strong.etag.in.get/request",
        "strong.etag.in.get/response" })
    public void shouldReceiveStrongETagInGetResponse() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "strong.etag.in.head/request",
        "strong.etag.in.head/response" })
    public void shouldReceiveStrongETagInHeadResponse() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "strong.etag.in.post/request",
        "strong.etag.in.post/response" })
    public void shouldReceiveStrongETagInPostResponse() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "strong.etag.in.put/request",
        "strong.etag.in.put/response" })
    public void shouldReceiveStrongETagInPutResponse() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "weak.etag.in.get/request",
        "weak.etag.in.get/response" })
    public void shouldReceiveWeakETagInGetResponse() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "weak.etag.in.head/request",
        "weak.etag.in.head/response" })
    public void shouldReceiveWeakETagInHeadResponse() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "weak.etag.in.post/request",
        "weak.etag.in.post/response" })
    public void shouldReceiveWeakETagInPostResponse() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "weak.etag.in.put/request",
        "weak.etag.in.put/response" })
    public void shouldReceiveWeakETagInPutResponse() throws Exception {
        k3po.finish();
    }
}

