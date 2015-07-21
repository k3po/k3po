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
package org.kaazing.specification.httpx;

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

public class ExtendedHandshakeIT {

    private final K3poRule k3po = new K3poRule();

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @Test
    @Specification({
        "extended/connection.established.with.authorization/request",
        "extended/connection.established.with.authorization/response" })
    public void shouldEstablishConnectionWithAuthorization() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "extended/connection.established/request",
        "extended/connection.established/response" })
    public void shouldEstablishConnection() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "extended/client.sends.message.between.opening.and.extended.handshake/request",
        "extended/client.sends.message.between.opening.and.extended.handshake/response" })
    public void shouldFailWhenClientSendsMessageBetweenOpeningAndExtendedHandshake() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "extended/server.sends.message.between.opening.and.extended.handshake/request",
        "extended/server.sends.message.between.opening.and.extended.handshake/response" })
    public void shouldFailWhenServerSendsMessageBetweenOpeningAndExtendedHandshake() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "extended/extension.in.opening.and.extended.handshake/request",
        "extended/extension.in.opening.and.extended.handshake/response" })
    public void shouldFailWhenExtendedHandShakeHasExtensionFromOpeningHandshake() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({"extended/extension.in.opening.handshake/request",
        "extended/extension.in.opening.handshake/response" })
    public void shouldPassWhenExtensionIsNegotiatedInOpeningHandshake() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "extended/extension.in.extended.handshake/request",
        "extended/extension.in.extended.handshake/response" })
    public void shouldPassWhenExtensionIsNegotiatedInExtendedHandshake() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "extended/extended.handshake.response.code.200/request",
        "extended/extended.handshake.response.code.200/response" })
    public void shouldFailWhenWebSocketProtocolGets200StatusCode() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "extended/extended.handshake.response.code.101/request",
        "extended/extended.handshake.response.code.101/response" })
    public void shouldPassWhenWebSocketProtocolGets101StatusCode() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "extended/extended.handshake.response.code.302/request",
        "extended/extended.handshake.response.code.302/response" })
    public void shouldPassWhenWebSocketProtocolGets302StatusCode() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "extended/extended.handshake.response.code.401/request",
        "extended/extended.handshake.response.code.401/response" })
    public void shouldPassWhenWebSocketProtocolGets401StatusCode() throws Exception {
        k3po.finish();
    }

}

