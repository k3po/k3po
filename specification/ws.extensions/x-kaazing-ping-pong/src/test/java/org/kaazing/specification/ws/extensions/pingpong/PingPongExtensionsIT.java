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
package org.kaazing.specification.ws.extensions.pingpong;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.rules.RuleChain.outerRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.kaazing.k3po.junit.annotation.Specification;
import org.kaazing.k3po.junit.rules.K3poRule;

public class PingPongExtensionsIT {

    private final K3poRule k3po = new K3poRule()
            .setScriptRoot("org/kaazing/specification/ws.extensions/x-kaazing-ping-pong");

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @Test
    @Specification({
        "server.should.reply.to.standard.ping.with.standard.pong/request",
        "server.should.reply.to.standard.ping.with.standard.pong/response" })
    public void serverShouldReplyToStandardPingWithStandardPong() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "server.should.reply.to.extended.ping.with.extended.pong/request",
        "server.should.reply.to.extended.ping.with.extended.pong/response" })
    public void serverShouldReplyToExtendedPingWithExtendedPong() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "server.should.timeout.if.client.does.not.respond.to.extended.ping/request",
        "server.should.timeout.if.client.does.not.respond.to.extended.ping/response" })
    public void serverShouldTimeoutIfClientDoesNotRespondToExtendedPing() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "should.escape.text.frame.starting.with.control.bytes/request",
        "should.escape.text.frame.starting.with.control.bytes/response" })
    public void shouldEscapeTextFrameStartingWithControlBytes() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "should.not.escape.binary.frame/request",
        "should.not.escape.binary.frame/response" })
    public void shouldNotEscapeBinaryFrame() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "client.should.receive.extended.pong.frame/request",
        "client.should.receive.extended.pong.frame/response" })
    public void clientShouldReceiveExtendedPongFrame() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "client.should.reply.to.extended.ping.with.extended.pong/request",
        "client.should.reply.to.extended.ping.with.extended.pong/response" })
    public void clientShouldReplyToExtendedPingWithExtendedPong() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "client.should.reply.to.standard.ping.with.standard.pong/request",
        "client.should.reply.to.standard.ping.with.standard.pong/response" })
    public void clientShouldReplyToStandardPingWithStandardPong() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "client.should.disconnect.if.wrong.control.bytes.length/request",
        "client.should.disconnect.if.wrong.control.bytes.length/response" })
    public void clientShouldDisconnectIfWrongControlBytesLength() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "client.should.disconnect.if.no.control.bytes.sent/request",
        "client.should.disconnect.if.no.control.bytes.sent/response" })
    public void clientShouldDisconnectIfNoControlBytesSent() throws Exception {
        k3po.finish();
    }
}
