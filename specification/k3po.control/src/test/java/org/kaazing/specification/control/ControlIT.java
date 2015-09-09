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

package org.kaazing.specification.control;

import static java.util.concurrent.TimeUnit.SECONDS;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.kaazing.k3po.junit.annotation.Specification;
import org.kaazing.k3po.junit.rules.K3poRule;

public class ControlIT {

    private final K3poRule robot = new K3poRule();

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = RuleChain.outerRule(robot).around(timeout);

    @Test
    @Specification
    public void shouldBeEmpty() throws Exception {
        robot.finish();
    }

    @Test
    @Specification({"accept.finished.empty", "connect.finished.empty"})
    public void shouldPrepareStartThenFinishedEmpty() throws Exception {
        robot.finish();
    }

    @Test
    @Specification({"accept.finished", "connect.finished"})
    public void shouldPrepareStartThenFinished() throws Exception {
        robot.finish();
    }

    @Test
    @Specification({"accept.finished.with.diff", "connect.finished.with.diff"})
    public void shouldPrepareStartThenFinishedWithDiff() throws Exception {
        robot.finish();
    }

    @Test
    @Specification({"accept.finished.with.barriers", "connect.finished.with.barriers"})
    public void shouldPrepareStartThenFinishedWithBarriers() throws Exception {
        robot.finish();
    }
}
