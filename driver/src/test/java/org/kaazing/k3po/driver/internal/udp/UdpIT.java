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
package org.kaazing.k3po.driver.internal.udp;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.kaazing.k3po.driver.internal.test.utils.K3poTestRule;
import org.kaazing.k3po.driver.internal.test.utils.TestSpecification;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.rules.RuleChain.outerRule;

public class UdpIT {

    private final K3poTestRule robot = new K3poTestRule();

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(robot).around(timeout);

    @Test
    @TestSpecification("udp.server")
    public void testUdpServer() throws Exception {

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

    @Test
    @TestSpecification("udp.client")
    public void testUdpClient() throws Exception {

        DatagramSocket udpServer = new DatagramSocket(new InetSocketAddress("localhost", 8080));
        robot.start();

        for (int i = 0; i < 6; i++) {
            byte[] buf = new byte[20];
            DatagramPacket dp = new DatagramPacket(buf, 0, buf.length);
            udpServer.receive(dp);
            udpServer.send(dp);
        }

        // So udp client connections timeout (script specifies 1000ms)
        Thread.sleep(2000);

        udpServer.close();

        robot.finish();
    }

}
