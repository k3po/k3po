/**
 * Copyright 2007-2015, Kaazing Corporation. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaazing.k3po.maven.plugin.internal;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.kaazing.k3po.driver.internal.RobotServer;

/**
 * Abstract base class for Robot goals
 */
public abstract class AbstractMojo extends org.apache.maven.plugin.AbstractMojo {

    private static final ThreadLocal<RobotServer> ROBOT_SERVER = new ThreadLocal<>();

    @Parameter(defaultValue = "${project}", readonly = true)
    protected MavenProject project;

    @Parameter(defaultValue = "false", property = "skipTests")
    private boolean skipTests;

    @Parameter(defaultValue = "false", property = "skipITs")
    private boolean skipITs;

    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {

        if (skipTests || skipITs) {
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
