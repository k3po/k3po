/*
 * Copyright 2014, Kaazing Corporation. All rights reserved.
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
import org.kaazing.k3po.driver.internal.RobotServer;
import org.kaazing.k3po.maven.plugin.internal.logging.MavenLoggerFactory;

/**
 * Start K3PO
 *
 * @goal start
 * @phase pre-integration-test
 *
 * @requiresDependencyResolution test
 */
public class StartMojo extends AbstractMojo {

    /**
     * @parameter default-value="true" expression="${maven.k3po.daemon}"
     */
    private boolean daemon;

    /**
     * @parameter name="control" default-value="tcp://localhost:11642"
     */
    private URI controlURI;

    /**
     * @parameter default-value="src/test/scripts"
     */
    private File scriptDir;

    /**
     * @parameter default-value="false" expression="${maven.k3po.verbose}"
     */
    private boolean verbose;

    @Override
    protected void executeImpl() throws MojoExecutionException {

        try {
            ClassLoader scriptLoader = createScriptLoader();

            RobotServer server = new RobotServer(controlURI, verbose, scriptLoader);

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
            if (getLog().isDebugEnabled()) {
                getLog().debug(format("K3PO [%08x] started in %.3fsec", identityHashCode(server), duration));
            } else {
                getLog().info("K3PO started");
            }

            setServer(server);

            if (!daemon) {
                server.join();
            } else {
                getLog().info(format("K3PO will terminate when Maven process terminates"));
            }
        }
        catch (Exception e) {
            throw new MojoExecutionException("K3PO failed to start", e);
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

        ClassLoader parent = getClass().getClassLoader();
        return new URLClassLoader(scriptPath.toArray(new URL[scriptPath.size()]), parent);
    }
}
