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
 *  See <a href="https://tools.ietf.org/html/rfc7231#section-7">RFC 7230 section 7: Response Header Fields</a>.
 */
public class ResponseHeaderFieldsIT {

    private final K3poRule k3po = new K3poRule().setScriptRoot("org/kaazing/specification/http/rfc7231/response.header");

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    /**
     * See <a href="https://tools.ietf.org/html/rfc7231#section-7.4.1">RFC 7231 section 7.4.1</a>.
     */
    @Test
    @Specification({
        "allow.lists.resource.methods/request",
        "allow.lists.resource.methods/response" })
    public void allowHeaderInformsMethodsAssociatedWithResource() throws Exception {
        k3po.finish();
    }

    /**
     * See <a href="https://tools.ietf.org/html/rfc7231#section-7.4.2">RFC 7231 section 7.4.2</a>.
     */
    @Test
    @Specification({
        "server.header.lists.software.used.by.server/request",
        "server.header.lists.software.used.by.server/response" })
    public void serverHeaderContainsInformationAboutSoftwareUsedByServer() throws Exception {
        k3po.finish();
    }

}
