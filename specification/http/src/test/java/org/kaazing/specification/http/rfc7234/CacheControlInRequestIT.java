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
 * RFC-7234, section 5.2.1 Request Cache-Control Directives
 */
public class CacheControlInRequestIT {
    private final K3poRule k3po = new K3poRule().setScriptRoot("org/kaazing/specification/http/rfc7234/request.cache-control");
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
        "max-stale.any.age.from.cache/request",
        "max-stale.any.age.from.cache/response" })
    public void shouldReceiveStoredResponseFromCacheForMaxStaleWithNoValue() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "max-stale.stale.response.from.cache/request",
        "max-stale.stale.response.from.cache/response" })
    public void shouldReceiveStoredStaleResponseFromCacheForMaxStaleWithinLimit() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "max-stale.stale.response.resource.modified.200/request",
        "max-stale.stale.response.resource.modified.200/response" })
    public void shouldReceiveOKWhenMaxStaleExceedsLimitAndResourceModified() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "max-stale.stale.response.resource.unmodified.304/request",
        "max-stale.stale.response.resource.unmodified.304/response" })
    public void shouldReceiveNotModifiedWhenMaxStaleExceedsLimitAndResourceUnmodified() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "min-fresh.fresh.response.from.cache/request",
        "min-fresh.fresh.response.from.cache/response" })
    public void shouldReceiveStoredResponseFromCacheForMinFreshWithinLimit() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "min-fresh.fresh.response.resource.modified.200/request",
        "min-fresh.fresh.response.resource.modified.200/response" })
    public void shouldReceiveOKWhenMinFreshExceedsLimitForFreshResponseAndResourceModified() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "min-fresh.fresh.response.resource.unmodified.304/request",
        "min-fresh.fresh.response.resource.unmodified.304/response" })
    public void shouldReceiveNotModifiedWhenMinFreshExceedsLimitForFreshResponseAndResourceUnmodified()
    		throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "min-fresh.stale.response.resource.modified.200/request",
        "min-fresh.stale.response.resource.modified.200/response" })
    public void shouldReceiveOKWhenMinFreshExceedsLimitForStaleResponseAndResourceModified() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "min-fresh.stale.response.resource.unmodified.304/request",
        "min-fresh.stale.response.resource.unmodified.304/response" })
    public void shouldReceiveNotModifiedWhenMinFreshExceedsLimitForStaleResponseAndResourceUnmodified()
    		throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "no-cache.resource.modified.200/request",
        "no-cache.resource.modified.200/response" })
    public void shouldReceiveOKWithNoCacheAndResourceModified() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "no-cache.resource.unmodified.304/request",
        "no-cache.resource.unmodified.304/response" })
    public void shouldReceiveNotModifiedWithNoCacheAndResourceUnmodified() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "only-if-cached.from.cache/request",
        "only-if-cached.from.cache/response" })
    public void shouldReceiveStoredResponseFromCacheWithOnlyIfCachedAndReachableCache() throws Exception {
        k3po.finish();
    }

}
