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
 * RFC-7234, section 5.3 Expires
 */
public class ExpiresIT {
    private final K3poRule k3po = new K3poRule().setScriptRoot("org/kaazing/specification/http/rfc7234/expires");
    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @Test
    @Specification({
        "already.expired.conditional.request.304/request",
        "already.expired.conditional.request.304/response" })
    public void shouldReceiveOKWhenCacheResponseExpiredForUnconditionalRequest() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "already.expired.unconditional.request.200/request",
        "already.expired.unconditional.request.200/response" })
    public void shouldReceiveNotModifiedWhenCacheResponseExpiredForConditionalRequest() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "fresh.response.from.cache/request",
        "fresh.response.from.cache/response" })
    public void shouldReceiveUnexpiredResponseFromCache() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "ignored.when.cache-control.max-age.is.available/request",
        "ignored.when.cache-control.max-age.is.available/response" })
    public void shouldIgnoreExpiresHeaderWhenCacheControlMaxAgeIsAvailable() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "ignored.when.cache-control.s-maxage.is.available/request",
        "ignored.when.cache-control.s-maxage.is.available/response" })
    public void shouldIgnoreExpiresHeaderWhenCacheControlSharedMaxAgeIsAvailable() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "ignored.when.multiple.expires.200/request",
        "ignored.when.multiple.expires.200/response" })
    public void shouldIgnoreMultipleExpiresHeaderInResponse() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "invalid.date.conditional.request.304/request",
        "invalid.date.conditional.request.304/response" })
    public void shouldReceiveNotModifiedWhenCachedResponseIsStaleDueToInvalidDateForConditionalRequest() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "invalid.date.unconditional.request.200/request",
        "invalid.date.unconditional.request.200/response" })
    public void shouldReceiveOKWhenCachedResponseIsStaleDueToInvalidDateForUnconditionalRequest() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "stale.response.conditional.request.304/request",
        "stale.response.conditional.request.304/response" })
    public void shouldReceiveNotModifiedWhenCachedResponseIsStaleForConditionalRequest() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "stale.response.unconditional.request.200/request",
        "stale.response.unconditional.request.200/response" })
    public void shouldReceiveOKWhenCachedResponseIsStaleForUnconditionalRequest() throws Exception {
        k3po.finish();
    }
}
