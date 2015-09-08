/*
 * Copyright (c) 2007-2014 Kaazing Corporation. All rights reserved.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
import org.kaazing.k3po.driver.internal.test.utils.TestAwaitBarrier;
import org.kaazing.k3po.driver.internal.test.utils.TestNotifyBarrier;
import org.kaazing.k3po.driver.internal.test.utils.TestSpecification;

public class TestTestSpecification {

    private final K3poTestRule k3po = new K3poTestRule();

    private final TestRule timeout = new DisableOnDebug(new Timeout(1, SECONDS));

    @Rule
    public final ExpectedException expectedExceptions = ExpectedException.none();

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @TestSpecification("test.barrier.passing.from.test.framework")
    @TestAwaitBarrier("NOTIFYING_BARRIER")
    // From the tests framework point of view, it notifies an await, and waits for a notify?
    @TestNotifyBarrier("AWAITING_BARRIER")
    // And vice-versa, not sure if this is clear so maybe re-do naming
    @Test
    public void testPassingBarriers() throws Exception {
        k3po.awaitBarrier("NOTIFYING_BARRIER");
        k3po.notifyBarrier("AWAITING_BARRIER");
        k3po.finish();
    }

    // TODO, find out how to catch timeout, or don't have this test, Note: I see a comparison failure and not a timeout
    // exception
    // Which is correct behavior but not sure how to have a test for this cause I don't know how to expect the exception
/*
    @TestSpecification("test.barrier.passing.from.test.framework")
    @TestAwaitBarrier("NOTIFYING_BARRIER")
    @TestNotifyBarrier("AWAITING_BARRIER")
    @Test
    public void testBarriersTimeout() throws Exception {
        k3po.awaitBarrier("NOTIFYING_BARRIER");
        k3po.finish();
    }
*/
}
