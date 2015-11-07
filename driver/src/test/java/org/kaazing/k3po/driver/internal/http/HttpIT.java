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

package org.kaazing.k3po.driver.internal.http;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.rules.RuleChain.outerRule;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.kaazing.k3po.driver.internal.test.utils.K3poTestRule;
import org.kaazing.k3po.driver.internal.test.utils.TestSpecification;

public class HttpIT {

    private final K3poTestRule k3po = new K3poTestRule();

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final ExpectedException expectedExceptions = ExpectedException.none();

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @Test
    @TestSpecification({
        "http.accept.header.missing",
        "http.connect.header.missing"
    })
    public void shouldNotAcceptHeaderWhenExpectedMissing() throws Exception {
        k3po.finish();
        expectedExceptions.expect(AssertionError.class);
    }

    @Test
    @TestSpecification({
        "http.echo.long.request.payload/request",
        "http.echo.long.request.payload/response"
    })
    public void shouldEchoLongRequestPayload() throws Exception {
        k3po.finish();
    }

    @Test
    @TestSpecification({
        "http.accept.header.with.multiple.tokens",
        "tcp.connect.header.with.multiple.tokens" })
    public void shouldAcceptHeaderWithMultipleTokens() throws Exception {

        k3po.finish();
    }

    @Test
    @TestSpecification({
        "http.accept.websocket.handshake.then.client.hard.close",
        "http.connect.websocket.handshake.then.client.hard.close" })
    public void shouldAcceptWebsocketHandshakeThenClientHardClose() throws Exception {
        k3po.finish();
    }

    @Test
    @TestSpecification({
        "http.accept.read.parameter.with.multiple.tokens",
        "tcp.connect.write.parameter.with.multiple.tokens" })
    public void shouldAcceptReadParameterWithMultipleTokens() throws Exception {

        k3po.finish();
    }

    @Test
    @TestSpecification({
        "http.connect.write.parameter.with.multiple.tokens",
        "tcp.accept.read.parameter.with.multiple.tokens" })
    public void shouldAcceptWriteParameterWithMultipleTokens() throws Exception {

        k3po.finish();
    }

    @Test
    @TestSpecification({
        "http.accept.get.request.with.no.content.on.response",
        "tcp.connect.get.request.with.no.content.on.response" })
    public void shouldReceiveGetRequestAndProvideResponse() throws Exception {

        k3po.finish();

    }

    @Test
    @TestSpecification({
        "http.accept.get.request.with.content.on.response",
        "tcp.connect.get.request.with.content.on.response" })
    public void shouldReceiveGetRequestAndProvideResponseWithContent() throws Exception {

        k3po.finish();

    }

    @Test
    @TestSpecification({
        "http.connect.get.request.with.no.content.on.response",
        "tcp.accept.get.request.with.no.content.on.response" })
    public void shouldSendGetRequestAndReceiveResponseWithNoContent() throws Exception {
        k3po.finish();
    }

    @Test
    @TestSpecification({
        "http.connect.get.request.with.content.on.response",
        "tcp.accept.get.request.with.content.on.response" })
    public void shouldSendGetRequestAndReceiveResponseWithContent() throws Exception {
        k3po.finish();
    }

    @Test
    @TestSpecification({
        "http.accept.websocket.handshake",
        "tcp.connect.websocket.handshake" })
    public void shouldAcceptWebsocketHandshake() throws Exception {
        k3po.finish();
    }

    @Test
    @TestSpecification({
        "http.accept.websocket.handshake.then.server.close",
        "http.connect.websocket.handshake.then.server.close" })
    public void shouldAcceptWebsocketHandshakeThenServerClose() throws Exception {
        k3po.finish();
    }

    @Test
    @TestSpecification({
        "http.connect.websocket.handshake",
        "tcp.accept.websocket.handshake" })
    public void shouldConnectWebsocketHandshake() throws Exception {
        k3po.finish();
    }

    @Test
    @TestSpecification({
        "http.accept.post.with.chunking",
        "tcp.connect.post.with.chunking" })
    public void shouldAcceptPostMessageWithChunking() throws Exception {
        k3po.finish();
    }

    @Test
    @TestSpecification({
        "http.connect.post.with.chunking",
        "tcp.accept.post.with.chunking" })
    public void shouldConnectPostMessageWithChunking() throws Exception {
        k3po.finish();
    }

    @Test
    @TestSpecification({
        "http.accept.response.with.chunking",
        "tcp.connect.response.with.chunking" })
    public void shouldAcceptResponseWithChunking() throws Exception {
        k3po.finish();
    }

    @Test
    @TestSpecification({
        "http.connect.response.with.chunking",
        "tcp.accept.response.with.chunking" })
    public void shouldConnectResponseWithChunking() throws Exception {
        k3po.finish();
    }

    @Test
    @TestSpecification({
        "http.connect.connection.close.response",
        "tcp.accept.connection.close.response" })
    public void shouldConnectConnectionCloseResponse() throws Exception {
        k3po.finish();
    }

    @Test
    @TestSpecification({
        "http.accept.connection.close.response",
        "tcp.connect.connection.close.response" })
    public void shouldAcceptConnectionCloseResponse() throws Exception {
        k3po.finish();
    }

    @Test
    @TestSpecification({
        "http.accept.two.http.200",
        "tcp.connect.two.http.200.on.different.streams" })
    public void shouldAcceptMultipleHttpOnDifferentTcp() throws Exception {
        k3po.finish();
    }

    @Test
    @TestSpecification({
        "http.accept.two.http.200",
        "tcp.connect.two.http.200.on.same.streams" })
    public void shouldAcceptMultipleHttpOnSameTcp() throws Exception {
        k3po.finish();
    }

    @Test
    @Ignore("kepo#256")
    @TestSpecification({
        "server.closes.abruptly.client.closed" })
    public void closedShouldWorkOrBeRejected() throws Exception {
        k3po.finish();
    }

    @Test
    @TestSpecification({
        "specify.only.part.of.http.response/request",
        "specify.only.part.of.http.response/response" })
    public void specifyOnlyPartOfHttpResponse() throws Exception {
        k3po.finish();
    }

    @Test
    @TestSpecification({
        "read.content.length.via.regex/request",
        "read.content.length.via.regex/response" })
    public void testReadContentLengthViaAVariable() throws Exception {
        k3po.finish();
    }

}
