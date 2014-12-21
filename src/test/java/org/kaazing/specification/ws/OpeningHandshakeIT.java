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

package org.kaazing.specification.ws;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.rules.RuleChain.outerRule;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.kaazing.k3po.junit.annotation.Robotic;
import org.kaazing.k3po.junit.rules.RobotRule;

public class OpeningHandshakeIT {

    private final RobotRule robot = new RobotRule().setScriptRoot("org/kaazing/specification/ws/opening");

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(robot).around(timeout);

    // TODO:
    // one-handshake-per-target-IP-address-even-different-host-names
    // proxy => HTTP CONNECT w/ optional authorization, auto-configuration via ws://, wss://
    // TLS (not SSL) w/ SNI for wss://
    //
    // 101 status code OK, others follow HTTP semantics (not required to process)
    // Upgrade: websocket (case-insensitive)
    // Connection: Upgrade (case-insensitive)
    // Sec-WebSocket-Accept: [calculated]
    // Sec-WebSocket-Extensions: unrequested server extensions not permitted
    // Sec-WebSocket-Protocol: unrequested server protocol not permitted

    /**
     * RFC-6455, section 4.1 "Client-Side Requirements"
     * RFC-6455, section 4.2 "Server-Side Requirements"
     */
    @Test
    @Robotic({"handshake.request", "handshake.response" })
    public void shouldEstablishConnection() throws Exception {
        robot.join();
    }

    /**
     * RFC-6455, section 4.1 "Client-Side Requirements"
     * RFC-6455, section 4.2 "Server-Side Requirements"
     */
    @Test
    @Robotic({"handshake.request.with.header.origin",
              "handshake.response.for.header.origin" })
    public void shouldEstablishConnectionWithRequestHeaderOrigin() throws Exception {
        robot.join();
    }

    /**
     * RFC-6455, section 4.1 "Client-Side Requirements"
     * RFC-6455, section 4.2 "Server-Side Requirements"
     */
    @Test
    @Robotic({"handshake.request.with.header.sec.websocket.protocol",
              "handshake.response.for.header.sec.websocket.protocol" })
    public void shouldEstablishConnectionWithRequestHeaderSecWebSocketProtocol() throws Exception {
        robot.join();
    }

    /**
     * RFC-6455, section 4.1 "Client-Side Requirements"
     * RFC-6455, section 4.2 "Server-Side Requirements"
     */
    @Test
    @Robotic({"handshake.request.with.header.sec.websocket.extensions",
              "handshake.response.for.header.sec.websocket.extensions" })
    public void shouldEstablishConnectionWithRequestHeaderSecWebSocketExtensions() throws Exception {
        robot.join();
    }

    /**
     * RFC-6455, section 4.2 "Server-Side Requirements"
     */
    @Test
    @Robotic({"handshake.request.with.method.not.get", "handshake.response.for.method.not.get" })
    public void shouldFailHandshakeWhenMethodNotGet() throws Exception {
        robot.join();
    }

    /**
     * RFC-6455, section 4.2 "Server-Side Requirements"
     */
    @Test
    @Robotic({"handshake.request.with.version.not.http.1.1", "handshake.response.for.version.not.http.1.1" })
    public void shouldFailHandshakeWhenVersionNotHttp11() throws Exception {
        robot.join();
    }

    /**
     * RFC-6455, section 4.2 "Server-Side Requirements"
     */
    @Test
    @Ignore("Requires fail on 'missing' header K3PO language feature")
    @Robotic({"handshake.request.with.header.host.missing", "handshake.response.for.header.host.missing" })
    public void shouldFailHandshakeWhenRequestHeaderHostMissing() throws Exception {
        robot.join();
    }

    /**
     * RFC-6455, section 4.2 "Server-Side Requirements"
     */
    @Test
    @Ignore("Requires fail on 'missing' header K3PO language feature")
    @Robotic({"handshake.request.with.header.upgrade.missing", "handshake.response.for.header.upgrade.missing" })
    public void shouldFailHandshakeWhenRequestHeaderUpgradeMissing() throws Exception {
        robot.join();
    }

    /**
     * RFC-6455, section 4.2 "Server-Side Requirements"
     */
    @Test
    @Robotic({"handshake.request.with.header.upgrade.not.websocket", "handshake.response.for.header.upgrade.not.websocket" })
    public void shouldFailHandshakeWhenRequestHeaderUpgradeNotWebSocket() throws Exception {
        robot.join();
    }

    /**
     * RFC-6455, section 4.2 "Server-Side Requirements"
     */
    @Test
    @Ignore("Requires fail on 'missing' header K3PO language feature")
    @Robotic({"handshake.request.with.header.connection.missing",
              "handshake.response.for.header.connection.missing" })
    public void shouldFailHandshakeWhenRequestHeaderConnectionMissing() throws Exception {
        robot.join();
    }

