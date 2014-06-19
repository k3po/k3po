/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.cli;

import org.kaazing.robot.RobotServer;
import org.kaazing.robot.RobotServerFactory;
import org.kaazing.robot.control.RobotControl;
import org.kaazing.robot.control.RobotControlFactory;

import java.net.URI;

public class InProcessRobotController extends AbstractRobotController {

    private final RobotServerFactory robotServerFactory;
    private RobotServer server;
    private final RobotControlFactory robotControlFactory;
    private URI uri = URI.create("tcp://localhost:11642");

    public InProcessRobotController(Interpreter interpreter, RobotControlFactory robotControlFactory,
                                    RobotServerFactory robotServerFactory) {
        super(interpreter);
        this.robotControlFactory = robotControlFactory;
        this.robotServerFactory = robotServerFactory;
    }

    @Override
    public void startRobotServer() throws Exception {
        if (server == null) {
            server = robotServerFactory.createRobotServer(uri, false);
            try {
                interpreter.println("Starting robot");
                server.start();
                interpreter.println("Started robot");
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("Robot failed to start");
            }
        } else {
            throw new Exception("Robot already running");
        }
    }

    @Override
    public void stopRobotServer() throws Exception {
        if (server == null) {
            throw new Exception("Robot not running, thus can not be stopped");
        } else {
            try {
                interpreter.println("Stopping robot");
                server.stop();
                interpreter.println("Stopped robot");
                server = null;
            } catch (Exception e) {
                throw new Exception("Robot failed to stop");
            }
        }
    }

    @Override
    public void setURI(URI uri) {
        this.uri = uri;
    }

    @Override
    RobotControl getRobotClient() throws Exception {
        RobotControl client;
        try {
            client = robotControlFactory.newClient(uri);
            client.connect();
        } catch (Exception e) {
            throw new Exception("Test Error: Failed to connect to robot: " + e.getMessage());
        }
        return client;
    }

}
