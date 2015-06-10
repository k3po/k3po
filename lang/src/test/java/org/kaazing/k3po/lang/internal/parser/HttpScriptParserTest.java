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

package org.kaazing.k3po.lang.internal.parser;

import static org.junit.Assert.assertEquals;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.READ_CLOSED;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.READ_HTTP_HEADER;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.READ_HTTP_METHOD;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.READ_HTTP_PARAMETER;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.READ_HTTP_STATUS;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.READ_HTTP_VERSION;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.SCRIPT;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.WRITE_CLOSE;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.WRITE_FLUSH;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.WRITE_HTTP_CONTENT_LENGTH;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.WRITE_HTTP_HEADER;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.WRITE_HTTP_HOST;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.WRITE_HTTP_METHOD;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.WRITE_HTTP_PARAMETER;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.WRITE_HTTP_REQUEST;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.WRITE_HTTP_STATUS;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.WRITE_HTTP_VERSION;
import static org.kaazing.k3po.lang.internal.test.junit.Assert.assertEquals;

import java.net.URI;

import org.junit.Test;
import org.kaazing.k3po.lang.internal.ast.AstReadClosedNode;
import org.kaazing.k3po.lang.internal.ast.AstReadConfigNode;
import org.kaazing.k3po.lang.internal.ast.AstScriptNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteCloseNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteConfigNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteFlushNode;
import org.kaazing.k3po.lang.internal.ast.builder.AstReadClosedNodeBuilder;
import org.kaazing.k3po.lang.internal.ast.builder.AstReadConfigNodeBuilder;
import org.kaazing.k3po.lang.internal.ast.builder.AstScriptNodeBuilder;
import org.kaazing.k3po.lang.internal.ast.builder.AstWriteCloseNodeBuilder;
import org.kaazing.k3po.lang.internal.ast.builder.AstWriteConfigNodeBuilder;
import org.kaazing.k3po.lang.internal.ast.builder.AstWriteFlushNodeBuilder;
import org.kaazing.k3po.lang.internal.ast.value.AstLocation;
import org.kaazing.k3po.lang.internal.ast.value.AstLocationLiteral;
import org.kaazing.k3po.lang.internal.el.ExpressionContext;

public class HttpScriptParserTest {

