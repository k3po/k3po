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

package org.kaazing.robot.cli;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.kaazing.net.URLFactory.createURL;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import org.kaazing.robot.driver.RobotServer;
import org.kaazing.robot.driver.RobotServerFactories;
import org.kaazing.robot.driver.RobotServerFactory;
import org.kaazing.robot.driver.RobotServerFactorySPI;
import org.kaazing.robot.cli.utils.ServiceClassLoader;
import org.kaazing.robot.control.RobotControl;
import org.kaazing.robot.control.RobotControlFactories;
import org.kaazing.robot.control.RobotControlFactory;
import org.kaazing.robot.control.RobotControlFactorySPI;

public abstract class AbstractRobotControllerTest {

    Mockery context;
    Interpreter interpreter;
    private RobotControlFactory robotControlFactory;
    RobotControl robotClient;
    private RobotServerFactory robotServerFactory;
    RobotServer robotServer;
    private RobotController robotController;

    abstract RobotController getRobotController();

    public static class TestRobotControlFactory extends RobotControlFactorySPI {
        static RobotControl robotControl;
        static URI controlURI;

        @Override
        public RobotControl newClient(URI controlURI) throws Exception {
            this.controlURI = controlURI;
            return robotControl;
        }

        @Override
        public String getSchemeName() {
            return "test";
        }

        static void setRobotControl(RobotControl robotControl) {
            TestRobotControlFactory.robotControl = robotControl;
        }

        static URI getControlURI() {
            return controlURI;
        }

    }

    public static class TestRobotServerFactory extends RobotServerFactorySPI {
        static RobotServer robotServer;
        static boolean verbose;
        static URI uri;

        @Override
        public RobotServer createRobotServer(URI uri, boolean verbose) {
            this.uri = uri;
            this.verbose = verbose;
            return robotServer;
        }

        @Override
        public String getSchemeName() {
            return "test";
        }

        static void setRobotServer(RobotServer robotServer) {
            TestRobotServerFactory.robotServer = robotServer;
        }

        static public boolean isVerbose() {
            return verbose;
        }

        static public URI getUri() {
            return uri;
        }
    }

    public RobotControlFactory getRobotControlFactory() {
        return robotControlFactory;
    }

    public RobotServerFactory getRobotServerFactory() {
        return robotServerFactory;
    }

    @Before
    public void setup() throws MalformedURLException {

        URL controlFactoryURL = createURL(null, format("data:,%s", TestRobotControlFactory.class.getName()));
        ClassLoader controlFactoryClassLoader = new ServiceClassLoader(RobotControlFactorySPI.class, controlFactoryURL);

        URL serverFactoryURL = createURL(null, format("data:,%s", TestRobotServerFactory.class.getName()));
        ClassLoader serverFactoryClassLoader = new ServiceClassLoader(RobotServerFactorySPI.class, serverFactoryURL);

        context = new Mockery();

        robotClient = context.mock(RobotControl.class);
        robotControlFactory = RobotControlFactories
                .createRobotControlFactory(controlFactoryClassLoader);
        TestRobotControlFactory.setRobotControl(robotClient);

        robotServer = context.mock(RobotServer.class);
        robotServerFactory = RobotServerFactories
                .createRobotServerFactory(serverFactoryClassLoader);
        TestRobotServerFactory.setRobotServer(robotServer);

        interpreter = context.mock(Interpreter.class);
        robotController = getRobotController();
    }

    @Test
    public void testStart() throws Exception {

        context.checking(
                new Expectations() {
                    {
                        allowing(interpreter).println(with(any(String.class)));
                    }
                });
        robotController.startRobotServer();
        context.assertIsSatisfied();
    }

    @Test
    public void testStartWithURI() throws Exception {
        final URI uri = URI.create("test:localhost:8000");

        context.checking(
                new Expectations() {
                    {
                        allowing(interpreter).println(with(any(String.class)));
                        oneOf(robotServer).start();
                    }
                });

        robotController.setURI(uri);
        robotController.startRobotServer();
        context.assertIsSatisfied();
        assertEquals(TestRobotServerFactory.getUri(), uri);
    }

    @Test
    public void testStop() throws Exception {
        final URI uri = URI.create("test:localhost:8000");

        context.checking(
                new Expectations() {
                    {
                        allowing(interpreter).println(with(any(String.class)));
                        oneOf(robotServer).start();
                        oneOf(robotServer).stop();
                    }
                });

        robotController.setURI(uri);
        robotController.startRobotServer();
        robotController.stopRobotServer();
        context.assertIsSatisfied();
    }

