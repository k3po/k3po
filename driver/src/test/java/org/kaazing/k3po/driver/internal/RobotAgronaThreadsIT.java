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
import org.kaazing.k3po.driver.internal.test.utils.ThreadCountTestRule;

public class RobotAgronaThreadsIT {

    private final K3poTestRule robot = new K3poTestRule().setScriptRoot("org/kaazing/k3po/driver/internal/udp");
    
    private ExpectedException thrown = ExpectedException.none();

    private final TestRule timeout = new DisableOnDebug(new Timeout(10, SECONDS));
    
    {
        thrown.expect(RuntimeException.class);
    }
    
    private TestRule countingThreads = new ThreadCountTestRule(1000);


    @Rule
    public final TestRule chain = outerRule(countingThreads).around(thrown).around(robot).around(timeout);

    @Test
    @TestSpecification("non-existent")
    public void testAgronaThreadsLeak() throws Exception {
        robot.finish();
    }
}
