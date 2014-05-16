/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.cli;

import org.kaazing.robot.control.RobotControl;

import java.io.File;
import java.net.URI;

public interface RobotController {
    void startRobotServer() throws Exception;

    void stopRobotServer() throws Exception;

    void test(File scriptFile, Integer timeout) throws Exception;

    void setURI(URI uri);
}
