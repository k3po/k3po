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
package org.kaazing.specification.udp.rfc768;

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
 * RFC-768
 */
public class UdpIT {

    private final K3poRule k3po = new K3poRule();

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(timeout).around(k3po);

    @Test
    @Specification({
        "establish.connection/client",
        "establish.connection/server" })
    public void shouldEstablishConnection() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "server.sent.data/client",
        "server.sent.data/server" })
    public void shouldReceiveServerSentData() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "client.sent.data/client",
        "client.sent.data/server" })
    public void shouldReceiveClientSentData() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "echo.data/client",
        "echo.data/server"
    })
    public void shouldEchoData() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "server.close/client",
        "server.close/server" })
    public void shouldInitiateServerClose() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "client.close/client",
        "client.close/server" })
    public void shouldInitiateClientClose() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "concurrent.connections/client",
        "concurrent.connections/server" })
    public void shouldEstablishConcurrentConnections() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
            "concurrent.writes.together/client",
            "concurrent.writes.together/server" })
    public void shouldProcessWritesTogether() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "idle.concurrent.connections/client",
        "idle.concurrent.connections/server" })
    public void shouldCloseIdleConcurrentConnections() throws Exception {
        k3po.finish();
    }

}
