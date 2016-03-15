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
package org.kaazing.specification.http.rfc7231;

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
 * Test to validate behavior as specified in <a href="https://tools.ietf.org/html/rfc7231#section-4">RFC 7231 section 4:
 * Request Methods</a>.
 */
public class RequestMethodsIT {
    private final K3poRule k3po = new K3poRule().setScriptRoot("org/kaazing/specification/http/rfc7231/method.definitions");
    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @Test
    @Specification({
        "501/request",
        "501/response" })
    public void serverMustRespondWith501ToNotImplementedMethods() throws Exception {
        // When a request method is received
        // that is unrecognized or not implemented by an origin server, the
        // origin server SHOULD respond with the 501 (Not Implemented) status
        // code.
        k3po.finish();
    }

    @Test
    @Specification({
        "get/request",
        "get/response" })
    public void serverShouldImplementGet() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "head/request",
        "head/response" })
    public void serverShouldImplementHead() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "post/request",
        "post/response" })
    public void serverShouldImplementPost() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "put/request",
        "put/response" })
    public void serverShouldImplementPut() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "405/request",
        "405/response" })
    public void serverMustRespondWith405ToNotAllowedMethods() throws Exception {
        k3po.finish();
    }
}
