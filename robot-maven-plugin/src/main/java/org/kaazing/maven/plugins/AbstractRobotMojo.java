/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.maven.plugins;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import org.kaazing.robot.RobotServer;

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
