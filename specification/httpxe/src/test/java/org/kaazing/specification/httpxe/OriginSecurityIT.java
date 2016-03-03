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

public class OriginSecurityIT {

    private final K3poRule k3po = new K3poRule().setScriptRoot("org/kaazing/specification/httpxe/origin");

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @Test
    @Specification({
        "request.with.origin.header/request",
        "request.with.origin.header/response"})
    public void shouldPassWithOriginRequestHeader() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
            "unauthorized.request.with.origin.header/request",
            "unauthorized.request.with.origin.header/response"})
    public void shouldFailWithOriginRequestHeader() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "request.with.origin.header.and.x.origin.header/request",
        "request.with.origin.header.and.x.origin.header/response"})
    public void shouldPassWithOriginAndXoriginRequests() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
            "unauthorized.request.with.origin.header.and.x.origin.header/request",
            "unauthorized.request.with.origin.header.and.x.origin.header/response"})
    public void shouldFailWithOriginAndXoriginRequests() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "origin.request.using.ko.parameter/request",
        "origin.request.using.ko.parameter/response"})
    public void shouldPassWhenUsingKoParameter() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "origin.request.using.referer/request",
        "origin.request.using.referer/response"})
    public void shouldPassWithOnlyRefererAndXoriginRequest() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
            "unauthorized.origin.request.using.referer/request",
            "unauthorized.origin.request.using.referer/response"})
    public void shouldFailWithOnlyRefererAndXoriginRequest() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "x.origin.header.not.identical.to.origin.header/request",
        "x.origin.header.not.identical.to.origin.header/response"})
    public void shouldPassWhenXoriginHeaderDiffersFromOriginHeader() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
            "unauthorized.x.origin.header.not.identical.to.origin.header/request",
            "unauthorized.x.origin.header.not.identical.to.origin.header/response"})
    public void shouldFailWhenXoriginHeaderDiffersFromOriginHeader() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "request.with.kac.parameter/request",
        "request.with.kac.parameter/response"})
    public void shouldPassWithAccessControlWithKacParameter() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "x.origin.encoded.request.header/request",
        "x.origin.encoded.request.header/response"})
    public void shouldPassWithEncodedXoriginRequest() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
            "unauthorized.x.origin.encoded.request.header/request",
            "unauthorized.x.origin.encoded.request.header/response"})
    public void shouldFailWithEncodedXoriginRequest() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
            "unauthorized.x.origin.encoded.request.header.1/request",
            "unauthorized.x.origin.encoded.request.header.1/response"})
    public void shouldFailWithEncodedXoriginRequest1() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
            "unauthorized.x.origin.encoded.request.header.2/request",
            "unauthorized.x.origin.encoded.request.header.2/response"})
    public void shouldFailWithEncodedXoriginRequest2() throws Exception {
        k3po.finish();
    }

}

