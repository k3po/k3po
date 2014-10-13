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

package org.kaazing.robot.lang.parser;

import static org.junit.Assert.assertEquals;
import static org.kaazing.robot.lang.parser.ScriptParseStrategy.READ_CLOSED;
import static org.kaazing.robot.lang.parser.ScriptParseStrategy.READ_HTTP_HEADER;
import static org.kaazing.robot.lang.parser.ScriptParseStrategy.READ_HTTP_METHOD;
import static org.kaazing.robot.lang.parser.ScriptParseStrategy.READ_HTTP_PARAMETER;
import static org.kaazing.robot.lang.parser.ScriptParseStrategy.READ_HTTP_STATUS;
import static org.kaazing.robot.lang.parser.ScriptParseStrategy.READ_HTTP_VERSION;
import static org.kaazing.robot.lang.parser.ScriptParseStrategy.SCRIPT;
import static org.kaazing.robot.lang.parser.ScriptParseStrategy.WRITE_CLOSE;
import static org.kaazing.robot.lang.parser.ScriptParseStrategy.WRITE_HTTP_CONTENT_LENGTH;
import static org.kaazing.robot.lang.parser.ScriptParseStrategy.WRITE_HTTP_HEADER;
import static org.kaazing.robot.lang.parser.ScriptParseStrategy.WRITE_HTTP_METHOD;
import static org.kaazing.robot.lang.parser.ScriptParseStrategy.WRITE_HTTP_PARAMETER;
import static org.kaazing.robot.lang.parser.ScriptParseStrategy.WRITE_HTTP_STATUS;
import static org.kaazing.robot.lang.parser.ScriptParseStrategy.WRITE_HTTP_VERSION;
import static org.kaazing.robot.lang.test.junit.Assert.assertEquals;

import java.net.URI;

import org.junit.Test;
import org.kaazing.robot.lang.ast.AstReadClosedNode;
import org.kaazing.robot.lang.ast.AstReadConfigNode;
import org.kaazing.robot.lang.ast.AstScriptNode;
import org.kaazing.robot.lang.ast.AstWriteCloseNode;
import org.kaazing.robot.lang.ast.builder.AstReadClosedNodeBuilder;
import org.kaazing.robot.lang.ast.builder.AstReadConfigNodeBuilder;
import org.kaazing.robot.lang.ast.builder.AstScriptNodeBuilder;
import org.kaazing.robot.lang.ast.builder.AstWriteCloseNodeBuilder;
import org.kaazing.robot.lang.http.ast.AstWriteHttpContentLengthNode;
import org.kaazing.robot.lang.http.ast.AstWriteHttpHeaderNode;
import org.kaazing.robot.lang.http.ast.AstWriteHttpMethodNode;
import org.kaazing.robot.lang.http.ast.AstWriteHttpParameterNode;
import org.kaazing.robot.lang.http.ast.AstWriteHttpStatusNode;
import org.kaazing.robot.lang.http.ast.AstWriteHttpVersionNode;
import org.kaazing.robot.lang.http.ast.builder.AstWriteHttpContentLengthNodeBuilder;
import org.kaazing.robot.lang.http.ast.builder.AstWriteHttpHeaderNodeBuilder;
import org.kaazing.robot.lang.http.ast.builder.AstWriteHttpMethodNodeBuilder;
import org.kaazing.robot.lang.http.ast.builder.AstWriteHttpParameterNodeBuilder;
import org.kaazing.robot.lang.http.ast.builder.AstWriteHttpStatusNodeBuilder;
import org.kaazing.robot.lang.http.ast.builder.AstWriteHttpVersionNodeBuilder;

public class HttpScriptParserTest {

