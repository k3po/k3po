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
package org.kaazing.specification.sse;

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
 * W3C Server-Sent Events specification - https://www.w3.org/TR/eventsource:
 *     Section 6 - Parsing an event stream 
 *     Section 7 - Interpreting an event stream
 */
public class RetryIT {
    private final K3poRule k3po = new K3poRule().setScriptRoot("org/kaazing/specification/sse/retry");
    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @Test
    @Specification({
        "invalid.utf8/request",
        "invalid.utf8/response" })
    public void shouldFailConnectionWhenRetryFieldContainsInvalidUTF8() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "no.value/request",
        "no.value/response" })
    public void shouldHandleEmptyRetryField() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "non.numeric/request",
        "non.numeric/response" })
    public void shouldHandleNonNumericRetryValue() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "value.starts.with.space/request",
        "value.starts.with.space/response" })
    public void shouldHandleRetryValuePrefixedWithWhitespace() throws Exception {
        k3po.finish();
    }
}
