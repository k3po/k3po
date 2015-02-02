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

package org.kaazing.specification.tcp.rfc2581;

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
 * RFC-793
 */
public class TcpIT {

    private final K3poRule k3po = new K3poRule().setScriptRoot("org/kaazing/specification/tcp/rfc793");

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @Test
    @Specification({
        "establish.connection/tcp.client",
        "establish.connection/tcp.server" })
    public void establishConnection() throws Exception {
        k3po.join();
    }

    @Test
    @Specification({
        "server.sent.data/tcp.client",
        "server.sent.data/tcp.server" })
    public void serverSentData() throws Exception {
        k3po.join();
    }

    @Test
    @Specification({
        "client.sent.data/tcp.client",
        "client.sent.data/tcp.server" })
    public void clientSentData() throws Exception {
        k3po.join();
    }

    @Test
    @Specification({
        "bidirectional.data/tcp.client",
        "bidirectional.data/tcp.server" })
    public void bidirectionalData() throws Exception {
        k3po.join();
    }

    @Test
    @Specification({
        "server.close/tcp.client",
        "server.close/tcp.server" })
    public void serverClose() throws Exception {
        k3po.join();
    }

    @Test
    @Specification({
        "client.close/tcp.client",
        "client.close/tcp.server" })
    public void clientClose() throws Exception {
        k3po.join();
    }

    @Test
    @Specification({
        "concurrent.connections/tcp.client",
        "concurrent.connections/tcp.server" })
    public void concurrentConnections() throws Exception {
        k3po.join();
    }

}
