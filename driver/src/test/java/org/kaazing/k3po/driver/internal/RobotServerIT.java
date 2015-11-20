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

package org.kaazing.k3po.driver.internal;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.CharBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;

public class RobotServerIT {

    private RobotServer robot;
    private Socket control;
    private Socket client;
    private ServerSocket server;
    private Socket accepted;

    @Rule
    public TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setupRobot() throws Exception {
        robot =
                new RobotServer(URI.create("tcp://localhost:9080"), false, new URLClassLoader(new URL[]{new File(
                        "src/test/scripts").toURI().toURL()}));
        robot.start();
        control = new Socket();
        control.connect(new InetSocketAddress("localhost", 9080));

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
    }

    @Test
    public void shouldFinishEmptyOK() throws Exception {

        String path = "org/kaazing/robot/driver/emptyScript";
        // @formatter:off
        String strPrepared = "PREPARED\n" +
                             "content-length:0\n" +
                             "\n";
        String strExpected = "STARTED\n" +
                             "\n" +
                             "FINISHED\n" +
                             "content-length:0\n" +
                             "\n";
        // @formatter:on
        CharBuffer expectedPrepared = CharBuffer.wrap(strPrepared);
        CharBuffer expectedStartedAndFinished = CharBuffer.wrap(strExpected);

        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(control.getOutputStream()));
        out.append("PREPARE\n");
        out.append("version:2.0\n");
        out.append("name:" + path + "\n");
        out.append("\n");
        out.flush();

        BufferedReader in = new BufferedReader(new InputStreamReader(control.getInputStream()));

        CharBuffer prepared = CharBuffer.allocate(strPrepared.length());
        while (prepared.hasRemaining()) {
            in.read(prepared);
        }
        prepared.flip();

        assertEquals(expectedPrepared, prepared);

        out.append("START\n");
        out.append("\n");
        out.flush();

        CharBuffer startedAndFinished = CharBuffer.allocate(strExpected.length());
        while (startedAndFinished.hasRemaining()) {
            in.read(startedAndFinished);
        }
        startedAndFinished.flip();

