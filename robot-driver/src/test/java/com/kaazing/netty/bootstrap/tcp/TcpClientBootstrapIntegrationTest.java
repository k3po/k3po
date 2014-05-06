/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.netty.bootstrap.tcp;
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

import com.kaazing.netty.bootstrap.BootstrapFactory;
import com.kaazing.netty.bootstrap.ClientBootstrap;
import com.kaazing.netty.channel.ChannelAddress;
import com.kaazing.netty.channel.ChannelAddressFactory;
import com.kaazing.netty.test.RobotScriptChannelRecorder;
import com.kaazing.robot.junit.annotation.Robotic;
import com.kaazing.robot.junit.rules.ServerRobot;
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
