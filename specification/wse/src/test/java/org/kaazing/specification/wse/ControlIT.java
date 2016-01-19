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

public class ControlIT {

    private final K3poRule k3po = new K3poRule()
            .setScriptRoot("org/kaazing/specification/wse/control");

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @Test
    @Specification({
        "client.send.ping/request",
        "client.send.ping/response" })
    public void shouldReplyClientPingWithPong() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "client.send.pong/request",
        "client.send.pong/response" })
    public void shouldReceivePongFromClient() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "server.send.ping/request",
        "server.send.ping/response" })
    public void shouldReplyServerPingWithPong() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "server.send.pong/request",
        "server.send.pong/response" })
    public void shouldReceivePongFromServer() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "client.send.invalid.ping/request",
        "client.send.invalid.ping/response" })
    public void shouldCloseConnectionOnReceivingInvalidPingFromClient()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "client.send.invalid.pong/request",
        "client.send.invalid.pong/response" })
    public void shouldCloseConnectionOnReceivingInvalidPongFromClient()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "server.send.invalid.ping/request",
        "server.send.invalid.ping/response" })
    public void shouldCloseConnectionOnReceivingInvalidPingFromServer()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "server.send.invalid.pong/request",
        "server.send.invalid.pong/response" })
    public void shouldCloseConnectionOnReceivingInvalidPongFromServer()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "client.send.unexpected.ping/request",
        "client.send.unexpected.ping/response" })
    public void shouldCloseConnectionOnReceivingUnexpectedPingFromClient()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "client.send.unexpected.pong/request",
        "client.send.unexpected.pong/response" })
    public void shouldCloseConnectionOnReceivingUnexpectedPongFromClient()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "server.send.unexpected.ping/request",
        "server.send.unexpected.ping/response" })
    public void shouldCloseConnectionOnReceivingUnexpectedPingFromServer()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "server.send.unexpected.pong/request",
        "server.send.unexpected.pong/response" })
    public void shouldCloseConnectionOnReceivingUnexpectedPongFromServer()
            throws Exception {
        k3po.finish();
    }
}
