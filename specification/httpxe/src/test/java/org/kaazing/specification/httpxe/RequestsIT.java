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

package org.kaazing.specification.httpxe;

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
 * Defines how httpxe will deal with http methods.
 */
public class RequestsIT {

    private final K3poRule k3po = new K3poRule().setScriptRoot("org/kaazing/specification/httpxe/requests");

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @Test
    @Specification({
        "post.request.with.km.parameter.get/request",
        "post.request.with.km.parameter.get/response"})
    public void shouldProcessPostRequestAsGetRequest() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "post.request.with.path.encoded.get/request",
        "post.request.with.path.encoded.get/response"})
    public void shouldProcessPathEncodedPostRequestAsGetRequest() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "post.request.with.km.parameter.head/request",
        "post.request.with.km.parameter.head/response"})
    public void shouldProcessPostRequestAsHeadRequest() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "post.request.with.path.encoded.head/request",
        "post.request.with.path.encoded.head/response"})
    public void shouldProcessPathEncodedPostRequestAsHeadRequest() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "post.request.with.path.encoded.post/request",
        "post.request.with.path.encoded.post/response"})
    public void shouldProcessPathEncodedGetRequestAsPostRequest() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "post.request.with.km.parameter.put/request",
        "post.request.with.km.parameter.put/response"})
    public void shouldProcessPostRequestAsPutRequest() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "post.request.with.path.encoded.put/request",
        "post.request.with.path.encoded.put/response"})
    public void shouldProcessPathEncodedPostRequestAsPutRequest() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "post.request.with.km.parameter.delete/request",
        "post.request.with.km.parameter.delete/response"})
    public void shouldProcessPostRequestAsDeleteRequest() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "post.request.with.path.encoded.delete/request",
        "post.request.with.path.encoded.delete/response"})
    public void shouldProcessPathEncodedPostRequestAsDeleteRequest() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "post.request.with.km.parameter.options/request",
        "post.request.with.km.parameter.options/response"})
    public void shouldProcessPostRequestAsOptionsRequest() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "post.request.with.path.encoded.options/request",
        "post.request.with.path.encoded.options/response"})
    public void shouldProcessPathEncodedPostRequestAsOptionsRequest() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "post.request.with.km.parameter.trace/request",
        "post.request.with.km.parameter.trace/response"})
    public void shouldProcessPostRequestAsTraceRequest() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "post.request.with.path.encoded.trace/request",
        "post.request.with.path.encoded.trace/response"})
    public void shouldProcessPathEncodedPostRequestAsTraceRequest() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "post.request.with.km.parameter.custom/request",
        "post.request.with.km.parameter.custom/response" })
    public void shouldProcessPostRequestAsCustomRequest() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "post.request.with.path.encoded.custom/request",
        "post.request.with.path.encoded.custom/response" })
    public void shouldProcessPathEncodedPostRequestAsCustomRequest() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "client.sends.httpxe.request/request",
        "client.sends.httpxe.request/response" })
    public void shouldPassWithHttpxeReqestUsingHttpContentType() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "client.sends.httpxe.request.using.kct.parameter/request",
        "client.sends.httpxe.request.using.kct.parameter/response" })
    public void shouldPassWithHttpxeReqestUsingKctParamter() throws Exception {
        k3po.finish();
    }

}

