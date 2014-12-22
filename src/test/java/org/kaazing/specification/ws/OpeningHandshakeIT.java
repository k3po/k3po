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
    @Robotic({"connection.established/handshake.request", "connection.established/handshake.response" })
    public void shouldEstablishConnection() throws Exception {
        robot.join();
    }

    /**
     * RFC-6455, section 4.1 "Client-Side Requirements"
     * RFC-6455, section 4.2 "Server-Side Requirements"
     */
    @Test
    @Robotic({"request.header.origin/handshake.request",
              "request.header.origin/handshake.response" })
    public void shouldEstablishConnectionWithRequestHeaderOrigin() throws Exception {
        robot.join();
    }

    /**
     * RFC-6455, section 4.1 "Client-Side Requirements"
     * RFC-6455, section 4.2 "Server-Side Requirements"
     */
    @Test
    @Robotic({"request.header.sec.websocket.protocol/handshake.request",
              "request.header.sec.websocket.protocol/handshake.response" })
    public void shouldEstablishConnectionWithRequestHeaderSecWebSocketProtocol() throws Exception {
        robot.join();
    }

    /**
     * RFC-6455, section 4.1 "Client-Side Requirements"
     * RFC-6455, section 4.2 "Server-Side Requirements"
     */
    @Test
    @Robotic({"request.header.sec.websocket.extensions/handshake.request",
              "request.header.sec.websocket.extensions/handshake.response" })
    public void shouldEstablishConnectionWithRequestHeaderSecWebSocketExtensions() throws Exception {
        robot.join();
    }

    /**
     * RFC-6455, section 4.2 "Server-Side Requirements"
     */
    @Test
    @Robotic({"request.method.not.get/handshake.request",
              "request.method.not.get/handshake.response" })
    public void shouldFailHandshakeWhenMethodNotGet() throws Exception {
        robot.join();
    }

    /**
     * RFC-6455, section 4.2 "Server-Side Requirements"
     */
    @Test
    @Robotic({"request.version.not.http.1.1/handshake.request",
              "request.version.not.http.1.1/handshake.response" })
    public void shouldFailHandshakeWhenVersionNotHttp11() throws Exception {
        robot.join();
    }

    /**
     * RFC-6455, section 4.2 "Server-Side Requirements"
     */
    @Test
    @Ignore("Requires fail on 'missing' header K3PO language feature")
    @Robotic({"request.header.host.missing/handshake.request",
              "request.header.host.missing/handshake.response" })
    public void shouldFailHandshakeWhenRequestHeaderHostMissing() throws Exception {
        robot.join();
    }

    /**
     * RFC-6455, section 4.2 "Server-Side Requirements"
     */
    @Test
    @Ignore("Requires fail on 'missing' header K3PO language feature")
    @Robotic({"request.header.upgrade.missing/handshake.request",
              "request.header.upgrade.missing/handshake.response" })
    public void shouldFailHandshakeWhenRequestHeaderUpgradeMissing() throws Exception {
        robot.join();
    }

    /**
     * RFC-6455, section 4.2 "Server-Side Requirements"
     */
    @Test
    @Robotic({"request.header.upgrade.not.websocket/handshake.request",
              "request.header.upgrade.not.websocket/handshake.response" })
    public void shouldFailHandshakeWhenRequestHeaderUpgradeNotWebSocket() throws Exception {
        robot.join();
    }

    /**
     * RFC-6455, section 4.2 "Server-Side Requirements"
     */
    @Test
    @Ignore("Requires fail on 'missing' header K3PO language feature")
    @Robotic({"request.header.connection.missing/handshake.request",
              "request.header.connection.missing/handshake.response" })
    public void shouldFailHandshakeWhenRequestHeaderConnectionMissing() throws Exception {
        robot.join();
    }

    /**
     * RFC-6455, section 4.2 "Server-Side Requirements"
     */
    @Test
    @Robotic({"request.header.connection.not.upgrade/handshake.request",
              "request.header.connection.not.upgrade/handshake.response" })
    public void shouldFailHandshakeWhenRequestHeaderConnectionNotUpgrade() throws Exception {
        robot.join();
    }

    /**
     * RFC-6455, section 4.2 "Server-Side Requirements"
     */
    @Test
    @Ignore("Requires fail on 'missing' header K3PO language feature")
    @Robotic({"request.header.sec.websocket.key.missing/handshake.request",
              "request.header.sec.websocket.key.missing/handshake.response" })
    public void shouldFailHandshakeWhenRequestHeaderSecWebSocketKeyMissing() throws Exception {
        robot.join();
    }

    /**
     * RFC-6455, section 4.2 "Server-Side Requirements"
     */
    @Test
    @Robotic({"request.header.sec.websocket.key.not.16bytes.base64/handshake.request",
              "request.header.sec.websocket.key.not.16bytes.base64/handshake.response" })
    public void shouldFailHandshakeWhenRequestHeaderSecWebSocketKeyNot16BytesBase64() throws Exception {
        robot.join();
    }

    /**
     * RFC-6455, section 4.4 "Handling Multiple Versions of WebSocket Protocol"
     */
    @Test
    @Robotic({"request.header.sec.websocket.version.not.13/handshake.request",
              "request.header.sec.websocket.version.not.13/handshake.response" })
    public void shouldFailHandshakeWhenRequestHeaderSecWebSocketVersionNot13() throws Exception {
        robot.join();
    }

    /**
     * RFC-6455, section 4.1 "Client-Side Requirements"
     */
    @Test
    @Robotic({"response.header.connection.not.upgrade/handshake.request",
              "response.header.connection.not.upgrade/handshake.response" })
    public void shouldFailConnectionWhenResponseHeaderConnectionNotUpgrade() throws Exception {
        robot.join();
    }

    /**
     * RFC-6455, section 4.1 "Client-Side Requirements"
     */
    @Test
    @Ignore("Requires fail on 'missing' header K3PO language feature")
    @Robotic({"response.header.connection.missing/handshake.request",
              "response.header.connection.missing/handshake.response" })
    public void shouldFailConnectionWhenResponseHeaderConnectionMissing() throws Exception {
        robot.join();
    }

    /**
     * RFC-6455, section 4.1 "Client-Side Requirements"
     */
    @Test
    @Robotic({"response.header.upgrade.not.websocket/handshake.request",
              "response.header.upgrade.not.websocket/handshake.response" })
    public void shouldFailConnectionWhenResponseHeaderUpgradeNotWebSocket() throws Exception {
        robot.join();
    }

    /**
     * RFC-6455, section 4.1 "Client-Side Requirements"
     */
    @Test
    @Ignore("Requires fail on 'missing' header K3PO language feature")
    @Robotic({"response.header.upgrade.missing/handshake.request",
              "response.header.upgrade.missing/handshake.response" })
    public void shouldFailConnectionWhenResponseHeaderUpgradeMissing() throws Exception {
        robot.join();
    }

    /**
     * RFC-6455, section 4.1 "Client-Side Requirements"
     */
    @Test
    @Robotic({"response.header.sec.websocket.accept.not.hashed/handshake.request",
              "response.header.sec.websocket.accept.not.hashed/handshake.response" })
    public void shouldFailConnectionWhenResponseHeaderSecWebSocketAcceptNotHashed() throws Exception {
        robot.join();
    }

    /**
     * RFC-6455, section 4.1 "Client-Side Requirements"
     */
    @Test
    @Ignore("Requires fail on 'missing' header K3PO language feature")
    @Robotic({"response.header.sec.websocket.accept.missing/handshake.request",
              "response.header.sec.websocket.accept.missing/handshake.response" })
    public void shouldFailConnectionWhenResponseHeaderSecWebSocketAcceptMissing() throws Exception {
        robot.join();
    }

    /**
     * RFC-6455, section 4.1 "Client-Side Requirements"
     */
    @Test
    @Robotic({"response.header.sec.websocket.extensions.not.negotiated/handshake.request",
              "response.header.sec.websocket.extensions.not.negotiated/handshake.response" })
    public void shouldFailConnectionWhenResponseHeaderSecWebSocketExtensionsNotNegotiated() throws Exception {
        robot.join();
    }

    /**
     * RFC-6455, section 4.1 "Client-Side Requirements"
     */
    @Test
    @Robotic({"response.header.sec.websocket.protocol.not.negotiated/handshake.request",
              "response.header.sec.websocket.protocol.not.negotiated/handshake.response" })
    public void shouldFailConnectionWhenResponseHeaderSecWebSocketProtocolNotNegotiated() throws Exception {
        robot.join();
    }

    /**
     * RFC-6455, section 4.1 "Client-Side Requirements"
     */
    @Test
    @Robotic({"multiple.connections.established/handshake.requests",
              "multiple.connections.established/handshake.responses" })
    public void shouldEstablishMultipleConnections() throws Exception {
        robot.join();
    }
}
