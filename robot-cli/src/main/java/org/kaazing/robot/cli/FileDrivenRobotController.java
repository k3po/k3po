/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.cli;

import org.kaazing.robot.DefaultRobotServer;
import org.kaazing.robot.RobotServer;
import org.kaazing.robot.RobotServerFactory;
import org.kaazing.robot.control.RobotControl;
import org.kaazing.robot.control.RobotControlFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;

/**
 * TODO When running the CLI in non interactive mode, it should be possible to launch the robot and leave it running
 * As Of now we just startup the server before and after a test, this class/implementation needs to be completed before
 * we can do this
 */
public class FileDrivenRobotController extends AbstractRobotController {

    private static final URI DEFAULT_URI = URI.create("tcp://localhost:11642");
    private final RobotServerFactory robotServerFactory;
    private final RobotControlFactory robotControlFactory;
    private File robotInfoFile;
    private URI uri;
    private Boolean running = false;
    private String pid;

    public FileDrivenRobotController(Interpreter interpreter, RobotControlFactory robotControlFactory,
                                     RobotServerFactory robotServerFactory) {
        super(interpreter);
        this.robotControlFactory = robotControlFactory;
        this.robotServerFactory = robotServerFactory;
    }

    @Override
    public void start() throws Exception {
        loadRobotStatus();
        if (running) {
            throw new Exception("Robot is already running according to " + robotInfoFile + ", will not override file");
        }
        RobotServer server = robotServerFactory.createRobotServer();
        server.setAccept(uri);
        server.setVerbose(false);
        server.join();
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
    public void start(URI uri) throws Exception {
        loadRobotStatus();
        this.uri = uri;
        saveRobotStatus();
        start();
    }

    @Override
    public void stop() throws Exception {
        loadRobotStatus();

        running = false;
        saveRobotStatus();
    }

    @Override
    public RobotControl getRobotClient() throws Exception {
        loadRobotStatus();
        if (!running) {
            interpreter.println("Robot is not running, starting robot");
            start();
        }
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
        writer.println("uri," + uri);
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
                            this.pid = tokens[1];
                            break;
                        default:
                            break;
                    }
                }
            }
            br.close();
        } else {
            running = false;
            uri = DEFAULT_URI;
            pid = null;
        }

    }
}
