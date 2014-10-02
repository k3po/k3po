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
import org.junit.rules.ExpectedException;

public class TcpControlledRobotServerIT {

    private RobotServer robot;
    private Socket control;
    private Socket client;
    private ServerSocket server;
    private Socket accepted;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setupRobot() throws Exception {
        robot = new TcpControlledRobotServer(URI.create("tcp://localhost:61234"), false, new URLClassLoader(new URL[] { new File("src/test/scripts").toURI().toURL() }));
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

        String path = "org/kaazing/robot/driver/emptyScript";
        // @formatter:off
        String strPrepared = "PREPARED\n" +
                             "name:" + path + "\n" + 
                             "\n"; 
        String strExpected = "STARTED\n" +
                             "name:" + path + "\n" +
                             "\n" +
                             "FINISHED\n" +
                             "name:" + path + "\n" +
                             "content-length:0\n" +
                             "\n" +
                             "content-length:0\n" +
                             "\n";
        // @formatter:on
        CharBuffer expectedPrepared = CharBuffer.wrap(strPrepared);
        CharBuffer expectedStartedAndFinished = CharBuffer.wrap(strExpected);      
        
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(control.getOutputStream()));
        out.append("PREPARE\n");
        out.append("name:" + path + "\n" + "\n");
        out.flush();
        
        BufferedReader in = new BufferedReader(new InputStreamReader(control.getInputStream()));
        
        CharBuffer prepared = CharBuffer.allocate(strPrepared.length());
        while (prepared.hasRemaining()) {
            in.read(prepared);
        }
        prepared.flip();
        
        assertEquals(expectedPrepared, prepared);
        
        out.append("START\n");
        out.append("name:" + path + "\n" + "\n");
        out.flush();

        CharBuffer startedAndFinished = CharBuffer.allocate(strExpected.length());
        while (startedAndFinished.hasRemaining()) {
            in.read(startedAndFinished);
        }
        startedAndFinished.flip();

