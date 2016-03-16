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

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.kaazing.k3po.junit.annotation.Specification;
import org.kaazing.k3po.junit.rules.K3poRule;

public class ClosingIT {
    private final K3poRule k3po = new K3poRule()
            .setScriptRoot("org/kaazing/specification/wse/closing");

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @Test
    @Specification({
        "client.send.close/request",
        "client.send.close/response" })
    public void shouldPerformClientInitiatedClose() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "client.send.close.no.reply.from.server/request",
        "client.send.close.no.reply.from.server/response" })
    public void clientShouldCloseIfServerDoesNotEchoCloseFrame() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "client.abruptly.closes.downstream/request",
        "client.abruptly.closes.downstream/response" })
    public void clientAbruptlyClosesDownstream() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "client.abruptly.closes.upstream/request",
        "client.abruptly.closes.upstream/response" })
    public void clientAbruptlyClosesUpstream() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "server.abruptly.closes.downstream/request",
        "server.abruptly.closes.downstream/response" })
    public void serverAbruptlyClosesDownstream() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "server.abruptly.closes.upstream/request",
        "server.abruptly.closes.upstream/response" })
    public void serverAbruptlyClosesUpstream() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "server.send.close/request",
        "server.send.close/response" })
    public void shouldPerformServerInitiatedClose() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "server.send.close.no.reply.from.client/request",
        "server.send.close.no.reply.from.client/response" })
    public void serverShouldCloseIfClientDoesNotEchoCloseFrame() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "server.send.data.after.close/request",
        "server.send.data.after.close/response" })
    public void shouldIgnoreDataFromServerAfterCloseFrame() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "server.send.data.after.reconnect/request",
        "server.send.data.after.reconnect/response" })
    public void shouldIgnoreDataFromServerAfterReconnectFrame()
            throws Exception {
        k3po.finish();
    }
}
