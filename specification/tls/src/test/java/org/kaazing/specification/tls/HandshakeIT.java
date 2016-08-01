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
package org.kaazing.specification.tls;

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

public class HandshakeIT {

    private final K3poRule robot = new K3poRule().setScriptRoot("org/kaazing/specification/tls/handshake");

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(robot).around(timeout);

    @Test
    @Specification({"hello.request/client", "hello.request/server"})
    public void shouldPassWithSimpleHelloRequest() throws Exception {
        robot.finish();
    }

    @Test
    @Specification({"only.client.hello/client", "only.client.hello/server"})
    public void shouldPassWithSimpleClientHello() throws Exception {
        robot.finish();
    }

    @Test
    @Specification({"client.server.hello/client", "client.server.hello/server"})
    public void shouldPassWithClientServerHelloCommunicate() throws Exception {
        robot.finish();
    }

    @Test
    @Specification({"complete.handshake.without.cipher.spec/client", "complete.handshake.without.cipher.spec/server"})
    public void shouldPassWithCompleteHandshake() throws Exception {
        robot.finish();
    }

    @Test
    @Specification({"simple.server.certificate/client", "simple.server.certificate/server"})
    public void shouldPassWithNullServerCertificate() throws Exception {
        robot.finish();
    }
    
    @Test
    @Specification({"simple.server.key.exchange/client", "simple.server.key.exchange/server"})
    public void shouldPassWithSimpleServerKey() throws Exception {
        robot.finish();
    }
    
    @Test
    @Specification({"simple.server.certificate.request/client", "simple.server.certificate.request/server"})
    public void shouldPassWithSimpleServerCertificateRequest() throws Exception {
        robot.finish();
    }

    @Test
    @Specification({"client.key.exchange/client", "client.key.exchange/server"})
    public void shouldPassWithSimpleClientKeyExchange() throws Exception {
        robot.finish();
    }

    @Test
    @Specification({"client.finished/client", "client.finished/server"})
    public void shouldPassWithSimpleClientFinished() throws Exception {
        robot.finish();
    }

    @Test
    @Specification({"client.certificate.verify/client", "client.certificate.verify/server"})
    public void shouldPassWithSimpleCertificateVerification() throws Exception {
        robot.finish();
    }
    
    @Ignore("TODO")
    @Test
    @Specification({"change.cipher.spec.complete/client", "change.cipher.spec.complete/server"})
    public void shouldPassWithChangeCipherSpec() throws Exception {
        robot.finish();
    }
    
    @Test
    @Specification({"certificate.x.509/client", "certificate.x.509/server"})
    public void shouldPassWithFullX509Certificate() throws Exception {
        robot.finish();
    }

}

