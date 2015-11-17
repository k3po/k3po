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
package org.kaazing.specification.http.rfc7234;

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
 * RFC-7234, section 5.2.2 Response Cache-Control Directives
 */
public class CacheControlInResponseIT {
    private final K3poRule k3po = new K3poRule().setScriptRoot("org/kaazing/specification/http/rfc7234/response.cache-control");
    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @Test
    @Specification({
        "max-age.fresh.response.from.cache/request",
        "max-age.fresh.response.from.cache/response" })
    public void shouldReceiveStoredResponseFromCacheWhenResponseIsFresh() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "max-age.stale.response.resource.modified.200/request",
        "max-age.stale.response.resource.modified.200/response" })
    public void shouldReceiveOKForStaleResponseWhenResourceModified() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "max-age.stale.response.resource.unmodified.304/request",
        "max-age.stale.response.resource.unmodified.304/response" })
    public void shouldReceiveNotModifiedForStaleResponseWhenResourceUnmodified() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "must-revalidate.resource.modified.200/request",
        "must-revalidate.resource.modified.200/response" })
    public void shouldReceiveOKForStaleResponseWithMustRevalidateWhenResourceModified()
    		throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "must-revalidate.resource.unmodified.304/request",
        "must-revalidate.resource.unmodified.304/response" })
    public void shouldReceiveNotModifiedForStaleResponseWithMustRevalidateWhenResourceUnmodified()
    		throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "no-cache.resource.modified.200/request",
        "no-cache.resource.modified.200/response" })
    public void shouldReceiveOKWithNoCacheWhenResourceModified()
    		throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "no-cache.resource.unmodified.304/request",
        "no-cache.resource.unmodified.304/response" })
    public void shouldReceiveNotModifiedWithNoCacheWhenResourceModified()
    		throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "private.fresh.response.from.cache/request",
        "private.fresh.response.from.cache/response" })
    public void shouldReceiveStoredResponseFromCacheWithPrivateWhenResponseIsFresh() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "private.stale.response.resource.modified.200/request",
        "private.stale.response.resource.modified.200/response" })
    public void shouldReceiveOKForStaleResponseWithPrivateWhenResourceModified() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "private.stale.response.resource.unmodified.304/request",
        "private.stale.response.resource.unmodified.304/response" })
    public void shouldReceiveNotModifiedForStaleResponseWithPrivateWhenResourceUnmodified() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "proxy-revalidate.resource.modified.200/request",
        "proxy-revalidate.resource.modified.200/response" })
    public void shouldReceiveOKForStaleResponseWithProxyRevalidateWhenResourceModified()
    		throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "proxy-revalidate.resource.unmodified.304/request",
        "proxy-revalidate.resource.unmodified.304/response" })
    public void shouldReceiveNotModifiedForStaleResponseWithProxyRevalidateWhenResourceUnmodified()
    		throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "public.fresh.response.from.cache/request",
        "public.fresh.response.from.cache/response" })
    public void shouldReceiveStoredResponseFromCacheWithPublicWhenResponseIsFresh() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "public.stale.response.resource.modified.200/request",
        "public.stale.response.resource.modified.200/response" })
    public void shouldReceiveOKForStaleResponseWithPublicWhenResourceModified() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "public.stale.response.resource.unmodified.304/request",
        "public.stale.response.resource.unmodified.304/response" })
    public void shouldReceiveNotModifiedForStaleResponseWithPublicWhenResourceUnmodified() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "s-maxage.fresh.response.from.cache/request",
        "s-maxage.fresh.response.from.cache/response" })
    public void shouldReceiveStoredResponseFromCacheWithSharedMaxageWhenResponseIsFresh() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "s-maxage.stale.response.resource.modified.200/request",
        "s-maxage.stale.response.resource.modified.200/response" })
    public void shouldReceiveOKForStaleResponseWithSharedMaxageWhenResourceModified() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "s-maxage.stale.response.resource.unmodified.304/request",
        "s-maxage.stale.response.resource.unmodified.304/response" })
    public void shouldReceiveNotModifiedForStaleResponseWithSharedMaxageWhenResourceUnmodified() throws Exception {
        k3po.finish();
    }

}
