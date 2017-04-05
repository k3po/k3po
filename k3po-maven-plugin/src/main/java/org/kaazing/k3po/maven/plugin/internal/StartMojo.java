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

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.currentThread;
import static org.apache.maven.plugins.annotations.LifecyclePhase.PRE_INTEGRATION_TEST;
import static org.apache.maven.plugins.annotations.ResolutionScope.TEST;
import static org.jboss.netty.logging.InternalLoggerFactory.setDefaultFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.kaazing.k3po.driver.internal.RobotServer;
import org.kaazing.k3po.maven.plugin.internal.logging.MavenLoggerFactory;

/**
 * Start K3PO
 */
@Mojo(name = "start", defaultPhase = PRE_INTEGRATION_TEST, requiresDependencyResolution = TEST)
public class StartMojo extends AbstractMojo {

    @Parameter(defaultValue = "true", property = "maven.k3po.daemon")
    private boolean daemon;

    @Parameter(name = "control", defaultValue = "tcp://localhost:11642")
    private URI controlURI;

    @Parameter(defaultValue = "src/test/scripts")
    private File scriptDir;

    @Parameter(defaultValue = "false", property = "maven.k3po.verbose")
    private boolean verbose;

    @Parameter(property = "basedir")
    private File workingDirectory;

    public URI getControl() {
        return controlURI;
    }

    public void setControl(URI controlURI) {
        this.controlURI = controlURI;
    }

    @Override
    protected void executeImpl() throws MojoExecutionException {

        final ClassLoader contextClassLoader = currentThread().getContextClassLoader();

        try {
            Log log = getLog();
            if (log.isDebugEnabled()) {
                log.debug(format("Setting System property \"user.dir\" to [%s]", workingDirectory.getAbsolutePath()));
            }
            System.setProperty("user.dir", workingDirectory.getAbsolutePath());

            ClassLoader testClassLoader = createTestClassLoader();

            RobotServer server = new RobotServer(getControl(), verbose, testClassLoader);

            Map<?, ?> pluginsAsMap = project.getBuild().getPluginsAsMap();
            Plugin plugin = (Plugin) pluginsAsMap.get("org.kaazing:k3po-maven-plugin");
            if (plugin != null)
            {
                for (Dependency dependency : plugin.getDependencies())
                {
                    if (project.getGroupId().equals(dependency.getGroupId()) &&
                            project.getArtifactId().equals(dependency.getArtifactId()) &&
                            project.getVersion().equals(dependency.getVersion()))
                    {
                        // load extensions from project
                        currentThread().setContextClassLoader(testClassLoader);
                    }
                }
            }

            // TODO: detect Maven version to determine logger factory
            //         3.0 -> MavenLoggerFactory
            //         3.1 -> Slf4JLoggerFactory
            // see http://brettporter.wordpress.com/2010/10/05/creating-a-custom-build-extension-for-maven-3-0/

            // note: using SLf4J for Robot breaks in Maven 3.0 at runtime
            // setDefaultFactory(new Slf4JLoggerFactory());

            // use Maven3 logger for Robot when started via plugin
            setDefaultFactory(new MavenLoggerFactory(log));

            long checkpoint = currentTimeMillis();
            server.start();
            float duration = (currentTimeMillis() - checkpoint) / 1000.0f;
            if (log.isDebugEnabled()) {
                String version = (plugin != null) ? plugin.getVersion() : "unknown";
                if (!daemon) {
                    log.debug(format("K3PO [%s] started in %.3fsec (CTRL+C to stop)", version, duration));
                }
                else {
                    log.debug(format("K3PO [%s] started in %.3fsec", version, duration));
                }
            } else {
                if (!daemon) {
                    log.info("K3PO started (CTRL+C to stop)");
                }
                else {
                    log.info("K3PO started");
                }
            }

            setServer(server);

            if (!daemon) {
                server.join();
            }
        }
        catch (Exception e) {
            throw new MojoExecutionException("K3PO failed to start", e);
        }
        finally
        {
            currentThread().setContextClassLoader(contextClassLoader);
        }
    }

    private ClassLoader createTestClassLoader()
            throws DependencyResolutionRequiredException, MalformedURLException {
        List<URL> scriptPath = new LinkedList<>();
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
