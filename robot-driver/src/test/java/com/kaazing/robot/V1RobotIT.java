/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.regex.Pattern;

import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import com.kaazing.robot.behavior.RobotCompletionFuture;
import com.kaazing.robot.lang.parser.ScriptParseException;

public class V1RobotIT {

    private static final long TEST_TIMEOUT = 5000;
    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(V1RobotIT.class);
    private static final String FORMAT_VERSION = "text/x-robot";

    @Rule
    public TestWatcher watcher = new TestWatcher() {
        @Override
        protected void starting(Description description) {
            LOGGER.info("Starting test: " + description.getMethodName());
        }

        @Override
        protected void finished(Description description) {
            LOGGER.info("Finished test: " + description.getMethodName());
        }

        @Override
        protected void failed(Throwable e, Description description) {
            LOGGER.error("Failed test: " + description.getMethodName(), e);
        }
    };

    private Robot             robot;
    private Socket client;
    private Socket client2;
    private ServerSocket server;
    private Socket accepted;
    private Socket accepted2;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup() throws Exception {
        robot = new Robot();
        client = new Socket();
        client2 = new Socket();
        server = new ServerSocket();
    }

    @After
    public void shutdown() throws Exception {
        client.close();
        client2.close();

        if (accepted != null) {
            accepted.close();
        }

        if (accepted2 != null) {
            accepted2.close();
        }

        server.close();
    }

    @Test(timeout = TEST_TIMEOUT)
    public void parseErrorThrowsExceptionOnPrepare() throws Exception {

        String script = "foobar";

        thrown.expect(ScriptParseException.class);
        robot.prepare(script, FORMAT_VERSION);
    }

