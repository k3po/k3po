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
package org.kaazing.specification.http2.negotiate;

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
 * HTTP 2 - draft 16, section 3 "Starting HTTP/2"
 */
public class NegotiateHTTP2ConnectionIT {

    private final K3poRule k3po = new K3poRule().setScriptRoot("org/kaazing/specification/http2/negotiate");

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @Test
    @Specification({ "negotiate.http2.connection.request", "negotiate.http2.connection.response" })
    public void shouldEstablishConnection() throws Exception {
        k3po.finish();
    }

    @Test
    public void shouldContinueUsingHTTP11() {
        // test the client setting an upgrade request but the server returning HTTP status 200 rather than a 101
    }

    @Test
    public void shouldIgnoreUpgradeUsingPlainTextAndH2() {
        // A server MUST ignore a "h2" token in an Upgrade header field.
        // Presence of a token with "h2" implies HTTP/2 over TLS, which is
        // instead negotiated as described in Section 3.3.
    }

    @Test
    public void shouldIgnoreUpgradeWithoutHTTP2Settings() {
        // A server MUST NOT upgrade the connection to HTTP/2 if this header
        // field is not present, or if more than one is present.  A server MUST
        // NOT send this header field.
    }

    @Test
    public void shouldSendConnectionPrefaceAfterNegotiation() {
        // The first HTTP/2 frame sent by the server MUST be a SETTINGS frame
        // (Section 6.5) as the server connection preface (Section 3.5).  Upon
        // receiving the 101 response, the client MUST send a connection preface
        // (Section 3.5), which includes a SETTINGS frame.

        // NOTE:
        // The SETTINGS frames received from a peer as part of the connection
        // preface MUST be acknowledged (see Section 6.5.3) after sending the
        // connection preface.
    }

    @Test
    public void shouldCloseWithProtocolErrorOnInvalidPreface() {
        // Clients and servers MUST treat an invalid connection preface as a
        // connection error (Section 5.4.1) of type PROTOCOL_ERROR.
    }

    /////////////////////////////////////////////////////////////////////////
    //                                                                     //
    //                           TLS Tests                                 //
    //                                                                     //
    // The TLS Tests potentially could be considered a layering violation  //
    // but the HTTP 2.0 spec dictates application layer behavior related   //
    // to HTTP over TLS.  As a result there should be tests that validate  //
    // the application layer behavior, and one possible implementation     //
    // strategy could be to add TLS semantics to the robot similar to how  //
    // the HTTP semantics have been added (e.g. "write header" for HTTP    //
    // has been added to the robot, so "write client hello" or some        //
    // directives could be added.                                          //
    //                                                                     //
    /////////////////////////////////////////////////////////////////////////

    @Test
    public void shouldNegotiateHTTP2OverTLS() {
        // A client that makes a request to an "https" URI uses TLS [TLS12] with
        // the application layer protocol negotiation extension [TLS-ALPN].

        // HTTP/2 over TLS uses the "h2" application token.  The "h2c" token
        // MUST NOT be sent by a client or selected by a server.

        // Once TLS negotiation is complete, both the client and the server MUST
        // send a connection preface (Section 3.5).
    }

    @Test
    public void shouldIgnoreHTTP2OverTLS() {
        // An incorrect ALPN advertisement (e.g. h2c instead of h2) should result in
        // staying on http/1.1
    }

    @Test
    public void shouldCloseWithInadequateSecurity() {
        // Implementations of HTTP/2 MUST use TLS [TLS12] version 1.2 or higher
        // for HTTP/2 over TLS.  The general TLS usage guidance in [TLSBCP]
        // SHOULD be followed, with some additional restrictions that are
        // specific to HTTP/2.

        // An endpoint MAY immediately terminate an HTTP/2 connection that
        // does not meet these TLS requirements with a connection error
        // (Section 5.4.1) of type INADEQUATE_SECURITY.
    }

    @Test
    public void shouldCloseWithProtocolErrorIfTLSRenegotiationEnabled() {
        // A deployment of HTTP/2 over TLS 1.2 MUST disable renegotiation.  An
        // endpoint MUST treat a TLS renegotiation as a connection error
        // (Section 5.4.1) of type PROTOCOL_ERROR.
    }

    @Test
    public void shouldDisableCompressionOverTLS() {
        // A deployment of HTTP/2 over TLS 1.2 MUST disable compression.  TLS
        // compression can lead to the exposure of information that would not
        // otherwise be revealed [RFC3749].
    }

    @Test
    public void shouldNotUseProhibitedCipherSuites() {
        // A deployment of HTTP/2 over TLS 1.2 SHOULD NOT use any of the cipher
        // suites that are listed in Appendix A.
    }
}
