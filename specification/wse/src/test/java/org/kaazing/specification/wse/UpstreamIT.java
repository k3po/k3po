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
package org.kaazing.specification.wse;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.rules.RuleChain.outerRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.kaazing.k3po.junit.annotation.Specification;
import org.kaazing.k3po.junit.rules.K3poRule;

public class UpstreamIT {

    private final K3poRule k3po = new K3poRule()
            .setScriptRoot("org/kaazing/specification/wse/upstream");

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @Test
    @Specification({
        "request.method.not.post/upstream.request",
        "request.method.not.post/upstream.response" })
    public void shouldCloseConnectionWhenUpstreamRequestMethodNotPost()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "response.status.code.not.200/upstream.request",
        "response.status.code.not.200/upstream.response" })
    public void shouldCloseConnectionWhenUpstreamStatusCodeNot200()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "client.send.overlapping.request/upstream.request",
        "client.send.overlapping.request/upstream.response" })
    public void shouldRejectParallelUpstreamRequest() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "request.out.of.order/upstream.request",
        "request.out.of.order/upstream.response" })
    public void shouldRejectOutOfOrderUpstreamRequest() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "subsequent.request.out.of.order/request",
        "subsequent.request.out.of.order/response" })
    public void shouldCloseConnectionWhenSubsequentUpstreamRequestIsOutOfOrder() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "client.send.multiple.requests/upstream.request",
        "client.send.multiple.requests/upstream.response" })
    public void shouldAllowMultipleSequentialUpstreamRequests() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "zero.content.length.request/upstream.request",
        "zero.content.length.request/upstream.response" })
    public void shouldRejectZeroContentLengthUpstreamRequest() throws Exception {
        k3po.finish();
    }
}
