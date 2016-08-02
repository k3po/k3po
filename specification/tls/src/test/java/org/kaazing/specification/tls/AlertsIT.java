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

public class AlertsIT {

    private final K3poRule robot = new K3poRule().setScriptRoot("org/kaazing/specification/tls/alerts");

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(robot).around(timeout);

    @Test
    @Specification({"unsupported_extension/client", "unsupported_extension/server"})
    public void shouldGiveFatalAlertFollowingAnUnnegotiatedExtension() throws Exception {
        robot.finish();
    }

    @Test
    @Specification({"no_renegotiation.client.hello/client", "no_renegotiation.client.hello/server"})
    public void shouldGiveWarningToRenegotiationViaClientHelloAfterInitalHandshake() throws Exception {
        robot.finish();
    }

    @Test
    @Specification({"no_renegotiation.hello.request/client", "no_renegotiation.hello.request/server"})
    public void shouldGiveWarningToRenegotiationViaHelloRequestAfterInitalHandshake() throws Exception {
        robot.finish();
    }

    @Test
    @Specification({"protocol_version/client", "protocol_version/server"})
    public void shouldGiveFatalAlertFollowingDatedVersion() throws Exception {
        robot.finish();
    }

    @Ignore("TODO")
    @Test
    public void shouldGiveWarningAlertWithUserCancel() {
        // For TLS Alert user_canceled
    }

    @Ignore("TODO")
    @Test
    public void shouldGiveFatalAlertCausedByAnyInternalError() {
        // For TLS Alert internal_error
    }

    @Test
    @Specification({"insufficient_security/client", "insufficient_security/server"})
    public void shouldGiveFatalAlertWhenClientLacksRequiredNumberOfCiphers() throws Exception {
        robot.finish();
    }

    @Ignore("TODO")
    @Test
    public void shouldGiveFatalAlertWhenCryptographicOpertionFailed() {
        // For TLS Alert decrypt_error
    }

    @Ignore("TODO")
    @Test
    public void shouldGiveFatalAlertWhenMessageContainsErrorsAndCannotBeDecoded() {
        // For TLS Alert decode_error
    }

    @Ignore("TODO")
    @Test
    public void shouldGiveFatalAlertWhenSenderDecidesNotToNegotiate() {
        // For TLS Alert access_denied
    }

    @Ignore("TODO")
    @Test
    public void shouldGiveFatalAlertWithUnkownCACertificate() {
        // For TLS Alert unknown_ca
    }

    @Test
    @Specification({"bad_certificate/client", "bad_certificate/server"})
    public void shouldGiveFatalErrorWithBadCertificate() throws Exception {
        robot.finish();
    }

    @Ignore("TODO")
    @Test
    public void shouldGiveFatalAlertToUnknownCertificate() {
        // For TLS Alert certificate_unknown
    }

    @Ignore("TODO")
    @Test
    public void shouldGiveFatalAlertToExpiredCertificate() {
        // For TLS Alert certificate_expired
    }

    @Ignore("TODO")
    @Test
    public void shouldGiveFatalAlertWithRevokedCertificate() {
        // For TLS Alert certificate_revoked
    }

    @Ignore("TODO")
    @Test
    public void shouldGiveFatalAlertWithUnsupportedCertificate() {
        // For TLS Alert unsupported_certificate
    }

    @Test
    @Specification({"handshake_failure/client", "handshake_failure/server"})
    public void shouldGiveFatalAlertWhenUnableToNegotiateCorrectSecurity() throws Exception {
        robot.finish();
    }

    @Ignore("TODO")
    @Test
    public void shouldGiveFatalAlertWithInvalidDataInDecompressionFunction() {
        // For TLS Alert decompression_failure
    }

    @Ignore("TODO")
    @Test
    public void shouldGiveFatalAlertWithTooLongRecord() {
        // For TLS Alert record_overflow
    }

    @Ignore("TODO")
    @Test
    public void shouldGiveFatalAlertWithUnexpectedMessage() {
        // For TLS Alert unexpected_message
    }

    @Ignore("TODO")
    @Test
    public void shouldGiveFatalAlertWithBadMACAddress() {
        // For TLS Alert bad_record_mac
    }
    
    @Test
    @Specification({"illegal_parameter.client.hello/client", "illegal_parameter.client.hello/server"})
    public void shouldGiveFatalAlertWhenGivenIllegalParametersWithClientHello() throws Exception {
        robot.finish();
    }
    
    @Test
    @Specification({"illegal_parameter.hello.request/client", "illegal_parameter.hello.request/server"})
    public void shouldGiveFatalAlertWhenGivenIllegalParametersWithHelloRequest() throws Exception {
        robot.finish();
    }
    
    @Test
    @Specification({"illegal_parameter.server.hello/client", "illegal_parameter.server.hello/server"})
    public void shouldGiveFatalAlertWhenGivenIllegalParametersWithServerHello() throws Exception {
        robot.finish();
    }
    
    @Test
    @Specification({"unexpected_message/client", "unexpected_message/server"})
    public void shouldGiveFatalErrorWhenIncorrectOrder() throws Exception {
        robot.finish();
    }

}
