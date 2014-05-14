/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.control;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.nio.charset.Charset;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.kaazing.robot.control.command.AbortCommand;
import org.kaazing.robot.control.command.PrepareCommand;
import org.kaazing.robot.control.command.StartCommand;
import org.kaazing.robot.control.event.CommandEvent;
import org.kaazing.robot.control.event.ErrorEvent;
import org.kaazing.robot.control.event.FinishedEvent;
import org.kaazing.robot.control.event.StartedEvent;

public class RobotControlTest {

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private RobotControl control;

    @Rule
    public JUnitRuleMockery mockery = new JUnitRuleMockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    private InputStream input;

    private OutputStream output;

    @Before
    public void setupControl() throws Exception {
        input = mockery.mock(InputStream.class);
        output = mockery.mock(OutputStream.class);

        control = new DefaultRobotControl(new URL(null, "test://internal", new URLStreamHandler() {
            @Override
            protected URLConnection openConnection(URL location) throws IOException {
                return new URLConnection(location) {

                    @Override
                    public void connect() throws IOException {
                        // no-op
                    }

                    @Override
                    public InputStream getInputStream() {
                        return input;
                    }

                    @Override
                    public OutputStream getOutputStream() {
                        return output;
                    }
                };
            }
        }));

    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotWriteCommand() throws Exception {
        StartCommand start = new StartCommand();
        start.setName("my.script");
        control.writeCommand(start);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotReadEvent() throws Exception {
        control.readEvent();
    }

    @Test
    public void shouldConnect() throws Exception {
        control.connect();
    }

    @Test
    public void shouldConnectAndDisconnect() throws Exception {
        mockery.checking(new Expectations() {
            {
                oneOf(input).close();
                oneOf(output).close();
            }
        });

        control.connect();
        control.disconnect();
    }

    @Test
    public void shouldWritePrepareCommand() throws Exception {
        final byte[] expectedPrepare =
                ("PREPARE\n" +
                 "name:my.script\n" +
                 "content-length:9\n" +
                 "\n" +
                 "# comment").getBytes(UTF_8);

        mockery.checking(new Expectations() {
            {
                oneOf(output).write(with(hasInitialBytes(expectedPrepare)), with(equal(0)), with(equal(expectedPrepare.length)));
                oneOf(output).flush();
            }
        });

        PrepareCommand start = new PrepareCommand();
        start.setName("my.script");
        start.setScript("# comment");

        control.connect();
        control.writeCommand(start);

    }

    @Test
    public void shouldWriteStartCommand() throws Exception {
        final byte[] expectedStart =
                ("START\n" +
                 "name:my.script\n" +
                 "\n").getBytes(UTF_8);

        mockery.checking(new Expectations() {
            {
                oneOf(output).write(with(hasInitialBytes(expectedStart)), with(equal(0)), with(equal(expectedStart.length)));
                oneOf(output).flush();
            }
        });

        StartCommand start = new StartCommand();
        start.setName("my.script");

        control.connect();
        control.writeCommand(start);

    }

    @Test
    public void shouldWriteAbortCommand() throws Exception {
        final byte[] expectedStart =
                ("ABORT\n" +
                 "name:my.script\n" +
                 "\n").getBytes(UTF_8);

        mockery.checking(new Expectations() {
            {
                oneOf(output).write(with(hasInitialBytes(expectedStart)), with(equal(0)), with(equal(expectedStart.length)));
                oneOf(output).flush();
            }
        });

        AbortCommand abort = new AbortCommand();
        abort.setName("my.script");

        control.connect();
        control.writeCommand(abort);

    }

    @Test
    public void shouldReadStartedEvent() throws Exception {

        StartedEvent expectedStarted = new StartedEvent();
        expectedStarted.setName("my.script");

        mockery.checking(new Expectations() {
            {
                oneOf(input).read(with(any(byte[].class)), with(equal(0)), with(any(int.class)));
                will(readInitialBytes(0, ("STARTED\n" +
                                          "name:my.script\n" +
                                          "\n").getBytes(UTF_8)));
                allowing(input).available();
                will(returnValue(0));
            }
        });

        control.connect();
        CommandEvent started = control.readEvent();

        assertEquals(expectedStarted, started);
    }

    @Test
    public void shouldReadFinishedEvent() throws Exception {

        FinishedEvent expectedFinished = new FinishedEvent();
        expectedFinished.setName("my.script");
        expectedFinished.setScript("# comment");

        mockery.checking(new Expectations() {
            {
                oneOf(input).read(with(any(byte[].class)), with(equal(0)), with(any(int.class)));
                will(readInitialBytes(0, ("FINISHED\n" +
                                          "name:my.script\n" +
                                          "content-length:9\n" +
                                          "\n" +
                                          "# comment").getBytes(UTF_8)));
                allowing(input).available();
                will(returnValue(0));
            }
        });

        control.connect();
        CommandEvent finished = control.readEvent();

        assertEquals(expectedFinished, finished);
    }

    @Test
    public void shouldReadErrorEvent() throws Exception {

        ErrorEvent expectedError = new ErrorEvent();
        expectedError.setName("my.script");
        expectedError.setSummary("summary text");
        expectedError.setDescription("description text");

        mockery.checking(new Expectations() {
            {
                oneOf(input).read(with(any(byte[].class)), with(equal(0)), with(any(int.class)));
                will(readInitialBytes(0, ("ERROR\n" +
                                          "name:my.script\n" +
                                          "summary:summary text\n" +
                                          "content-length:16\n" +
                                          "\n" +
                                          "description text").getBytes(UTF_8)));
                allowing(input).available();
                will(returnValue(0));
            }
        });

        control.connect();
        CommandEvent error = control.readEvent();

        assertEquals(expectedError, error);
    }

    private static Matcher<byte[]> hasInitialBytes(final byte[] expected) {
        return new BaseMatcher<byte[]>() {

            @Override
            public boolean matches(Object item) {
                if (!(item instanceof byte[])) {
                    return false;
                }

                byte[] actual = (byte[]) item;
                if (actual.length < expected.length) {
                    return false;
                }

                for (int i = 0; i < expected.length; i++) {
                    if (actual[i] != expected[i]) {
                        return false;
                    }
                }

                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("has initial bytes");
            }
        };
    }

    private static Action readInitialBytes(final int parameter, final byte[] initialBytes) {
        return new Action() {

            @Override
            public Object invoke(Invocation invocation) throws Throwable {
                byte[] array = (byte[]) invocation.getParameter(parameter);

                if (array.length < initialBytes.length) {
                    throw new IndexOutOfBoundsException();
                }

                for (int i = 0; i < initialBytes.length; i++) {
                    array[i] = initialBytes[i];
                }

                return initialBytes.length;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("read initial bytes");
            }
        };
    }
}
