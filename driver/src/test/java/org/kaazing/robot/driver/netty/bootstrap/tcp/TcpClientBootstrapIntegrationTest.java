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

package org.kaazing.robot.driver.netty.bootstrap.tcp;
/*
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;

import java.net.URI;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.Channels;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.kaazing.robot.driver.netty.bootstrap.BootstrapFactory;
import org.kaazing.robot.driver.netty.bootstrap.ClientBootstrap;
import org.kaazing.robot.driver.netty.channel.ChannelAddress;
import org.kaazing.robot.driver.netty.channel.ChannelAddressFactory;
import org.kaazing.robot.driver.netty.test.RobotScriptChannelRecorder;
import org.kaazing.robot.junit.annotation.Robotic;
import org.kaazing.robot.junit.rules.ServerRobot;
*/

    // This is based on having a dependency on robot.junit which I don't think we should have on robot
    // - DPW
public class TcpClientBootstrapIntegrationTest {
/*
    @Rule
    public ServerRobot server = new ServerRobot();
    private ClientBootstrap client;

    @Before
    public void setupServerBootstrap() throws Exception {
        BootstrapFactory bootstrapFactory = BootstrapFactory.newBootstrapFactory();
        client = bootstrapFactory.newClientBootstrap("tcp");
    }

    @After
    public void teardownServerBootstrap() throws Exception {
        if (client != null) {
            client.releaseExternalResources();
        }
    }

    @Robotic(script = "server-accept-then-close")
    @Test(timeout = 5000)
    @Ignore("Needs updating")
    public void shouldConnectThenCloseAutomatically() throws Exception {

        RobotScriptChannelRecorder recorder = new RobotScriptChannelRecorder();
        client.setPipeline(Channels.pipeline(recorder));

        ChannelAddressFactory channelAddressFactory = ChannelAddressFactory.newChannelAddressFactory();
        ChannelAddress channelAddress = channelAddressFactory.newChannelAddress(URI.create("tcp://localhost:62020"));
        ChannelFuture connectFuture = client.connect(channelAddress);
        connectFuture.sync();

        // ensure all events delivered to pipeline before comparing behaviors
        client.releaseExternalResources();

        assertEquals(
                asList("open", "connect tcp://127.0.0.1:62020", "bound", "connected", "disconnected", "unbound",
                        "closed"), recorder.getScript());
    }
*/
}
