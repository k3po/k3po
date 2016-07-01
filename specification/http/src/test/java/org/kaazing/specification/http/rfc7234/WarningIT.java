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
 * RFC-7234, section 5.5 Warning
 */
public class WarningIT {
    private final K3poRule k3po = new K3poRule().setScriptRoot("org/kaazing/specification/http/rfc7234/warning");
    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @Test
    @Specification({
        "199.misc.warning/request",
        "199.misc.warning/response" })
    public void shouldReceiveResponseWithWarningHeader() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "110.response.stale.from.cache/request",
        "110.response.stale.from.cache/response" })
    public void shouldReceiveResponseWithStaleHeader() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "111.revalidation.failed.from.cache/request",
        "111.revalidation.failed.from.cache/response" })
    public void shouldReceiveResponseWithRevalidateHeader() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "112.disconnected.operation.from.cache/request",
        "112.disconnected.operation.from.cache/response" })
    public void shouldReceiveResponseWithDisconnectedHeader() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "113.heuristic.expiration.from.cache/request",
        "113.heuristic.expiration.from.cache/response" })
    public void shouldReceiveResponseWithHeuristicHeader() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "214.transformation.applied.from.cache/request",
        "214.transformation.applied.from.cache/response" })
    public void shouldReceiveResponseWithTransformationHeader() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "299.misc.persistent.warning/request",
        "299.misc.persistent.warning/response" })
    public void shouldReceiveResponseWithMiscPersistentWarning() throws Exception {
        k3po.finish();
    }


}
