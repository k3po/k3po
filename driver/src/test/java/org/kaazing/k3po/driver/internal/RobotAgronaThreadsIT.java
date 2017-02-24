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

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.rules.RuleChain.outerRule;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.kaazing.k3po.driver.internal.test.utils.K3poTestRule;
import org.kaazing.k3po.driver.internal.test.utils.TestSpecification;

public class RobotAgronaThreadsIT {

    private final K3poTestRule robot = new K3poTestRule().setScriptRoot("org/kaazing/k3po/driver/internal/udp");
    
    private ExpectedException thrown = ExpectedException.none();

    private final TestRule timeout = new DisableOnDebug(new Timeout(10, SECONDS));
    
    {
        thrown.expect(RuntimeException.class);
    }
    
    private TestRule countingThreads = new TestRule() {
        @Override
        public Statement apply(Statement base, Description description) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    Set<Thread> threadsBefore = Thread.getAllStackTraces().keySet();
                    int threadCountBefore = threadsBefore.size();
                    base.evaluate();

                    // let the driver shutdown all threads
                    Thread.sleep(1000);
                    Set<Thread> threadsAfter = Thread.getAllStackTraces().keySet();
                    int threadCountAfter = threadsAfter.size();
                    
                    if (threadCountBefore != threadCountAfter) {
                        System.out.println("Threads before execution:");
                        for (Iterator<Thread> iterator = threadsBefore.iterator(); iterator.hasNext();) {
                            Thread type = iterator.next();
                            System.out.println(type.getName());
                        }
                        
                        System.out.println("\nThreads after execution:");
                        for (Iterator<Thread> iterator = threadsAfter.iterator(); iterator.hasNext();) {
                            Thread type = iterator.next();
                            System.out.println(type.getName());
                        }
                    }
                    
                    assertEquals("Number of threads is not equal", threadCountBefore, threadCountAfter);
                }
            };
        }
    };


    @Rule
    public final TestRule chain = outerRule(countingThreads).around(thrown).around(robot).around(timeout);

    @Test
    @TestSpecification("non-existent")
    public void testAgronaThreadsLeak() throws Exception {
        robot.finish();
    }
}
