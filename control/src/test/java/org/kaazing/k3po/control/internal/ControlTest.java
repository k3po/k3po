/*
 * Copyright (c) 2007-2014 Kaazing Corporation. All rights reserved.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.kaazing.k3po.control.internal;

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
import org.kaazing.k3po.control.internal.command.AbortCommand;
import org.kaazing.k3po.control.internal.command.PrepareCommand;
import org.kaazing.k3po.control.internal.command.StartCommand;
import org.kaazing.k3po.control.internal.event.CommandEvent;
import org.kaazing.k3po.control.internal.event.ErrorEvent;
import org.kaazing.k3po.control.internal.event.FinishedEvent;
import org.kaazing.k3po.control.internal.event.PreparedEvent;
import org.kaazing.k3po.control.internal.event.StartedEvent;

public class ControlTest {

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private Control control;

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
        control = new Control(new URL(null, "test://internal", new URLStreamHandler() {
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
        String path = "org/kaazing/robot/control/myscript";

        final byte[] expectedPrepare =
                ("PREPARE\n" +
                 "version:2.0\n" +
                 "name:" + path + "\n" +
                 "\n").getBytes(UTF_8);

        mockery.checking(new Expectations() {
            {
                oneOf(output).write(with(hasInitialBytes(expectedPrepare)), with(equal(0)), with(equal(expectedPrepare.length)));
                oneOf(output).flush();
            }
        });

        PrepareCommand prepare = new PrepareCommand();
        prepare.setName(path);

        control.connect();
        control.writeCommand(prepare);

    }

    @Test
    public void shouldWriteStartCommand() throws Exception {
        final byte[] expectedStart =
                ("START\n" +
                 "\n").getBytes(UTF_8);

        mockery.checking(new Expectations() {
            {
                oneOf(output).write(with(hasInitialBytes(expectedStart)), with(equal(0)), with(equal(expectedStart.length)));
                oneOf(output).flush();
            }
        });

        StartCommand start = new StartCommand();

        control.connect();
        control.writeCommand(start);

    }

    @Test
    public void shouldWriteAbortCommand() throws Exception {
        final byte[] expectedStart =
                ("ABORT\n" +
                 "\n").getBytes(UTF_8);

        mockery.checking(new Expectations() {
            {
                oneOf(output).write(with(hasInitialBytes(expectedStart)), with(equal(0)), with(equal(expectedStart.length)));
                oneOf(output).flush();
            }
        });

        AbortCommand abort = new AbortCommand();

        control.connect();
        control.writeCommand(abort);

    }

    @Test
    public void shouldReadPreparedEvent() throws Exception {
        PreparedEvent expectedPrepared = new PreparedEvent();
        expectedPrepared.setScript("# comment");

        mockery.checking(new Expectations() {
            {
                oneOf(input).read(with(any(byte[].class)), with(equal(0)), with(any(int.class)));
                will(readInitialBytes(0, ("PREPARED\n" +
                                          "content-length:9\n" +
                                          "future-header:future-value\n" + // test forward compatibility
                                          "\n" +
                                          "# comment").getBytes(UTF_8)));
                allowing(input).available();
                will(returnValue(0));
            }
        });

        control.connect();
        CommandEvent finished = control.readEvent();

        assertEquals(expectedPrepared, finished);
    }

    @Test
    public void shouldReadStartedEvent() throws Exception {
        StartedEvent expectedStarted = new StartedEvent();

        mockery.checking(new Expectations() {
            {
                oneOf(input).read(with(any(byte[].class)), with(equal(0)), with(any(int.class)));
                will(readInitialBytes(0, ("STARTED\n" + "future-header:future-value\n" + // test forward compatibility
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
        expectedFinished.setScript("# comment");

        mockery.checking(new Expectations() {
            {
                oneOf(input).read(with(any(byte[].class)), with(equal(0)), with(any(int.class)));
                will(readInitialBytes(0, ("FINISHED\n" +
                                          "content-length:9\n" +
                                          "future-header:future-value\n" + // test forward compatibility
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
        expectedError.setSummary("summary text");
        expectedError.setDescription("description text");

        mockery.checking(new Expectations() {
            {
                oneOf(input).read(with(any(byte[].class)), with(equal(0)), with(any(int.class)));
                will(readInitialBytes(0, ("ERROR\n" +
                                          "summary:summary text\n" +
                                          "content-length:16\n" +
                                          "future-header:future-value\n" + // test forward compatibility
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
