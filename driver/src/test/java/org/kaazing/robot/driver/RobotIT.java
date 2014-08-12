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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.kaazing.robot.driver.behavior.RobotCompletionFuture;
import org.kaazing.robot.driver.Robot;
import org.kaazing.robot.lang.parser.ScriptParseException;

public class RobotIT {

    private static final long TEST_TIMEOUT = 2000;

    private Robot robot;
    private Socket client;
    private ServerSocket server;
    private Socket accepted;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup() throws Exception {
        robot = new Robot();
        client = new Socket();
        server = new ServerSocket();
    }

    @After
    public void shutdown() throws Exception {
        client.close();

        if (accepted != null) {
            accepted.close();
        }

        server.close();
    }

    @Test(timeout = TEST_TIMEOUT)
    public void parseErrorThrowsExceptionOnPrepare() throws Exception {

        String script = "foobar";

        thrown.expect(ScriptParseException.class);
        robot.prepare(script);
    }

    @Test(timeout = TEST_TIMEOUT)
    public void parseErrorThrowsExceptionOnPrepareAndStart() throws Exception {

        String script = "foobar";

        thrown.expect(ScriptParseException.class);
        robot.prepareAndStart(script);
    }

    @Test(timeout = TEST_TIMEOUT)
    public void canNotStartWithoutPrepare() throws Exception {
        thrown.expect(IllegalStateException.class);
        robot.start();
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldFinishEmptyOK() throws Exception {

        // Empty Script
        final String script = "";
        final String expected = script;

        robot.prepareAndStart(script).await();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldAcceptThenCloseOK() throws Exception {

        // @formatter:off
        String script =
            "accept tcp://localhost:62345\n" +
            "accepted\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        robot.prepareAndStart(script).await();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        client.connect(new InetSocketAddress("localhost", 62345));

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

        assertEquals(-1, client.getInputStream().read());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldConnectThenCloseOK() throws Exception {
        // @formatter:off
        String script =
            "connect tcp://localhost:62345\n" +
            "connected\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script).await();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        accepted = server.accept();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldAbortConnectOK() throws Exception {

        // @formatter:off
        String script =
            "connect tcp://localhost:60001\n" +
            "connected\n" +
            "write \"Hello\\n\"\n" +
            "read [0..4]\n" +
            "close\n" +
            "closed\n";

        String expected = "(?s)" +
            "connect tcp://localhost:60001\n" +
            "connected\n" +
            ".*";
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 60001));

        robot.prepareAndStart(script).await();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        accepted = server.accept();

        BufferedReader acceptedIn = new BufferedReader(new InputStreamReader(accepted.getInputStream()));
        String in = acceptedIn.readLine();
        assertEquals("Hello", in);

        robot.abort().await();

        String observedScript = doneFuture.getObservedScript();
        assertNotEquals(script, observedScript);

        // TODO: Make the abort happen in the same thread as the robot I/O threads. So we can deterministically get the
        // same
        // answer. Sometimes ... the abort will occur before we are notified that the write completed.
        Pattern p = Pattern.compile(expected);
        assertTrue(p.matcher(observedScript).matches());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldAbortAcceptedOK() throws Exception {

        // @formatter:off
        String script =
            "accept tcp://localhost:60002\n" +
            "accepted\n" +
            "write \"Hello\\n\"\n" +
            "read [0..4]\n" +
            "close\n" +
            "closed\n";

        String expected = "(?s)" +
                "accept tcp://localhost:60002\n" +
                "accepted\n" +
                ".*";
        // @formatter:on

        robot.prepareAndStart(script).await();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        client.connect(new InetSocketAddress("localhost", 60002));

        // We added this read here to make sure the robot accept's the connection before it is shut down
        // with the abort
        BufferedReader acceptedIn = new BufferedReader(new InputStreamReader(client.getInputStream()));
        String in = acceptedIn.readLine();
        assertEquals("Hello", in);

        robot.abort().await();

        String observedScript = doneFuture.getObservedScript();
        assertNotEquals(script, observedScript);

        // TODO: Make the abort happen in the same thread as the robot I/O threads. So we can deterministically get the
        // same
        // answer
        // Sometimes ... the abort will occur before we are notified that the write completed.
        Pattern p = Pattern.compile(expected);
        assertTrue(p.matcher(observedScript).matches());

        assertEquals(-1, client.getInputStream().read());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldAbortAcceptNoConnectionsOK() throws Exception {

        // @formatter:off
        String script =
            "accept tcp://localhost:62345\n" +
            "accepted\n" +
            "close\n" +
            "closed\n";

        String expected =
                "accept tcp://localhost:62345\n";
        // @formatter:on

        robot.prepareAndStart(script).await();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        robot.abort().await();

        String observedScript = doneFuture.getObservedScript();

        assertEquals(expected, observedScript);
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldAbortPreparedNotStartedOK() throws Exception {

        // @formatter:off
        String script =
            "accept tcp://localhost:62345\n" +
            "accepted\n" +
            "close\n" +
            "closed\n";

        // TODO: Since the client connects we really should be able to at least see "accepted". However, the cancel
        //  beets the notification of the OPEN channel.
        String expected =
                "accept tcp://localhost:62345\n";
        // @formatter:on

        robot.prepare(script).await();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        robot.abort().await();

        String observedScript = doneFuture.getObservedScript();

        assertEquals(expected, observedScript);
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldConnectReadThenCloseOK() throws Exception {
        // @formatter:off
        String script =
            "connect tcp://localhost:62345\n" +
            "connected\n" +
            "read \"Hello\"\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script).await();

        accepted = server.accept();

        BufferedWriter acceptedOut = new BufferedWriter(new OutputStreamWriter(accepted.getOutputStream()));

        acceptedOut.write("Hello");
        acceptedOut.flush();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldWriteMultiTextLiteralsOK() throws Exception {
        // @formatter:off
        String script =
            "connect tcp://localhost:62345\n" +
            "connected\n" +
            "write \"Hello\" \" World\\n\"\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script).await();

        accepted = server.accept();

        BufferedReader acceptedIn = new BufferedReader(new InputStreamReader(accepted.getInputStream()));
        String in = acceptedIn.readLine();

        assertEquals("Hello World", in);

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldWriteMultiByteAndTextOK() throws Exception {
        // @formatter:off
        String script =
            "connect tcp://localhost:62345\n" +
            "connected\n" +
            "write [0x01 0x02 0x03] [0x04 0x05 0x06]\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script).await();

        accepted = server.accept();

        byte[] byteArr = new byte[6];
        int totalRead = 0;
        InputStream in = accepted.getInputStream();
        while (totalRead != 6) {
            totalRead += in.read(byteArr);
        }

        assertArrayEquals(new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06}, byteArr);

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldFailReadWrongOK() throws Exception {
        // @formatter:off
        String script =
            "connect tcp://localhost:62345\n" +
            "connected\n" +
            "read \"M\"\n" +
            "close\n" +
            "closed\n";

        String expected = "(?s)" +
                "connect tcp://localhost:62345\n" +
                "connected\n" +
                ".*Hello.*";
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script).await();

        accepted = server.accept();

        BufferedWriter acceptedOut = new BufferedWriter(new OutputStreamWriter(accepted.getOutputStream()));

        acceptedOut.write("Hello");
        acceptedOut.flush();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        String observedScript = doneFuture.getObservedScript();

        assertNotEquals(script, observedScript);

        Pattern p = Pattern.compile(expected);
        assertTrue(p.matcher(observedScript).matches());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldFailConnectNoOneHome() throws Exception {
        // @formatter:off
         String script =
             "connect tcp://localhost:9000\n" +
             "connected\n" +
             "close\n" +
             "closed\n";
         // @formatter:on

        robot.prepareAndStart(script).await();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        String observedScript = doneFuture.getObservedScript();

        assertNotEquals(script, observedScript);
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldEcho() throws Exception {
        // @formatter:off
        String script =
            "accept tcp://localhost:62345\n" +
            "accepted\n" +
            "read \"Hello\"\n" +
            "closed\n" +
            "\n" +
            "#Connect channel\n" +
            "connect tcp://localhost:62345\n" +
            "connected\n" +
            "write \"Hello\"\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        robot.prepareAndStart(script).await();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldEchoWrongOK() throws Exception {
        // @formatter:off
        String script =
            "accept tcp://localhost:62345\n" +
            "accepted\n" +
            "read \"ello\"\n" +
            "closed\n" +
            "\n" +
            "#Connect channel\n" +
            "connect tcp://localhost:62345\n" +
            "connected\n" +
            "write \"Hello\"\n" +
            "close\n" +
            "closed\n";

        String expected = "(?s)" +
                "accept tcp://localhost:62345\n" +
                "accepted\n" +
                ".+" +
                "\n" +
                "#Connect channel\n" +
                "connect tcp://localhost:62345\n" +
                "connected\n" +
                "write \"Hello\"\n"  +
                "close\n" +
                "closed\n";
        // @formatter:on
        robot.prepareAndStart(script).await();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();
        doneFuture.await();
        String observed = doneFuture.getObservedScript();

        Pattern p = Pattern.compile(expected);
        assertTrue(p.matcher(observed).matches());

        // assertNotEquals("", observed);
        // assertNotEquals(script, observed);
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldReadNotifyOK() throws Exception {
        // @formatter:off
        String script =
            "connect tcp://localhost:62345\n" +
            "connected\n" +
            "read notify BARRIER\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script).await();

        accepted = server.accept();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldWriteNotifyOK() throws Exception {
        // @formatter:off
        String script =
            "connect tcp://localhost:62345\n" +
            "connected\n" +
            "write notify BARRIER\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script).await();

        accepted = server.accept();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldWriteNotifyAwaitOK() throws Exception {
        // @formatter:off
        String script =
            "connect tcp://localhost:62345\n" +
            "connected\n" +
            "write notify BARRIER\n" +
            "write await BARRIER\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script).await();

        accepted = server.accept();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldReadNotifyAwaitOK() throws Exception {
        // @formatter:off
        String script =
            "connect tcp://localhost:62345\n" +
            "connected\n" +
            "read notify BARRIER\n" +
            "read await BARRIER\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script).await();

        accepted = server.accept();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldReadNotifyWriteAwaitOK() throws Exception {
        // @formatter:off
        String script =
            "connect tcp://localhost:62345\n" +
            "connected\n" +
            "read notify BARRIER\n" +
            "write await BARRIER\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script).await();

        accepted = server.accept();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldImplicitBarrierOK() throws Exception {
        // @formatter:off
        String script =
            "connect tcp://localhost:62345\n" +
            "connected\n" +
            "read \"HELLO\"\n" +
            "write \"FOO\\n\"\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script).await();

        accepted = server.accept();

        BufferedWriter acceptedOut = new BufferedWriter(new OutputStreamWriter(accepted.getOutputStream()));

        acceptedOut.write("HELLO");
        acceptedOut.flush();

        BufferedReader acceptedIn = new BufferedReader(new InputStreamReader(accepted.getInputStream()));

        String in = acceptedIn.readLine();

        assertEquals("FOO", in);

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldReadNewLineOK() throws Exception {
        // @formatter:off
        String script =
            "connect tcp://localhost:62345\n" +
            "connected\n" +
            "read \"\\n\"\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script).await();

        accepted = server.accept();

        BufferedWriter acceptedOut = new BufferedWriter(new OutputStreamWriter(accepted.getOutputStream()));

        acceptedOut.write("\n");
        acceptedOut.flush();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldWriteNewLineOK() throws Exception {
        // @formatter:off
        String script =
            "connect tcp://localhost:62345\n" +
            "connected\n" +
            "write \"\\n\"\n" +
            "write notify BARRIER\n" +
            "read await BARRIER\n" +
            "read \"\\n\"\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script).await();

        accepted = server.accept();

        BufferedReader acceptedIn = new BufferedReader(new InputStreamReader(accepted.getInputStream()));

        String in = acceptedIn.readLine();

        assertEquals("", in);

        BufferedWriter acceptedOut = new BufferedWriter(new OutputStreamWriter(accepted.getOutputStream()));

        acceptedOut.write("\n");
        acceptedOut.flush();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldReadFixedBytesOK() throws Exception {
        // @formatter:off
        String script =
            "connect tcp://localhost:62345\n" +
            "connected\n" +
            "read  [0..6]\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script).await();

        accepted = server.accept();

        byte[] b = new byte[]{0, 1, 2, 3, 4, 5};

        OutputStream out = accepted.getOutputStream();
        out.write(b);
        out.flush();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldReadByteOK() throws Exception {
        // @formatter:off
        String script =
            "connect tcp://localhost:62345\n" +
            "connected\n" +
            "read [0..1]\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script).await();

        accepted = server.accept();

        byte[] b = new byte[]{(byte) 0xff};

        OutputStream out = accepted.getOutputStream();
        out.write(b);
        out.flush();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldReadByteLiteralOK() throws Exception {
        // @formatter:off
        String script =
            "connect tcp://localhost:62345\n" +
            "connected\n" +
            "read 0xFF\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script).await();

        accepted = server.accept();

        byte[] b = new byte[]{(byte) 0xff};

        OutputStream out = accepted.getOutputStream();
        out.write(b);
        out.flush();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldReadCaptureByteOK() throws Exception {
        // @formatter:off
        String script =
            "connect tcp://localhost:62345\n" +
            "connected\n" +
            "read ([0..1]:capture)\n" +
            "read ${capture}\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script).await();

        accepted = server.accept();

        byte[] b = new byte[]{(byte) 0xff};

        OutputStream out = accepted.getOutputStream();
        out.write(b);
        out.write(b);
        out.flush();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldReadCapturedWithExpressionByteOK() throws Exception {
        // @formatter:off
        String script =
            "connect tcp://localhost:62345\n" +
            "connected\n" +
            "read (byte:capture)\n" +
            "read ${capture-1}\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script).await();

        accepted = server.accept();

        byte[] b = new byte[]{(byte) 0xff};
        byte[] b2 = ByteBuffer.allocate(8).putLong(-2).array();

        OutputStream out = accepted.getOutputStream();
        out.write(b);
        out.write(b2);
        out.flush();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldReadShortOK() throws Exception {
        // @formatter:off
        String script =
            "connect tcp://localhost:62345\n" +
            "connected\n" +
            "read [0..2]\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script).await();

        accepted = server.accept();

        byte[] b = new byte[]{0x00, 0x01};

        OutputStream out = accepted.getOutputStream();
        out.write(b);
        out.flush();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldReadShortLiteralOK() throws Exception {
        // @formatter:off
        String script =
            "connect tcp://localhost:62345\n" +
            "connected\n" +
            "read 0x0001\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script).await();

        accepted = server.accept();

        byte[] b = new byte[]{0x00, 0x01};

        OutputStream out = accepted.getOutputStream();
        out.write(b);
        out.flush();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldReadCaptureShortOK() throws Exception {
        // @formatter:off
        String script =
            "connect tcp://localhost:62345\n" +
            "connected\n" +
            "read (short:capture)\n" +
            "read ${capture}\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script).await();

        accepted = server.accept();

        byte[] b = new byte[]{0x00, 0x01};

        OutputStream out = accepted.getOutputStream();
        out.write(b);
        out.write(b);
        out.flush();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldReadCapturedWithExpressionShortOK() throws Exception {
        // @formatter:off
        String script =
            "connect tcp://localhost:62345\n" +
            "connected\n" +
            "read (short:capture)\n" +
            "read ${capture-1}\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script).await();

        accepted = server.accept();

        byte[] b = new byte[]{0x00, 0x01};
        byte[] b2 = ByteBuffer.allocate(8).putLong(0).array();

        OutputStream out = accepted.getOutputStream();
        out.write(b);
        out.write(b2);
        out.flush();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldReadIntOK() throws Exception {
        // @formatter:off
        String script =
            "connect tcp://localhost:62345\n" +
            "connected\n" +
            "read [0..4]\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script).await();

        accepted = server.accept();

        byte[] b = new byte[]{0x00, 0x00, 0x00, 0x01};

        OutputStream out = accepted.getOutputStream();
        out.write(b);
        out.flush();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldReadIntLiteralOK() throws Exception {
        // @formatter:off
        String script =
            "connect tcp://localhost:62345\n" +
            "connected\n" +
            "read 1\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script).await();

        accepted = server.accept();

        byte[] b = new byte[]{0x00, 0x00, 0x00, 0x01};

        OutputStream out = accepted.getOutputStream();
        out.write(b);
        out.flush();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldReadLongOK() throws Exception {
        // @formatter:off
        String script =
            "connect tcp://localhost:62345\n" +
            "connected\n" +
            "read [0..8]\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script).await();

        accepted = server.accept();

        byte[] b = new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01};

        OutputStream out = accepted.getOutputStream();
        out.write(b);
        out.flush();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldReadLongLiteralOK() throws Exception {
        // @formatter:off
        String script =
            "connect tcp://localhost:62345\n" +
            "connected\n" +
            "read 1L\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script).await();

        accepted = server.accept();

        byte[] b = new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01};

        OutputStream out = accepted.getOutputStream();
        out.write(b);
        out.flush();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldReadRegexGroupNoCapturesOK() throws Exception {
        // @formatter:off
        String script =
            "connect tcp://localhost:62345\n" +
            "connected\n" +
            "read  /Hello (.*)\\n/\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script).await();

        accepted = server.accept();

        OutputStreamWriter writer = new OutputStreamWriter(accepted.getOutputStream());
        writer.write("Hello World\n");
        writer.flush();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldReadRegexGroupCapturesOK() throws Exception {
        // @formatter:off
        String script =
            "connect tcp://localhost:62345\n" +
            "connected\n" +
            "read  /Hello (?<var>.*)\\n/\n" +
            "read \"Hello \"\n" +
            "read ${var}\n" +
            "read \"\\n\"\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script).await();

        accepted = server.accept();

        OutputStreamWriter writer = new OutputStreamWriter(accepted.getOutputStream());
        writer.write("Hello World\n");
        writer.write("Hello World\n");
        writer.flush();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldReadRegexGroupTwoCapturesOK() throws Exception {
        // @formatter:off
        String script =
            "connect tcp://localhost:62345\n" +
            "connected\n" +
            "read  /(?<var>H\\w+)(?<cap> W\\w+)\\n/\n" +
            "read ${var}\n" +
            "read ${cap}\n" +
            "read \"\\n\"\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script).await();

        accepted = server.accept();

        OutputStreamWriter writer = new OutputStreamWriter(accepted.getOutputStream());
        writer.write("Hello World\n");
        writer.write("Hello World\n");
        writer.flush();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Ignore("Can't get the nested matcher this test was created with to compile in java")
    @Test(timeout = TEST_TIMEOUT)
    public void shouldReadRegexWithBackRefAndCapturesOK() throws Exception {
        Pattern pattern = Pattern.compile("/(?<all>Hello (?<subgroup>\\d+) Bye from \\g{2})\\n/");
        Matcher matcher = pattern.matcher("Hello 123 Bye from 123\n");
        assertTrue(matcher.matches());
        // @formatter:off
        String script =
            "connect tcp://localhost:62345\n" +
            "connected\n" +
                    //(:all(:subgroup))
            "read  /(?<all>Hello (?<subgroup>\\d\\d\\d) Bye from \\2)\\n/\n" +
//            "read  /(Hello (\\d\\d\\d) Bye from \\2)\\n/(:all(:subgroup))/\n" +
            "read ${all}\n" +
            "read \"\\n\"\n" +
            "read \"Hello \"\n" +
            "read ${subgroup}\n" +
            "read \" Bye from \"\n" +
            "read ${subgroup}\n" +
            "read \"\\n\"\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script).await();

        accepted = server.accept();

        OutputStreamWriter writer = new OutputStreamWriter(accepted.getOutputStream());
        writer.write("Hello 123 Bye from 123\n");
        writer.write("Hello 123 Bye from 123\n");
        writer.write("Hello 123 Bye from 123\n");
        writer.flush();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldReadRegexInnerGroupsOK() throws Exception {
        // @formatter:off
         String script =
              "connect tcp://localhost:62345\n" +
              "connected\n" +
              "read  /(?<all>\\w+\\s(?<world>\\w+))\\n/\n" +
              "read ${all}\n" +
              "read \"\\n\"\n" +
              "read ${world}\n" +
              "close\n" +
              "closed\n";
        // @formatter:on

        String expected = script;

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script).await();

        accepted = server.accept();

        OutputStreamWriter writer = new OutputStreamWriter(accepted.getOutputStream());
        writer.write("Hello World\n");
        writer.write("Hello World\n");
        writer.write("World");
        writer.flush();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldReadRegexOK() throws Exception {
        // @formatter:off
        String script =
            "connect tcp://localhost:62345\n" +
             "connected\n" +
             "read  /.*Bar\\n/\n" +
             "close\n" +
             "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script).await();

        accepted = server.accept();

        OutputStreamWriter writer = new OutputStreamWriter(accepted.getOutputStream());
        writer.write("Foo Bar\n");
        writer.flush();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldReadRegexDoubleNewLineTerminatorOK() throws Exception {
        // @formatter:off
        String script =
            "connect tcp://localhost:62345\n" +
             "connected\n" +
             "read  /(?s).*Bar\\r\\n\\r\\n/\n" +
             "close\n" +
             "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script).await();

        accepted = server.accept();

        OutputStreamWriter writer = new OutputStreamWriter(accepted.getOutputStream());
        writer.write("Hello World\r\n");
        writer.write("Foo Bar\r\n\r\n");
        writer.flush();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldCaptureByteArrayAndReadValueOK() throws Exception {
        // @formatter:off
        String script =
            "connect tcp://localhost:62345\n" +
             "connected\n" +
             "read  ([0..5]:capture)\n" +
             "read ${capture}\n" +
             "close\n" +
             "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script).await();

        accepted = server.accept();

        OutputStreamWriter writer = new OutputStreamWriter(accepted.getOutputStream());
        writer.write("HELLO");
        writer.write("HELLO");
        writer.flush();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldCaptureAndWriteValueOK() throws Exception {
        // @formatter:off
        String script =
            "connect tcp://localhost:62345\n" +
            "connected\n" +
            "read  ([0..6]:capture)\n" +
            "write ${capture}\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script).await();

        accepted = server.accept();

        OutputStreamWriter writer = new OutputStreamWriter(accepted.getOutputStream());
        writer.write("HELLO\n");
        writer.flush();

        BufferedReader acceptedIn = new BufferedReader(new InputStreamReader(accepted.getInputStream()));

        String in = acceptedIn.readLine();

        assertEquals("HELLO", in);

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldNotUseByteArrayAsIntegerOK() throws Exception {
        // @formatter:off
        String script =
            "connect tcp://localhost:62345\n" +
            "connected\n" +
             //Read in the number of bytes coming next
            "read  ([0..4]:numcoming)\n" +
             //Now read that number of bytes.
            "read  [0..${numcoming}]\n" +
            "close\n" +
            "closed\n";
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script).await();

        accepted = server.accept();

        OutputStream out = accepted.getOutputStream();
        byte[] numberOfBytesNext = ByteBuffer.allocate(4).putInt(10).array();
        byte[] bytes = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        out.write(numberOfBytesNext);
        out.write(bytes);
        out.flush();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertNotEquals(script, doneFuture.getObservedScript());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldReadVariableBytesOK() throws Exception {
        // @formatter:off
        String script =
            "connect tcp://localhost:62345\n" +
            "connected\n" +
                //Read in the number of bytes coming next
            "read (int:numcoming)\n" +
                 //Now read that number of bytes.
            "read  [0..${numcoming}]\n" +
            "close\n" +
            "closed\n";
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script).await();

        accepted = server.accept();

        OutputStream out = accepted.getOutputStream();
        byte[] numberOfBytesNext = ByteBuffer.allocate(4).putInt(10).array();
        byte[] bytes = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        out.write(numberOfBytesNext);
        out.write(bytes);
        out.flush();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(script, doneFuture.getObservedScript());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldFailBadReadOK() throws Exception {
        // @formatter:off
        String script =
            "connect tcp://localhost:62345\n" +
            "connected\n" +
            "read  \"Hello World\"\n" +
            "read  \"Bye Bye\"\n" +
            "close\n" +
            "closed\n";
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script).await();

        accepted = server.accept();

        OutputStreamWriter writer = new OutputStreamWriter(accepted.getOutputStream());
        writer.write("Hello World\n");
        writer.write("Hello World\n");
        writer.flush();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertNotEquals(script, doneFuture.getObservedScript());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldWriteNotifyReadAwaitOK() throws Exception {
        // @formatter:off
        String script =
            "connect tcp://localhost:62345\n" +
            "connected\n" +
            "write notify BARRIER\n" +
            "read await BARRIER\n" +
            "close\n" +
            "closed\n";
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script).await();

        accepted = server.accept();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(script, doneFuture.getObservedScript());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldReadEscapedQuote() throws Exception {
        // @formatter:off
        String script =
            "connect tcp://localhost:62345\n" +
            "connected\n" +
            "read \"whatever\\\"\" \n" +
            "close\n" +
            "closed\n";
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script).await();

        accepted = server.accept();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();
        OutputStream outputStream = accepted.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        System.out.println("got before");
        while (!accepted.isConnected()) {
            Thread.sleep(1);
        }
        System.out.println("got here");
        writer.write("whatever\"");
        writer.flush();

        doneFuture.await();

        assertEquals(script, doneFuture.getObservedScript());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void noBindOk() throws Exception {
        // @formatter:off
        String script =
            "accept tcp://www.google.com:8080\n" +
            "accepted\n" +
            "close\n" +
            "closed\n";
        // @formatter:on

        robot.prepareAndStart(script).await();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        // TODO: Ideally we would see the accept line and then the reason for failure.
        assertEquals("", doneFuture.getObservedScript());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldEchoWrong2OK() throws Exception {
        // @formatter:off
        String script =
            "accept tcp://localhost:62345\n" +
            "accepted\n" +
            "read \"ello\"\n" +
            "read notify BARRIER\n" +
            "close\n" +
            "closed\n" +
            "\n" +
            "#Connect channel\n" +
            "connect tcp://localhost:62346\n" +
            "connected\n" +
            "write await BARRIER\n" +
            "close\n" +
            "closed\n";

        String expected = "(?s)" +
                "accept tcp://localhost:62345\n" +
                "accepted\n" +
                ".+" +
                "\n" +
                "#Connect channel\n" +
                "connect tcp://localhost:62346\n" +
                "connected\n";
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62346));

        robot.prepareAndStart(script).await();

        // Leave hung open we want this channel to be incomplete
        // accepted = server.accept();
        accepted = server.accept();

        client.connect(new InetSocketAddress("localhost", 62345));

        // Write some data to fail the first stream
        BufferedWriter acceptedOut = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
        acceptedOut.write("Hello");
        acceptedOut.flush();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        // We need to let the robot catch. So we have a script that looks right. TODO How can we not need the sleep.
        Thread.sleep(500);
        robot.abort().await();

        // doneFuture.await();
        String observed = doneFuture.getObservedScript();

        Pattern p = Pattern.compile(expected);
        assertTrue(p.matcher(observed).matches());
    }
    
    @Test(timeout = TEST_TIMEOUT)
    public void shouldReadOptionMask() throws Exception {
        // @formatter:off
        String script =
            "connect tcp://localhost:62345\n" +
            "connected\n" +
            "read  [(:maskingKey){4}]\n" +
            "read  option mask ${maskingKey}\n" +
            "read \"HELLO\"\n" +
            "read option mask [0x00]\n" +
            "close\n" +
            "closed\n";
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script).await();

        accepted = server.accept();

        OutputStreamWriter writer = new OutputStreamWriter(accepted.getOutputStream());
        // masking key
        writer.write("3201");
        // "HELLO" masked
        writer.write("{w|}|");
        writer.flush();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(script, doneFuture.getObservedScript());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Ignore("KG-7385 needed ... but ok since clients can ABORT")
    @Test(timeout = TEST_TIMEOUT)
    public void shouldAcceptNoStreamsOk() throws Exception {
        // String script =
        // "accept tcp://" + HOSTNAME + ":" + CLIENTPORT + "\n" +
        // "accepted\n" +
        // "connected\n" +
        // "close\n" +
        // "closed\n";
        //
        // Future<String> finishedFuture = client.start(
        // "canStartConnectThenCloseOK", script, 2000, TimeUnit.MILLISECONDS );
        //
        // SocketAddress testAddr = new InetSocketAddress(HOSTNAME, NOONELISTENING);
        //
        // try {
        // Socket socket = new Socket();
        // System.out.println(String.format("attempting to connect to %s",
        // testAddr));
        // socket.connect(testAddr);
        // fail( "Server should not have connected" );
        //
        // } catch (Exception e) {
        // //Expect an exception on connect
        // }
        //
        // String expectedScript =
        // "accept tcp://" + HOSTNAME + ":" + CLIENTPORT + "\n" +
        // "\n" +
        // "\n" +
        // "\n" +
        // "\n";
        //
        // assertEquals( expectedScript, finishedFuture.get() );
    }

    //
    // @Test( timeout=2000 )
    // public void notStartOnParseErrorOK() throws Exception {
    // String expectedScript = "foobar";
    // client.start("canConnectThenClose", expectedScript, 2000,
    // TimeUnit.MILLISECONDS);
    // assertTrue( "Server should not send a start message", !client.isStarted()
    // );
    // }
    //
    // @Test( timeout=2000 )
    // public void startOnAcceptConnectedOK() throws Exception {
    // String expectedScript =
    // "accept tcp://" + HOSTNAME + ":" + CLIENTPORT + "\n" +
    // "accepted\n" +
    // "connected\n" +
    // "close\n" +
    // "closed\n";
    // client.start("startOnAcceptConnectedOK", expectedScript, 2000,
    // TimeUnit.MILLISECONDS);
    // Throwable failedCause = client.getStartFailureCause();
    // assertTrue( "Server sent a started message " +
    // failedCause == null ? "" : "Test failed due to: " + failedCause,
    // client.isStarted() );
    // }
}
