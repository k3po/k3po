/*
 * Copyright (c) 2014 "Kaazing Corporation," (www.kaazing.com)
 *
 * This file is part of Robot.
 *
 * Robot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.kaazing.robot.junit.rules;

import static org.junit.rules.RuleChain.outerRule;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import org.junit.ComparisonFailure;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestRule;

import org.kaazing.robot.junit.annotation.Robotic;

public class RobotRuleIT {

    private final RobotRule robot = new RobotRule();

    // For negative testing
    private final ExpectedException thrown = ExpectedException.none().handleAssertionErrors();

    private final UnBoundRule testUnBind = UnBoundRule.none();

    @Rule
    public TestRule chain = outerRule(testUnBind).around(thrown).around(robot);

    @Robotic(script = "my-tcp-server")
    @Test(timeout = 2000)
    public void canAcceptAndBeClosedOK() throws Exception {

        final int testPort = 7788;
        SocketAddress testAddr = new InetSocketAddress("localhost", testPort);
        testUnBind.expectPortUnbound(testPort);

        try {
            Socket socket = new Socket();
            socket.connect(testAddr);
            socket.close();

        } catch (Exception e) {
            throw e;
        }

        robot.join();
    }

    @Robotic(script = "my-tcp-server-two-accepts")
    @Test(timeout = 4000)
    public void canAcceptAndBeClosedTwiceOK() throws Exception {

        final int testPort = 7788;
        SocketAddress testAddr = new InetSocketAddress("localhost", testPort);
        testUnBind.expectPortUnbound(testPort);

        try {
            Socket socket = new Socket();
            socket.connect(testAddr);
            socket.close();

            Socket socket2 = new Socket();
            socket2.connect(testAddr);
            socket2.close();

        } catch (Exception e) {
            throw e;
        }
        robot.join();
    }

    @Robotic(script = "my-tcp-server-two-accepts")
    @Test(timeout = 2000)
    public void canAcceptOneChannelTimeOutOnOther() throws Exception {

        final int testPort = 7788;
        SocketAddress testAddr = new InetSocketAddress("localhost", testPort);
        testUnBind.expectPortUnbound(testPort);

        try {
            Socket socket = new Socket();
            socket.connect(testAddr);
            socket.close();
        } catch (Exception e) {
            throw e;
        }
        // Expect a comparison failure since we only accepted once. Note that this works cause thrown is a rule that is
        // around the robot rule.
        thrown.expect(ComparisonFailure.class);
        robot.join();
    }

    @Robotic(script = "my-tcp-server")
    @Ignore("DPW - To Fix")
    @Test
    public void unBindsWhenTestTerminatesEarly() throws Exception {

        final int testPort = 7788;
        testUnBind.expectPortUnbound(testPort);

        // Robot should throw a comparisonfailure wrapped with the exception below.
        thrown.expect(ComparisonFailure.class);
        throw new Exception("Just an exception to terminate the script runner early");

        // Note if you just run off the edge with no exception and no call to join you get an assertion failure with the
        // below message. But in this case the robot does not unbind soon enough and the test will fail.
        // thrown.expectMessage(format("Did you call %s.join()?", RobotRule.class.getSimpleName()));
    }

    @Robotic(script = "fail-binding")
    @Test(timeout = 2000)
    public void noBindOk() throws Exception {
        // We really expect the empty script back.
        thrown.expect(ComparisonFailure.class);
        robot.join();
    }

}
