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
package org.kaazing.k3po.driver.internal.tls;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.rules.RuleChain.outerRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.kaazing.k3po.driver.internal.test.utils.K3poTestRule;
import org.kaazing.k3po.driver.internal.test.utils.TestSpecification;

public class TlsIT {

    private final K3poTestRule k3po = new K3poTestRule();

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @Test
    @TestSpecification({
        "connection.established/server",
        "connection.established/client"
    })
    public void shouldEstablishConnection() throws Exception {
        k3po.finish();
    }

    @Test
    @TestSpecification({
        "connection.established.with.alpn/server",
        "connection.established.with.alpn/client"
    })
    public void shouldEstablishConnectionWithAlpn() throws Exception {
        k3po.finish();
    }

    @Test
    @TestSpecification({
        "echo.payload/server",
        "echo.payload/client"
    })
    public void shouldEchoPayload() throws Exception {
        k3po.finish();
    }

    @Test
    @TestSpecification({
        "server.sent.close/server",
        "server.sent.close/client"
    })
    public void shouldReceiveServerSentClose() throws Exception {
        k3po.finish();
    }

    @Test
    @TestSpecification({
        "server.sent.write.abort/server",
        "server.sent.write.abort/client"
    })
    public void shouldReceiveServerSentWriteAbort() throws Exception {
        k3po.finish();
    }

    @Test
    @TestSpecification({
        "client.sent.close/server",
        "client.sent.close/client"
    })
    public void shouldReceiveClientSentClose() throws Exception {
        k3po.finish();
    }

    @Test
    @TestSpecification({
        "client.sent.write.abort/server",
        "client.sent.write.abort/client"
    })
    public void shouldReceiveClientSentWriteAbort() throws Exception {
        k3po.finish();
    }

    @Test
    @TestSpecification({
        "server.sent.write.close/server",
        "server.sent.write.close/client"
    })
    public void shouldReceiveServerSentWriteClose() throws Exception {
        k3po.finish();
    }

    @Test
    @TestSpecification({
        "client.sent.write.close/server",
        "client.sent.write.close/client"
    })
    public void shouldReceiveClientSentWriteClose() throws Exception {
        k3po.finish();
    }

    @Test
    @TestSpecification({
        "client.auth/server",
        "client.auth/client"
    })
    public void shouldClientAuth() throws Exception {
        k3po.finish();
    }
}
