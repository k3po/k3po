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
package org.kaazing.k3po.driver.internal.test.utils;

import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertThat;

import java.util.Iterator;
import java.util.Set;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * A rule that will check that at the end of the execution the number of threads is identical with the initial number
 * of threads. Basically a thread leak checker. 
 */
public class ThreadCountTestRule implements TestRule {
    final long waitTimeMilis;

    /**
     * @param newWaitTimeMilis The maximum time in milliseconds to wait for the test threads to end. After this time, the test will
     *  fail if the number of threads is not as before the test execution.
     */
    public ThreadCountTestRule(long newWaitTimeMilis) {
        super();
        waitTimeMilis = newWaitTimeMilis;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                Set<Thread> threadsBefore = Thread.getAllStackTraces().keySet();
                int threadCountBefore = threadsBefore.size();
                base.evaluate();

                // let the driver shutdown all threads, retry for 1 second
                int threadCountAfter = -1;
                Set<Thread> threadsAfter = null;
                long initialTime = System.currentTimeMillis();
                do {
                    threadsAfter = Thread.getAllStackTraces().keySet();
                    threadCountAfter = threadsAfter.size();
                    Thread.sleep(10);
                } while (threadCountBefore != threadCountAfter && initialTime + waitTimeMilis > System.currentTimeMillis());

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

                assertThat(threadCountAfter, lessThanOrEqualTo(threadCountBefore));
            }
        };
    }
}