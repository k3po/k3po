/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.cli;

import org.kaazing.robot.control.RobotControl;

import java.io.File;
import java.net.URI;

public interface RobotController {
    void test(File scriptFile, Integer timeout) throws Exception;

    void start() throws Exception;

    void start(URI uri) throws Exception;

    void stop() throws Exception;

    RobotControl getRobotClient() throws Exception;
}
