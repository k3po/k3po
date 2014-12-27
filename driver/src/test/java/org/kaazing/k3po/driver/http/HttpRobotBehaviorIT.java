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

package org.kaazing.k3po.driver.http;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.kaazing.k3po.driver.Robot;

public class HttpRobotBehaviorIT {

    @Rule
    public TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

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

    @Test
    public void shouldNotAcceptHeaderWhenExpectedMissing() throws Exception {

        String script = combineScripts("http.accept.header.missing.rpt",
                "http.connect.header.missing.rpt");

        String expected = script;

        robot.prepareAndStart(script).await();
        robot.finish().await();

        assertNotEquals(expected, robot.getObservedScript());
    }

    @Test
    public void shouldAcceptHeaderWithMultipleTokens() throws Exception {
        
        String script = combineScripts("http.accept.header.with.multiple.tokens.rpt",
                "tcp.connect.header.with.multiple.tokens.rpt");

        String expected = script;

        robot.prepareAndStart(script).await();
        robot.finish().await();

        assertEquals(expected, robot.getObservedScript());
    }

    @Test
    public void shouldAcceptReadParameterWithMultipleTokens() throws Exception {
        
        String script = combineScripts("http.accept.read.parameter.with.multiple.tokens.rpt",
                "tcp.connect.write.parameter.with.multiple.tokens.rpt");

        String expected = script;

        robot.prepareAndStart(script).await();
        robot.finish().await();

        assertEquals(expected, robot.getObservedScript());
    }
    
    @Test
    public void shouldAcceptWriteParameterWithMultipleTokens() throws Exception {
        
        String script = combineScripts("http.connect.write.parameter.with.multiple.tokens.rpt",
                "tcp.accept.read.parameter.with.multiple.tokens.rpt");

        String expected = script;

        robot.prepareAndStart(script).await();
        robot.finish().await();

        assertEquals(expected, robot.getObservedScript());
    }
    
    @Test
    public void shouldReceiveGetRequestAndProvideResponse() throws Exception {

        String script = combineScripts("http.accept.get.request.with.no.content.on.response.rpt",
                "tcp.connect.get.request.with.no.content.on.response.rpt");

        String expected = script;

        robot.prepareAndStart(script).await();
        robot.finish().await();

        assertEquals(expected, robot.getObservedScript());

    }

    @Test
    public void shouldReceiveGetRequestAndProvideResponseWithContent() throws Exception {

        String script = combineScripts("http.accept.get.request.with.content.on.response.rpt",
                "tcp.connect.get.request.with.content.on.response.rpt");

        String expected = script;

        robot.prepareAndStart(script).await();
        robot.finish().await();

        assertEquals(expected, robot.getObservedScript());

    }

    @Test
    public void shouldSendGetRequestAndReceiveResponseWithNoContent() throws Exception {

        String script = combineScripts("http.connect.get.request.with.no.content.on.response.rpt",
                "tcp.accept.get.request.with.no.content.on.response.rpt");

        String expected = script;

        robot.prepareAndStart(script).await();
        robot.finish().await();

        assertEquals(expected, robot.getObservedScript());

    }

    @Test
    public void shouldSendGetRequestAndReceiveResponseWithContent() throws Exception {

        String script = combineScripts("http.connect.get.request.with.content.on.response.rpt",
                "tcp.accept.get.request.with.content.on.response.rpt");

        String expected = script;

        robot.prepareAndStart(script).await();
        robot.finish().await();

        assertEquals(expected, robot.getObservedScript());

    }

    @Test
    public void shouldAcceptWebsocketHandshake() throws Exception {

        String script = combineScripts("http.accept.websocket.handshake.rpt", "tcp.connect.websocket.handshake.rpt");

        String expected = script;

        robot.prepareAndStart(script).await();
        robot.finish().await();

        assertEquals(expected, robot.getObservedScript());

    }

    @Test
    public void shouldConnectWebsocketHandshake() throws Exception {

        String script = combineScripts("http.connect.websocket.handshake.rpt", "tcp.accept.websocket.handshake.rpt");

        String expected = script;

        robot.prepareAndStart(script).await();
        robot.finish().await();

        assertEquals(expected, robot.getObservedScript());

    }

    @Test
    public void shouldAcceptPostMessageWithChunking() throws Exception {

        String script = combineScripts("http.accept.post.with.chunking.rpt", "tcp.connect.post.with.chunking.rpt");

        String expected = script;

        robot.prepareAndStart(script).await();
        robot.finish().await();

        assertEquals(expected, robot.getObservedScript());

    }

    @Test
    public void shouldConnectPostMessageWithChunking() throws Exception {

        String script = combineScripts("http.connect.post.with.chunking.rpt", "tcp.accept.post.with.chunking.rpt");

        String expected = script;

        robot.prepareAndStart(script).await();
        robot.finish().await();

        assertEquals(expected, robot.getObservedScript());

    }

    @Test
    public void shouldAcceptResponseWithChunking() throws Exception {

        String script = combineScripts("http.accept.response.with.chunking.rpt",
                "tcp.connect.response.with.chunking.rpt");

        String expected = script;

        robot.prepareAndStart(script).await();
        robot.finish().await();

        assertEquals(expected, robot.getObservedScript());

    }

    @Test
    public void shouldConnectResponseWithChunking() throws Exception {

        String script = combineScripts("http.connect.response.with.chunking.rpt",
                "tcp.accept.response.with.chunking.rpt");

        String expected = script;

        robot.prepareAndStart(script).await();
        robot.finish().await();

        assertEquals(expected, robot.getObservedScript());

    }

    @Test
    public void shouldConnectConnectionCloseResponse() throws Exception {

        String script = combineScripts("http.connect.connection.close.response.rpt",
                "tcp.accept.connection.close.response.rpt");

        String expected = script;

        robot.prepareAndStart(script).await();
        robot.finish().await();

        assertEquals(expected, robot.getObservedScript());

    }

    @Test
    public void shouldAcceptConnectionCloseResponse() throws Exception {

        String script = combineScripts("http.accept.connection.close.response.rpt",
                "tcp.connect.connection.close.response.rpt");

        String expected = script;

        robot.prepareAndStart(script).await();
        robot.finish().await();

        assertEquals(expected, robot.getObservedScript());

    }

    @Test
    public void shouldAcceptMultipleHttpOnDifferentTcp() throws Exception {

        String script = combineScripts("http.accept.two.http.200.rpt",
                "tcp.connect.two.http.200.on.different.streams.rpt");

        String expected = script;

        robot.prepareAndStart(script).await();
        robot.finish().await();

        assertEquals(expected, robot.getObservedScript());

    }

    @Test
    public void shouldAcceptMultipleHttpOnSameTcp() throws Exception {

        String script = combineScripts("http.accept.two.http.200.rpt", "tcp.connect.two.http.200.on.same.streams.rpt");

        String expected = script;

        robot.prepareAndStart(script).await();
        robot.finish().await();

        assertEquals(expected, robot.getObservedScript());

    }

    private String SCRIPT_PATH = "/src/test/scripts/org/kaazing/robot/driver/http/";

    private String combineScripts(String... scriptNames) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (String scriptName : scriptNames) {
            sb.append("#");
            sb.append(scriptName);
            sb.append("\n");
            List<String> lines = Files.readAllLines(Paths.get(String.format("%s%s%s", Paths.get("").toAbsolutePath()
                    .toString(), SCRIPT_PATH, scriptName)), StandardCharsets.UTF_8);
            for (String line : lines) {
                sb.append(line);
                sb.append("\n");
            }
        }
        return sb.toString();
    }

}
