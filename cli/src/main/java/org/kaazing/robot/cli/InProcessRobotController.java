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

package org.kaazing.robot.cli;

import org.kaazing.robot.driver.RobotServer;
import org.kaazing.robot.driver.RobotServerFactory;
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
