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
package org.kaazing.specification.http.rfc7230;

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

/**
 * Test to validate behavior as specified in <a href="https://tools.ietf.org/html/rfc7230#section-3">RFC 7230 section 3:
 * Message Format</a>.
 */
public class MessageFormatIT {

    private final K3poRule k3po = new K3poRule().setScriptRoot("org/kaazing/specification/http/rfc7230/message.format");

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-3">RFC 7230 section 3: Message Format</a>.
     *
     * @throws Exception when K3PO is not started
     */
    @Test
    @Specification({
            "inbound.should.accept.request.with.no.headers/request",
            "inbound.should.accept.request.with.no.headers/response" })
    public void inboundShouldAcceptNoHeaders() throws Exception {
        k3po.join();
    }

    @Test
    @Ignore("Not Implemented")
    public void inboundShouldAcceptHeaders() {

    }

    @Test
    @Ignore("Not Implemented")
    public void outboundShouldAcceptNoHeaders() {

    }

    @Test
    @Ignore("Not Implemented")
    public void outboundShouldAcceptHeaders() {

    }

    @Test
    @Ignore("Not Implemented")
    public void inboundShouldRejectRequestWithWhitespaceBetweenStartLineAndFirstHaeder() {
        // As per RFC, alternatively could process everything before whitespace,
        // but the better choice is to reject, so that is what is tested in this
        // SPEC
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-3.1">RFC 7230 section 3.1.1: Start Line</a>.
     */
    @Test
    @Ignore("Not Implemented")
    public void requestMustStartWithRequestLine() {

    }

    @Test
    @Ignore("Not Implemented")
    public void upstreamShouldRejectInvalidRequestLine() {
        // responde 400 Bad Request

    }

    @Test
    @Ignore("Not Implemented")
    public void serverShouldSend501ToUnImplementedMethods() {
        // 501 (Not Implemented)
    }

    @Test
    @Ignore("Not Implemented")
    public void serverShouldSend414ToRequestWithTooLongARequest() {
        // 414 (URI Too Long) (rule of thumb is no more then 8000 octets)
    }

    @Test
    @Ignore("Not Implemented")
    public void serverShouldSendStatusLineInStartLine() {

    }

    @Test
    @Ignore("Not Implemented")
    public void proxyShouldPreserveUnrecognizedHeaders() {

    }

    @Test
    @Ignore("Not Implemented")
    public void downstreamShouldAllowBadWhitespaceButNotSendAny() {
        // Multiple SP SP in header

    }

    @Test
    @Ignore("Not Implemented")
    public void upstreamShouldAllowBadWhitespaceButNotSendAny() {

    }

    @Test
    @Ignore("Not Implemented")
    public void clientShouldAcceptHeaderWithSpaceBetweenHeaderNameAndColon() {
        // parsing wise it should remove them
        // header :
    }

    @Test
    @Ignore("Not Implemented")
    public void serverMustRejectHeaderWithSpaceBetweenHeaderNameAndColon() {
        // header :
        // return 400 Bad request
    }

    @Test
    @Ignore("Not Implemented")
    public void proxyMustRemoveSpaceInHeaderWithSpaceBetweenHeaderNameAndColon() {
    }

    @Test
    @Ignore("Not Implemented")
    public void downstreamShouldAllowNoSpaceBetweenColonAndHeaderValue() {
        // header :value
    }

    @Test
    @Ignore("Not Implemented")
    public void upstreamShouldAllowNoSpaceBetweenColonAndHeaderValue() {
        // header :value
    }

    @Test
    @Ignore("Not Implemented")
    public void clientMustAcceptOBSInHeaderValue() {
        // A user agent that receives an obs-fold in a response message that is
        // not within a message/http container MUST replace each received
        // obs-fold with one or more SP octets prior to interpreting the field
        // value.
        // parsing wise it should remove them
        // header :
    }

    @Test
    @Ignore("Not Implemented")
    public void serverShouldRejectOBSInHeaderValue() {
        // header :value\nvalue
        // return 400 Bad request
    }

    @Test
    @Ignore("Not Implemented")
    public void proxyOrGatewayMustRejectOBSInHeaderValue() {
        // header :value\nvalue
        // return 502 Bad Gateway
    }

    @Test
    @Ignore("Not Implemented")
    public void upstreamOnReceivingFieldWithLenghtNotWantingToProcessMustReplyWith4xx() {

    }

    @Test
    @Ignore("Not Implemented")
    public void serverShouldSend501ToUnknownTransferEncoding() {

    }

    @Test
    @Ignore("Not Implemented")
    public void shouldProcessRequestWithContentLength() {

    }

    @Test
    @Ignore("Not Implemented")
    public void shouldProcessResponseWithContentLength() {

    }

    @Test
    @Ignore("Not Implemented")
    public void clientShouldSendContentLengthHeaderInPostEvenIfNoContent() {

    }

    @Test
    @Ignore("Not Implemented")
    public void upstreamWithMultipleContentLengthsOfDifferentValuesMustBeRejected() {
        // Bad Request
    }

    @Test
    @Ignore("Not Implemented")
    public void headRequestWith100ResponseMustNotHaveContent() {
        // no header
    }

    @Test
    @Ignore("Not Implemented")
    public void headRequestWith204ResponseMustNotHaveContent() {
        // no header
    }

    @Test
    @Ignore("Not Implemented")
    public void headRequestWith304ResponseMustNotHaveContent() {
        // no header
    }

    @Test
    @Ignore("Not Implemented")
    public void serverMustRejectRequestWithMultipleDifferentContentLengthAndTransferEncoding() {
        // 400 Bad request
    }

    @Test
    @Ignore("Not Implemented")
    public void gatewayMustRejectRequestWithMultipleDifferentContentLengthAndTransferEncoding() {
        // 502 Bad Gateway
    }

    @Test
    @Ignore("Not Implemented")
    public void clientMustCloseConnectionWhenContentIsLessThanContentLength() {

    }

    @Test
    @Ignore("Not Implemented")
    public void rebustUpstreamServerShouldAllowExtraCRLFAfterRequestLine() {
        // In the interest of robustness, a server that is expecting to receive
        // and parse a request-line SHOULD ignore at least one empty line (CRLF)
        // received prior to the request-line.
    }

    @Test
    @Ignore("Not Implemented")
    public void nonHttpRequestToHttpServerShouldBeRespondedToWith400() {
        // 400 Bad request
    }

}