    /**
     * RFC-6455, section 4.2 "Server-Side Requirements"
     */
    @Test
    @Robotic({"handshake.request.with.header.connection.not.upgrade", "handshake.response.for.header.connection.not.upgrade" })
    public void shouldFailHandshakeWhenRequestHeaderConnectionNotUpgrade() throws Exception {
        robot.join();
    }

    /**
     * RFC-6455, section 4.2 "Server-Side Requirements"
     */
    @Test
    @Ignore("Requires fail on 'missing' header K3PO language feature")
    @Robotic({"handshake.request.with.header.sec.websocket.key.missing",
              "handshake.response.for.header.sec.websocket.key.missing" })
    public void shouldFailHandshakeWhenRequestHeaderSecWebSocketKeyMissing() throws Exception {
        robot.join();
    }

    /**
     * RFC-6455, section 4.2 "Server-Side Requirements"
     */
    @Test
    @Robotic({"handshake.request.with.header.sec.websocket.key.not.16bytes.base64",
              "handshake.response.for.header.sec.websocket.key.not.16bytes.base64" })
    public void shouldFailHandshakeWhenRequestHeaderSecWebSocketKeyNot16BytesBase64() throws Exception {
        robot.join();
    }

    /**
     * RFC-6455, section 4.4 "Handling Multiple Versions of WebSocket Protocol"
     */
    @Test
    @Robotic({"handshake.request.with.header.sec.websocket.version.not.13",
              "handshake.response.for.header.sec.websocket.version.not.13" })
    public void shouldFailHandshakeWhenRequestHeaderSecWebSocketVersionNot13() throws Exception {
        robot.join();
    }

    /**
     * RFC-6455, section 4.1 "Client-Side Requirements"
     */
    @Test
    @Robotic({"handshake.request.for.header.connection.not.upgrade", "handshake.response.with.header.connection.not.upgrade" })
    public void shouldFailConnectionWhenResponseHeaderConnectionNotUpgrade() throws Exception {
        robot.join();
    }

    /**
     * RFC-6455, section 4.1 "Client-Side Requirements"
     */
    @Test
    @Ignore("Requires fail on 'missing' header K3PO language feature")
    @Robotic({"handshake.request.for.header.connection.missing", "handshake.response.with.header.connection.missing" })
    public void shouldFailConnectionWhenResponseHeaderConnectionMissing() throws Exception {
        robot.join();
    }

    /**
     * RFC-6455, section 4.1 "Client-Side Requirements"
     */
    @Test
    @Robotic({"handshake.request.for.header.upgrade.not.websocket", "handshake.response.with.header.upgrade.not.websocket" })
    public void shouldFailConnectionWhenResponseHeaderUpgradeNotWebSocket() throws Exception {
        robot.join();
    }

    /**
     * RFC-6455, section 4.1 "Client-Side Requirements"
     */
    @Test
    @Ignore("Requires fail on 'missing' header K3PO language feature")
    @Robotic({"handshake.request.for.header.upgrade.missing", "handshake.response.with.header.upgrade.missing" })
    public void shouldFailConnectionWhenResponseHeaderUpgradeMissing() throws Exception {
        robot.join();
    }

    /**
     * RFC-6455, section 4.1 "Client-Side Requirements"
     */
    @Test
    @Robotic({"handshake.request.for.header.sec.websocket.accept.not.hashed",
              "handshake.response.with.header.sec.websocket.accept.not.hashed" })
    public void shouldFailConnectionWhenResponseHeaderSecWebSocketAcceptNotHashed() throws Exception {
        robot.join();
    }

    /**
     * RFC-6455, section 4.1 "Client-Side Requirements"
     */
    @Test
    @Ignore("Requires fail on 'missing' header K3PO language feature")
    @Robotic({"handshake.request.for.header.sec.websocket.accept.missing",
              "handshake.response.with.header.sec.websocket.accept.missing" })
    public void shouldFailConnectionWhenResponseHeaderSecWebSocketAcceptMissing() throws Exception {
        robot.join();
    }

    /**
     * RFC-6455, section 4.1 "Client-Side Requirements"
     */
    @Test
    @Robotic({"handshake.request.for.header.sec.websocket.extensions.not.negotiated",
              "handshake.response.with.header.sec.websocket.extensions.not.negotiated" })
    public void shouldFailConnectionWhenResponseHeaderSecWebSocketExtensionsNotNegotiated() throws Exception {
        robot.join();
    }

    /**
     * RFC-6455, section 4.1 "Client-Side Requirements"
     */
    @Test
    @Robotic({"handshake.request.for.header.sec.websocket.protocol.not.negotiated",
              "handshake.response.with.header.sec.websocket.protocol.not.negotiated" })
    public void shouldFailConnectionWhenResponseHeaderSecWebSocketProtocolNotNegotiated() throws Exception {
        robot.join();
    }

    /**
     * RFC-6455, section 4.1 "Client-Side Requirements"
     */
    @Test
    @Robotic({"handshake.requests.with.multiple.connections.serialized",
              "handshake.responses.for.multiple.connections.serialized" })
    public void shouldEstablishConnectionsWhenHandshakesSerialized() throws Exception {
        robot.join();
    }
}
