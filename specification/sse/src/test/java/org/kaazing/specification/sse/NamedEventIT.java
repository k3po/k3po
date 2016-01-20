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
public class NamedEventIT {
    private final K3poRule k3po = new K3poRule().setScriptRoot("org/kaazing/specification/sse/named.event");
    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @Test
    @Specification({
        "empty.string/request",
        "empty.string/response" })
    public void shouldReceiveMessageWithEmptyEventName() throws Exception {
        k3po.finish();
    }
    
    @Test
    @Specification({
        "event.follows.data/request",
        "event.follows.data/response" })
    public void shouldReceiveMessageWhenEventFieldFollowsDataField() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "invalid.utf8/request",
        "invalid.utf8/response" })
    public void shouldFailConnectionWhenEventFieldContainsInvalidUTF8() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "multi.line.data/request",
        "multi.line.data/response" })
    public void shouldReceiveMessageWithMultipleDataFields() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "multiple.events/request",
        "multiple.events/response" })
    public void shouldReceiveMessagesWithMultipleEventsInAStream() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "single.line.data/request",
        "single.line.data/response" })
    public void shouldReceiveMessageWithSingleDataField() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "single.line.data/request",
        "single.line.data/response" })
    public void shouldReceiveMessageWhenEventNameStartsWithWhitespace() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "single.line.data/request",
        "single.line.data/response" })
    public void shouldReceiveMessagesWithNamedAndUnnamedEvnts() throws Exception {
        k3po.finish();
    }
}
