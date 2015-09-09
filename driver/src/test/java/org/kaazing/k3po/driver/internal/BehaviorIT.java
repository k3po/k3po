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

package org.kaazing.k3po.driver.internal;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.rules.RuleChain.outerRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.kaazing.k3po.driver.internal.test.utils.K3poTestRule;
import org.kaazing.k3po.driver.internal.test.utils.TestSpecification;

public class BehaviorIT {

    private final K3poTestRule k3po = new K3poTestRule();

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final ExpectedException expectedExceptions = ExpectedException.none();

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @Test
    @TestSpecification({
        "many.el.expressions" })
    public void testManyReadsAndWriteExpressions() throws Exception {
        k3po.finish();
    }

    @Test
    @TestSpecification({
        "delayed.connect" })
    public void testDelayedClientConnect() throws Exception {
        k3po.finish();
    }

    @Test
    @TestSpecification({
        "delayed.http.connect" })
    public void testDelayedHttpClientConnect() throws Exception {
        k3po.finish();
    }

    @Test
    @TestSpecification({
        "connect.expression" })
    public void testConnectWithExpression() throws Exception {
        k3po.finish();
    }

    @Test
    @TestSpecification({
        "accept.expression" })
    public void testAcceptWithExpression() throws Exception {
        k3po.finish();
    }

    @TestSpecification("test.barrier.passing.from.test.framework")
    @Test
    public void testPassingBarriers() throws Exception {
        k3po.notifyBarrier("AWAITING_BARRIER");
        k3po.awaitBarrier("NOTIFYING_BARRIER");
        k3po.finish();
    }
}
