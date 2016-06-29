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
package org.kaazing.specification.wse;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.rules.RuleChain.outerRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.kaazing.k3po.junit.annotation.Specification;
import org.kaazing.k3po.junit.rules.K3poRule;

public class TextEscapedEncodingIT {
    private final K3poRule k3po = new K3poRule().setScriptRoot("org/kaazing/specification/wse/text.escaped.encoding");

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @Test
    @Specification({"echo.escaped.characters/request",
        "echo.escaped.characters/response" })
    public void shouldEchoEscapedCharacters() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "echo.binary.payload.all.byte.values/request",
        "echo.binary.payload.all.byte.values/response" })
    public void shouldEchoBinaryFrameWithAllByteValues() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "echo.binary.with.fragmented.encoded.byte/request",
        "echo.binary.with.fragmented.encoded.byte/response" })
    public void shouldEchoBinaryWithFragmentedEncodedByte() throws Exception {
        k3po.start();
        Thread.sleep(1000);
        k3po.notifyBarrier("WRITE_SECOND_FRAGMENT");
        k3po.finish();
    }

    @Test
    @Specification({
        "echo.binary.with.fragmented.escaped.byte/request",
        "echo.binary.with.fragmented.escaped.byte/response" })
    public void shouldEchoBinaryWithFragmentedEscapedByte() throws Exception {
        k3po.start();
        Thread.sleep(1000);
        k3po.notifyBarrier("WRITE_SECOND_FRAGMENT");
        k3po.finish();
    }

    @Test
    @Specification({
        "echo.binary.payload.length.0/request",
        "echo.binary.payload.length.0/response" })
    public void shouldEchoFrameWithPayloadLength0() throws Exception {
        k3po.finish();
    }

}
