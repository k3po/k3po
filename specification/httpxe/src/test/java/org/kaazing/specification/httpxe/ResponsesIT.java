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
 * 
 */
public class ResponsesIT {

    private final K3poRule k3po = new K3poRule().setScriptRoot("org/kaazing/specification/httpxe/responses");

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @Test
    @Specification({
        "wrapped.101.response.in.200/request",
        "wrapped.101.response.in.200/response"})
    public void shouldPassWithWrapped101ResponseIn200() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "wrapped.201.response.in.200/request",
        "wrapped.201.response.in.200/response"})
    public void shouldPassWithWrapped201ResponseIn200() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "wrapped.302.response.in.200/request",
        "wrapped.302.response.in.200/response"})
    public void shouldPassWithWrapped302ResponseIn200() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "wrapped.400.response.in.200/request",
        "wrapped.400.response.in.200/response"})
    public void shouldPassWithWrapped400ResponseIn200() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "wrapped.501.response.in.200/request",
        "wrapped.501.response.in.200/response"})
    public void shouldPassWithWrapped501ResponseIn200() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "connection.header.not.enveloped.in.response.body/request",
        "connection.header.not.enveloped.in.response.body/response"})
    public void shouldPassWhenConnectionHeaderInHeaderNotBody() throws Exception {
        k3po.finish();
    }

}

