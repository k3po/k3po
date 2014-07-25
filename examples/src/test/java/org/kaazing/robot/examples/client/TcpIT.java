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

package org.kaazing.robot.examples.client;

import org.junit.Rule;
import org.junit.Test;
import org.kaazing.robot.examples.ListedEventClient;
import org.kaazing.robot.examples.ListedEventClientBuilder;
import org.kaazing.robot.junit.annotation.Robotic;
import org.kaazing.robot.junit.rules.RobotRule;

public class TcpIT {

    @Rule
    public RobotRule robot = new RobotRule();

    private ListedEventClient helloWorldClient = new ListedEventClientBuilder()
                .connect("localhost", 8001)
                .write("hello world")
                .read("hello client")
                .close()
            .done();

    @Test
    @Robotic(script="helloWorld")
    public void testHelloWorld() throws Exception {
        helloWorldClient.run();
        robot.join();
    }
}
