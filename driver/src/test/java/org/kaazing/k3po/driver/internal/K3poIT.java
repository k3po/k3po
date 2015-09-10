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

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.kaazing.k3po.driver.internal.test.utils.K3poTestRule;
import org.kaazing.k3po.driver.internal.test.utils.TestSpecification;

public class K3poIT {
    private final K3poTestRule k3po = new K3poTestRule().setScriptRoot("org/kaazing/specification/control");

    private final TestRule timeout = new DisableOnDebug(new Timeout(3, SECONDS));

    private RobotServer robot;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setupRobot() throws Exception {
        robot =
                new RobotServer(URI.create("tcp://localhost:12345"), false, new URLClassLoader(new URL[]{new File(
                        "src/test/scripts").toURI().toURL()}));
        robot.start();
    }

    @After
    public void shutdownRobot() throws Exception {
        robot.stop();
    }

    @Rule
    public final ExpectedException expectedExceptions = ExpectedException.none();

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @TestSpecification("connect.finished.empty")
    @Test
    public void connectFinishEmpty() throws Exception {
        k3po.finish();
    }

    @TestSpecification("connect.finished")
    @Test
    public void connectFinished() throws Exception {
        k3po.finish();
    }

    @TestSpecification("connect.finished.with.barriers")
    @Test
    public void connectFinishedWithBarriers() throws Exception {
        k3po.finish();
    }

}