    @Test
    public void shouldParseReadHttpHeaderExactText() throws Exception {

        String scriptFragment = "read header \"Host\" \"localhost:8000\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstReadConfigNode actual = parser.parseWithStrategy(scriptFragment, READ_HTTP_HEADER);

        // @formatter:off
        AstReadConfigNode expected = new AstReadConfigNodeBuilder()
            .setType("header")
            .setValueExactText("name", "Host")
            .addMatcherExactText("localhost:8000")
            .setNextLineInfo(1, 0)
        .done();
        // @formatter:on

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseWriteHttpHeaderExactText() throws Exception {

        String scriptFragment = "write header \"Host\" \"localhost:8000\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstWriteHttpHeaderNode actual = parser.parseWithStrategy(scriptFragment, WRITE_HTTP_HEADER);

        // @formatter:off
        AstWriteHttpHeaderNode expected = new AstWriteHttpHeaderNodeBuilder()
            .setNameExactText("Host")
            .addValueExactText("localhost:8000")
            .setNextLineInfo(1, 0)
        .done();
        // @formatter:on

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseWriteHttpContentLength() throws Exception {

        String scriptFragment = "write content-length";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstWriteHttpContentLengthNode actual = parser.parseWithStrategy(scriptFragment, WRITE_HTTP_CONTENT_LENGTH);

        // @formatter:off
        AstWriteHttpContentLengthNode expected = new AstWriteHttpContentLengthNodeBuilder()
            .setNextLineInfo(1, 0)
        .done();
        // @formatter:on

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseReadHttpMethodExactTest() throws Exception {

        String scriptFragment = "read method \"get\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstReadConfigNode actual = parser.parseWithStrategy(scriptFragment, READ_HTTP_METHOD);

        // @formatter:off
        AstReadConfigNode expected = new AstReadConfigNodeBuilder()
            .setType("method")
            .setMatcherExactText("name", "get")
            .setNextLineInfo(1, 0)
        .done();
        // @formatter:on

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseWriteHttpMethodExactTest() throws Exception {

        String scriptFragment = "write method \"get\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstWriteHttpMethodNode actual = parser.parseWithStrategy(scriptFragment, WRITE_HTTP_METHOD);

        // @formatter:off
        AstWriteHttpMethodNode expected = new AstWriteHttpMethodNodeBuilder()
            .setExactText("get")
            .setNextLineInfo(1, 0)
        .done();
        // @formatter:on

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseReadHttpParameterExactTest() throws Exception {

        String scriptFragment = "read parameter \".kl\" \"y\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstReadConfigNode actual = parser.parseWithStrategy(scriptFragment, READ_HTTP_PARAMETER);

        // @formatter:off
        AstReadConfigNode expected = new AstReadConfigNodeBuilder()
            .setType("parameter")
            .setValueExactText("name", ".kl")
            .addMatcherExactText("y")
            .setNextLineInfo(1, 0)
        .done();
        // @formatter:on

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseWriteHttpParameterExactTest() throws Exception {

        String scriptFragment = "write parameter \".kl\" \"y\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstWriteHttpParameterNode actual = parser.parseWithStrategy(scriptFragment, WRITE_HTTP_PARAMETER);

        // @formatter:off
        AstWriteHttpParameterNode expected = new AstWriteHttpParameterNodeBuilder()
            .setNameExactText(".kl")
            .addValueExactText("y")
            .setNextLineInfo(1, 0)
        .done();
        // @formatter:on

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseReadHttpVersionExactTest() throws Exception {

        String scriptFragment = "read version \"Http/1.1\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstReadConfigNode actual = parser.parseWithStrategy(scriptFragment, READ_HTTP_VERSION);

        // @formatter:off
        AstReadConfigNode expected = new AstReadConfigNodeBuilder()
            .setType("version")
            .setMatcherExactText("version", "Http/1.1")
            .setNextLineInfo(1, 0)
        .done();
        // @formatter:on

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseWriteHttpVersionExactTest() throws Exception {

        String scriptFragment = "write version \"Http/1.1\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstWriteHttpVersionNode actual = parser.parseWithStrategy(scriptFragment, WRITE_HTTP_VERSION);

        // @formatter:off
        AstWriteHttpVersionNode expected = new AstWriteHttpVersionNodeBuilder()
            .setExactText("Http/1.1")
            .setNextLineInfo(1, 0)
        .done();
        // @formatter:on

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseReadHttpStatusExactTest() throws Exception {

        String scriptFragment = "read status \"403\" \"Unauthorized\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstReadConfigNode actual = parser.parseWithStrategy(scriptFragment, READ_HTTP_STATUS);

        // @formatter:off
        AstReadConfigNode expected = new AstReadConfigNodeBuilder()
            .setType("status")
            .setMatcherExactText("code", "403")
            .setMatcherExactText("reason", "Unauthorized")
            .setNextLineInfo(1, 0)
        .done();
        // @formatter:on

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseWriteHttpStatusExactTest() throws Exception {

        String scriptFragment = "write status \"403\" \"Unauthorized\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstWriteHttpStatusNode actual = parser.parseWithStrategy(scriptFragment, WRITE_HTTP_STATUS);

        // @formatter:off
        AstWriteHttpStatusNode expected = new AstWriteHttpStatusNodeBuilder()
            .setCodeExactText("403")
            .setReasonExactText("Unauthorized")
            .setNextLineInfo(1, 0)
        .done();
        // @formatter:on

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseReadClose() throws Exception {

        String scriptFragment = "read closed";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstReadClosedNode actual = parser.parseWithStrategy(scriptFragment, READ_CLOSED);

        // @formatter:off
        AstReadClosedNode expected = new AstReadClosedNodeBuilder()
            .setNextLineInfo(1, 0)
        .done();
        // @formatter:on

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseWriteClose() throws Exception {

        String scriptFragment = "write close";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstWriteCloseNode actual = parser.parseWithStrategy(scriptFragment, WRITE_CLOSE);

        // @formatter:off
        AstWriteCloseNode expected = new AstWriteCloseNodeBuilder()
            .setNextLineInfo(1, 0)
        .done();
        // @formatter:on

        assertEquals(expected, actual);
    }

     @Test
     public void shouldParseHttpReadRequestWriteResponseScript() throws Exception {
         // @formatter:off
         String script =
             "accept http://somehost:8000/path\n" +
             "accepted\n" +
             "read method \"get\"\n" +
             "read parameter \".kl\" \"y\"\n" +
             "read header \"Upgrade\" \"websocket\"\n" +
             "read [0x82]\n" +
             "read closed\n" +
             "write status \"101\" \"Switching Protocols\"\n" +
             "write header \"upgrade\" \"websocket\"\n" +
             "write close \n" +
             "close\n" +
             "closed\n";
         // @formatter:on

         ScriptParserImpl parser = new ScriptParserImpl();
         AstScriptNode actual = parser.parseWithStrategy(script, SCRIPT);

         // @formatter:off
         AstScriptNode expected = new AstScriptNodeBuilder()
             .addAcceptStream()
                 .setNextLineInfo(1, 0)
                 .setLocation(URI.create("http://somehost:8000/path"))
             .done()
             .addAcceptedStream()
                 .setNextLineInfo(1, 0)
                 .addReadConfigEvent()
                     .setType("method")
                     .setMatcherExactText("name", "get")
                     .setNextLineInfo(1, 0)
                 .done()
                 .addReadConfigEvent()
                     .setType("parameter")
                     .setValueExactText("name", ".kl")
                     .addMatcherExactText("y")
                     .setNextLineInfo(1, 0)
                 .done()
                 .addReadConfigEvent()
                     .setType("header")
                     .setValueExactText("name", "Upgrade")
                     .addMatcherExactText("websocket")
                     .setNextLineInfo(1, 0)
                 .done()
                 .addReadEvent()
                     .addExactBytes(new byte[] {(byte) 0x82})
                     .setNextLineInfo(1, 0)
                 .done()
                 .addReadCloseCommand()
                     .setNextLineInfo(1, 0)
                 .done()
                 .addWriteHttpStatusCommand()
                     .setCodeExactText("101")
                     .setReasonExactText("Switching Protocols")
                     .setNextLineInfo(1, 0)
                 .done()
                 .addWriteHttpHeaderCommand()
                     .setNameExactText("upgrade")
                     .addValueExactText("websocket")
                     .setNextLineInfo(1, 0)
                 .done()
                 .addWriteCloseCommand()
                     .setNextLineInfo(1, 0)
                 .done()
                 .addCloseCommand()
                     .setNextLineInfo(1, 0)
                     .done()
                 .addClosedEvent()
                     .setNextLineInfo(1, 0)
                     .done()
                 .done()
             .done();
         // @formatter:on
         assertEquals(expected, actual);
     }

     @Test
     public void shouldParseHttpWriteRequestReadResponseScript() throws Exception {
         // @formatter:off
         String script =
             "connect http://somehost:8000/path\n" +
             "connected\n" +
             "write method \"get\"\n" +
             "write parameter \".kl\" \"y\"\n" +
             "write header \"Upgrade\" \"websocket\"\n" +
             "write close\n" +
             "read status \"101\" \"Switching Protocols\"\n" +
             "read header \"upgrade\" \"websocket\"\n" +
             "read closed \n" +
             "write [0x82]\n" +
             "close\n" +
             "closed\n";
         // @formatter:on

         ScriptParserImpl parser = new ScriptParserImpl();
         AstScriptNode actual = parser.parseWithStrategy(script, SCRIPT);

         // @formatter:off
         AstScriptNode expected = new AstScriptNodeBuilder()
             .addConnectStream()
                 .setNextLineInfo(1, 0)
                 .setLocation(URI.create("http://somehost:8000/path"))
                 .addConnectedEvent()
                    .setNextLineInfo(1, 0)
                 .done()
                 .addWriteHttpMethodCommand()
                     .setExactText("get")
                     .setNextLineInfo(1, 0)
                 .done()
                 .addWriteHttpParameterCommand()
                     .setNameExactText(".kl")
                     .addValueExactText("y")
                     .setNextLineInfo(1, 0)
                 .done()
                 .addWriteHttpHeaderCommand()
                     .setNameExactText("Upgrade")
                     .addValueExactText("websocket")
                     .setNextLineInfo(1, 0)
                 .done()
                 .addWriteCloseCommand()
                     .setNextLineInfo(1, 0)
                 .done()
                 .addReadConfigEvent()
                     .setType("status")
                     .setMatcherExactText("code", "101")
                     .setMatcherExactText("reason", "Switching Protocols")
                     .setNextLineInfo(1, 0)
                 .done()
                 .addReadConfigEvent()
                     .setType("header")
                     .setValueExactText("name", "upgrade")
                     .addMatcherExactText("websocket")
                     .setNextLineInfo(1, 0)
                 .done()
                 .addReadCloseCommand()
                     .setNextLineInfo(1, 0)
                 .done()
                 .addWriteCommand()
                     .addExactBytes(new byte[] {(byte) 0x82})
                     .setNextLineInfo(1, 0)
                 .done()
                 .addCloseCommand()
                     .setNextLineInfo(1, 0)
                     .done()
                 .addClosedEvent()
                     .setNextLineInfo(1, 0)
                     .done()
                 .done()
             .done();
         // @formatter:on
         assertEquals(expected, actual);
     }

     @Test
     public void shouldParseHttpWithContent() throws Exception {
         // @formatter:off
         String script =
             "accept http://somehost:8000/path\n" +
             "accepted\n" +
             "read method \"get\"\n" +
             "read closed\n" +
             "write status \"200\" \"OK\"\n" +
             "write content-length\n" +
             "write \"some content\"\n" +
             "write close \n";
         // @formatter:on

         ScriptParserImpl parser = new ScriptParserImpl();
         AstScriptNode actual = parser.parseWithStrategy(script, SCRIPT);

         // @formatter:off
         AstScriptNode expected = new AstScriptNodeBuilder()
             .addAcceptStream()
                 .setNextLineInfo(1, 0)
                 .setLocation(URI.create("http://somehost:8000/path"))
             .done()
             .addAcceptedStream()
                 .setNextLineInfo(1, 0)
                 .addReadConfigEvent()
                     .setType("method")
                     .setMatcherExactText("name", "get")
                     .setNextLineInfo(1, 0)
                 .done()
                 .addReadCloseCommand()
                     .setNextLineInfo(1, 0)
                 .done()
                 .addWriteHttpStatusCommand()
                     .setCodeExactText("200")
                     .setReasonExactText("OK")
                     .setNextLineInfo(1, 0)
                 .done()
                 .addWriteHttpContentLengthCommand()
                     .setNextLineInfo(1, 0)
                 .done()
                 .addWriteCommand()
                     .addExactText("some content")
                     .setNextLineInfo(1, 0)
                 .done()
                 .addWriteCloseCommand()
                     .setNextLineInfo(1, 0)
                 .done()
             .done()
         .done();
         // @formatter:on
         assertEquals(expected, actual);
     }
}