        assertEquals(expectedStartedAndFinished, startedAndFinished);
        assertFalse(in.ready());
    }

    @Test
    public void shouldPrepareThenStartOK() throws Exception {

        String path = "org/kaazing/robot/driver/accept-then-close";
        // @formatter:off
        String strExpectedPrepared = "PREPARED\n" +
                                     "content-length:60\n" +
                                     "\n" +
                                     "accept tcp://localhost:8080\n" +
                                     "accepted\n" +
                                     "connected\n" +
                                     "close\n" +
                                     "closed\n";
        String strExpectedStarted = "STARTED\n" +
                                    "\n";
        // @formatter:on
        CharBuffer expectedPrepared = CharBuffer.wrap(strExpectedPrepared);
        CharBuffer expectedStarted = CharBuffer.wrap(strExpectedStarted);

        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(control.getOutputStream()));
        out.append("PREPARE\n");
        out.append("version:2.0\n");
        out.append("name:" + path + "\n");
        out.append("\n");
        out.flush();

        BufferedReader in = new BufferedReader(new InputStreamReader(control.getInputStream()));

        CharBuffer prepared = CharBuffer.allocate(strExpectedPrepared.length());
        while (prepared.hasRemaining()) {
            in.read(prepared);
        }
        prepared.flip();

        out.append("START\n");
        out.append("\n");
        out.flush();

        CharBuffer started = CharBuffer.allocate(strExpectedStarted.length());
        while (started.hasRemaining()) {
            in.read(started);
        }
        started.flip();

        assertEquals(expectedPrepared, prepared);
        assertEquals(expectedStarted, started);
    }

    @Test
    public void shouldAcceptThenCloseWithPrepareOK() throws Exception {

        String path = "org/kaazing/robot/driver/accept-then-close";
        // @formatter:off
        String strExpectedPrepared = "PREPARED\n" +
                                     "content-length:60\n" +
                                     "\n" +
                                     "accept tcp://localhost:8080\n" +
                                     "accepted\n" +
                                     "connected\n" +
                                     "close\n" +
                                     "closed\n";
        String strExpectedStarted = "STARTED\n" +
                                    "\n";
        String strExpectedFinished = "FINISHED\n" +
                                     "content-length:60\n" +
                                     "\n" +
                                     "accept tcp://localhost:8080\n" +
                                     "accepted\n" +
                                     "connected\n" +
                                     "close\n" +
                                     "closed\n";
        // @formatter:on
        CharBuffer expectedPrepared = CharBuffer.wrap(strExpectedPrepared);
        CharBuffer expectedStarted = CharBuffer.wrap(strExpectedStarted);
        CharBuffer expectedFinished = CharBuffer.wrap(strExpectedFinished);

        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(control.getOutputStream()));
        out.append("PREPARE\n");
        out.append("version:2.0\n");
        out.append("name:" + path + "\n");
        out.append("\n");
        out.flush();

        BufferedReader in = new BufferedReader(new InputStreamReader(control.getInputStream()));

        CharBuffer prepared = CharBuffer.allocate(strExpectedPrepared.length());
        while (prepared.hasRemaining()) {
            in.read(prepared);
        }
        prepared.flip();

        assertEquals(expectedPrepared, prepared);

        // Connect before we START
        client.connect(new InetSocketAddress("localhost", 8080));

        out.append("START\n");
        out.append("\n");
        out.flush();

        CharBuffer started = CharBuffer.allocate(strExpectedStarted.length());
        while (started.hasRemaining()) {
            in.read(started);
        }
        started.flip();

        assertEquals(expectedStarted, started);

        CharBuffer finished = CharBuffer.allocate(strExpectedFinished.length());
        while (finished.hasRemaining()) {
            in.read(finished);
        }
        finished.flip();

        assertEquals(expectedFinished, finished);
        assertFalse(in.ready());

        assertEquals(-1, client.getInputStream().read());
    }

    @Test
    public void shouldAcceptThenCloseOK() throws Exception {

        String path = "org/kaazing/robot/driver/accept-then-close";
        // @formatter:off
        String strExpectedPrepared = "PREPARED\n" +
                                     "content-length:60\n" +
                                     "\n" +
                                     "accept tcp://localhost:8080\n" +
                                     "accepted\n" +
                                     "connected\n" +
                                     "close\n" +
                                     "closed\n";
        String strExpectedStarted = "STARTED\n" +
                                    "\n";
        String strExpectedFinished = "FINISHED\n" +
                                     "content-length:60\n" +
                                     "\n" +
                                     "accept tcp://localhost:8080\n" +
                                     "accepted\n" +
                                     "connected\n" +
                                     "close\n" +
                                     "closed\n";
        // @formatter:on
        CharBuffer expectedPrepared = CharBuffer.wrap(strExpectedPrepared);
        CharBuffer expectedStarted = CharBuffer.wrap(strExpectedStarted);
        CharBuffer expectedFinished = CharBuffer.wrap(strExpectedFinished);

        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(control.getOutputStream()));
        out.append("PREPARE\n");
        out.append("version:2.0\n");
        out.append("name:" + path + "\n");
        out.append("\n");
        out.flush();

        BufferedReader in = new BufferedReader(new InputStreamReader(control.getInputStream()));

        CharBuffer prepared = CharBuffer.allocate(strExpectedPrepared.length());
        while (prepared.hasRemaining()) {
            in.read(prepared);
        }
        prepared.flip();

        assertEquals(expectedPrepared, prepared);

        client.connect(new InetSocketAddress("localhost", 8080));

        out.append("START\n");
        out.append("\n");
        out.flush();

        CharBuffer started = CharBuffer.allocate(strExpectedStarted.length());
        while (started.hasRemaining()) {
            in.read(started);
        }
        started.flip();

        assertEquals(expectedStarted, started);

        CharBuffer finished = CharBuffer.allocate(strExpectedFinished.length());
        while (finished.hasRemaining()) {
            in.read(finished);
        }
        finished.flip();

        assertEquals(expectedFinished, finished);
        assertFalse(in.ready());

        assertEquals(-1, client.getInputStream().read());
    }

    @Test
    public void shouldConnectThenCloseOK() throws Exception {

        server.bind(new InetSocketAddress("localhost", 8080));

        String path = "org/kaazing/robot/driver/connect-then-close";
        // @formatter:off
        String strExpectedPrepared = "PREPARED\n" +
                                     "content-length:52\n" +
                                     "\n" +
                                     "connect tcp://localhost:8080\n" +
                                     "connected\n" +
                                     "close\n" +
                                     "closed\n";
        String strExpectedStarted = "STARTED\n" +
                                    "\n";
        String strExpectedFinished = "FINISHED\n" +
                                     "content-length:52\n" +
                                     "\n" +
                                     "connect tcp://localhost:8080\n" +
                                     "connected\n" +
                                     "close\n" +
                                     "closed\n";
        // @formatter:on
        CharBuffer expectedPrepared = CharBuffer.wrap(strExpectedPrepared);
        CharBuffer expectedStarted = CharBuffer.wrap(strExpectedStarted);
        CharBuffer expectedFinished = CharBuffer.wrap(strExpectedFinished);

        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(control.getOutputStream()));

        BufferedReader in = new BufferedReader(new InputStreamReader(control.getInputStream()));
        out.append("PREPARE\n");
        out.append("version:2.0\n");
        out.append("name:" + path + "\n");
        out.append("\n");
        out.flush();

        CharBuffer prepared = CharBuffer.allocate(strExpectedPrepared.length());
        while (prepared.hasRemaining()) {
            in.read(prepared);
        }
        prepared.flip();

        assertEquals(expectedPrepared, prepared);

        out.append("START\n");
        out.append("\n");
        out.flush();

        CharBuffer started = CharBuffer.allocate(strExpectedStarted.length());
        while (started.hasRemaining()) {
            in.read(started);
        }
        started.flip();

        assertEquals(expectedStarted, started);

        accepted = server.accept();

        CharBuffer finished = CharBuffer.allocate(strExpectedFinished.length());
        while (finished.hasRemaining()) {
            in.read(finished);
        }
        finished.flip();

        assertEquals(expectedFinished, finished);
        assertFalse(in.ready());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Test
    public void shouldConnectThenCloseWithPrepareOK() throws Exception {

        server.bind(new InetSocketAddress("localhost", 8080));

        String path = "org/kaazing/robot/driver/connect-then-close";
        // @formatter:off
        String strExpectedPrepared = "PREPARED\n" +
                                     "content-length:52\n" +
                                     "\n" +
                                     "connect tcp://localhost:8080\n" +
                                     "connected\n" +
                                     "close\n" +
                                     "closed\n";
        String strExpectedStarted = "STARTED\n" +
                                    "\n";
        String strExpectedFinished = "FINISHED\n" +
                                     "content-length:52\n" +
                                     "\n" +
                                     "connect tcp://localhost:8080\n" +
                                     "connected\n" +
                                     "close\n" +
                                     "closed\n";
        // @formatter:on
        CharBuffer expectedPrepared = CharBuffer.wrap(strExpectedPrepared);
        CharBuffer expectedStarted = CharBuffer.wrap(strExpectedStarted);
        CharBuffer expectedFinished = CharBuffer.wrap(strExpectedFinished);

        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(control.getOutputStream()));
        out.append("PREPARE\n");
        out.append("version:2.0\n");
        out.append("name:" + path + "\n");
        out.append("\n");
        out.flush();

        BufferedReader in = new BufferedReader(new InputStreamReader(control.getInputStream()));

        CharBuffer prepared = CharBuffer.allocate(strExpectedPrepared.length());
        while (prepared.hasRemaining()) {
            in.read(prepared);
        }
        prepared.flip();

        assertEquals(expectedPrepared, prepared);

        out.append("START\n");
        out.append("\n");
        out.flush();

        CharBuffer started = CharBuffer.allocate(strExpectedStarted.length());
        while (started.hasRemaining()) {
            in.read(started);
        }
        started.flip();

        assertEquals(expectedStarted, started);

        accepted = server.accept();

        CharBuffer finished = CharBuffer.allocate(strExpectedFinished.length());
        while (finished.hasRemaining()) {
            in.read(finished);
        }
        finished.flip();

        assertEquals(expectedFinished, finished);
        assertFalse(in.ready());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Test
    public void shouldPrepareStartThenAbortOK() throws Exception {

        server.bind(new InetSocketAddress("localhost", 8080));

        String path = "org/kaazing/robot/driver/shouldAbortOK";
        // @formatter:off
        String strExpectedPrepared = "PREPARED\n" +
                                     "content-length:64\n" +
                                     "\n" +
                                     "connect tcp://localhost:8080\n" +
                                     "connected\n" +
                                     "read [0..4]\n" +
                                     "close\n" +
                                     "closed\n";
        String strExpectedStarted = "STARTED\n" +
                                    "\n";
        String strExpectedFinished = "FINISHED\n" +
                                     "content-length:40\n" +
                                     "\n" +
                                     "connect tcp://localhost:8080\n" +
                                     "connected\n" +
                                     "\n";

        // @formatter:on
        CharBuffer expectedPrepared = CharBuffer.wrap(strExpectedPrepared);
        CharBuffer expectedStarted = CharBuffer.wrap(strExpectedStarted);
        CharBuffer expectedFinished = CharBuffer.wrap(strExpectedFinished);

        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(control.getOutputStream()));
        out.append("PREPARE\n");
        out.append("version:2.0\n");
        out.append("name:" + path + "\n");
        out.append("\n");
        out.flush();

        BufferedReader in = new BufferedReader(new InputStreamReader(control.getInputStream()));

        CharBuffer prepared = CharBuffer.allocate(strExpectedPrepared.length());
        while (prepared.hasRemaining()) {
            in.read(prepared);
        }
        prepared.flip();

        assertEquals(expectedPrepared, prepared);

        out = new BufferedWriter(new OutputStreamWriter(control.getOutputStream()));
        out.append("START\n");
        out.append("\n");
        out.flush();

        CharBuffer started = CharBuffer.allocate(strExpectedStarted.length());
        while (started.hasRemaining()) {
            in.read(started);
        }
        started.flip();

        assertEquals(expectedStarted, started);

        accepted = server.accept();

        // let the connect succeed before we abort
        // TODO: remove this sleep
        Thread.sleep(100);

        out = new BufferedWriter(new OutputStreamWriter(control.getOutputStream()));
        out.append("ABORT\n");
        out.append("\n");
        out.flush();

        CharBuffer finished = CharBuffer.allocate(strExpectedFinished.length());
        while (finished.hasRemaining()) {
            in.read(finished);
            System.out.println("Read in data, still has " + finished.length() + " to read");
        }
        finished.flip();

        assertEquals(expectedFinished, finished);
        assertFalse(in.ready());

    }

    @Test
    public void shouldPrepareThenAbortOK() throws Exception {

        String path = "org/kaazing/robot/driver/shouldAbortOK";
        // @formatter:off
        String strExpectedPrepared = "PREPARED\n" +
                                     "content-length:64\n" +
                                     "\n" +
                                     "connect tcp://localhost:8080\n" +
                                     "connected\n" +
                                     "read [0..4]\n" +
                                     "close\n" +
                                     "closed\n";
        String strExpectedFinished = "FINISHED\n" +
                                     "content-length:1\n" +
                                     "\n" +
                                     "\n";

        // @formatter:on
        CharBuffer expectedPrepared = CharBuffer.wrap(strExpectedPrepared);
        CharBuffer expectedFinished = CharBuffer.wrap(strExpectedFinished);

        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(control.getOutputStream()));
        out.append("PREPARE\n");
        out.append("version:2.0\n");
        out.append("name:" + path + "\n");
        out.append("\n");
        out.flush();

        BufferedReader in = new BufferedReader(new InputStreamReader(control.getInputStream()));

        CharBuffer prepared = CharBuffer.allocate(strExpectedPrepared.length());
        while (prepared.hasRemaining()) {
            in.read(prepared);
        }
        prepared.flip();

        assertEquals(expectedPrepared, prepared);

        out = new BufferedWriter(new OutputStreamWriter(control.getOutputStream()));
        out.append("ABORT\n");
        out.append("\n");
        out.flush();

        CharBuffer finished = CharBuffer.allocate(strExpectedFinished.length());
        while (finished.hasRemaining()) {
            in.read(finished);
        }
        finished.flip();

        assertEquals(expectedFinished, finished);
    }

    @Test
    public void shouldDisconnectOK() throws Exception {

        String path = "org/kaazing/robot/driver/accept-then-close";
        // @formatter:off
        String strExpectedPrepared = "PREPARED\n" +
                                     "content-length:60\n" +
                                     "\n" +
                                     "accept tcp://localhost:8080\n" +
                                     "accepted\n" +
                                    "connected\n" +
                                     "close\n" +
                                     "closed\n";
        // @formatter:on
        CharBuffer expectedPrepared = CharBuffer.wrap(strExpectedPrepared);

        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(control.getOutputStream()));
        out.append("PREPARE\n");
        out.append("version:2.0\n");
        out.append("name:" + path + "\n");
        out.append("\n");
        out.flush();

        BufferedReader in = new BufferedReader(new InputStreamReader(control.getInputStream()));

        CharBuffer prepared = CharBuffer.allocate(strExpectedPrepared.length());
        while (prepared.hasRemaining()) {
            in.read(prepared);
        }
        prepared.flip();

        assertEquals(expectedPrepared, prepared);

        // Close the control channel
        control.close();

        // TODO: How can I test this correctly? It takes some amount of time
        // before the server channel is closed.
        Thread.sleep(1000);

        thrown.expect(ConnectException.class);
        thrown.expectMessage("Connection refused");

        // Now this should fail ...
        client.connect(new InetSocketAddress("localhost", 8080));
    }

    @Test
    public void shouldParseErrorOK() throws Exception {

        String path = "org/kaazing/robot/driver/invalidScript";
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(control.getOutputStream()));
        out.append("PREPARE\n");
        out.append("version:2.0\n");
        out.append("name:" + path + "\n");
        out.append("\n");
        out.flush();

        BufferedReader in = new BufferedReader(new InputStreamReader(control.getInputStream()));

        String msg = in.readLine();
        assertEquals("ERROR", msg);

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
