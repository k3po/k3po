/**
 * Copyright (c) 2007-2014 Kaazing Corporation. All rights reserved.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.kaazing.specification.ws.extensions.x_kaazing_idle_timeout;

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
 * Junit for IdleTimeoutExtensionIT
 *
 */
public class IdleTimeoutExtensionIT {

    private final K3poRule k3po = new K3poRule().setScriptRoot("org/kaazing/specification/ws.extensions/x-kaazing-idle-timeout");

    private final TestRule timeout = new DisableOnDebug(new Timeout(7, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @Specification({
        "downstream.data.sent.by.server.no.client.timeout/request",
        "downstream.data.sent.by.server.no.client.timeout/response" })
    @Test
    public void downstreamDataSentByServerNoClientTimeout() throws Exception {
        k3po.start();
        k3po.awaitBarrier("HANDSHAKE_COMPLETE");
        Thread.sleep(1000);
        k3po.notifyBarrier("TICK_ONE");
        Thread.sleep(1000);
        k3po.notifyBarrier("TICK_TWO");
        Thread.sleep(1000);
        k3po.notifyBarrier("TICK_THREE");
        k3po.finish();
    }

    @Specification({
        "extension.ping.pong.frames.sent.by.server.no.client.timeout/request",
        "extension.ping.pong.frames.sent.by.server.no.client.timeout/response" })
    @Test
    public void extensionPingPongFramesSentByServerNoClientTimeout() throws Exception {
        k3po.start();
        k3po.awaitBarrier("HANDSHAKE_COMPLETE");
        Thread.sleep(1000);
        k3po.notifyBarrier("TICK_ONE");
        Thread.sleep(1000);
        k3po.notifyBarrier("TICK_TWO");
        k3po.finish();
    }

    @Specification({
        "extension.pong.frames.sent.by.server.no.client.timeout/request",
        "extension.pong.frames.sent.by.server.no.client.timeout/response" })
    @Test
    public void extensionPongFramesSentByServerNoClientTimeout() throws Exception {
        k3po.start();
        k3po.awaitBarrier("HANDSHAKE_COMPLETE");
        Thread.sleep(1000);
        k3po.notifyBarrier("TICK_ONE");
        Thread.sleep(1000);
        k3po.notifyBarrier("TICK_TWO");
        k3po.finish();
    }

    @Specification({
        "negative.timeout.sent.by.server.client.closes.connection/request",
        "negative.timeout.sent.by.server.client.closes.connection/response" })
    @Test
    public void negativeTimeoutSentByServerClientClosesConnection() throws Exception {
        k3po.finish();
    }

    @Specification({
        "no.data.sent.by.server.client.timeout/request",
        "no.data.sent.by.server.client.timeout/response" })
    @Test
    public void noDataSentByServerClientTimeout() throws Exception {
        k3po.start();
        k3po.awaitBarrier("HANDSHAKE_COMPLETE");
        Thread.sleep(3000);
        k3po.notifyBarrier("TICK_ONE");
        k3po.finish();
    }

    @Specification({
        "standard.ping.pong.frames.sent.by.server.no.client.timeout/request",
        "standard.ping.pong.frames.sent.by.server.no.client.timeout/response" })
    @Test
    public void standardPingPongFramesSentByServerNoClientTimeout() throws Exception {
        k3po.start();
        k3po.awaitBarrier("HANDSHAKE_COMPLETE");
        Thread.sleep(1000);
        k3po.notifyBarrier("TICK_ONE");
        Thread.sleep(1000);
        k3po.notifyBarrier("TICK_TWO");
        k3po.finish();
    }

    @Specification({
        "standard.pong.frames.sent.by.server.no.client.timeout/request",
        "standard.pong.frames.sent.by.server.no.client.timeout/response" })
    @Test
    public void standardPongFramesSentByServerNoClientTimeout() throws Exception {
        k3po.start();
        k3po.awaitBarrier("HANDSHAKE_COMPLETE");
        Thread.sleep(1000);
        k3po.notifyBarrier("TICK_ONE");
        Thread.sleep(1000);
        k3po.notifyBarrier("TICK_TWO");
        k3po.finish();
    }

    @Specification({
        "zero.timeout.sent.by.server.client.closes.connection/request",
        "zero.timeout.sent.by.server.client.closes.connection/response" })
    @Test
    public void zeroTimeoutSentByServerClientClosesConnection() throws Exception {
        k3po.finish();
    }
}