    @Test(timeout = TEST_TIMEOUT)
    public void parseErrorThrowsExceptionOnPrepareAndStart() throws Exception {

        String script = "foobar";

        thrown.expect(ScriptParseException.class);
        robot.prepareAndStart(script, FORMAT_VERSION);
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

        robot.prepareAndStart(script, FORMAT_VERSION).await();

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
            "connected\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        robot.prepareAndStart(script, FORMAT_VERSION).await();

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

        robot.prepareAndStart(script, FORMAT_VERSION).await();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        accepted = server.accept();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Test(timeout = 2000)
    public void shouldConnect2ThenCloseOK() throws Exception {
        // @formatter:off
        String script =
            "accept tcp://localhost:60002\n" +
            "accepted\n" +
            "connected\n" +
            "close\n" +
            "closed\n" +

            "accept tcp://localhost:60003\n" +
            "accepted\n" +
            "connected\n" +
            "close\n" +
            "closed\n" +

            "connect tcp://localhost:62345\n" +
            "connected\n" +
            "close\n" +
            "closed\n" +

            "connect tcp://localhost:62345\n" +
            "connected\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot = new Robot();

        robot.prepareAndStart(script, FORMAT_VERSION).await();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        client.connect(new InetSocketAddress("localhost", 60002));
        client2.connect(new InetSocketAddress("localhost", 60003));
        accepted = server.accept();
        accepted2 = server.accept();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

        assertEquals(-1, accepted.getInputStream().read());
        assertEquals(-1, accepted2.getInputStream().read());
        assertEquals(-1, client.getInputStream().read());
        assertEquals(-1, client2.getInputStream().read());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldAbortConnectOK() throws Exception {

        // @formatter:off
        String script =
            "connect tcp://localhost:60001\n" +
            "connected\n" +
            "write \"Hello\\n\"\n" +
            "read int\n" +
            "close\n" +
            "closed\n";

        String expected = "(?s)" +
            "connect tcp://localhost:60001\n" +
            "connected\n" +
            ".*";
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 60001));

        robot.prepareAndStart(script, FORMAT_VERSION).await();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        accepted = server.accept();

        BufferedReader acceptedIn = new BufferedReader(new InputStreamReader(accepted.getInputStream()));
        String in = acceptedIn.readLine();
        assertEquals("Hello", in);

        // hack we have a race. We need to wait for the robot handlers to recognize that hello was written!
        Thread.sleep(500);

        robot.abort().await();

        String observedScript = doneFuture.getObservedScript();
        assertNotEquals(script, observedScript);

        // TODO: Make the abort happen in the same thread as the robot I/O threads. So we can deterministically get the same
        // answer
        // Sometimes ... the abort will occur before we are notified that the write completed.
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
            "connected\n" +
            "write \"Hello\\n\"\n" +
            "read int\n" +
            "close\n" +
            "closed\n";

        String expected = "(?s)" +
                "accept tcp://localhost:60002\n" +
                "accepted\n" +
                "connected\n" +
                ".*";
        // @formatter:on

        robot.prepareAndStart(script, FORMAT_VERSION).await();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        client.connect(new InetSocketAddress("localhost", 60002));

        // We added this read here to make sure the robot accept's the connection before it is shut down
        // with the abort
        BufferedReader acceptedIn = new BufferedReader(new InputStreamReader(client.getInputStream()));
        String in = acceptedIn.readLine();
        assertEquals("Hello", in);

        // hack we have a race. We need to wait for the robot handlers to recognize that hello was written!
        Thread.sleep(500);

        robot.abort().await();

        String observedScript = doneFuture.getObservedScript();
        assertNotEquals(script, observedScript);

        // TODO: Make the abort happen in the same thread as the robot I/O threads. So we can deterministically get the same
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
            "connected\n" +
            "close\n" +
            "closed\n";

        String expected =
                "accept tcp://localhost:62345\n";
        // @formatter:on

        robot.prepareAndStart(script, FORMAT_VERSION).await();

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
            "connected\n" +
            "close\n" +
            "closed\n";

        // TODO: Since the client connects we really should be able to at least see "accepted". However, the cancel
        //  beets the notification of the OPEN channel.
        String expected =
                "accept tcp://localhost:62345\n";
        // @formatter:on

        robot.prepare(script, FORMAT_VERSION).await();

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

        robot.prepareAndStart(script, FORMAT_VERSION).await();

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

        robot.prepareAndStart(script, FORMAT_VERSION).await();

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

        robot.prepareAndStart(script, FORMAT_VERSION).await();

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
            "connected\n" +
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

        robot.prepareAndStart(script, FORMAT_VERSION).await();

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
            "connected\n" +
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
                "connected\n" +
                ".+" +
                "\n" +
                "#Connect channel\n" +
                "connect tcp://localhost:62345\n" +
                "connected\n" +
                "write \"Hello\"\n"  +
                "close\n" +
                "closed\n";
        // @formatter:on

        robot.prepareAndStart(script, FORMAT_VERSION).await();

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

        robot.prepareAndStart(script, FORMAT_VERSION).await();

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

        robot.prepareAndStart(script, FORMAT_VERSION).await();

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

        robot.prepareAndStart(script, FORMAT_VERSION).await();

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

        robot.prepareAndStart(script, FORMAT_VERSION).await();

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

        robot.prepareAndStart(script, FORMAT_VERSION).await();

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

        robot.prepareAndStart(script, FORMAT_VERSION).await();

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

        robot.prepareAndStart(script, FORMAT_VERSION).await();

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

        robot.prepareAndStart(script, FORMAT_VERSION).await();

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
            "read  [(...){6}]\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script, FORMAT_VERSION).await();

        accepted = server.accept();

        byte[] b = new byte[] { 0, 1, 2, 3, 4, 5 };

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
            "read byte\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script, FORMAT_VERSION).await();

        accepted = server.accept();

        byte[] b = new byte[] { (byte) 0xff };

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
            "read byte -1\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script, FORMAT_VERSION).await();

        accepted = server.accept();

        byte[] b = new byte[] { (byte) 0xff };

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
            "read byte (:capture)\n" +
            "read ${capture}\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script, FORMAT_VERSION).await();

        accepted = server.accept();

        byte[] b = new byte[] { (byte) 0xff };

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
            "read byte (:capture)\n" +
            "read ${capture-1}\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script, FORMAT_VERSION).await();

        accepted = server.accept();

        byte[] b = new byte[] { (byte) 0xff };
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
            "read short\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script, FORMAT_VERSION).await();

        accepted = server.accept();

        byte[] b = new byte[] { 0x00, 0x01 };

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
            "read short 1\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script, FORMAT_VERSION).await();

        accepted = server.accept();

        byte[] b = new byte[] { 0x00, 0x01 };

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
            "read short (:capture)\n" +
            "read ${capture}\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script, FORMAT_VERSION).await();

        accepted = server.accept();

        byte[] b = new byte[] { 0x00, 0x01 };

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
            "read short (:capture)\n" +
            "read ${capture-1}\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script, FORMAT_VERSION).await();

        accepted = server.accept();

        byte[] b = new byte[] { 0x00, 0x01 };
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
            "read int\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script, FORMAT_VERSION).await();

        accepted = server.accept();

        byte[] b = new byte[] { 0x00, 0x00, 0x00, 0x01 };

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
            "read int 1\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script, FORMAT_VERSION).await();

        accepted = server.accept();

        byte[] b = new byte[] { 0x00, 0x00, 0x00, 0x01 };

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
            "read long\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script, FORMAT_VERSION).await();

        accepted = server.accept();

        byte[] b = new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01 };

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
            "read long 1L\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script, FORMAT_VERSION).await();

        accepted = server.accept();