    @Test
    public void testExceptionWhenRobotAlreadyStarted() throws Exception {
        final URI uri = URI.create("test:localhost:8000");

        context.checking(
                new Expectations() {
                    {
                        allowing(interpreter).println(with(any(String.class)));
                        oneOf(robotServer).start();
                    }
                });
        robotController.setURI(uri);
        robotController.startRobotServer();
        boolean sawException = false;
        try {

            robotController.setURI(uri);
            robotController.startRobotServer();
        } catch (Exception e) {
            sawException = true;
        }
        assertTrue("Saw exception because robot is already running", sawException);
        context.assertIsSatisfied();
    }

    @Test
    public void testRunTestSuccess() throws Exception {
        final URI uri = URI.create("tcp://localhost:8001");
        final File outputDir = new File("target/testFiles/AbstractRobotControllerTests");
        File temp = File.createTempFile("tempScriptFile", ".rpt");

        // Delete temp file when program exits.
        temp.deleteOnExit();

        // Write to temp file
        BufferedWriter out = new BufferedWriter(new FileWriter(temp));
        out.write("accept tcp://localhost:8010\n" +
                "accepted\n" +
                "connected\n" +
                "closed\n" +
                "connect tcp://localhost:8010\n" +
                "connected\n" +
                "close\n" +
                "closed");
        out.close();
        outputDir.mkdirs();
        context.checking(
                new Expectations() {
                    {
                        allowing(interpreter).println(with(any(String.class)));
                        allowing(interpreter).getOutputDir();
                        will(returnValue(outputDir));
                    }
                });
        robotController.setURI(uri);
        robotController.startRobotServer();
        robotController.test(temp, 10);
        robotController.stopRobotServer();

        File resultFile = new File(outputDir, temp.getName().replace(".rpt", "/result.txt"));
        assertTrue("Test output dir exists", resultFile.exists());
        assertTrue("Test results show test passing", readFile(resultFile.getPath()).startsWith("passed"));
    }

    @Test
    public void testRunTestFail() throws Exception {
        final URI uri = URI.create("tcp://localhost:8002");
        final File outputDir = new File("target/testFiles/AbstractRobotControllerTests");
        File temp = File.createTempFile("tempScriptFile", ".rpt");

        // Delete temp file when program exits.
        temp.deleteOnExit();

        // Write to temp file
        BufferedWriter out = new BufferedWriter(new FileWriter(temp));
        out.write("accept tcp://localhost:8010\n" +
                "accepted\n" +
                "connected\n" +
                "read \"hello\"\n" +
                "closed\n" +
                "connect tcp://localhost:8010\n" +
                "connected\n" +
                "write \"Aloha\"\n" +
                "close\n" +
                "closed");
        out.close();
        outputDir.mkdirs();
        context.checking(
                new Expectations() {
                    {
                        allowing(interpreter).println(with(any(String.class)));
                        allowing(interpreter).getOutputDir();
                        will(returnValue(outputDir));
                    }
                });
        robotController.setURI(uri);
        robotController.startRobotServer();
        robotController.test(temp, 10);
        robotController.stopRobotServer();

        File resultFile = new File(outputDir, temp.getName().replace(".rpt", "/result.txt"));
        assertTrue("Test output dir exists", resultFile.exists());
        final String fileContents = readFile(resultFile.getPath());
        System.out.print(fileContents);
        assertTrue("Test results show test passing", fileContents.startsWith("failed"));

        File expectedScriptFile = new File(outputDir, temp.getName().replace(".rpt", "/expectedScript.rpt"));
        assertTrue("expectedScriptFile exists", expectedScriptFile.exists());
        File actualScriptFile = new File(outputDir, temp.getName().replace(".rpt", "/actualScript.rpt"));
        assertTrue("actualScriptFile exists", actualScriptFile.exists());
    }

    @Test
    public void testParseErrorException() throws Exception {
        final URI uri = URI.create("tcp://localhost:8002");
        final File outputDir = new File("target/testFiles/AbstractRobotControllerTests");
        File temp = File.createTempFile("tempScriptFile", ".rpt");

        // Delete temp file when program exits.
        temp.deleteOnExit();

        // Write to temp file
        BufferedWriter out = new BufferedWriter(new FileWriter(temp));
        out.write("accept tcp://localhost:8010\n" +
                "closed");
        out.close();
        outputDir.mkdirs();
        context.checking(
                new Expectations() {
                    {
                        allowing(interpreter).println(with(any(String.class)));
                        allowing(interpreter).getOutputDir();
                        will(returnValue(outputDir));
                    }
                });
        robotController.setURI(uri);
        robotController.startRobotServer();
        boolean sawExpectedException = false;
        try {
            robotController.test(temp, 10);
        } catch (Exception failedException) {
            sawExpectedException = true;
        }
        robotController.stopRobotServer();
        assertTrue("Saw expected exception", sawExpectedException);
    }

    static String readFile(String path)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, StandardCharsets.UTF_8);
    }
}
