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

public class BrowsersIT {

    private final K3poRule k3po = new K3poRule()
            .setScriptRoot("org/kaazing/specification/wse/browsers");

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @Test
    @Specification({
        "client.reconnect.downstream/request",
        "client.reconnect.downstream/response" })
    public void serverShouldSendReconnectFrameAfterDetectingNewDownstreamRequestFromClient()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "client.send.kb.parameter.in.downstream.request/request",
        "client.send.kb.parameter.in.downstream.request/response" })
    public void serverShouldSendReconnectFrameAfterRequestedClientBufferSizeIsExceeded()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "client.request.padded.response/request",
        "client.request.padded.response/response" })
    public void serverShouldSendPaddingInDownstream() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "client.send.ksn.parameter/request",
        "client.send.ksn.parameter/response" })
    public void serverShouldReadRequestSequenceNumberFromQueryParameter() throws Exception {
        k3po.finish();
    }
}