        byte[] b = new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01 };

        OutputStream out = accepted.getOutputStream();
        out.write(b);
        out.flush();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

        assertEquals(-1, accepted.getInputStream().read());
    }

    @Test(timeout = TEST_TIMEOUT)
    @Ignore("DPW - Ignore until regex are fixed")
    public void shouldReadRegexGroupNoCapturesOK() throws Exception {
        // @formatter:off
        String script =
            "connect tcp://localhost:62345\n" +
            "connected\n" +
            "read  /Hello (.*)/  \"\\n\"\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script, FORMAT_VERSION).await();

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
            "read  /Hello (.*)/  (:var) \"\\n\"\n" +
            "read \"Hello \"\n" +
            "read ${var}\n" +
            "read \"\\n\"\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script, FORMAT_VERSION).await();

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
            "read  /(H\\D+)( W\\D+)/  (:var :cap) \"\\n\"\n" +
            "read ${var}\n" +
            "read ${cap}\n" +
            "read \"\\n\"\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script, FORMAT_VERSION).await();

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
    public void shouldReadRegexWithBackRefAndCapturesOK() throws Exception {
        // @formatter:off
        String script =
            "connect tcp://localhost:62345\n" +
            "connected\n" +
            "read  /(Hello (\\d\\d\\d) Bye from \\2)/ (:all:subgroup) \"\\n\"\n" +
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

        robot.prepareAndStart(script, FORMAT_VERSION).await();

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
             "read  /(\\D+\\s(\\D+))/  (:all :world) \"\\n\"\n" +
             "read ${all}\n" +
             "read \"\\n\"\n" +
             "read ${world}\n" +
             "close\n" +
             "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script, FORMAT_VERSION).await();

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
             "read  /.*Bar/ \"\\n\"\n" +
             "close\n" +
             "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script, FORMAT_VERSION).await();

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
    @Ignore("DPW - Ignore until regex are fixed")
    public void shouldReadRegexDoubleNewLineTerminatorOK() throws Exception {
        // @formatter:off
        String script =
            "connect tcp://localhost:62345\n" +
             "connected\n" +
             "read  /(?s).*Bar/ \"\\r\\n\\r\\n\"\n" +
             "close\n" +
             "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script, FORMAT_VERSION).await();

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
             "read  [(:capture){5}]\n" +
             "read ${capture}\n" +
             "close\n" +
             "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script, FORMAT_VERSION).await();

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
            "read  [(:capture){6}]\n" +
            "write ${capture}\n" +
            "close\n" +
            "closed\n";

        String expected = script;
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script, FORMAT_VERSION).await();

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
            "read  [(:numcoming){4}]\n" +
             //Now read that number of bytes.
            "read  [(...){${numcoming}}]\n" +
            "close\n" +
            "closed\n";
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script, FORMAT_VERSION).await();

        accepted = server.accept();

        OutputStream out = accepted.getOutputStream();
        byte[] numberOfBytesNext = ByteBuffer.allocate(4).putInt(10).array();
        byte[] bytes = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
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
            "read int (:numcoming)\n" +
                 //Now read that number of bytes.
            "read  [(...){${numcoming}}]\n" +
            "close\n" +
            "closed\n";
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62345));

        robot.prepareAndStart(script, FORMAT_VERSION).await();

        accepted = server.accept();

        OutputStream out = accepted.getOutputStream();
        byte[] numberOfBytesNext = ByteBuffer.allocate(4).putInt(10).array();
        byte[] bytes = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
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

        robot.prepareAndStart(script, FORMAT_VERSION).await();

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

        robot.prepareAndStart(script, FORMAT_VERSION).await();

        accepted = server.accept();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

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
            "connected\n" +
            "close\n" +
            "closed\n";
        // @formatter:on

        // Note when bind fails you get a warning to implement exceptionCaught. But this is because the pipeline isn't
        // attached yet which apparently won't happen until the bind succeeds.
        robot.prepareAndStart(script, FORMAT_VERSION).await();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        // TODO: Ideally I think we want the exception event.
        assertEquals("", doneFuture.getObservedScript());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldEchoWrong2OK() throws Exception {
        // @formatter:off
        String script =
            "accept tcp://localhost:62345\n" +
            "accepted\n" +
            "connected\n" +
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
                "connected\n" +
                ".+" +
                "\n" +
                "#Connect channel\n" +
                "connect tcp://localhost:62346\n" +
                "connected\n";
        // @formatter:on

        server.bind(new InetSocketAddress("localhost", 62346));

        robot.prepareAndStart(script, FORMAT_VERSION).await();

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

    // @Test(timeout = 2000)
    // public void notStartOnParseErrorOK() throws Exception {
    // String expectedScript = "foobar";
    // client.start("canConnectThenClose", expectedScript, 2000, TimeUnit.MILLISECONDS);
    // assertTrue("Server should not send a start message", !client.isStarted());
    // }
    //
    // @Test(timeout = 2000)
    // public void startOnAcceptConnectedOK() throws Exception {
    // String expectedScript =
    // "accept tcp://" + HOSTNAME + ":" + CLIENTPORT + "\n" + "accepted\n" + "connected\n" + "close\n" + "closed\n";
    // client.start("startOnAcceptConnectedOK", expectedScript, 2000, TimeUnit.MILLISECONDS);
    // Throwable failedCause = client.getStartFailureCause();
    // assertTrue("Server sent a started message " + failedCause == null ? "" : "Test failed due to: " + failedCause,
    // client.isStarted());
    // }
}
