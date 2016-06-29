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
package org.kaazing.specification.control;

import static java.util.concurrent.TimeUnit.SECONDS;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.kaazing.k3po.driver.internal.test.utils.K3poTestRule;
import org.kaazing.k3po.driver.internal.test.utils.TestSpecification;

public class ControlIT {

    private final K3poTestRule robot = new K3poTestRule();

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = RuleChain.outerRule(robot).around(timeout);

    @Test
    @TestSpecification
    public void shouldBeEmpty() throws Exception {
        robot.finish();
    }

    @Test
    @TestSpecification({ "accept.finished.empty", "connect.finished.empty" })
    public void shouldPrepareStartThenFinishedEmpty() throws Exception {
        robot.finish();
    }

    @Test
    @TestSpecification({ "accept.finished", "connect.finished" })
    public void shouldPrepareStartThenFinished() throws Exception {
        robot.finish();
    }

    @Test
    @TestSpecification({ "accept.finished.with.diff", "connect.finished.with.diff" })
    public void shouldPrepareStartThenFinishedWithDiff() throws Exception {
        robot.finish();
    }

    @Test
    @TestSpecification({ "accept.finished.with.barriers", "connect.finished.with.barriers" })
    public void shouldPrepareStartThenFinishedWithBarriers() throws Exception {
        robot.finish();
    }

    @Test
    @TestSpecification({ "accept.finished.with.override.properties", "connect.finished.with.override.properties" })
    public void shouldOverrideProperties() throws Exception {
        robot.finish();
    }
}
