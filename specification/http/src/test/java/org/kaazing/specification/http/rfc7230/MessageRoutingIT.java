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

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.kaazing.k3po.junit.annotation.Specification;
import org.kaazing.k3po.junit.rules.K3poRule;

/**
 * Test to validate behavior as specified in <a href="https://tools.ietf.org/html/rfc7230#section-5">RFC 7230 section 5:
 * Message Routing Host</a>.
 */
public class MessageRoutingIT {

    private final K3poRule k3po = new K3poRule().setScriptRoot("org/kaazing/specification/http/rfc7230/message.routing");

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-5.4">RFC 7230 section 5.4: Host</a>.
     * @throws Exception
     */
    @Test
    @Specification({
        "inbound.host.header.should.follow.request.line/request",
        "inbound.host.header.should.follow.request.line/response" })
    public void inboundHostHeaderShouldFollowRequestLine() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-5.4">RFC 7230 section 5.4: Host</a>.
     *
     * <blockquote> When a proxy receives a request with an absolute-form of request-target, the proxy MUST ignore the
     * received Host header field (if any) and instead replace it with the host information of the request-target. A
     * proxy that forwards such a request MUST generate a new Host field-value based on the received request-target
     * rather than forward the received Host field-value. </blockquote>
     *
     * @throws Exception
     */
    @Test
    @Specification({
        "proxy.should.rewrite.host.header/client",
        "proxy.should.rewrite.host.header/proxy",
        "proxy.should.rewrite.host.header/server" })
    public void proxyShouldRewriteHostHeader() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-5.4">RFC 7230 section 5.4: Host</a>.
     */
    @Test
    @Specification({
        "inbound.must.reject.request.with.400.if.missing.host.header/request",
        "inbound.must.reject.request.with.400.if.missing.host.header/response" })
    public void inboundMustRejectRequestWith400IfMissingHostHeader() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-5.4">RFC 7230 section 5.4: Host</a>.
     */
    @Test
    @Specification({
        "inbound.must.reject.request.with.400.if.host.header.does.not.match.uri/request",
        "inbound.must.reject.request.with.400.if.host.header.does.not.match.uri/response" })
    public void inboundMustRejectRequestWith400IfHostHeaderDoesNotMatchURI() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-5.4">RFC 7230 section 5.4: Host</a>.
     */
    @Test
    @Specification({
        "inbound.must.reject.request.with.400.if.host.header.occurs.more.than.once/request",
        "inbound.must.reject.request.with.400.if.host.header.occurs.more.than.once/response" })
    public void inboundMustRejectRequestWith400IfHostHeaderOccursMoreThanOnce() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-5.7.1">RFC 7230 section 5.7: Message Forwarding</a>.
     *
     * A proxy MUST send an appropriate Via header field, as described below, in each message that it forwards. An
     * HTTP-to-HTTP gateway MUST send an appropriate Via header field in each inbound request message and MAY send a Via
     * header field in forwarded response messages.
     *
     */
    @Test
    @Specification({
        "proxy.must.attach.appropriate.via.header/client",
        "proxy.must.attach.appropriate.via.header/proxy",
        "proxy.must.attach.appropriate.via.header/server" })
    public void proxyMustAttachAppropriateViaHeader() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-5.7">RFC 7230 section 5.7: Message Forwarding</a>.
     */
    @Test
    @Specification({
        "proxy.must.attach.appropriate.via.headers.even.when.others/client",
        "proxy.must.attach.appropriate.via.headers.even.when.others/proxy",
        "proxy.must.attach.appropriate.via.headers.even.when.others/server",
    })
    public void proxyMustAttachAppropriateViaHeadersEvenWhenOthers() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-5.7">RFC 7230 section 5.7: Message Forwarding</a>.
     */
    @Test
    @Specification({
        "gateway.must.attach.appropriate.via.header.on.request.and.may.attach.on.response/client",
        "gateway.must.attach.appropriate.via.header.on.request.and.may.attach.on.response/proxy",
        "gateway.must.attach.appropriate.via.header.on.request.and.may.attach.on.response/server" })
    public void gatewayMustAttachAppropriateViaHeaderOnRequestAndMayAttachOnResponse() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-5.7">RFC 7230 section 5.7: Message Forwarding</a>.
     *
     * An intermediary used as a portal through a network firewall SHOULD NOT forward the names and ports of hosts
     * within the firewall region unless it is explicitly enabled to do so. If not enabled, such an intermediary SHOULD
     * replace each received-by host of any host behind the firewall by an appropriate pseudonym for that host.
     *
     */
    @Test
    @Specification({
        "firewall.intermediary.should.replace.host.in.via.header.with.pseudonym/client",
        "firewall.intermediary.should.replace.host.in.via.header.with.pseudonym/proxy",
        "firewall.intermediary.should.replace.host.in.via.header.with.pseudonym/server" })
    public void firewallIntermediaryShouldReplaceHostInViaHeaderWithPseudonym() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-5.7">RFC 7230 section 5.7: Message Forwarding</a>.
     *
     * A proxy MUST NOT transform the payload (Section 3.3 of [RFC7231]) of a message that contains a no-transform
     * cache-control directive (Section 5.2 of [RFC7234]).
     *
     */
    @Test
    @Specification({
        "proxy.must.not.transform.the.payload.of.a.request.that.contains.a.no.transform.cache.control/request",
        "proxy.must.not.transform.the.payload.of.a.request.that.contains.a.no.transform.cache.control/response" })
    @Ignore("Not Implemented")
    public void proxyMustNotTransformThePayloadOfARequestThatContainsANoTransformCacheControl() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-5.7">RFC 7230 section 5.7: Message Forwarding</a>.
     *
     * A proxy MUST NOT transform the payload (Section 3.3 of [RFC7231]) of a message that contains a no-transform
     * cache-control directive (Section 5.2 of [RFC7234]).
     */
    @Test
    @Specification({
        "proxy.must.not.transform.the.payload.of.a.response.that.contains.a.no.transform.cache.control/request",
        "proxy.must.not.transform.the.payload.of.a.response.that.contains.a.no.transform.cache.control/response" })
    @Ignore("Not Implemented")
    public void proxyMustNotTransformThePayloadOfAResponseThatContainsANoTransformCacheControl() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-5.7">RFC 7230 section 5.7: Message Forwarding</a>. // A
     * proxy MUST NOT modify the "absolute-path" and "query" parts of the // received request-target when forwarding it
     * to the next inbound // server, except as noted above to replace an empty path with "/" or // "*".
     */
    @Test
    @Specification({
        "proxy.must.not.modify.query.or.absolute.path.of.request/request",
        "proxy.must.not.modify.query.or.absolute.path.of.request/response" })
    @Ignore("Not Implemented")
    public void proxyMustNotModifyQueryOrAbsolutePathOfRequest() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-5.3">RFC 7230 section 5.3: Request Target</a>.
     *
     * origin-form = absolute-path [ "?" query ], GET /where?q=now HTTP/1.1 ,Host: www.example.org
     * @throws Exception
     */
    @Test
    @Specification({
        "inbound.must.accept.origin.form/request",
        "inbound.must.accept.origin.form/response" })
    @Ignore("Not Implemented")
    public void inboundMustAcceptOriginForm() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-5.3">RFC 7230 section 5.3: Request Target</a>.
     *
     * GET http://www.example.org/pub/WWW/TheProject.html HTTP/1.1
     */
    @Test
    @Specification({
        "inbound.must.accept.absolute.form/request",
        "inbound.must.accept.absolute.form/response" })
    @Ignore("Not Implemented")
    public void inboundMustAcceptAbsoluteForm() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-5.3">RFC 7230 section 5.3: Request Target</a>.
     *
     * CONNECT www.example.com:80 HTTP/1.1
     */
    @Test
    @Specification({
        "intermediary.must.accept.authority.form.connect.request/request",
        "intermediary.must.accept.authority.form.connect.request/response" })
    @Ignore("Not Implemented")
    public void intermediaryMustAcceptAuthorityFormConnectRequest() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-5.3">RFC 7230 section 5.3: Request Target</a>.
     *
     * OPTIONS * HTTP/1.1
     */
    @Test
    @Specification({
        "inbound.must.accept.asterick.form.options.request/request",
        "inbound.must.accept.asterick.form.options.request/response" })
    @Ignore("Not Implemented")
    public void inboundMustAcceptAsterickFormOptionsRequest() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7230#section-5.3">RFC 7230 section 5.3: Request Target</a>.
     *
     * OPTIONS * HTTP/1.1 Host: www.example.org:8001
     */
    @Test
    @Specification({
        "last.proxy.must.convert.options.in.absolute.form.to.asterick.form/request",
        "last.proxy.must.convert.options.in.absolute.form.to.asterick.form/response" })
    @Ignore("Not Implemented")
    public void lastProxyMustConvertOptionsInAbsoluteFormToAsterickForm() throws Exception {
        k3po.finish();
    }

}
