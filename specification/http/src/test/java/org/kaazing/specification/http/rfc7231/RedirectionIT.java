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
public class RedirectionIT {
    private final K3poRule k3po = new K3poRule().setScriptRoot("org/kaazing/specification/http/rfc7231/redirection");
    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @Test
    @Specification({
        "absolute.location/request",
        "absolute.location/response" })
    public void absoluteLocationHeader() throws Exception {
        k3po.finish();
    }

    @Test
    @Ignore("Pending https://github.com/k3po/k3po/issues/385, note scripts have it already added as comment")
    // See https://tools.ietf.org/html/rfc7231#section-7.1.2 for description of fragment change
    @Specification({
        "change.fragment/request",
        "change.fragment/response" })
    public void changeFragment() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "relative.location/request",
        "relative.location/response" })
    public void relativeLocation() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "secure.location/request",
        "secure.location/response" })
    public void secureLocationHeader() throws Exception {
        k3po.finish();
    }
}
