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

package org.kaazing.maven.plugins;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.identityHashCode;

import org.apache.maven.plugin.MojoExecutionException;

import org.kaazing.robot.RobotServer;

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
            getLog().debug(format("Robot [%08x] stopped in %.3fsec", identityHashCode(server), duration));

            setServer(null);
        }
        catch (Exception e) {
            throw new MojoExecutionException(format("Robot [%08x] failed to stop", identityHashCode(server)), e);
        }
    }
}
