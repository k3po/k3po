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
    public void shouldReceiveOKWhenCachedResponseIsStaleForUnconditionalRequestWithMaxAge() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "must-revalidate.conditional.request.304/request",
        "must-revalidate.conditional.request.304/response" })
    public void shouldReceiveNotModifiedWhenCachedResponseIsStaleForConditionalRequestWithMustRevalidate()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "must-revalidate.unconditional.request.200/request",
        "must-revalidate.unconditional.request.200/response" })
    public void shouldReceiveOKWhenCachedResponseIsStaleForUnconditionalRequestWithMustRevalidate()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "no-cache.conditional.request.304/request",
        "no-cache.conditional.request.304/response" })
    public void shouldReceiveNotModifiedWithNoCacheForConditionalRequest()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "no-cache.unconditional.request.200/request",
        "no-cache.unconditional.request.200/response" })
    public void shouldReceiveOKWithNoCacheForUnconditionalRequest()
            throws Exception {
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
        "private.fresh.response.from.cache/request",
        "private.fresh.response.from.cache/response" })
    public void shouldReceiveCachedResponseWithPrivateWhenCachedResponseIsFresh() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "private.stale.response.conditional.request.304/request",
        "private.stale.response.conditional.request.304/response" })
    public void shouldReceiveNotModifiedWhenCachedResponseIsStaleForConditionalRequestWithPrivate() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "private.stale.response.unconditional.request.200/request",
        "private.stale.response.unconditional.request.200/response" })
    public void shouldReceiveOKWhenCachedResponseIsStaleForUnconditionalRequestWithPrivate() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "proxy-revalidate.conditional.request.304/request",
        "proxy-revalidate.conditional.request.304/response" })
    public void shouldReceiveNotModifiedWhenCachedResponseIsStaleForConditionalRequestWithProxyRevalidate()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "proxy-revalidate.unconditional.request.200/request",
        "proxy-revalidate.unconditional.request.200/response" })
    public void shouldReceiveOKWhenCachedResponseIsStaleForUnconditionalRequestWithProxyRevalidate()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "public.fresh.response.from.cache/request",
        "public.fresh.response.from.cache/response" })
    public void shouldReceiveCachedResponseWithPublicWhenCachedResponseIsFresh() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "public.stale.response.conditional.request.304/request",
        "public.stale.response.conditional.request.304/response" })
    public void shouldReceiveNotModifiedWhenCachedResponseIsStaleForConditionalRequestWithPublic() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "public.stale.response.unconditional.request.200/request",
        "public.stale.response.unconditional.request.200/response" })
    public void shouldReceiveOKWhenCachedResponseIsStaleForUnconditionalRequestWithPublic() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "s-maxage.fresh.response.from.cache/request",
        "s-maxage.fresh.response.from.cache/response" })
    public void shouldReceiveCachedResponseWithSharedMaxAgeWhenCachedResponseIsFresh() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "s-maxage.stale.response.conditional.request.304/request",
        "s-maxage.stale.response.conditional.request.304/response" })
    public void shouldReceiveNotModifiedWhenCachedResponseIsStaleForConditionalRequestWithSharedMaxAge() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "s-maxage.stale.response.unconditional.request.200/request",
        "s-maxage.stale.response.unconditional.request.200/response" })
    public void shouldReceiveOKWhenCachedResponseIsStaleForUnconditionalRequestWithSharedMaxAge() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "must-revalidate.504/request",
        "must-revalidate.504/response" })
    public void shouldRespondToMustRevalidateHeaderWith504() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "no-cache.with.fields/request",
        "no-cache.with.fields/response" })
    public void shouldSucceedWithNoCacheHeaderWithFields() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "private.with.fields/request",
        "private.with.fields/response" })
    public void shouldSucceedWithPrivateHeaderWithFields() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "proxy-revalidate.504/request",
        "proxy-revalidate.504/response" })
    public void shouldRespondToProxyRevalidateWith504() throws Exception {
        k3po.finish();
    }
}
