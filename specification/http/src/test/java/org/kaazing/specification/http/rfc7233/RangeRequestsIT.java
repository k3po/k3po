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
package org.kaazing.specification.http.rfc7233;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.rules.RuleChain.outerRule;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.kaazing.k3po.junit.annotation.Specification;
import org.kaazing.k3po.junit.rules.K3poRule;

/**
 *  Test to validate behavior as specified in <a href="https://tools.ietf.org/html/rfc7233">RFC 7233:
 *   Hypertext Transfer Protocol (HTTP/1.1): Range Requests</a>.
 */
public class RangeRequestsIT {

    private final K3poRule k3po = new K3poRule().setScriptRoot("org/kaazing/specification/http/rfc7233/range.requests");

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    /**
     * See <a href="https://tools.ietf.org/html/rfc7233#section-3.1">RFC 7233 section 3</a>.
     */
    @Test
    @Specification({
        "range.request/request",
        "range.request/response" })
    public void rangeRequest() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7233#section-3.1">RFC 7233 section 3</a>.
     */
    @Test
    @Specification({
        "unsatisfactory.range.gives.416/request",
        "unsatisfactory.range.gives.416/response" })
    public void shouldGive416IfRangeIsNotSatisfactory() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7233#section-3.2">RFC 7233 section 3</a>.
     */
    @Test
    @Specification({
        "ignore.if-range.without.range.header/request",
        "ignore.if-range.without.range.header/response" })
    public void serverIgnoresIfRangeRequestWithoutRangeHeader() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7233#section-3.2">RFC 7233 section 3</a>.
     */
    @Test
    @Specification({
        "partial.range.request/request",
        "partial.range.request/response" })
    public void partialRangeRequest() throws Exception {
        k3po.finish();
    }

}

