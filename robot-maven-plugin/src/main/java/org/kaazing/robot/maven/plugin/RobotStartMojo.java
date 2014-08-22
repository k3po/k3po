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

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.identityHashCode;
import static org.jboss.netty.logging.InternalLoggerFactory.setDefaultFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoExecutionException;
import org.kaazing.robot.driver.RobotServer;
import org.kaazing.robot.driver.RobotServerFactories;
import org.kaazing.robot.driver.RobotServerFactory;
import org.kaazing.robot.maven.plugin.logging.MavenLoggerFactory;

/**
 * Start the Robot
 *
 * @goal start
 * @phase pre-integration-test
 *
 * @requiresDependencyResolution test
 */
public class RobotStartMojo extends AbstractRobotMojo {

    /**
     * @parameter default-value="true" expression="${maven.robot.daemon}"
     */
    private boolean daemon;

    /**
     * @parameter name="connect" default-value="tcp://localhost:11642"
     */
    private URI connectURI;

    /**
     * @parameter default-value="src/test/scripts"
     */
    private File scriptDir;

    /**
     * @parameter default-value="false" expression="${maven.robot.verbose}"
     */
    private boolean verbose;

    @Override
    protected void executeImpl() throws MojoExecutionException {  

        try {
            ClassLoader scriptLoader = createScriptLoader();

            RobotServerFactory robotServerFactory = RobotServerFactories.createRobotServerFactory();
            RobotServer server = robotServerFactory.createRobotServer(connectURI, verbose, scriptLoader);

            // TODO: detect Maven version to determine logger factory
            //         3.0 -> MavenLoggerFactory
            //         3.1 -> Slf4JLoggerFactory
            // see http://brettporter.wordpress.com/2010/10/05/creating-a-custom-build-extension-for-maven-3-0/

            // note: using SLf4J for Robot breaks in Maven 3.0 at runtime
            // setDefaultFactory(new Slf4JLoggerFactory());

            // use Maven3 logger for Robot when started via plugin
            setDefaultFactory(new MavenLoggerFactory(getLog()));

            long checkpoint = currentTimeMillis();
            server.start();
            float duration = (currentTimeMillis() - checkpoint) / 1000.0f;
            getLog().debug(format("Robot [%08x] started in %.3fsec", identityHashCode(server), duration));

            setServer(server);

            if (!daemon) {
                server.join();
            }
        }
        catch (Exception e) {
            throw new MojoExecutionException("Robot failed to start", e);
        }
    }

    private ClassLoader createScriptLoader()
            throws DependencyResolutionRequiredException, MalformedURLException {
        List<URL> scriptPath = new LinkedList<URL>();
        if (scriptDir != null) {
            scriptPath.add(scriptDir.getAbsoluteFile().toURI().toURL());
        }
        for (Object scriptPathEntry : project.getTestClasspathElements()) {
            URI scriptPathURI = new File(scriptPathEntry.toString()).getAbsoluteFile().toURI();
            scriptPath.add(scriptPathURI.toURL());
        }
        return new URLClassLoader(scriptPath.toArray(new URL[scriptPath.size()]));
    }
}
