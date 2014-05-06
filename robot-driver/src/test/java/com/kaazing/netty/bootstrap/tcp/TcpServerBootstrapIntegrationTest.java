/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.netty.bootstrap.tcp;
/*
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;

import java.net.URI;

import org.jboss.netty.channel.Channels;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import com.kaazing.netty.bootstrap.BootstrapFactory;
import com.kaazing.netty.bootstrap.ServerBootstrap;
import com.kaazing.netty.channel.ChannelAddress;
import com.kaazing.netty.channel.ChannelAddressFactory;
import com.kaazing.netty.test.RobotScriptChannelRecorder;
import com.kaazing.robot.junit.annotation.Robotic;
import com.kaazing.robot.junit.rules.ClientRobot;
*/

    // This is based on having a dependency on robot.junit which I don't think we should have on robot
    // - DPW

/*
public class TcpServerBootstrapIntegrationTest {

    @Rule
    public ClientRobot client = new ClientRobot();

    private ServerBootstrap server;

    @Before
    public void setupServerBootstrap() throws Exception {
        BootstrapFactory bootstrapFactory = BootstrapFactory.newBootstrapFactory();
        server = bootstrapFactory.newServerBootstrap("tcp");
    }

    @After
    public void teardownServerBootstrap() throws Exception {
        if (server != null) {
            server.releaseExternalResources();
        }
    }

    // This is based on having a dependency on robot.junit which I don't think we should have on robot
    // - DPW
    @Robotic(script = "client-connect-then-close")
    @Test(timeout = 5000)
    @Ignore("Needs updating")
    public void shouldAcceptThenCloseAutomatically() throws Exception {

        RobotScriptChannelRecorder recorder = new RobotScriptChannelRecorder();
        server.setParentHandler(recorder);
        server.setPipeline(Channels.pipeline(recorder));

        ChannelAddressFactory channelAddressFactory = ChannelAddressFactory.newChannelAddressFactory();
        ChannelAddress channelAddress = channelAddressFactory.newChannelAddress(URI.create("tcp://127.0.0.1:62020"));
        server.bind(channelAddress);

        client.awaitFinish();

        assertEquals(
                asList("open", "bind tcp://127.0.0.1:62020", "bound", "child open", "open", "bound", "connected",
                        "disconnected", "unbound", "closed", "child closed"), recorder.getScript());
    }
}
*/
