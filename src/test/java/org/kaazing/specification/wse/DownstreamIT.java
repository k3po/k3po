/*
 * Copyright 2014, Kaazing Corporation. All rights reserved.
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

public class DownstreamIT {
    private final K3poRule k3po = new K3poRule()
            .setScriptRoot("org/kaazing/specification/wse/downstream");

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @Test
    @Specification({
        "binary.downstream.response.content.type.not.application.octet.stream/downstream.request",
        "binary.downstream.response.content.type.not.application.octet.stream/downstream.response" })
    public void shouldCloseConnectionWhenBinaryDownstreamResponseContentTypeIsNotApplicationOctetstream()
            throws Exception {
        k3po.join();
    }

    @Test
    @Specification({
        "response.status.code.not.200/downstream.request",
        "response.status.code.not.200/downstream.response" })
    public void shouldCloseConnectionWhenDownstreamResponseStatusCodeNot200()
            throws Exception {
        k3po.join();
    }

    @Test
    @Specification({
        "response.containing.frame.after.reconnect.frame/downstream.request",
        "response.containing.frame.after.reconnect.frame/downstream.response" })
    public void shouldCloseConnectionWhenDownstreamResponseContainsFrameAfterReconnectFrame()
            throws Exception {
        k3po.join();
    }

    @Test
    @Specification({
        "request.method.not.get/downstream.request",
        "request.method.not.get/downstream.response" })
    public void shouldRespondWithBadRequestWhenDownstreamRequestMethodNotGet()
            throws Exception {
        k3po.join();
    }
}
