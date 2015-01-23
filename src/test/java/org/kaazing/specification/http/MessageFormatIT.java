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
package org.kaazing.specification.http;

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
 * rfc7230#section-3
 *
 */
public class MessageFormatIT {

	private final K3poRule k3po = new K3poRule().setScriptRoot("org/kaazing/specification/http/message");

	private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

	@Rule
	public final TestRule chain = outerRule(k3po).around(timeout);

	@Test
	@Specification({ "upstream.should.accept.request.with.no.headers/request",
			"upstream.should.accept.request.with.no.headers/response" })
	public void upstreamShouldAcceptNoHeaders() throws Exception {
		k3po.join();
	}

	@Test
	public void upstreamShouldAcceptHeaders() {

	}

	@Test
	public void downstreamShouldAcceptNoHeaders() {

	}

	@Test
	public void downstreamShouldAcceptHeaders() {

	}

	@Test
	public void upstreamShouldRejectRequestWithWhitespaceBetweenStartLineAndFirstHaeder() {
		// As per RFC, alternatively could process everything before whitespace,
		// but the better choice is to reject, so that is what is tested in this
		// SPEC
	}

	@Test
	public void clientShouldSendRequestLineInStartLine() {

	}

	@Test
	public void upstreamShouldRejectInvalidRequestLine() {
		// responde 400 Bad Request

	}

	@Test
	public void serverShouldSend501ToUnImplementedMethods() {
		// 501 (Not Implemented)
	}

	@Test
	public void serverShouldSend414ToRequestWithTooLongARequest() {
		// 414 (URI Too Long) (rule of thumb is no more then 8000 octets)
	}

	@Test
	public void serverShouldSendStatusLineInStartLine() {

	}

	@Test
	public void proxyShouldPreserveUnrecognizedHeaders() {

	}

	@Test
	public void downstreamShouldAllowBadWhitespaceButNotSendAny() {
		// Multiple SP SP in header

	}

	@Test
	public void upstreamShouldAllowBadWhitespaceButNotSendAny() {

	}

	@Test
	public void clientShouldAcceptHeaderWithSpaceBetweenHeaderNameAndColon() {
		// parsing wise it should remove them
		// header :
	}

	@Test
	public void serverMustRejectHeaderWithSpaceBetweenHeaderNameAndColon() {
		// header :
		// return 400 Bad request
	}

	@Test
	public void proxyMustRemoveSpaceInHeaderWithSpaceBetweenHeaderNameAndColon() {
	}

	@Test
	public void downstreamShouldAllowNoSpaceBetweenColonAndHeaderValue() {
		// header :value
	}

	@Test
	public void upstreamShouldAllowNoSpaceBetweenColonAndHeaderValue() {
		// header :value
	}

	@Test
	public void clientMustAcceptOBSInHeaderValue() {
		// A user agent that receives an obs-fold in a response message that is
		// not within a message/http container MUST replace each received
		// obs-fold with one or more SP octets prior to interpreting the field
		// value.
		// parsing wise it should remove them
		// header :
	}

	@Test
	public void serverShouldRejectOBSInHeaderValue() {
		// header :value\nvalue
		// return 400 Bad request
	}

	@Test
	public void proxyOrGatewayMustRejectOBSInHeaderValue() {
		// header :value\nvalue
		// return 502 Bad Gateway
	}

	@Test
	public void upstreamOnReceivingFieldWithLenghtNotWantingToProcessMustReplyWith4xx() {

	}

	@Test
	public void serverShouldSend501ToUnknownTransferEncoding() {

	}

	@Test
	public void shouldProcessRequestWithContentLength() {

	}

	@Test
	public void shouldProcessResponseWithContentLength() {

	}

	@Test
	public void clientShouldSendContentLengthHeaderInPostEvenIfNoContent() {

	}

	@Test
	public void upstreamWithMultipleContentLengthsOfDifferentValuesMustBeRejected() {
		// Bad Request
	}

	@Test
	public void headRequestWith100ResponseMustNotHaveContent() {
		// no header
	}

	@Test
	public void headRequestWith204ResponseMustNotHaveContent() {
		// no header
	}

	@Test
	public void headRequestWith304ResponseMustNotHaveContent() {
		// no header
	}

	@Test
	public void serverMustRejectRequestWithMultipleDifferentContentLengthAndTransferEncoding() {
		// 400 Bad request
	}

	@Test
	public void gatewayMustRejectRequestWithMultipleDifferentContentLengthAndTransferEncoding() {
		// 502 Bad Gateway
	}

	@Test
	public void clientMustCloseConnectionWhenContentIsLessThanContentLength() {

	}

	@Test
	public void rebustUpstreamServerShouldAllowExtraCRLFAfterRequestLine() {
		// In the interest of robustness, a server that is expecting to receive
		// and parse a request-line SHOULD ignore at least one empty line (CRLF)
		// received prior to the request-line.
	}

	@Test
	public void nonHttpRequestToHttpServerShouldBeRespondedToWith400() {
		// 400 Bad request
	}

}
