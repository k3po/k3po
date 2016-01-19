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
 * RFC-7234, section 5.4 Pragma
 */
public class PragmaIT {
    private final K3poRule k3po = new K3poRule().setScriptRoot("org/kaazing/specification/http/rfc7234/pragma");
    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

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
        "no-cache.with.cache-control.no-cache/request",
        "no-cache.with.cache-control.no-cache/response" })
    public void shouldReceiveOKForNoCacheWithUnconditionalRequestWhenCachedResponseHasCacheControlNoCache()
            throws Exception {
        k3po.finish();
    }
}
