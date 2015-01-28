/*
 * Copyright (c) 2014 "Kaazing Corporation," (www.kaazing.com)
 *
 * This file is part of Robot.
 *
 * Robot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.kaazing.k3po.itest;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.rules.RuleChain.outerRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.kaazing.k3po.junit.annotation.Specification;
import org.kaazing.k3po.junit.rules.K3poRule;

public class HttpIT {

    private final K3poRule k3po = new K3poRule();

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @Test
    @Specification({
        "http.echo.long.request.payload/request", 
        "http.echo.long.request.payload/response" })
    public void shouldEchoLongRequestPayload() throws Exception {
        k3po.join();
    }

    @Test
    @Specification({
        "http.accept.header.with.multiple.tokens", 
        "tcp.connect.header.with.multiple.tokens" })
    public void shouldAcceptHeaderWithMultipleTokens() throws Exception {

        k3po.join();
    }

    @Test
    @Specification({
        "http.accept.read.parameter.with.multiple.tokens",
        "tcp.connect.write.parameter.with.multiple.tokens" })
    public void shouldAcceptReadParameterWithMultipleTokens() throws Exception {

        k3po.join();
    }

    @Test
    @Specification({
        "http.connect.write.parameter.with.multiple.tokens",
        "tcp.accept.read.parameter.with.multiple.tokens" })
    public void shouldAcceptWriteParameterWithMultipleTokens() throws Exception {

        k3po.join();
    }

    @Test
    @Specification({
        "http.accept.get.request.with.no.content.on.response",
        "tcp.connect.get.request.with.no.content.on.response" })
    public void shouldReceiveGetRequestAndProvideResponse() throws Exception {

        k3po.join();

    }

    @Test
    @Specification({
        "http.accept.get.request.with.content.on.response",
        "tcp.connect.get.request.with.content.on.response" })
    public void shouldReceiveGetRequestAndProvideResponseWithContent() throws Exception {

        k3po.join();

    }

    @Test
    @Specification({
        "http.connect.get.request.with.no.content.on.response",
        "tcp.accept.get.request.with.no.content.on.response" })
    public void shouldSendGetRequestAndReceiveResponseWithNoContent() throws Exception {
        k3po.join();
    }

    @Test
    @Specification({
        "http.connect.get.request.with.content.on.response",
        "tcp.accept.get.request.with.content.on.response" })
    public void shouldSendGetRequestAndReceiveResponseWithContent() throws Exception {
        k3po.join();
    }

    @Test
    @Specification({
        "http.accept.websocket.handshake",
        "tcp.connect.websocket.handshake" })
    public void shouldAcceptWebsocketHandshake() throws Exception {
        k3po.join();
    }

    @Test
    @Specification({
        "http.accept.websocket.handshake.then.server.close",
        "http.connect.websocket.handshake.then.server.close" })
    public void shouldAcceptWebsocketHandshakeThenServerClose() throws Exception {
        k3po.join();
    }

    @Test
    @Specification({
        "http.connect.websocket.handshake",
        "tcp.accept.websocket.handshake" })
    public void shouldConnectWebsocketHandshake() throws Exception {
        k3po.join();
    }

    @Test
    @Specification({
        "http.accept.post.with.chunking",
        "tcp.connect.post.with.chunking" })
    public void shouldAcceptPostMessageWithChunking() throws Exception {
        k3po.join();
    }

    @Test
    @Specification({
        "http.connect.post.with.chunking",
        "tcp.accept.post.with.chunking" })
    public void shouldConnectPostMessageWithChunking() throws Exception {
        k3po.join();
    }

    @Test
    @Specification({
        "http.accept.response.with.chunking",
        "tcp.connect.response.with.chunking" })
    public void shouldAcceptResponseWithChunking() throws Exception {
        k3po.join();
    }

    @Test
    @Specification({
        "http.connect.response.with.chunking",
        "tcp.accept.response.with.chunking" })
    public void shouldConnectResponseWithChunking() throws Exception {
        k3po.join();
    }

    @Test
    @Specification({
        "http.connect.connection.close.response",
        "tcp.accept.connection.close.response" })
    public void shouldConnectConnectionCloseResponse() throws Exception {
        k3po.join();
    }

    @Test
    @Specification({
        "http.accept.connection.close.response",
        "tcp.connect.connection.close.response" })
    public void shouldAcceptConnectionCloseResponse() throws Exception {
        k3po.join();
    }

    @Test
    @Specification({
        "http.accept.two.http.200",
        "tcp.connect.two.http.200.on.different.streams" })
    public void shouldAcceptMultipleHttpOnDifferentTcp() throws Exception {
        k3po.join();
    }

    @Test
    @Specification({
        "http.accept.two.http.200",
        "tcp.connect.two.http.200.on.same.streams" })
    public void shouldAcceptMultipleHttpOnSameTcp() throws Exception {
        k3po.join();
    }
}