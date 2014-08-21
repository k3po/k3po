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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;

import org.kaazing.robot.driver.RobotServer;
import org.kaazing.robot.driver.RobotServerFactory;
import org.kaazing.robot.control.RobotControl;
import org.kaazing.robot.control.RobotControlFactory;

/**
 *  When running the CLI in non interactive mode, it should be possible to launch the robot and leave it running,
 *  One option is to save state to disk, the other is to require two terminals.  This was the implementation that
 *  would save state to disk, it is currently not in use though it may be in the future.
 */
public class FileDrivenRobotController extends AbstractRobotController {

    private final RobotServerFactory robotServerFactory;
    private final RobotControlFactory robotControlFactory;
    private URI uri = URI.create("tcp://localhost:11642");
    private File robotInfoFile;
    private Boolean running = false;
    private File scriptDir;

    public void setScriptDir(File scriptDir) {
        this.scriptDir = scriptDir;
    }

    public FileDrivenRobotController(Interpreter interpreter, RobotControlFactory robotControlFactory,
                                     RobotServerFactory robotServerFactory) {
        super(interpreter);
        this.robotControlFactory = robotControlFactory;
        this.robotServerFactory = robotServerFactory;
    }

    @Override
    public void startRobotServer() throws Exception {
        loadRobotStatus();
        if (running) {
            throw new Exception("Robot is already running according to " + robotInfoFile + ", will not override file");
        }
        RobotServer server = robotServerFactory.createRobotServer(uri, false, scriptDir);
        try {
            interpreter.println("Starting robot");
            server.start();
            interpreter.println("Started robot");

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Robot failed to start");
        }
        running = true;
        saveRobotStatus();
    }

    @Override
    public void stopRobotServer() throws Exception {
        loadRobotStatus();

        running = false;
        saveRobotStatus();
    }

    @Override
    public void setURI(URI uri) {
        this.uri = uri;
    }

    @Override
    public RobotControl getRobotClient() throws Exception {
        RobotControl client = null;
        try {
            client = robotControlFactory.newClient(uri);
            client.connect();
        } catch (Exception e) {
            throw new Exception("Test Error: Failed to connect to robot: " + e.getMessage());
        }
        return client;
    }

    // Probably want to use some standard format at some point, maybe even serialize the class to a File
    private void updateRobotInfoFileLocation() {
        robotInfoFile = new File(interpreter.getOutputDir(), "robot-info.csv");
    }

    private void saveRobotStatus() throws IOException {
        updateRobotInfoFileLocation();
        robotInfoFile.createNewFile();
        PrintWriter writer = new PrintWriter(robotInfoFile);
        writer.println("running," + running);
        if (uri != null) {
            writer.println("uri," + uri);
        }
        writer.close();
    }

    private void loadRobotStatus() throws IOException {
        updateRobotInfoFileLocation();
        if (robotInfoFile.exists()) {
            BufferedReader br = new BufferedReader(new FileReader(robotInfoFile));
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length == 2) {
                    switch (tokens[0]) {
                        case "running":
                            running = Boolean.valueOf(tokens[1]);
                            break;
                        case "uri":
                            this.uri = URI.create(tokens[1]);
                            break;
                        case "PID":
                        break;
                        default:
                            break;
                    }
                }
            }
            br.close();
        } else {
            running = false;
            uri = null;
        }

    }
}
