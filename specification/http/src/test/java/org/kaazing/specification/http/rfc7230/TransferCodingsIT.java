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
 * Test to validate behavior as specified in <a href="https://tools.ietf.org/html/rfc7230#section-4">RFC 7230 section 4:
 * Transfer Codings</a>.
 */
public class TransferCodingsIT {

    private final K3poRule k3po = new K3poRule().setScriptRoot("org/kaazing/specification/http/rfc7230/transfer.codings");

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @Test
    @Specification({
        "request.transfer.encoding.chunked/request",
        "request.transfer.encoding.chunked/response" })
    public void requestTransferEncodingChunked() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "response.transfer.encoding.chunked/request",
        "response.transfer.encoding.chunked/response" })
    public void responseTransferEncodingChunked() throws Exception {
        k3po.finish();
    }

    @Test
    @Ignore("requires enhancement https://github.com/k3po/k3po/issues/313")
    public void requestTransferEncodingChunkedExtension() throws Exception {
        k3po.finish();
    }

    @Test
    @Ignore("requires enhancement https://github.com/k3po/k3po/issues/313")
    public void responseTransferEncodingChunkedExtension() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "request.transfer.encoding.chunked.with.trailer/request",
        "request.transfer.encoding.chunked.with.trailer/response" })
    public void requestTransferEncodingChunkedWithTrailer() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "response.transfer.encoding.chunked.with.trailer/request",
        "response.transfer.encoding.chunked.with.trailer/response" })
    public void responseTransferEncodingChunkedWithTrailer() throws Exception {
        k3po.finish();
    }

}
