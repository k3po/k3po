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
public class MiscIT {
    private final K3poRule k3po = new K3poRule().setScriptRoot("org/kaazing/specification/sse/misc");
    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @Test
    @Specification({
        "custom.fields/request",
        "custom.fields/response" })
    public void shouldReceiveMessageWithCustomFields() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "line.starts.with.colon/request",
        "line.starts.with.colon/response" })
    public void shouldReceiveComment() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "line.without.colon/request",
        "line.without.colon/response" })
    public void shouldReceiveFieldWithoutColon() throws Exception {
        k3po.finish();
    }
    
    @Test
    @Specification({
        "not.event.stream/request",
        "not.event.stream/response" })
    public void shouldFailConnectionWhenContentTypeIsNotEventStream() throws Exception {
        k3po.finish();
    }
}
