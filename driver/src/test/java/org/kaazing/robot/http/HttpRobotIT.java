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

package org.kaazing.robot.http;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.kaazing.robot.Robot;
import org.kaazing.robot.behavior.RobotCompletionFuture;

public class HttpRobotIT {

    private static final long TEST_TIMEOUT = 2000;
    private static final String FORMAT_VERSION = "text/x-robot-2";
    private static final Charset UTF_8 = Charset.forName("UTF-8");

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
        robot.destroy();
    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldRecieveGetRequestAndProvideResponse() throws Exception {

        String script = combineScripts("http.accept.get.request.with.no.content.on.response.rpt",
                "tcp.connect.get.request.with.no.content.on.response.rpt");

        String expected = script;

        robot.prepareAndStart(script).await();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldRecieveGetRequestAndProvideResponseWithContent() throws Exception {

        String script = combineScripts("http.accept.get.request.with.content.on.response.rpt",
                "tcp.connect.get.request.with.content.on.response.rpt");

        String expected = script;

        robot.prepareAndStart(script).await();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldSendGetRequestAndRecieveResponseWithNoContent() throws Exception {

        String script = combineScripts("http.connect.get.request.with.no.content.on.response.rpt",
                "tcp.accept.get.request.with.no.content.on.response.rpt");

        String expected = script;

        robot.prepareAndStart(script).await();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldSendGetRequestAndRecieveResponseWithContent() throws Exception {

        String script = combineScripts("http.connect.get.request.with.content.on.response.rpt",
                "tcp.accept.get.request.with.content.on.response.rpt");

        String expected = script;

        robot.prepareAndStart(script).await();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldAcceptWebsocketHandshake() throws Exception {

        String script = combineScripts("http.accept.websocket.handshake.rpt", "tcp.connect.websocket.handshake.rpt");

        String expected = script;
        robot.prepareAndStart(script).await();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

    }

    @Test(timeout = TEST_TIMEOUT)
    public void shouldConnectWebsocketHandshake() throws Exception {

        String script = combineScripts("http.connect.websocket.handshake.rpt", "tcp.accept.websocket.handshake.rpt");

        String expected = script;
        robot.prepareAndStart(script).await();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

    }

    @Test(timeout = TEST_TIMEOUT)
    @Ignore("not yet implemented")
    public void shouldAcceptPostMessageWithChunking() throws Exception {

        String script = combineScripts("http.accept.post.with.chunking.rpt", "tcp.connect.post.with.chunking.rpt");

        String expected = script;
        robot.prepareAndStart(script).await();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

    }

    @Test(timeout = TEST_TIMEOUT)
    @Ignore("not yet implemented")
    public void shouldConnectPostMessageWithChunking() throws Exception {

        String script = combineScripts("http.connect.post.with.chunking.rpt", "tcp.accept.post.with.chunking.rpt");

        String expected = script;
        robot.prepareAndStart(script).await();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

    }

    @Test(timeout = TEST_TIMEOUT)
    @Ignore("not yet implemented")
    public void shouldAcceptResponseWithChunking() throws Exception {

        String script = combineScripts("http.accept.response.with.chunking.rpt",
                "tcp.connect.response.with.chunking.rpt");

        String expected = script;
        robot.prepareAndStart(script).await();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

    }

    @Test(timeout = TEST_TIMEOUT)
    @Ignore("not yet implemented")
    public void shouldConnectResponseWithChunking() throws Exception {

        String script = combineScripts("http.connect.response.with.chunking.rpt",
                "tcp.accept.response.with.chunking.rpt");

        String expected = script;
        robot.prepareAndStart(script).await();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

    }

    @Test(timeout = TEST_TIMEOUT)
    @Ignore("not yet implemented")
    public void shouldConnectConnectionCloseResponse() throws Exception {

        String script = combineScripts("http.connect.connection.close.response.rpt",
                "tcp.accept.connection.close.response.rpt");

        String expected = script;
        robot.prepareAndStart(script).await();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

    }

    @Test(timeout = TEST_TIMEOUT)
    @Ignore("not yet implemented")
    public void shouldAcceptConnectionCloseResponse() throws Exception {

        String script = combineScripts("http.accept.connection.close.response.rpt",
                "tcp.connect.connection.close.response.rpt");

        String expected = script;
        robot.prepareAndStart(script).await();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

    }

    @Ignore
    @Test(timeout = TEST_TIMEOUT)
    public void shouldAcceptMutlipleHttpOnDifferentTcp() throws Exception {

        String script = combineScripts("http.accept.two.http.200.rpt",
                "tcp.connect.two.http.200.on.different.streams.rpt");

        String expected = script;
        robot.prepareAndStart(script).await();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

    }

    @Ignore
    @Test(timeout = TEST_TIMEOUT)
    public void shouldAcceptMutlipleHttpOnSameTcp() throws Exception {

        String script = combineScripts("http.accept.two.http.200.rpt", "tcp.connect.two.http.200.on.same.streams.rpt");

        String expected = script;
        robot.prepareAndStart(script).await();

        RobotCompletionFuture doneFuture = robot.getScriptCompleteFuture();

        doneFuture.await();

        assertEquals(expected, doneFuture.getObservedScript());

    }

    private String SCRIPT_PATH = "src/test/scripts/org/kaazing/robot/http/";

    private String combineScripts(String... scriptNames) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (String scriptName : scriptNames) {
            sb.append("#");
            sb.append(scriptName);
            sb.append("\n");
            byte[] encoded = Files.readAllBytes(Paths.get(String.format("%s%s", SCRIPT_PATH, scriptName)));
            sb.append(new String(encoded, UTF_8));
        }
        return sb.toString();
    }
}
