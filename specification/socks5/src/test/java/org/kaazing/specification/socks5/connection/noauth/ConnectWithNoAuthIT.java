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
package org.kaazing.specification.socks5.connection.noauth;

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
 * SOCKS5 - RFC 1928, section 3 "Procedure for TCP-based clients" using method 0x00
 */
public class ConnectWithNoAuthIT {
    private final K3poRule k3po = new K3poRule().setScriptRoot("org/kaazing/specification/socks5/connection");

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @Test
    @Specification({
        "noauth/connection.noauth.request",
        "noauth/connection.noauth.response"
        })
    public void shouldConnectWithNoAuthRequired() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "noauth/connection.noauth.with.multiple.methods.request",
        "noauth/connection.noauth.with.multiple.methods.response"
        })
    public void shouldConnectWithNoAuthRequiredAndMultipleMethods() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "noauth/connection.noauth.with.no.acceptable.methods.request",
        "noauth/connection.noauth.with.no.acceptable.methods.response"
        })
    public void shouldNotConnectWithNoAuthRequired() throws Exception {
        k3po.finish();
    }
}
