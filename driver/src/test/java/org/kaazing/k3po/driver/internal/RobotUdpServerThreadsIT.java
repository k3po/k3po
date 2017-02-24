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
import java.util.Set;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.kaazing.k3po.driver.internal.test.utils.K3poTestRule;
import org.kaazing.k3po.driver.internal.test.utils.TestSpecification;

public class RobotUdpServerThreadsIT {

    private final K3poTestRule robot = new K3poTestRule().setScriptRoot("org/kaazing/k3po/driver/internal/udp");
    
    private final TestRule timeout = new DisableOnDebug(new Timeout(10, SECONDS));
    
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
    public final TestRule chain = outerRule(countingThreads).around(robot).around(timeout);

    @Test
    @TestSpecification("udp.server")
    public void testUdpServerThreadsLeak() throws Exception {
        udpClient(8080, "Hello1", "Hello World1");
        udpClient(8080, "Hello2", "Hello World2");
        udpClient(8081, "Hello3", "Hello World3");

        robot.finish();
    }

    private void udpClient(int port, String read, String write) throws Exception {
        DatagramSocket udpClient = new DatagramSocket();
        udpClient.connect(new InetSocketAddress("localhost", port));
        for (int i = 1; i < 3; i++) {
            String readI = read + i;
            byte[] buf = readI.getBytes(UTF_8);
            DatagramPacket dp = new DatagramPacket(buf, buf.length);
            udpClient.send(dp);
        }

        for (int i = 1; i < 3; i++) {
            byte[] buf = new byte[20];
            DatagramPacket dp = new DatagramPacket(buf, 0, buf.length);
            udpClient.receive(dp);
            String got = new String(dp.getData(), dp.getOffset(), dp.getLength(), UTF_8);
            assertEquals(write + i, got);
        }
        udpClient.close();
    }
}