        assertEquals(expectedStartedAndFinished, startedAndFinished);
        assertFalse(in.ready());
    }

    @Test(timeout = 2000)
    public void shouldPrepareThenStartOK() throws Exception {

        String path = "org/kaazing/robot/driver/accept-then-close";
        // @formatter:off
        String strExpectedPrepared = "PREPARED\n" +
                                    "name:" + path + "\n" +
                                    "\n";
        String strExpectedStarted = "STARTED\n" +
                                   "name:" + path + "\n" +
                                   "\n";    
        // @formatter:on
        CharBuffer expectedPrepared = CharBuffer.wrap(strExpectedPrepared);
        CharBuffer expectedStarted = CharBuffer.wrap(strExpectedStarted);
        
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(control.getOutputStream()));
        out.append("PREPARE\n");
        out.append("name:" + path + "\n" + "\n");
        out.flush();

        BufferedReader in = new BufferedReader(new InputStreamReader(control.getInputStream()));

        CharBuffer prepared = CharBuffer.allocate(strExpectedPrepared.length());
        while (prepared.hasRemaining()) {
            in.read(prepared);
        }
        prepared.flip();

        out.append("START\n");
        out.append("name:" + path + "\n");
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

    @Test(timeout = 2000)
    public void shouldAcceptThenCloseWithPrepareOK() throws Exception {

        String path = "org/kaazing/robot/driver/accept-then-close";
        // @formatter:off
        String strExpectedPrepared = "PREPARED\n" +
                                     "name:" + path + "\n" +
                                     "\n";
        String strExpectedStarted = "STARTED\n" +
                                    "name:" + path + "\n" +
                                    "\n";
        String strExpectedFinished = "FINISHED\n" +
                                     "name:" + path + "\n" +
                                     "content-length:51\n" +
                                     "\n" +
                                     "accept tcp://localhost:62345\n" +
                                     "accepted\n" +
                                     "close\n" +
                                     "closed\n" + 
                                     "content-length:51\n" +
                                     "\n" +
                                     "accept tcp://localhost:62345\n" +
                                     "accepted\n" +
                                     "close\n" +
                                     "closed\n";
        // @formatter:on
        CharBuffer expectedPrepared = CharBuffer.wrap(strExpectedPrepared);
        CharBuffer expectedStarted = CharBuffer.wrap(strExpectedStarted);
        CharBuffer expectedFinished = CharBuffer.wrap(strExpectedFinished);
        
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(control.getOutputStream()));
        out.append("PREPARE\n");
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
        client.connect(new InetSocketAddress("localhost", 62345));

        out.append("START\n");
        out.append("name:" + path + "\n");
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

    @Test(timeout = 2000)
    public void shouldAcceptThenCloseOK() throws Exception {

        String path = "org/kaazing/robot/driver/accept-then-close";
        // @formatter:off
        String strExpectedPrepared = "PREPARED\n" +
                                     "name:" + path + "\n" + 
                                     "\n";
        String strExpectedStarted = "STARTED\n" +
                                    "name:" + path + "\n" +
                                    "\n";
        String strExpectedFinished = "FINISHED\n" +
                                     "name:" + path + "\n" +
                                     "content-length:51\n" +
                                     "\n" +
                                     "accept tcp://localhost:62345\n" +
                                     "accepted\n" +
                                     "close\n" +
                                     "closed\n" + 
                                     "content-length:51\n" +
                                     "\n" +
                                     "accept tcp://localhost:62345\n" +
                                     "accepted\n" +
                                     "close\n" +
                                     "closed\n";
        // @formatter:on
        CharBuffer expectedPrepared = CharBuffer.wrap(strExpectedPrepared);
        CharBuffer expectedStarted = CharBuffer.wrap(strExpectedStarted);
        CharBuffer expectedFinished = CharBuffer.wrap(strExpectedFinished);
        
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(control.getOutputStream()));
        out.append("PREPARE\n");
        out.append("name:" + path + "\n");
        out.append("\n");
        out.flush();

        BufferedReader in = new BufferedReader(new InputStreamReader(control.getInputStream()));
        
        CharBuffer prepared = CharBuffer.allocate(strExpectedPrepared.length());
        while(prepared.hasRemaining()) {
            in.read(prepared);
        }
        prepared.flip();
        
        assertEquals(expectedPrepared, prepared);
        
        client.connect(new InetSocketAddress("localhost", 62345));
        
        out.append("START\n");
        out.append("name:" + path + "\n");
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

    @Test(timeout = 2000)
    public void shouldConnectThenCloseOK() throws Exception {

        server.bind(new InetSocketAddress("localhost", 62345));

        String path = "org/kaazing/robot/driver/connect-then-close";
        // @formatter:off
        String strExpectedPrepared = "PREPARED\n" +
                                     "name:" + path + "\n" +
                                     "\n";
        String strExpectedStarted = "STARTED\n" +
                                    "name:" + path + "\n" +
                                    "\n";
        String strExpectedFinished = "FINISHED\n" +
                                     "name:" + path + "\n" +
                                     "content-length:53\n" +
                                     "\n" +
                                     "connect tcp://localhost:62345\n" +
                                     "connected\n" +
                                     "close\n" +
                                     "closed\n" +
                                     "content-length:53\n" +
                                     "\n" +
                                     "connect tcp://localhost:62345\n" +
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
        out.append("name:" + path + "\n");
        out.append("\n");
        out.flush();
        
        CharBuffer prepared = CharBuffer.allocate(strExpectedPrepared.length());
        while(prepared.hasRemaining()) {
            in.read(prepared);
        }
        prepared.flip();
        
        assertEquals(expectedPrepared, prepared);
        
        
        out.append("START\n");
        out.append("name:" + path + "\n");
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

    @Test(timeout = 2000)
    public void shouldConnectThenCloseWithPrepareOK() throws Exception {

        server.bind(new InetSocketAddress("localhost", 62345));

        String path = "org/kaazing/robot/driver/connect-then-close";
        // @formatter:off
        String strExpectedPrepared = "PREPARED\n" +
                                      "name:" + path + "\n" +
                                      "\n";
        String strExpectedStarted = "STARTED\n" +
                                    "name:" + path + "\n" +
                                    "\n";
        String strExpectedFinished = "FINISHED\n" +
                                     "name:" + path + "\n" +
                                     "content-length:53\n" +
                                     "\n" +
                                     "connect tcp://localhost:62345\n" +
                                     "connected\n" +
                                     "close\n" +
                                     "closed\n" +
                                     "content-length:53\n" +
                                     "\n" +
                                     "connect tcp://localhost:62345\n" +
                                     "connected\n" +
                                     "close\n" +
                                     "closed\n";
        // @formatter:on
        CharBuffer expectedPrepared = CharBuffer.wrap(strExpectedPrepared);
        CharBuffer expectedStarted = CharBuffer.wrap(strExpectedStarted);
        CharBuffer expectedFinished = CharBuffer.wrap(strExpectedFinished);
        
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(control.getOutputStream()));
        out.append("PREPARE\n");
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
        out.append("name:" + path + "\n");
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

    @Test(timeout = 2000)
    public void shouldAbortOK() throws Exception {

        server.bind(new InetSocketAddress("localhost", 62345));

        String path = "org/kaazing/robot/driver/shouldAbortOK";
        // @formatter:off
        String strExpectedPrepared = "PREPARED\n" +
                                     "name:" + path + "\n" +
                                     "\n";
        String strExpectedStarted = "STARTED\n" +
                                    "name:" + path + "\n" +
                                    "\n";
        // @formatter:on
        CharBuffer expectedPrepared = CharBuffer.wrap(strExpectedPrepared);
        CharBuffer expectedStarted = CharBuffer.wrap(strExpectedStarted);
        
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(control.getOutputStream()));
        out.append("PREPARE\n");
        out.append("name:" + path + "\n");
        out.append("\n");
        out.flush();
        
        BufferedReader in = new BufferedReader(new InputStreamReader(control.getInputStream()));
        
        CharBuffer prepared = CharBuffer.allocate(strExpectedPrepared.length());
        while(prepared.hasRemaining()) {
            in.read(prepared);
        }
        prepared.flip();
        
        assertEquals(expectedPrepared, prepared);
        
        out = new BufferedWriter(new OutputStreamWriter(control.getOutputStream()));
        out.append("START\n");
        out.append("name:" + path + "\n");
        out.append("\n");
        out.flush();



        CharBuffer started = CharBuffer.allocate(strExpectedStarted.length());
        while (started.hasRemaining()) {
            in.read(started);
        }
        started.flip();

        assertEquals(expectedStarted, started);

        accepted = server.accept();

        out = new BufferedWriter(new OutputStreamWriter(control.getOutputStream()));
        out.append("ABORT\n");
        out.append("name:" + path + "\n");
        out.append("\n");
        out.flush();

        String input = in.readLine();
        assertEquals("FINISHED", input);

        input = in.readLine();
        assertEquals("name:" + path, input);
        
        input = in.readLine();
        assertEquals("content-length:65", input);
        
        input = in.readLine();
        assertEquals("", input);
        
        input = in.readLine();
        assertEquals("connect tcp://localhost:62345", input);
        
        input = in.readLine();
        assertEquals("connected", input);
        
        input = in.readLine();
        assertEquals("read [0..4]", input);
        
        input = in.readLine();
        assertEquals("close", input);
        
        input = in.readLine();
        assertEquals("closed", input);

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
    
    @Test(timeout = 2000)
    public void shouldAbortOK2() throws Exception {
        server.bind(new InetSocketAddress("localhost", 62345));

        String path = "org/kaazing/robot/driver/shouldAbortOK";
        // @formatter:off
        String strExpectedPrepared = "PREPARED\n" +
                                     "name:" + path + "\n" +
                                     "\n";
        
        String strExpectedFinished = "FINISHED\n" + "name:" + path + "\n" + "content-length:65\n\n" +
                                    "connect tcp://localhost:62345\n" + "connected\n" + "read [0..4]\n" + "close\n" +
                                    "closed\n" + "content-length:0\n\n";
        // @formatter:on
        CharBuffer expectedPrepared = CharBuffer.wrap(strExpectedPrepared);
        CharBuffer expectedFinished = CharBuffer.wrap(strExpectedFinished);
        
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(control.getOutputStream()));
        out.append("PREPARE\n");
        out.append("name:" + path + "\n");
        out.append("\n");
        out.flush();
        
        BufferedReader in = new BufferedReader(new InputStreamReader(control.getInputStream()));
        
        CharBuffer prepared = CharBuffer.allocate(strExpectedPrepared.length());
        while(prepared.hasRemaining()) {
            in.read(prepared);
        }
        prepared.flip();
        
        assertEquals(expectedPrepared, prepared);

        out = new BufferedWriter(new OutputStreamWriter(control.getOutputStream()));
        out.append("ABORT\n");
        out.append("name:" + path + "\n");
        out.append("\n");
        out.flush();
        
        CharBuffer finished = CharBuffer.allocate(strExpectedFinished.length());
        while(finished.hasRemaining()) {
            in.read(finished);
        }
        finished.flip();

        assertEquals(expectedFinished, finished);
        assertFalse(in.ready());
    }

    @Test(timeout = 2500)
    public void shouldDisconnectOK() throws Exception {

        String path = "org/kaazing/robot/driver/accept-then-close";
        // @formatter:off
        String strExpectedPrepared = "PREPARED\n" +
                                     "name:" + path + "\n" +
                                     "\n";
        // @formatter:on
        CharBuffer expectedPrepared = CharBuffer.wrap(strExpectedPrepared);
        
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                control.getOutputStream()));
        out.append("PREPARE\n");
        out.append("name:" + path + "\n");
        out.append("\n");
        out.flush();

        BufferedReader in = new BufferedReader(new InputStreamReader(
                control.getInputStream()));

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
        client.connect(new InetSocketAddress("localhost", 62345));
    }

    @Test(timeout = 2000)
    public void shouldParseErrorOK() throws Exception {

        String path = "org/kaazing/robot/driver/invalidScript";
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(control.getOutputStream()));
        out.append("PREPARE\n");
        out.append("name:" + path + "\n");
        out.append("\n");
        out.flush();

        BufferedReader in = new BufferedReader(new InputStreamReader(control.getInputStream()));

        String msg = in.readLine();
        assertEquals("ERROR", msg);

        msg = in.readLine();
        assertEquals("name:" + path, msg);

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
