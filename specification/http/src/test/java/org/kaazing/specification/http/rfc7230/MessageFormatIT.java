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
package org.kaazing.specification.http.rfc7230;

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
     * <blockquote> All HTTP/1.1 messages consist of a start-line followed by a sequence of octets in a format similar
     * to the Internet Message Format [RFC5322]: zero or more header fields (collectively referred to as the "headers"
     * or the "header section"), an empty line indicating the end of the header section, and an optional message body.
     * </blockquote>
     *
     * @throws Exception when K3PO is not started
     */
    @Test
    @Specification({
            "inbound.should.accept.headers/request",
            "inbound.should.accept.headers/response" })
    public void inboundShouldAcceptHeaders() throws Exception {
        k3po.finish();

    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-3">RFC 7230 section 3: Message Format</a>.
     *
     * <blockquote> All HTTP/1.1 messages consist of a start-line followed by a sequence of octets in a format similar
     * to the Internet Message Format [RFC5322]: zero or more header fields (collectively referred to as the "headers"
     * or the "header section"), an empty line indicating the end of the header section, and an optional message body.
     * </blockquote>
     *
     * @throws Exception when K3PO is not started
     */
    @Test
    @Specification({
            "outbound.should.accept.no.headers/request",
            "outbound.should.accept.no.headers/response" })
    public void outboundShouldAcceptNoHeaders() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-3">RFC 7230 section 3: Message Format</a>.
     *
     * <blockquote> All HTTP/1.1 messages consist of a start-line followed by a sequence of octets in a format similar
     * to the Internet Message Format [RFC5322]: zero or more header fields (collectively referred to as the "headers"
     * or the "header section"), an empty line indicating the end of the header section, and an optional message body.
     * </blockquote>
     *
     * @throws Exception when K3PO is not started
     */
    @Test
    @Specification({
            "outbound.should.accept.headers/request",
            "outbound.should.accept.headers/response" })
    public void outboundShouldAcceptHeaders() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-3">RFC 7230 section 3: Message Format</a>.
     *
     * A sender MUST NOT send whitespace between the start-line and the first header field. A recipient that receives
     * whitespace between the start-line and the first header field MUST either reject the message as invalid or consume
     * each whitespace-preceded line without further processing of it (i.e., ignore the entire line, along with any
     * subsequent lines preceded by whitespace, until a properly formed header field is received or the header section
     * is terminated).
     *
     * @throws Exception when K3PO is not started
     */
    @Test
    @Specification({
            "inbound.should.reject.request.with.whitespace.between.start.line.and.first.header/request",
            "inbound.should.reject.request.with.whitespace.between.start.line.and.first.header/response" })
    public void inboundShouldRejectRequestWithWhitespaceBetweenStartLineAndFirstHeader() throws Exception {
        // As per RFC, alternatively could process everything before whitespace,
        // but the better choice is to reject
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-3.1">RFC 7230 section 3.1.1: Start Line</a>.
     *
     * @throws Exception when K3PO is not started
     */
    @Test
    @Specification({
            "request.must.start.with.request.line/request",
            "request.must.start.with.request.line/response" })
    public void requestMustStartWithRequestLine() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-3">RFC 7230 section 3: Message Format</a>.
     *
     * @throws Exception when K3PO is not started
     */
    @Test
    @Specification({
            "inbound.should.reject.invalid.request.line/request",
            "inbound.should.reject.invalid.request.line/response" })
    public void inboundShouldRejectInvalidRequestLine() throws Exception {
        // responds with 400 Bad Request
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-3">RFC 7230 section 3: Message Format</a>.
     *
     * @throws Exception when K3PO is not started
     */
    @Test
    @Specification({
            "server.should.send.501.to.unimplemented.methods/request",
            "server.should.send.501.to.unimplemented.methods/response" })
    public void serverShouldSend501ToUnImplementedMethods() throws Exception {
        // 501 (Not Implemented)
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-3">RFC 7230 section 3: Message Format</a>.
     *
     * @throws Exception when K3PO is not started
     */
    @Test
    @Specification({
            "server.should.send.414.to.request.with.too.long.a.request/request",
            "server.should.send.414.to.request.with.too.long.a.request/response" })
    public void serverShouldSend414ToRequestWithTooLongARequest() throws Exception {
        // 414 (URI Too Long) (rule of thumb is no more then 8000 octets)
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-3">RFC 7230 section 3: Message Format</a>.
     *
     * @throws Exception when K3PO is not started
     */
    @Test
    @Specification({
            "server.should.send.status.line.in.start.line/request",
            "server.should.send.status.line.in.start.line/response" })
    public void serverShouldSendStatusLineInStartLine() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-3">RFC 7230 section 3: Message Format</a>.
     *
     * @throws Exception when K3PO is not started
     */
    @Test
    @Specification({
            "proxy.should.preserve.unrecongnized.headers/client",
            "proxy.should.preserve.unrecongnized.headers/server",
            "proxy.should.preserve.unrecongnized.headers/proxy" })
    public void proxyShouldPreserveUnrecognizedHeaders() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-3">RFC 7230 section 3: Message Format</a>.
     *
     * No whitespace is allowed between the header field-name and colon. In the past, differences in the handling of
     * such whitespace have led to security vulnerabilities in request routing and response handling. A server MUST
     * reject any received request message that contains whitespace between a header field-name and colon with a
     * response code of 400 (Bad Request). A proxy MUST remove any such whitespace from a response message before
     * forwarding the message downstream.
     *
     * @throws Exception when K3PO is not started
     */
    @Test
    @Specification({
            "server.must.reject.header.with.space.between.header.name.and.colon/request",
            "server.must.reject.header.with.space.between.header.name.and.colon/response" })
    public void serverMustRejectHeaderWithSpaceBetweenHeaderNameAndColon() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-3">RFC 7230 section 3: Message Format</a>.
     *
     * No whitespace is allowed between the header field-name and colon. In the past, differences in the handling of
     * such whitespace have led to security vulnerabilities in request routing and response handling. A server MUST
     * reject any received request message that contains whitespace between a header field-name and colon with a
     * response code of 400 (Bad Request). A proxy MUST remove any such whitespace from a response message before
     * forwarding the message downstream.
     *
     * @throws Exception when K3PO is not started
     */
    @Test
    @Specification({
            "on.response.proxy.must.remove.space.in.header.with.space.between.header.name.and.colon/client",
            "on.response.proxy.must.remove.space.in.header.with.space.between.header.name.and.colon/server",
            "on.response.proxy.must.remove.space.in.header.with.space.between.header.name.and.colon/proxy" })
    public void onResponseProxyMustRemoveSpaceInHeaderWithSpaceBetweenHeaderNameAndColon() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-3">RFC 7230 section 3: Message Format</a>.
     *
     * A server that receives an obs-fold in a request message that is not within a message/http container MUST either
     * reject the message by sending a 400 (Bad Request), preferably with a representation explaining that obsolete line
     * folding is unacceptable, or replace each received obs-fold with one or more SP octets prior to interpreting the
     * field value or forwarding the message downstream.
     *
     * @throws Exception when K3PO is not started
     */
    @Test
    @Specification({
            "server.should.reject.obs.in.header.value/request",
            "server.should.reject.obs.in.header.value/response" })
    public void serverShouldRejectOBSInHeaderValue() throws Exception {
        // header :value\nvalue
        // return 400 Bad request
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-3">RFC 7230 section 3: Message Format</a>.
     *
     * A proxy or gateway that receives an obs-fold in a response message that is not within a message/http container
     * MUST either discard the message and replace it with a 502 (Bad Gateway) response, preferably with a
     * representation explaining that unacceptable line folding was received, or replace each received obs-fold with one
     * or more SP octets prior to interpreting the field value or forwarding the message downstream.
     *
     * @throws Exception when K3PO is not started
     */
    @Test
    @Specification({
            "proxy.or.gateway.must.reject.obs.in.header.value/request",
            "proxy.or.gateway.must.reject.obs.in.header.value/response" })
    public void proxyOrGatewayMustRejectOBSInHeaderValue() throws Exception {
        // header :value\nvalue
        // return 502 Bad Gateway
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-3">RFC 7230 section 3: Message Format</a>.
     *
     * @throws Exception when K3PO is not started
     */
    @Test
    @Specification({
            "inbound.on.receiving.field.with.length.larger.than.wanting.to.process.must.reply.with.4xx/request",
            "inbound.on.receiving.field.with.length.larger.than.wanting.to.process.must.reply.with.4xx/response" })
    public void inboundOnReceivingFieldWithLengthLargerThanWantingToProcessMustReplyWith4xx() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-3">RFC 7230 section 3: Message Format</a>.
     *
     * @throws Exception when K3PO is not started
     */
    @Test
    @Specification({
            "server.should.send.501.to.unknown.transfer.encoding/request",
            "server.should.send.501.to.unknown.transfer.encoding/response" })
    public void serverShouldSend501ToUnknownTransferEncoding() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-3">RFC 7230 section 3: Message Format</a>.
     *
     * @throws Exception when K3PO is not started
     */
    @Test
    @Specification({
            "outbound.should.process.response.with.content.length/request",
            "outbound.should.process.response.with.content.length/response" })
    public void outboundShouldProcessResponseWithContentLength() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-3">RFC 7230 section 3: Message Format</a>.
     *
     * @throws Exception when K3PO is not started
     */
    @Test
    @Specification({
            "inbound.should.process.request.with.content.length/request",
            "inbound.should.process.request.with.content.length/response" })
    public void inboundShouldProcessRequestWithContentLength() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-3">RFC 7230 section 3: Message Format</a>.
     *
     * @throws Exception when K3PO is not started
     */
    @Test
    @Specification({
            "client.should.send.content.length.header.in.post.even.if.no.content/request",
            "client.should.send.content.length.header.in.post.even.if.no.content/response" })
    public void clientShouldSendContentLengthHeaderInPostEvenIfNoContent() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-3">RFC 7230 section 3: Message Format</a>.
     *
     * Responses to the HEAD request method (Section 4.3.2 of [RFC7231]) never include a message body because the
     * associated response header fields (e.g., Transfer-Encoding, Content-Length, etc.), if present, indicate only what
     * their values would have been if the request method had been GET (Section 4.3.1 of [RFC7231]). 2xx (Successful)
     * responses to a CONNECT request method (Section 4.3.6 of [RFC7231]) switch to tunnel mode instead of having a
     * message body. All 1xx (Informational), 204 (No Content), and 304 (Not Modified) responses do not include a
     * message body.
     *
     * @throws Exception when K3PO is not started
     */
    @Test
    @Specification({
            "head.response.must.not.have.content/request",
            "head.response.must.not.have.content/response" })
    public void headResponseMustNotHaveContent() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-3">RFC 7230 section 3: Message Format</a>.
     *
     * Responses to the HEAD request method (Section 4.3.2 of [RFC7231]) never include a message body because the
     * associated response header fields (e.g., Transfer-Encoding, Content-Length, etc.), if present, indicate only what
     * their values would have been if the request method had been GET (Section 4.3.1 of [RFC7231]). 2xx (Successful)
     * responses to a CONNECT request method (Section 4.3.6 of [RFC7231]) switch to tunnel mode instead of having a
     * message body. All 1xx (Informational), 204 (No Content), and 304 (Not Modified) responses do not include a
     * message body.
     *
     * @throws Exception when K3PO is not started
     */
    @Test
    @Specification({
            "head.response.must.not.have.content.though.may.have.content.length/request",
            "head.response.must.not.have.content.though.may.have.content.length/response" })
    public void headResponseMustNotHaveContentThoughMayHaveContentLength() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-3.3.3">RFC 7230 section 3: Message Format</a>.
     *
     * If a message is received without Transfer-Encoding and with either multiple Content-Length header fields having
     * differing field-values or a single Content-Length header field having an invalid value, then the message framing
     * is invalid and the recipient MUST treat it as an unrecoverable error. If this is a request message, the server
     * MUST respond with a 400 (Bad Request) status code and then close the connection. If this is a response message
     * received by a proxy, the proxy MUST close the connection to the server, discard the received response, and send a
     * 502 (Bad Gateway) response to the client. If this is a response message received by a user agent, the user agent
     * MUST close the connection to the server and discard the received response.
     *
     * @throws Exception when K3PO is not started
     */
    @Test
    @Specification({
            "server.must.reject.request.with.multiple.different.content.length/request",
            "server.must.reject.request.with.multiple.different.content.length/response" })
    public void serverMustRejectRequestWithMultipleDifferentContentLength() throws Exception {
        // 400 Bad request
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-3.3.3">RFC 7230 section 3: Message Format</a>.
     *
     * If a message is received without Transfer-Encoding and with either multiple Content-Length header fields having
     * differing field-values or a single Content-Length header field having an invalid value, then the message framing
     * is invalid and the recipient MUST treat it as an unrecoverable error. If this is a request message, the server
     * MUST respond with a 400 (Bad Request) status code and then close the connection. If this is a response message
     * received by a proxy, the proxy MUST close the connection to the server, discard the received response, and send a
     * 502 (Bad Gateway) response to the client. If this is a response message received by a user agent, the user agent
     * MUST close the connection to the server and discard the received response.
     *
     * @throws Exception when K3PO is not started
     */
    @Test
    @Specification({
            "gateway.must.reject.request.with.multiple.different.content.length/request",
            "gateway.must.reject.request.with.multiple.different.content.length/gateway",
            "gateway.must.reject.request.with.multiple.different.content.length/response" })
    public void gatewayMustRejectResponseWithMultipleDifferentContentLength() throws Exception {
        // 502 Bad Gateway
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-3">RFC 7230 section 3: Message Format</a>.
     *
     * In the interest of robustness, a server that is expecting to receive and parse a request-line SHOULD ignore at
     * least one empty line (CRLF) received prior to the request-line.
     *
     * @throws Exception when K3PO is not started
     */
    @Test
    @Specification({
            "robust.server.should.allow.extra.CRLF.after.request.line/request",
            "robust.server.should.allow.extra.CRLF.after.request.line/response" })
    public void robustServerShouldAllowExtraCRLFAfterRequestLine() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-3">RFC 7230 section 3: Message Format</a>.
     *
     * @throws Exception when K3PO is not started
     */
    @Test
    @Specification({
            "non.http.request.to.http.server.should.be.responded.to.with.400/request",
            "non.http.request.to.http.server.should.be.responded.to.with.400/response" })
    public void nonHttpRequestToHttpServerShouldBeRespondedToWith400() throws Exception {
        k3po.finish();
    }

}