    @Test
    public void shouldParseWriteHttpRequestOriginForm() throws Exception {

        String scriptFragment = "write request \"origin-form\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstWriteConfigNode actual = parser.parseWithStrategy(scriptFragment, WRITE_HTTP_REQUEST);

        // @formatter:off
        AstWriteConfigNode expected = new AstWriteConfigNodeBuilder()
            .setType("request")
            .setValue("form", "origin-form")
        .done();
        // @formatter:on

        assertEquals(expected, actual);
        assertEquals(1, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseWriteHttpRequestAbsoluteForm() throws Exception {

        String scriptFragment = "write request \"absolute-form\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstWriteConfigNode actual = parser.parseWithStrategy(scriptFragment, WRITE_HTTP_REQUEST);

        // @formatter:off
        AstWriteConfigNode expected = new AstWriteConfigNodeBuilder()
            .setType("request")
            .setValue("form", "absolute-form")
        .done();
        // @formatter:on

        assertEquals(expected, actual);
        assertEquals(1, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseReadHttpHeaderMissing() throws Exception {

        String scriptFragment = "read header \"Connection\" missing";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstReadConfigNode actual = parser.parseWithStrategy(scriptFragment, READ_HTTP_HEADER);

        // @formatter:off
        AstReadConfigNode expected = new AstReadConfigNodeBuilder()
            .setType("header missing")
            .setValueExactText("name", "Connection")
        .done();
        // @formatter:on

        assertEquals(expected, actual);
        assertEquals(1, actual.getRegionInfo().children.size());
    }

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
        .done();
        // @formatter:on

        assertEquals(expected, actual);
        assertEquals(2, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseWriteHttpHeaderExactText() throws Exception {

        String scriptFragment = "write header \"Host\" \"localhost:8000\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstWriteConfigNode actual = parser.parseWithStrategy(scriptFragment, WRITE_HTTP_HEADER);

        // @formatter:off
        AstWriteConfigNode expected = new AstWriteConfigNodeBuilder()
            .setType("header")
            .setName("name", "Host")
            .addValue("localhost:8000")
        .done();
        // @formatter:on

        assertEquals(expected, actual);
        assertEquals(2, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseWriteHttpContentLength() throws Exception {

        String scriptFragment = "write header content-length";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstWriteConfigNode actual = parser.parseWithStrategy(scriptFragment, WRITE_HTTP_CONTENT_LENGTH);

        // @formatter:off
        AstWriteConfigNode expected = new AstWriteConfigNodeBuilder()
            .setType("content-length")
        .done();
        // @formatter:on

        assertEquals(expected, actual);
        assertEquals(0, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseWriteHttpHost() throws Exception {

        String scriptFragment = "write header host";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstWriteConfigNode actual = parser.parseWithStrategy(scriptFragment, WRITE_HTTP_HOST);

        // @formatter:off
        AstWriteConfigNode expected = new AstWriteConfigNodeBuilder()
            .setType("host")
        .done();
        // @formatter:on

        assertEquals(expected, actual);
        assertEquals(0, actual.getRegionInfo().children.size());
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
        .done();
        // @formatter:on

        assertEquals(expected, actual);
        assertEquals(1, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseWriteHttpMethodExactTest() throws Exception {

        String scriptFragment = "write method \"get\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstWriteConfigNode actual = parser.parseWithStrategy(scriptFragment, WRITE_HTTP_METHOD);

        // @formatter:off
        AstWriteConfigNode expected = new AstWriteConfigNodeBuilder()
            .setType("method")
            .addValue("get")
        .done();
        // @formatter:on

        assertEquals(expected, actual);
        // Zero list region info because of WriteConfigNode parsing but perhaps we
        // should change that
        assertEquals(0, actual.getRegionInfo().children.size());
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
        .done();
        // @formatter:on

        assertEquals(expected, actual);
        assertEquals(2, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseWriteHttpParameterExactTest() throws Exception {

        String scriptFragment = "write parameter \".kl\" \"y\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstWriteConfigNode actual = parser.parseWithStrategy(scriptFragment, WRITE_HTTP_PARAMETER);

        // @formatter:off
        AstWriteConfigNode expected = new AstWriteConfigNodeBuilder()
            .setType("parameter")
            .setName("name", ".kl")
            .addValue("y")
        .done();
        // @formatter:on

        assertEquals(expected, actual);
        assertEquals(2, actual.getRegionInfo().children.size());
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
        .done();
        // @formatter:on

        assertEquals(expected, actual);
        assertEquals(1, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseWriteHttpVersionExactTest() throws Exception {

        String scriptFragment = "write version \"Http/1.1\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstWriteConfigNode actual = parser.parseWithStrategy(scriptFragment, WRITE_HTTP_VERSION);

        // @formatter:off
        AstWriteConfigNode expected = new AstWriteConfigNodeBuilder()
            .setType("version")
            .addValue("Http/1.1")
        .done();
        // @formatter:on

        assertEquals(expected, actual);
        // Zero list region info because of WriteConfigNode parsing but perhaps we
        // should change that
        assertEquals(0, actual.getRegionInfo().children.size());
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
        .done();
        // @formatter:on

        assertEquals(expected, actual);
        assertEquals(2, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseWriteHttpStatusExactTest() throws Exception {

        String scriptFragment = "write status \"403\" \"Unauthorized\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstWriteConfigNode actual = parser.parseWithStrategy(scriptFragment, WRITE_HTTP_STATUS);

        // @formatter:off
        AstWriteConfigNode expected = new AstWriteConfigNodeBuilder()
            .setType("status")
            .setValue("code", "403")
            .setValue("reason", "Unauthorized")
        .done();
        // @formatter:on

        assertEquals(expected, actual);
        assertEquals(2, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseReadClose() throws Exception {

        String scriptFragment = "read closed";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstReadClosedNode actual = parser.parseWithStrategy(scriptFragment, READ_CLOSED);

        // @formatter:off
        AstReadClosedNode expected = new AstReadClosedNodeBuilder()
        .done();
        // @formatter:on

        assertEquals(expected, actual);
        assertEquals(0, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseWriteFlush() throws Exception {

        String scriptFragment = "write flush";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstWriteFlushNode actual = parser.parseWithStrategy(scriptFragment, WRITE_FLUSH);

        // @formatter:off
        AstWriteFlushNode expected = new AstWriteFlushNodeBuilder()
        .done();
        // @formatter:on

        assertEquals(expected, actual);
        assertEquals(0, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseWriteClose() throws Exception {

        String scriptFragment = "write close";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstWriteCloseNode actual = parser.parseWithStrategy(scriptFragment, WRITE_CLOSE);

        // @formatter:off
        AstWriteCloseNode expected = new AstWriteCloseNodeBuilder()
        .done();
        // @formatter:on

        assertEquals(expected, actual);
        assertEquals(0, actual.getRegionInfo().children.size());
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
                 .setLocation(new AstLocationLiteral(URI.create("http://somehost:8000/path")))
             .done()
             .addAcceptedStream()
                 .addReadConfigEvent()
                     .setType("method")
                     .setMatcherExactText("name", "get")
                 .done()
                 .addReadConfigEvent()
                     .setType("parameter")
                     .setValueExactText("name", ".kl")
                     .addMatcherExactText("y")
                 .done()
                 .addReadConfigEvent()
                     .setType("header")
                     .setValueExactText("name", "Upgrade")
                     .addMatcherExactText("websocket")
                 .done()
                 .addReadEvent()
                     .addExactBytes(new byte[] {(byte) 0x82}, new ExpressionContext())
                 .done()
                 .addReadCloseCommand()
                 .done()
                 .addWriteConfigCommand()
                     .setType("status")
                     .setValue("code", "101")
                     .setValue("reason", "Switching Protocols")
                 .done()
                 .addWriteConfigCommand()
                     .setType("header")
                     .setName("name", "upgrade")
                     .addValue("websocket")
                 .done()
                 .addWriteCloseCommand()
                 .done()
                 .addCloseCommand()
                 .done()
                 .addClosedEvent()
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
         AstLocation location = new AstLocationLiteral(URI.create("http://somehost:8000/path"));

         // @formatter:off
         AstScriptNode expected = new AstScriptNodeBuilder()
             .addConnectStream()
                 .setLocation(location)
                 .addConnectedEvent()
                 .done()
                 .addWriteConfigCommand()
                     .setType("method")
                     .addValue("get")
                 .done()
                 .addWriteConfigCommand()
                     .setType("parameter")
                     .setName("name", ".kl")
                     .addValue("y")
                 .done()
                 .addWriteConfigCommand()
                     .setType("header")
                     .setName("name", "Upgrade")
                     .addValue("websocket")
                 .done()
                 .addWriteCloseCommand()
                 .done()
                 .addReadConfigEvent()
                     .setType("status")
                     .setMatcherExactText("code", "101")
                     .setMatcherExactText("reason", "Switching Protocols")
                 .done()
                 .addReadConfigEvent()
                     .setType("header")
                     .setValueExactText("name", "upgrade")
                     .addMatcherExactText("websocket")
                 .done()
                 .addReadCloseCommand()
                 .done()
                 .addWriteCommand()
                     .addExactBytes(new byte[] {(byte) 0x82})
                 .done()
                 .addCloseCommand()
                 .done()
                 .addClosedEvent()
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
             "write header content-length\n" +
             "write \"some content\"\n" +
             "write close \n";
         // @formatter:on

         ScriptParserImpl parser = new ScriptParserImpl();
         AstScriptNode actual = parser.parseWithStrategy(script, SCRIPT);

         // @formatter:off
         AstScriptNode expected = new AstScriptNodeBuilder()
             .addAcceptStream()
                 .setLocation(new AstLocationLiteral(URI.create("http://somehost:8000/path")))
             .done()
             .addAcceptedStream()
                 .addReadConfigEvent()
                     .setType("method")
                     .setMatcherExactText("name", "get")
                 .done()
                 .addReadCloseCommand()
                 .done()
                 .addWriteConfigCommand()
                     .setType("status")
                     .setValue("code", "200")
                     .setValue("reason", "OK")
                 .done()
                 .addWriteConfigCommand()
                     .setType("content-length")
                 .done()
                 .addWriteCommand()
                     .addExactText("some content")
                 .done()
                 .addWriteCloseCommand()
                 .done()
             .done()
         .done();
         // @formatter:on
         assertEquals(expected, actual);
     }
}
