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

import static org.kaazing.robot.lang.parser.ScriptParseStrategy.CLOSE_HTTP_REQUEST;
import static org.kaazing.robot.lang.parser.ScriptParseStrategy.CLOSE_HTTP_RESPONSE;
import static org.kaazing.robot.lang.parser.ScriptParseStrategy.READ_HTTP_HEADER;
import static org.kaazing.robot.lang.parser.ScriptParseStrategy.READ_HTTP_METHOD;
import static org.kaazing.robot.lang.parser.ScriptParseStrategy.READ_HTTP_PARAMETER;
import static org.kaazing.robot.lang.parser.ScriptParseStrategy.READ_HTTP_STATUS;
import static org.kaazing.robot.lang.parser.ScriptParseStrategy.READ_HTTP_VERSION;
import static org.kaazing.robot.lang.parser.ScriptParseStrategy.SCRIPT;
import static org.kaazing.robot.lang.parser.ScriptParseStrategy.WRITE_HTTP_CONTENT_LENGTH;
import static org.kaazing.robot.lang.parser.ScriptParseStrategy.WRITE_HTTP_HEADER;
import static org.kaazing.robot.lang.parser.ScriptParseStrategy.WRITE_HTTP_METHOD;
import static org.kaazing.robot.lang.parser.ScriptParseStrategy.WRITE_HTTP_PARAMETER;
import static org.kaazing.robot.lang.parser.ScriptParseStrategy.WRITE_HTTP_STATUS;
import static org.kaazing.robot.lang.parser.ScriptParseStrategy.WRITE_HTTP_VERSION;
import static org.kaazing.robot.lang.test.junit.Assert.assertEquals;
import static org.junit.Assert.assertEquals;

import java.net.URI;

import org.junit.Test;
import org.kaazing.robot.lang.ast.AstCloseHttpRequestNode;
import org.kaazing.robot.lang.ast.AstCloseHttpResponseNode;
import org.kaazing.robot.lang.ast.AstReadHttpHeaderNode;
import org.kaazing.robot.lang.ast.AstReadHttpMethodNode;
import org.kaazing.robot.lang.ast.AstReadHttpParameterNode;
import org.kaazing.robot.lang.ast.AstReadHttpStatusNode;
import org.kaazing.robot.lang.ast.AstReadHttpVersionNode;
import org.kaazing.robot.lang.ast.AstScriptNode;
import org.kaazing.robot.lang.ast.AstWriteHttpContentLengthNode;
import org.kaazing.robot.lang.ast.AstWriteHttpHeaderNode;
import org.kaazing.robot.lang.ast.AstWriteHttpMethodNode;
import org.kaazing.robot.lang.ast.AstWriteHttpParameterNode;
import org.kaazing.robot.lang.ast.AstWriteHttpStatusNode;
import org.kaazing.robot.lang.ast.AstWriteHttpVersionNode;
import org.kaazing.robot.lang.ast.builder.AstCloseHttpRequestNodeBuilder;
import org.kaazing.robot.lang.ast.builder.AstCloseHttpResponseNodeBuilder;
import org.kaazing.robot.lang.ast.builder.AstReadHttpHeaderNodeBuilder;
import org.kaazing.robot.lang.ast.builder.AstReadHttpMethodNodeBuilder;
import org.kaazing.robot.lang.ast.builder.AstReadHttpParameterNodeBuilder;
import org.kaazing.robot.lang.ast.builder.AstReadHttpStatusNodeBuilder;
import org.kaazing.robot.lang.ast.builder.AstReadHttpVersionNodeBuilder;
import org.kaazing.robot.lang.ast.builder.AstScriptNodeBuilder;
import org.kaazing.robot.lang.ast.builder.AstWriteHttpContentLengthNodeBuilder;
import org.kaazing.robot.lang.ast.builder.AstWriteHttpHeaderNodeBuilder;
import org.kaazing.robot.lang.ast.builder.AstWriteHttpMethodNodeBuilder;
import org.kaazing.robot.lang.ast.builder.AstWriteHttpParameterNodeBuilder;
import org.kaazing.robot.lang.ast.builder.AstWriteHttpStatusNodeBuilder;
import org.kaazing.robot.lang.ast.builder.AstWriteHttpVersionNodeBuilder;
import org.kaazing.robot.lang.parser.ScriptParserImpl;

public class HttpScriptParserTest {

