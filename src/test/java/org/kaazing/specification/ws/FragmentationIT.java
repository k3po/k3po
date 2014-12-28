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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.kaazing.k3po.junit.annotation.Specification;
import org.kaazing.k3po.junit.rules.K3poRule;

/**
 * RFC-6455, section 5.4 "Fragmentation"
 */
public class FragmentationIT {

    private final K3poRule k3po = new K3poRule().setScriptRoot("org/kaazing/specification/ws/fragmentation");

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @Test
    @Specification({
        "send.continuation.payload.length.125.not.fragmented/handshake.request.and.frame",
        "send.continuation.payload.length.125.not.fragmented/handshake.response.and.frame" })
    public void shouldFailWebSocketConnectionWhenSendContinuationFrameWithPayloadNotFragmented() throws Exception {
        k3po.join();
    }

    @Test
    @Specification({
        "send.continuation.payload.length.125.fragmented/handshake.request.and.frames",
        "send.continuation.payload.length.125.fragmented/handshake.response.and.frame" })
    public void shouldFailWebSocketConnectionWhenSendContinuationFrameWithPayloadFragmented() throws Exception {
        k3po.join();
    }

    @Test
    @Specification({
        "echo.text.payload.length.125.not.fragmented/handshake.request.and.frame",
        "echo.text.payload.length.125.not.fragmented/handshake.response.and.frame" })
    public void shouldEchoTextFrameWithPayloadNotFragmented() throws Exception {
        k3po.join();
    }

    @Test
    @Specification({
        "echo.text.payload.length.0.fragmented/handshake.request.and.frames",
        "echo.text.payload.length.0.fragmented/handshake.response.and.frame" })
    public void shouldEchoTextFrameWithEmptyPayloadFragmented() throws Exception {
        k3po.join();
    }

    @Test
    @Specification({
        "echo.text.payload.length.0.fragmented.with.injected.ping.pong/handshake.request.and.frames",
        "echo.text.payload.length.0.fragmented.with.injected.ping.pong/handshake.response.and.frame" })
    public void shouldEchoTextFrameWithEmptyPayloadFragmentedAndInjectedPingPong() throws Exception {
        k3po.join();
    }

    @Test
    @Specification({
        "echo.text.payload.length.125.fragmented/handshake.request.and.frames",
        "echo.text.payload.length.125.fragmented/handshake.response.and.frame" })
    public void shouldEchoTextFrameWithPayloadFragmented() throws Exception {
        k3po.join();
    }

    @Test
    @Specification({
        "echo.text.payload.length.125.fragmented.with.some.empty.fragments/handshake.request.and.frames",
        "echo.text.payload.length.125.fragmented.with.some.empty.fragments/handshake.response.and.frame" })
    public void shouldEchoTextFrameWithPayloadFragmentedWithSomeEmptyFragments() throws Exception {
        k3po.join();
    }

    @Test
    @Specification({
        "echo.text.payload.length.125.fragmented.but.not.utf8.aligned/handshake.request.and.frames",
        "echo.text.payload.length.125.fragmented.but.not.utf8.aligned/handshake.response.and.frame" })
    public void shouldEchoTextFrameWithPayloadFragmentedEvenWhenNotUTF8Aligned() throws Exception {
        k3po.join();
    }

    @Test
    @Specification({
        "echo.text.payload.length.125.fragmented.with.injected.ping.pong/handshake.request.and.frames",
        "echo.text.payload.length.125.fragmented.with.injected.ping.pong/handshake.response.and.frame" })
    public void shouldEchoTextFrameWithPayloadFragmentedAndInjectedPingPong() throws Exception {
        k3po.join();
    }

    @Test
    @Specification({
        "send.text.payload.length.125.fragmented.but.not.continued/handshake.request.and.frames",
        "send.text.payload.length.125.fragmented.but.not.continued/handshake.response.and.frame" })
    public void shouldFailWebSocketConnectionWhenSendTextFrameWithPayloadFragmentedButNotContinued() throws Exception {
        k3po.join();
    }

    @Test
    @Specification({
        "echo.binary.payload.length.125.not.fragmented/handshake.request.and.frame",
        "echo.binary.payload.length.125.not.fragmented/handshake.response.and.frame" })
    public void shouldEchoBinaryFrameWithPayloadNotFragmented() throws Exception {
        k3po.join();
    }

    @Test
    @Specification({
        "echo.binary.payload.length.0.fragmented/handshake.request.and.frames",
        "echo.binary.payload.length.0.fragmented/handshake.response.and.frame" })
    public void shouldEchoBinaryFrameWithEmptyPayloadFragmented() throws Exception {
        k3po.join();
    }

    @Test
    @Specification({
        "echo.binary.payload.length.0.fragmented.with.injected.ping.pong/handshake.request.and.frames",
        "echo.binary.payload.length.0.fragmented.with.injected.ping.pong/handshake.response.and.frame" })
    public void shouldEchoBinaryFrameWithEmptyPayloadFragmentedAndInjectedPingPong() throws Exception {
        k3po.join();
    }

    @Test
    @Specification({
        "echo.binary.payload.length.125.fragmented/handshake.request.and.frames",
        "echo.binary.payload.length.125.fragmented/handshake.response.and.frame" })
    public void shouldEchoBinaryFrameWithPayloadFragmented() throws Exception {
        k3po.join();
    }

    @Test
    @Specification({
        "echo.binary.payload.length.125.fragmented.with.some.empty.fragments/handshake.request.and.frames",
        "echo.binary.payload.length.125.fragmented.with.some.empty.fragments/handshake.response.and.frame" })
    public void shouldEchoBinaryFrameWithPayloadFragmentedWithSomeEmptyFragments() throws Exception {
        k3po.join();
    }

    @Test
    @Specification({
        "echo.binary.payload.length.125.fragmented.with.injected.ping.pong/handshake.request.and.frames",
        "echo.binary.payload.length.125.fragmented.with.injected.ping.pong/handshake.response.and.frame" })
    public void shouldEchoBinaryFrameWithPayloadFragmentedAndInjectedPingPong() throws Exception {
        k3po.join();
    }

    @Test
    @Specification({
        "send.binary.payload.length.125.fragmented.but.not.continued/handshake.request.and.frames",
        "send.binary.payload.length.125.fragmented.but.not.continued/handshake.response.and.frame" })
    public void shouldFailWebSocketConnectionWhenSendBinaryFrameWithPayloadFragmentedButNotContinued() throws Exception {
        k3po.join();
    }

    @Test
    @Specification({
        "send.close.payload.length.2.fragmented/handshake.request.and.frames",
        "send.close.payload.length.2.fragmented/handshake.response.and.frame" })
    public void shouldFailWebSocketConnectionWhenSendCloseFrameWithPayloadFragmented() throws Exception {
        k3po.join();
    }

    @Test
    @Specification({
        "send.ping.payload.length.0.fragmented/handshake.request.and.frames",
        "send.ping.payload.length.0.fragmented/handshake.response.and.frame" })
    public void shouldFailWebSocketConnectionWhenSendPingFrameWithPayloadFragmented() throws Exception {
        k3po.join();
    }

    @Test
    @Specification({
        "send.pong.payload.length.0.fragmented/handshake.request.and.frames",
        "send.pong.payload.length.0.fragmented/handshake.response.and.frame" })
    public void shouldFailWebSocketConnectionWhenSendPongFrameWithPayloadFragmented() throws Exception {
        k3po.join();
    }

}
