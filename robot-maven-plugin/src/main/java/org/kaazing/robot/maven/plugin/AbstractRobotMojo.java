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

package org.kaazing.robot.maven.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import org.kaazing.robot.driver.RobotServer;

/**
 * Abstract base class for Robot goals
 */
public abstract class AbstractRobotMojo extends AbstractMojo {

    private static final ThreadLocal<RobotServer> ROBOT_SERVER = new ThreadLocal<RobotServer>();

    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     * @since 1.0
     */
    protected MavenProject project;

    /**
     * @parameter default-value="false" expression="${skipTests || skipITs}"
     */
    private boolean skipTests;

    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {

        if (skipTests) {
            getLog().info("Tests are skipped");
            return;
        }

        executeImpl();
    }

    protected abstract void executeImpl() throws MojoExecutionException, MojoFailureException;

    protected void setServer(RobotServer server) {
        if (server == null) {
            ROBOT_SERVER.remove();
        }
        else {
            ROBOT_SERVER.set(server);
        }
    }

    protected RobotServer getServer() {
        return ROBOT_SERVER.get();
    }

}
