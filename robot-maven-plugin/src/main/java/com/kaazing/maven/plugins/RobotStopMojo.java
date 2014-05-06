/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.maven.plugins;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.identityHashCode;

import org.apache.maven.plugin.MojoExecutionException;

import com.kaazing.robot.RobotServer;

/**
 * Stop the Robot
 *
 * @goal stop
 * @phase post-integration-test
 *
 * @requiresDependencyResolution test
 */
public class RobotStopMojo extends AbstractRobotMojo {

    protected void executeImpl() throws MojoExecutionException {

        RobotServer server = getServer();
        if (server == null) {
            getLog().error(format("Robot not running"));
        }

        try {
            long checkpoint = currentTimeMillis();
            server.stop();
            float duration = (currentTimeMillis() - checkpoint) / 1000.0f;
            getLog().info(format("Robot [%08x] stopped in %.3fsec", identityHashCode(server), duration));

            setServer(null);
        }
        catch (Exception e) {
            throw new MojoExecutionException(format("Robot [%08x] failed to stop", identityHashCode(server)), e);
        }
    }
}
