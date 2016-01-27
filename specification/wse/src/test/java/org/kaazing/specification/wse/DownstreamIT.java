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

public class DownstreamIT {
    private final K3poRule k3po = new K3poRule()
            .setScriptRoot("org/kaazing/specification/wse/downstream");

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @Test
    @Specification({
        "binary/response.header.content.type.has.unexpected.value/downstream.request",
        "binary/response.header.content.type.has.unexpected.value/downstream.response" })
    public void shouldCloseConnectionWhenBinaryDownstreamResponseContentTypeHasUnexpectedValue()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "binary/response.status.code.not.200/downstream.request",
        "binary/response.status.code.not.200/downstream.response" })
    public void shouldCloseConnectionWhenBinaryDownstreamResponseStatusCodeNot200()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "binary/server.send.frame.after.reconnect/downstream.request",
        "binary/server.send.frame.after.reconnect/downstream.response" })
    public void shouldCloseConnectionWhenBinaryDownstreamResponseContainsFrameAfterReconnectFrame()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "binary/request.header.origin/downstream.request",
        "binary/request.header.origin/downstream.response" })
    public void shouldConnectWithDownstreamRequestOriginHeaderSet()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "binary/request.method.post/downstream.request",
        "binary/request.method.post/downstream.response" })
    public void shouldConnectWithDownstreamRequestMethodPost()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "binary/request.method.post.with.body/downstream.request",
        "binary/request.method.post.with.body/downstream.response" })
    public void shouldConnectWithDownstreamRequestMethodPostWithBody()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "binary/request.method.not.get.or.post/downstream.request",
        "binary/request.method.not.get.or.post/downstream.response" })
    public void shouldRespondWithBadRequestWhenDownstreamRequestMethodNotGetOrPost()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "binary/request.out.of.order/downstream.request",
        "binary/request.out.of.order/downstream.response" })
    public void shouldCloseConnectionWhenBinaryDownstreamRequestIsOutOfOrder() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "binary/subsequent.request.out.of.order/request",
        "binary/subsequent.request.out.of.order/response" })
    public void shouldCloseConnectionWhenSubsequentBinaryDownstreamRequestIsOutOfOrder() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "binary.as.escaped.text/response.header.content.type.has.unexpected.value/downstream.request",
        "binary.as.escaped.text/response.header.content.type.has.unexpected.value/downstream.response" })
    public void shouldCloseConnectionWhenEscapedTextDownstreamResponseContentTypeHasUnexpectedValue()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "binary.as.mixed.text/response.header.content.type.has.unexpected.value/downstream.request",
        "binary.as.mixed.text/response.header.content.type.has.unexpected.value/downstream.response" })
    public void shouldCloseConnectionWhenMixedTextDownstreamResponseContentTypeHasUnexpectedValue()
            throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "binary.as.text/response.header.content.type.has.unexpected.value/downstream.request",
        "binary.as.text/response.header.content.type.has.unexpected.value/downstream.response" })
    public void shouldCloseConnectionWhenTextDownstreamResponseContentTypeHasUnexpectedValue()
            throws Exception {
        k3po.finish();
    }
}
