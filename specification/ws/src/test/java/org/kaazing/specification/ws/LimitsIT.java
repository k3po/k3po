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
 * RFC-6455, section 10.4 "Implementation-Specific Limits"
 */
public class LimitsIT {

    private final K3poRule k3po = new K3poRule().setScriptRoot("org/kaazing/specification/ws/limits");

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @Test
    @Specification({
        "should.fail.binary.payload.length.126/handshake.request.and.frame",
        "should.fail.binary.payload.length.126/handshake.response.and.frame" })
    public void shouldRefuseBinaryFrameWithPayloadLengthExceeding125() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "should.fail.binary.payload.length.131073/handshake.request.and.frame",
        "should.fail.binary.payload.length.131073/handshake.response.and.frame" })
    public void shouldRefuseBinaryFrameWithPayloadLengthExceeding128KiB() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "should.fail.text.payload.length.126/handshake.request.and.frame",
        "should.fail.text.payload.length.126/handshake.response.and.frame" })
    public void shouldRefuseTextFrameWithPayloadLengthExceeding125() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "should.fail.text.payload.length.131073/handshake.request.and.frame",
        "should.fail.text.payload.length.131073/handshake.response.and.frame" })
    public void shouldRefuseTextFrameWithPayloadLengthExceeding128KiB() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "should.fail.max.lifetime.exceeded/handshake.request.and.frame",
        "should.fail.max.lifetime.exceeded/handshake.response.and.frame" })
    public void shouldCloseWebSocketConnectionAfterSessionLifetimeIsUp() throws Exception {
        k3po.finish();
    }

}
