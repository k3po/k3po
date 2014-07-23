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

package org.kaazing.robot.driver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.nio.CharBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.kaazing.robot.driver.RobotServer;
import org.kaazing.robot.driver.TcpControlledRobotServer;

public class RobotServerIT {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(RobotServerIT.class);

//    @Rule
    public TestWatcher watcher = new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description) {
            LOGGER.info("Failed test: " + description.getMethodName() + " Cause: " + e);
        }
    };

    private RobotServer robot;
    private Socket control;
    private Socket client;
    private ServerSocket server;
    private Socket accepted;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    // @BeforeClass
    // public static void startRobot() throws Exception {
    // robot = new RobotServer();
    // robot.setAccept(URI.create("tcp://localhost:61234"));
    // robot.start();
    // }
    //
    // @AfterClass
    // public static void stopRobot() throws Exception {
    // robot.stop();
    // }

    @Before
    public void setupRobot() throws Exception {
        robot = new TcpControlledRobotServer(URI.create("tcp://localhost:61234"), false);
        robot.start();
        control = new Socket();
        control.connect(new InetSocketAddress("localhost", 61234));

        client = new Socket();

        server = new ServerSocket();
    }

    @After
    public void shutdownRobot() throws Exception {
        control.close();

        robot.stop();

        client.close();

        if (accepted != null) {
            accepted.close();
        }

        server.close();

        Thread.sleep(1000);

    }

    @Test(timeout = 2000)
    public void shouldFinishEmptyOK() throws Exception {

        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(control.getOutputStream()));
        out.append("START\n");
        out.append("name:empty\n");
        out.append("content-length:0\n");
        out.append("\n");
        out.flush();

        BufferedReader in = new BufferedReader(new InputStreamReader(control.getInputStream()));

        CharBuffer startedAndFinished = CharBuffer.allocate(58);
        while (startedAndFinished.hasRemaining()) {
            in.read(startedAndFinished);
        }
        startedAndFinished.flip();

        // @formatter:off
        CharBuffer expectedStartedAndFinished = CharBuffer.wrap(
                "STARTED\n" +
                "name:empty\n" +
                "\n" +
                "FINISHED\n" +
                "name:empty\n" +
                "content-length:0\n" +
                "\n");
        // @formatter:on

        assertEquals(expectedStartedAndFinished, startedAndFinished);
        assertFalse(in.ready());
    }

    @Test(timeout = 2000)
    public void shouldPrepareThenStartOK() throws Exception {

        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(control.getOutputStream()));
        out.append("PREPARE\n");
        out.append("name:connect-then-close\n");
        out.append("content-length:61\n");
        out.append("\n");
        out.append("accept tcp://localhost:62345\n");
        out.append("accepted\n");
        out.append("connected\n");
        out.append("close\n");
        out.append("closed\n");
        out.flush();

        BufferedReader in = new BufferedReader(new InputStreamReader(control.getInputStream()));

        CharBuffer prepared = CharBuffer.allocate(34);
        while (prepared.hasRemaining()) {
            in.read(prepared);
        }
        prepared.flip();

        out.append("START\n");
        out.append("name:connect-then-close\n");
        out.append("\n");
        out.flush();

        CharBuffer started = CharBuffer.allocate(33);
        while (started.hasRemaining()) {
            in.read(started);
        }
        started.flip();

        // @formatter:off
        CharBuffer expectedPrepared = CharBuffer.wrap(
                "PREPARED\n" +
                "name:connect-then-close\n" +
                "\n");
        // @formatter:on

        // @formatter:off
        CharBuffer expectedStarted = CharBuffer.wrap(
                "STARTED\n" +
                "name:connect-then-close\n" +
                "\n");
        // @formatter:on

        assertEquals(expectedPrepared, prepared);
        assertEquals(expectedStarted, started);
    }

    @Test(timeout = 2000)
    public void shouldAcceptThenCloseWithPrepareOK() throws Exception {

        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(control.getOutputStream()));
        out.append("PREPARE\n");
        out.append("name:connect-then-close\n");
        out.append("content-length:61\n");
        out.append("\n");
        out.append("accept tcp://localhost:62345\n");
        out.append("accepted\n");
        out.append("connected\n");
        out.append("close\n");
        out.append("closed\n");
        out.flush();

        BufferedReader in = new BufferedReader(new InputStreamReader(control.getInputStream()));

        CharBuffer prepared = CharBuffer.allocate(34);
        while (prepared.hasRemaining()) {
            in.read(prepared);
        }
        prepared.flip();

        // @formatter:off
        CharBuffer expectedPrepared = CharBuffer.wrap(
                "PREPARED\n" +
                "name:connect-then-close\n" +
                "\n");
        // @formatter:on

        assertEquals(expectedPrepared, prepared);

        // Connect before we START
        client.connect(new InetSocketAddress("localhost", 62345));

        out.append("START\n");
        out.append("name:connect-then-close\n");
        out.append("\n");
        out.flush();

        CharBuffer started = CharBuffer.allocate(33);
        while (started.hasRemaining()) {
            in.read(started);
        }
        started.flip();

        // @formatter:off
        CharBuffer expectedStarted = CharBuffer.wrap(
                "STARTED\n" +
                "name:connect-then-close\n" +
                "\n");
        // @formatter:on
        assertEquals(expectedStarted, started);

        CharBuffer finished = CharBuffer.allocate(113);
        while (finished.hasRemaining()) {
            in.read(finished);
        }
        finished.flip();

        // @formatter:off
        CharBuffer expectedFinished = CharBuffer.wrap(
                "FINISHED\n" +
                "name:connect-then-close\n" +
                "content-length:61\n" +
                "\n" +
                "accept tcp://localhost:62345\n" +
                "accepted\n" +
                "connected\n" +
                "close\n" +
                "closed\n");
        // @formatter:on

        assertEquals(expectedFinished, finished);
        assertFalse(in.ready());

        assertEquals(-1, client.getInputStream().read());
    }

    @Test(timeout = 2000)
    public void shouldAcceptThenCloseOK() throws Exception {

        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(control.getOutputStream()));
        out.append("START\n");
        out.append("name:connect-then-close\n");
        out.append("content-length:61\n");
        out.append("\n");
        out.append("accept tcp://localhost:62345\n");
        out.append("accepted\n");
        out.append("connected\n");
        out.append("close\n");
        out.append("closed\n");
        out.flush();

        BufferedReader in = new BufferedReader(new InputStreamReader(control.getInputStream()));

        CharBuffer started = CharBuffer.allocate(33);
        while (started.hasRemaining()) {
            in.read(started);
        }
        started.flip();

        // @formatter:off
        CharBuffer expectedStarted = CharBuffer.wrap(
                "STARTED\n" +
                "name:connect-then-close\n" +
                "\n");
        // @formatter:on

        assertEquals(expectedStarted, started);

        client.connect(new InetSocketAddress("localhost", 62345));

        CharBuffer finished = CharBuffer.allocate(113);
        while (finished.hasRemaining()) {
            in.read(finished);
        }
        finished.flip();

        // @formatter:off
        CharBuffer expectedFinished = CharBuffer.wrap(
                "FINISHED\n" +
                "name:connect-then-close\n" +
                "content-length:61\n" +
                "\n" +
                "accept tcp://localhost:62345\n" +
                "accepted\n" +
                "connected\n" +
                "close\n" +
                "closed\n");
        // @formatter:on

        assertEquals(expectedFinished, finished);
        assertFalse(in.ready());

        assertEquals(-1, client.getInputStream().read());
    }

    @Test(timeout = 2000)
    public void shouldConnectThenCloseOK() throws Exception {

        server.bind(new InetSocketAddress("localhost", 62345));

        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(control.getOutputStream()));
        out.append("START\n");
        out.append("name:connect-then-close\n");
        out.append("content-length:53\n");
        out.append("\n");
        out.append("connect tcp://localhost:62345\n");
        out.append("connected\n");
        out.append("close\n");
        out.append("closed\n");
        out.flush();

        BufferedReader in = new BufferedReader(new InputStreamReader(control.getInputStream()));

        CharBuffer started = CharBuffer.allocate(33);
        while (started.hasRemaining()) {
            in.read(started);
        }
        started.flip();

        // @formatter:off
        CharBuffer expectedStarted = CharBuffer.wrap(
                "STARTED\n" +
                "name:connect-then-close\n" +
                "\n");
        // @formatter:on

        assertEquals(expectedStarted, started);

        accepted = server.accept();

        CharBuffer finished = CharBuffer.allocate(105);
        while (finished.hasRemaining()) {
            in.read(finished);
        }
        finished.flip();

        // @formatter:off
        CharBuffer expectedFinished = CharBuffer.wrap(
                "FINISHED\n" +
                "name:connect-then-close\n" +
                "content-length:53\n" +
                "\n" +
                "connect tcp://localhost:62345\n" +
                "connected\n" +
                "close\n" +
                "closed\n");
        // @formatter:on

        assertEquals(expectedFinished, finished);
        assertFalse(in.ready());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Test(timeout = 2000)
    public void shouldConnectThenCloseWithPrepareOK() throws Exception {

        server.bind(new InetSocketAddress("localhost", 62345));

        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(control.getOutputStream()));
        out.append("PREPARE\n");
        out.append("name:connect-then-close\n");
        out.append("content-length:53\n");
        out.append("\n");
        out.append("connect tcp://localhost:62345\n");
        out.append("connected\n");
        out.append("close\n");
        out.append("closed\n");
        out.flush();

        BufferedReader in = new BufferedReader(new InputStreamReader(control.getInputStream()));

        CharBuffer prepared = CharBuffer.allocate(34);
        while (prepared.hasRemaining()) {
            in.read(prepared);
        }
        prepared.flip();

        // @formatter:off
        CharBuffer expectedPrepared = CharBuffer.wrap(
                "PREPARED\n" +
                "name:connect-then-close\n" +
                "\n");
        // @formatter:on

        assertEquals(expectedPrepared, prepared);

        out.append("START\n");
        out.append("name:connect-then-close\n");
        out.append("\n");
        out.flush();

        CharBuffer started = CharBuffer.allocate(33);
        while (started.hasRemaining()) {
            in.read(started);
        }
        started.flip();

        // @formatter:off
        CharBuffer expectedStarted = CharBuffer.wrap(
                "STARTED\n" +
                "name:connect-then-close\n" +
                "\n");
        // @formatter:on

        assertEquals(expectedStarted, started);

        accepted = server.accept();

        CharBuffer finished = CharBuffer.allocate(105);
        while (finished.hasRemaining()) {
            in.read(finished);
        }
        finished.flip();

        // @formatter:off
        CharBuffer expectedFinished = CharBuffer.wrap(
                "FINISHED\n" +
                "name:connect-then-close\n" +
                "content-length:53\n" +
                "\n" +
                "connect tcp://localhost:62345\n" +
                "connected\n" +
                "close\n" +
                "closed\n");
        // @formatter:on

        assertEquals(expectedFinished, finished);
        assertFalse(in.ready());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Test(timeout = 2000)
    public void shouldAbortOK() throws Exception {

        server.bind(new InetSocketAddress("localhost", 62345));

        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(control.getOutputStream()));
        out.append("START\n");
        out.append("name:shouldAbortOK\n");
        out.append("content-length:65\n");
        out.append("\n");
        out.append("connect tcp://localhost:62345\n");
        out.append("connected\n");
        out.append("read [0..4]\n");
        out.append("close\n");
        out.append("closed\n");
        out.flush();

        BufferedReader in = new BufferedReader(new InputStreamReader(control.getInputStream()));

        CharBuffer started = CharBuffer.allocate(28);
        while (started.hasRemaining()) {
            in.read(started);
        }
        started.flip();

        // @formatter:off
        CharBuffer expectedStarted = CharBuffer.wrap(
                "STARTED\n" +
                "name:shouldAbortOK\n" +
                "\n");
        // @formatter:on

        assertEquals(expectedStarted, started);

        accepted = server.accept();

        out = new BufferedWriter(new OutputStreamWriter(control.getOutputStream()));
        out.append("ABORT\n");
        out.append("name:shopuldAbortOK\n");
        out.append("\n");
        out.flush();

        String input = in.readLine();
        assertEquals("FINISHED", input);

        input = in.readLine();
        assertEquals("name:shouldAbortOK", input);

        input = in.readLine();
        Pattern p = Pattern.compile("content-length:(\\d+)");
        Matcher m = p.matcher(input);
        assertTrue(m.matches());

        // The observed script is not deterministic. Timing dependent
        int contentLength = Integer.parseInt(m.group(1));

        // End of header
        input = in.readLine();
        assertEquals("", input);

        CharBuffer finished = CharBuffer.allocate(contentLength);
        while (finished.hasRemaining()) {
            in.read(finished);
        }

        assertFalse(in.ready());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Test(timeout = 2500)
    public void shouldDisconnectOK() throws Exception {

        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                control.getOutputStream()));
        out.append("PREPARE\n");
        out.append("name:connect-then-close\n");
        out.append("content-length:61\n");
        out.append("\n");
        out.append("accept tcp://localhost:62345\n");
        out.append("accepted\n");
        out.append("connected\n");
        out.append("close\n");
        out.append("closed\n");
        out.flush();

        BufferedReader in = new BufferedReader(new InputStreamReader(
                control.getInputStream()));

        CharBuffer prepared = CharBuffer.allocate(34);
        while (prepared.hasRemaining()) {
            in.read(prepared);
        }
        prepared.flip();

        // @formatter:off
        CharBuffer expectedPrepared = CharBuffer.wrap(
                "PREPARED\n" +
                "name:connect-then-close\n" +
                "\n"
        );
        // @formatter:on

        assertEquals(expectedPrepared, prepared);

        // Close the control channel
        control.close();

        // TODO: How can I test this correctly? It takes some amount of time
        // before the server channel is closed.
        Thread.sleep(1000);

        thrown.expect(ConnectException.class);
        thrown.expectMessage("Connection refused");

        // Now this should fail ...
        client.connect(new InetSocketAddress("localhost", 62345));
    }

    @Test(timeout = 2000)
    public void shouldParseErrorOK() throws Exception {

        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(control.getOutputStream()));
        out.append("PREPARE\n");
        out.append("name:connect-then-close\n");
        out.append("content-length:7\n");
        out.append("\n");
        out.append("foobar\n");
        out.flush();

        BufferedReader in = new BufferedReader(new InputStreamReader(control.getInputStream()));

        String msg = in.readLine();
        assertEquals("ERROR", msg);

        msg = in.readLine();
        assertEquals("name:connect-then-close", msg);

        msg = in.readLine();
        Pattern p = Pattern.compile("summary:.+");
        Matcher m = p.matcher(msg);
        assertTrue(m.matches());

        msg = in.readLine();
        p = Pattern.compile("content-length:(\\d+)");
        m = p.matcher(msg);
        assertTrue(m.matches());

        final int contentLength = Integer.parseInt(m.group(1));

        // End of header
        in.readLine();

        CharBuffer error = CharBuffer.allocate(contentLength);
        while (error.hasRemaining()) {
            in.read(error);
        }
        error.flip();
    }

}
