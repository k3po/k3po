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

package org.kaazing.specification.socks5plus.request.connect;

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
 * SOCKS5 - RFC 1928, section 4 "Requests" and section 6 "Replies"
 */
public class Socks5ConnectHandshakeIT {
    private final K3poRule k3po = new K3poRule().setScriptRoot("org/kaazing/specification/socks5plus/request");

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @Test
    @Specification({
        "connect/connection.request.connect.with.uri.valid.port.succeeded.request",
        "connect/connection.request.connect.with.uri.valid.port.succeeded.response"
        })
    public void shouldConnectWithUriAndValidPortSucceeded() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "connect/connection.request.connect.with.uri.invalid.port.request",
        "connect/connection.request.connect.with.uri.invalid.port.response"
        })
    public void shouldNotConnectWithUriAndInvalidPort() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "connect/connection.request.connect.with.invalid.uri.request",
        "connect/connection.request.connect.with.invalid.uri.response"
        })
    public void shouldNotConnectWithInvalidUri() throws Exception {
        k3po.finish();
    }

}
