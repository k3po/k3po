/**
 * Copyright 2007-2015, Kaazing Corporation. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
    public void shouldReceiveCachedResponseWithMaxAgeWhenCachedResponseIsFresh() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "max-age.stale.response.conditional.request.304/request",
        "max-age.stale.response.conditional.request.304/response" })
    public void shouldReceiveNotModifiedWhenCachedResponseIsStaleForConditionalRequestWithMaxAge() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "max-age.stale.response.unconditional.request.200/request",
        "max-age.stale.response.unconditional.request.200/response" })
    public void shouldReceiveNotModifiedWhenCachedResponseIsStaleForUnconditionalRequestWithMaxAge() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "max-stale.any.age.from.cache/request",
        "max-stale.any.age.from.cache/response" })
    public void shouldReceiveCachedResponseForMaxStaleWithNoValue() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "max-stale.stale.response.from.cache/request",
        "max-stale.stale.response.from.cache/response" })
    public void shouldReceiveCachedResponseForMaxStaleWithinLimit() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "max-stale.stale.response.conditional.request.304/request",
        "max-stale.stale.response.conditional.request.304/response" })
    public void shouldReceiveNotModifiedWithStaleCachedResponseWhenMaxStaleExceedsLimitForConditionalRequest() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "max-stale.stale.response.unconditional.request.200/request",
        "max-stale.stale.response.unconditional.request.200/response" })
    public void shouldReceiveOKWithStaleCachedResponseWhenMaxStaleExceedsLimitForUnconditionalRequest() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "min-fresh.fresh.response.from.cache/request",
        "min-fresh.fresh.response.from.cache/response" })
    public void shouldReceiveCachedResponseForMinFreshWithinLimit() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "min-fresh.fresh.response.conditional.request.304/request",
        "min-fresh.fresh.response.conditional.request.304/response" })
    public void shouldReceiveNotModifiedWithFreshCachedResponseWhenMinFreshExceedsLimitForForConditionalRequest()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "min-fresh.fresh.response.unconditional.request.200/request",
        "min-fresh.fresh.response.unconditional.request.200/response" })
    public void shouldReceiveOKWithFreshCachedResponseWhenMinFreshExceedsLimitForForUnconditionalRequest() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "min-fresh.stale.response.conditional.request.304/request",
        "min-fresh.stale.response.conditional.request.304/response" })
    public void shouldReceiveNotModifiedWithStaleCachedResponseWhenMinFreshExceedsLimitForConditionalRequest()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "min-fresh.stale.response.unconditional.request.200/request",
        "min-fresh.stale.response.unconditional.request.200/response" })
    public void shouldReceiveNotModifiedWithStaleCachedResponseWhenMinFreshExceedsLimitForUnconditionalRequest()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "no-cache.conditional.request.304/request",
        "no-cache.conditional.request.304/response" })
    public void shouldReceiveNotModifiedWithNoCacheForConditionalRequest() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "no-cache.unconditional.request.200/request",
        "no-cache.unconditional.request.200/response" })
    public void shouldReceiveOKWithNoCacheForUnconditionalRequest() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "no-transform/request",
        "no-transform/response" })
    public void shouldReceiveUntransformedCachedResponse() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "only-if-cached.from.cache/request",
        "only-if-cached.from.cache/response" })
    public void shouldReceiveCachedResponseWithOnlyIfCachedAndReachableCache() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "max-stale.with.warn-code.110/request",
        "max-stale.with.warn-code.110/response" })
    public void shouldGiveWarningCode110WithMaxStale() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "max-stale.with.warn-code.112/request",
        "max-stale.with.warn-code.112/response" })
    public void shouldGiveWarningCode112WithMaxStale() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "only-if-cached.504/request",
        "only-if-cached.504/response" })
    public void shouldRespondToOnlyIfCachedWith504() throws Exception {
        k3po.finish();
    }
}