    @Test
    public void shouldParseReadHttpHeaderExactText() throws Exception {

        String scriptFragment = "read header \"Host\" \"localhost:8000\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstReadHttpHeaderNode actual = parser.parseWithStrategy(scriptFragment, READ_HTTP_HEADER);

        // @formatter:off
        AstReadHttpHeaderNode expected = new AstReadHttpHeaderNodeBuilder()
            .setNameExactText("Host")
            .setValueExactText("localhost:8000")
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
            .setValueExactText("localhost:8000")
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
        AstReadHttpMethodNode actual = parser.parseWithStrategy(scriptFragment, READ_HTTP_METHOD);

        // @formatter:off
        AstReadHttpMethodNode expected = new AstReadHttpMethodNodeBuilder()
            .setExactText("get")
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
        AstReadHttpParameterNode actual = parser.parseWithStrategy(scriptFragment, READ_HTTP_PARAMETER);

        // @formatter:off
        AstReadHttpParameterNode expected = new AstReadHttpParameterNodeBuilder()
            .setKeyExactText(".kl")
            .setValueExactText("y")
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
            .setKeyExactText(".kl")
            .setValueExactText("y")
            .setNextLineInfo(1, 0)
        .done();
        // @formatter:on

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseReadHttpVersionExactTest() throws Exception {

        String scriptFragment = "read version \"Http/1.1\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstReadHttpVersionNode actual = parser.parseWithStrategy(scriptFragment, READ_HTTP_VERSION);

        // @formatter:off
        AstReadHttpVersionNode expected = new AstReadHttpVersionNodeBuilder()
            .setExactText("Http/1.1")
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
        AstReadHttpStatusNode actual = parser.parseWithStrategy(scriptFragment, READ_HTTP_STATUS);

        // @formatter:off
        AstReadHttpStatusNode expected = new AstReadHttpStatusNodeBuilder()
            .setCodeExactText("403")
            .setReasonExactText("Unauthorized")
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
    public void shouldParseCloseRequest() throws Exception {

        String scriptFragment = "close request";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstCloseHttpRequestNode actual = parser.parseWithStrategy(scriptFragment, CLOSE_HTTP_REQUEST);

        // @formatter:off
        AstCloseHttpRequestNode expected = new AstCloseHttpRequestNodeBuilder()
            .setNextLineInfo(1, 0)
        .done();
        // @formatter:on

        assertEquals(expected, actual);
    }

    @Test
    public void shouldParseCloseResponse() throws Exception {

        String scriptFragment = "close response";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstCloseHttpResponseNode actual = parser.parseWithStrategy(scriptFragment, CLOSE_HTTP_RESPONSE);

        // @formatter:off
        AstCloseHttpResponseNode expected = new AstCloseHttpResponseNodeBuilder()
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
             "close request\n" +
             "write status \"101\" \"Switching Protocols\"\n" +
             "write header \"upgrade\" \"websocket\"\n" +
             "close response \n" +
             "read [0x82]\n" +
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
                 .addReadHttpMethodEvent()
                     .setExactText("get")
                     .setNextLineInfo(1, 0)
                 .done()
                 .addReadHttpParameterEvent()
                     .setKeyExactText(".kl")
                     .setValueExactText("y")
                     .setNextLineInfo(1, 0)
                 .done()
                 .addReadHttpHeaderEvent()
                     .setNameExactText("Upgrade")
                     .setValueExactText("websocket")
                     .setNextLineInfo(1, 0)
                 .done()
                 .addCloseHttpRequestCommand()
                     .setNextLineInfo(1, 0)
                 .done()
                 .addWriteHttpStatusCommand()
                     .setCodeExactText("101")
                     .setReasonExactText("Switching Protocols")
                     .setNextLineInfo(1, 0)
                 .done()
                 .addWriteHttpHeaderCommand()
                     .setNameExactText("upgrade")
                     .setValueExactText("websocket")
                     .setNextLineInfo(1, 0)
                 .done()
                 .addCloseHttpResponseCommand()
                     .setNextLineInfo(1, 0)
                 .done()
                 .addReadEvent()
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
     public void shouldParseHttpWriteRequestReadResponseScript() throws Exception {
         // @formatter:off
         String script =
             "connect http://somehost:8000/path\n" +
             "connected\n" +
             "write method \"get\"\n" +
             "write parameter \".kl\" \"y\"\n" +
             "write header \"Upgrade\" \"websocket\"\n" +
             "close request\n" +
             "read status \"101\" \"Switching Protocols\"\n" +
             "read header \"upgrade\" \"websocket\"\n" +
             "close response \n" +
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
                     .setKeyExactText(".kl")
                     .setValueExactText("y")
                     .setNextLineInfo(1, 0)
                 .done()
                 .addWriteHttpHeaderCommand()
                     .setNameExactText("Upgrade")
                     .setValueExactText("websocket")
                     .setNextLineInfo(1, 0)
                 .done()
                 .addCloseHttpRequestCommand()
                     .setNextLineInfo(1, 0)
                 .done()
                 .addReadHttpStatusEvent()
                     .setCodeExactText("101")
                     .setReasonExactText("Switching Protocols")
                     .setNextLineInfo(1, 0)
                 .done()
                 .addReadHttpHeaderEvent()
                     .setNameExactText("upgrade")
                     .setValueExactText("websocket")
                     .setNextLineInfo(1, 0)
                 .done()
                 .addCloseHttpResponseCommand()
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
             "close request\n" +
             "write status \"200\" \"OK\"\n" +
             "write content-length\n" +
             "write \"some content\"\n" +
             "close response \n";
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
                 .addReadHttpMethodEvent()
                     .setExactText("get")
                     .setNextLineInfo(1, 0)
                 .done()
                 .addCloseHttpRequestCommand()
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
                 .addCloseHttpResponseCommand()
                     .setNextLineInfo(1, 0)
                 .done()
             .done()
         .done();
         // @formatter:on
         assertEquals(expected, actual);
     }
}
