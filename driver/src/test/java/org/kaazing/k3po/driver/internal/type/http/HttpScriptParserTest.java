/**
 * Copyright 2007-2015, Kaazing Corporation. All rights reserved.
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
package org.kaazing.k3po.driver.internal.type.http;

import static org.junit.Assert.assertEquals;
import static org.kaazing.k3po.driver.internal.types.HttpTypeSystem.CONFIG_CONTENT_LENGTH;
import static org.kaazing.k3po.driver.internal.types.HttpTypeSystem.CONFIG_HEADER;
import static org.kaazing.k3po.driver.internal.types.HttpTypeSystem.CONFIG_HOST;
import static org.kaazing.k3po.driver.internal.types.HttpTypeSystem.CONFIG_METHOD;
import static org.kaazing.k3po.driver.internal.types.HttpTypeSystem.CONFIG_PARAMETER;
import static org.kaazing.k3po.driver.internal.types.HttpTypeSystem.CONFIG_REQUEST;
import static org.kaazing.k3po.driver.internal.types.HttpTypeSystem.CONFIG_STATUS;
import static org.kaazing.k3po.driver.internal.types.HttpTypeSystem.CONFIG_TRAILER;
import static org.kaazing.k3po.driver.internal.types.HttpTypeSystem.CONFIG_VERSION;
import static org.kaazing.k3po.driver.internal.types.HttpTypeSystem.OPTION_CHUNK_EXT;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.READ_CLOSED;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.READ_CONFIG;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.READ_OPTION;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.SCRIPT;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.WRITE_CLOSE;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.WRITE_CONFIG;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.WRITE_FLUSH;
import static org.kaazing.k3po.lang.internal.parser.ScriptParseStrategy.WRITE_OPTION;

import java.net.URI;

import org.junit.Test;
import org.kaazing.k3po.lang.internal.ast.AstReadClosedNode;
import org.kaazing.k3po.lang.internal.ast.AstReadConfigNode;
import org.kaazing.k3po.lang.internal.ast.AstReadOptionNode;
import org.kaazing.k3po.lang.internal.ast.AstScriptNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteCloseNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteConfigNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteFlushNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteOptionNode;
import org.kaazing.k3po.lang.internal.ast.builder.AstReadClosedNodeBuilder;
import org.kaazing.k3po.lang.internal.ast.builder.AstReadConfigNodeBuilder;
import org.kaazing.k3po.lang.internal.ast.builder.AstReadOptionNodeBuilder;
import org.kaazing.k3po.lang.internal.ast.builder.AstScriptNodeBuilder;
import org.kaazing.k3po.lang.internal.ast.builder.AstWriteCloseNodeBuilder;
import org.kaazing.k3po.lang.internal.ast.builder.AstWriteConfigNodeBuilder;
import org.kaazing.k3po.lang.internal.ast.builder.AstWriteFlushNodeBuilder;
import org.kaazing.k3po.lang.internal.ast.builder.AstWriteOptionNodeBuilder;
import org.kaazing.k3po.lang.internal.ast.value.AstLiteralURIValue;
import org.kaazing.k3po.lang.internal.ast.value.AstValue;
import org.kaazing.k3po.lang.internal.el.ExpressionContext;
import org.kaazing.k3po.lang.internal.parser.ScriptParserImpl;

public class HttpScriptParserTest {

    @Test
    public void shouldParseWriteHttpRequestOriginForm() throws Exception {

        String scriptFragment = "write http:request \"origin-form\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstWriteConfigNode actual = parser.parseWithStrategy(scriptFragment, WRITE_CONFIG);

        // @formatter:off
        AstWriteConfigNode expected = new AstWriteConfigNodeBuilder()
            .setType(CONFIG_REQUEST)
            .addValue("origin-form")
        .done();
        // @formatter:on

        assertEquals(expected, actual);
        assertEquals(1, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseWriteHttpRequestAbsoluteForm() throws Exception {

        String scriptFragment = "write http:request \"absolute-form\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstWriteConfigNode actual = parser.parseWithStrategy(scriptFragment, WRITE_CONFIG);

        // @formatter:off
        AstWriteConfigNode expected = new AstWriteConfigNodeBuilder()
            .setType(CONFIG_REQUEST)
            .addValue("absolute-form")
        .done();
        // @formatter:on

        assertEquals(expected, actual);
        assertEquals(1, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseReadHttpHeaderMissing() throws Exception {

        String scriptFragment = "read http:header \"Connection\" missing";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstReadConfigNode actual = parser.parseWithStrategy(scriptFragment, READ_CONFIG);

        // @formatter:off
        AstReadConfigNode expected = new AstReadConfigNodeBuilder()
            .setType(CONFIG_HEADER)
            .setMissing(true)
            .setMatcherExactText("name", "Connection")
        .done();
        // @formatter:on

        assertEquals(expected, actual);
        assertEquals(1, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseReadHttpHeaderExactText() throws Exception {

        String scriptFragment = "read http:header \"Host\" \"localhost:8000\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstReadConfigNode actual = parser.parseWithStrategy(scriptFragment, READ_CONFIG);

        // @formatter:off
        AstReadConfigNode expected = new AstReadConfigNodeBuilder()
            .setType(CONFIG_HEADER)
            .setMatcherExactText("name", "Host")
            .addMatcherExactText("localhost:8000")
        .done();
        // @formatter:on

        assertEquals(expected, actual);
        assertEquals(2, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseWriteHttpHeaderExactText() throws Exception {

        String scriptFragment = "write http:header \"Host\" \"localhost:8000\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstWriteConfigNode actual = parser.parseWithStrategy(scriptFragment, WRITE_CONFIG);

        // @formatter:off
        AstWriteConfigNode expected = new AstWriteConfigNodeBuilder()
            .setType(CONFIG_HEADER)
            .setValue("name", "Host")
            .addValue("localhost:8000")
        .done();
        // @formatter:on

        assertEquals(expected, actual);
        assertEquals(2, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseWriteHttpContentLength() throws Exception {

        String scriptFragment = "write http:content-length";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstWriteConfigNode actual = parser.parseWithStrategy(scriptFragment, WRITE_CONFIG);

        // @formatter:off
        AstWriteConfigNode expected = new AstWriteConfigNodeBuilder()
            .setType(CONFIG_CONTENT_LENGTH)
        .done();
        // @formatter:on

        assertEquals(expected, actual);
        assertEquals(0, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseWriteHttpHost() throws Exception {

        String scriptFragment = "write http:host";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstWriteConfigNode actual = parser.parseWithStrategy(scriptFragment, WRITE_CONFIG);

        // @formatter:off
        AstWriteConfigNode expected = new AstWriteConfigNodeBuilder()
            .setType(CONFIG_HOST)
        .done();
        // @formatter:on

        assertEquals(expected, actual);
        assertEquals(0, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseReadHttpMethodExactTest() throws Exception {

        String scriptFragment = "read http:method \"get\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstReadConfigNode actual = parser.parseWithStrategy(scriptFragment, READ_CONFIG);

        // @formatter:off
        AstReadConfigNode expected = new AstReadConfigNodeBuilder()
            .setType(CONFIG_METHOD)
            .addMatcherExactText("get")
        .done();
        // @formatter:on

        assertEquals(expected, actual);
        assertEquals(1, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseWriteHttpMethodExactTest() throws Exception {

        String scriptFragment = "write http:method \"get\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstWriteConfigNode actual = parser.parseWithStrategy(scriptFragment, WRITE_CONFIG);

        // @formatter:off
        AstWriteConfigNode expected = new AstWriteConfigNodeBuilder()
            .setType(CONFIG_METHOD)
            .addValue("get")
        .done();
        // @formatter:on

        assertEquals(expected, actual);
        assertEquals(1, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseReadHttpParameterExactTest() throws Exception {

        String scriptFragment = "read http:parameter \".kl\" \"y\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstReadConfigNode actual = parser.parseWithStrategy(scriptFragment, READ_CONFIG);

        // @formatter:off
        AstReadConfigNode expected = new AstReadConfigNodeBuilder()
            .setType(CONFIG_PARAMETER)
            .setMatcherExactText("name", ".kl")
            .addMatcherExactText("y")
        .done();
        // @formatter:on

        assertEquals(expected, actual);
        assertEquals(2, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseWriteHttpParameterExactTest() throws Exception {

        String scriptFragment = "write http:parameter \".kl\" \"y\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstWriteConfigNode actual = parser.parseWithStrategy(scriptFragment, WRITE_CONFIG);

        // @formatter:off
        AstWriteConfigNode expected = new AstWriteConfigNodeBuilder()
            .setType(CONFIG_PARAMETER)
            .setValue("name", ".kl")
            .addValue("y")
        .done();
        // @formatter:on

        assertEquals(expected, actual);
        assertEquals(2, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseReadHttpVersionExactTest() throws Exception {

        String scriptFragment = "read http:version \"Http/1.1\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstReadConfigNode actual = parser.parseWithStrategy(scriptFragment, READ_CONFIG);

        // @formatter:off
        AstReadConfigNode expected = new AstReadConfigNodeBuilder()
            .setType(CONFIG_VERSION)
            .addMatcherExactText("Http/1.1")
        .done();
        // @formatter:on

        assertEquals(expected, actual);
        assertEquals(1, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseWriteHttpVersionExactTest() throws Exception {

        String scriptFragment = "write http:version \"Http/1.1\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstWriteConfigNode actual = parser.parseWithStrategy(scriptFragment, WRITE_CONFIG);

        // @formatter:off
        AstWriteConfigNode expected = new AstWriteConfigNodeBuilder()
            .setType(CONFIG_VERSION)
            .addValue("Http/1.1")
        .done();
        // @formatter:on

        assertEquals(expected, actual);
        assertEquals(1, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseReadHttpStatusExactTest() throws Exception {

        String scriptFragment = "read http:status \"403\" \"Unauthorized\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstReadConfigNode actual = parser.parseWithStrategy(scriptFragment, READ_CONFIG);

        // @formatter:off
        AstReadConfigNode expected = new AstReadConfigNodeBuilder()
            .setType(CONFIG_STATUS)
            .setMatcherExactText("code", "403")
            .setMatcherExactText("reason", "Unauthorized")
        .done();
        // @formatter:on

        assertEquals(expected, actual);
        assertEquals(2, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseWriteHttpStatusExactTest() throws Exception {

        String scriptFragment = "write http:status \"403\" \"Unauthorized\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstWriteConfigNode actual = parser.parseWithStrategy(scriptFragment, WRITE_CONFIG);

        // @formatter:off
        AstWriteConfigNode expected = new AstWriteConfigNodeBuilder()
            .setType(CONFIG_STATUS)
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
    public void shouldParseWriteOptionChunkExtension() throws Exception {

        String scriptFragment = "write option http:chunkExtension \"chunkextension\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstWriteOptionNode actual = parser.parseWithStrategy(scriptFragment, WRITE_OPTION);

        // @formatter:off
        AstWriteOptionNode expected = new AstWriteOptionNodeBuilder()
                .setOptionType(OPTION_CHUNK_EXT)
                .setOptionName("http:chunkExtension")
                .setOptionValue("chunkextension")
            .done();
        // @formatter:on

        assertEquals(expected, actual);
        assertEquals(1, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseReadOptionChunkExtension() throws Exception {

        String scriptFragment = "read option http:chunkExtension \"chunkextension\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstReadOptionNode actual = parser.parseWithStrategy(scriptFragment, READ_OPTION);

        // @formatter:off
        AstReadOptionNode expected = new AstReadOptionNodeBuilder()
                .setOptionType(OPTION_CHUNK_EXT)
                .setOptionName("http:chunkExtension")
                .setOptionValue("chunkextension")
                .done();
        // @formatter:on

        assertEquals(expected, actual);
        assertEquals(1, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseWriteChunkTrailer() throws Exception {

        String scriptFragment = "write http:trailer \"checksum\" \"value\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstWriteConfigNode actual = parser.parseWithStrategy(scriptFragment, WRITE_CONFIG);

        // @formatter:off
        AstWriteConfigNode expected = new AstWriteConfigNodeBuilder()
                .setType(CONFIG_TRAILER)
                .setValue("name", "checksum")
                .addValue("value")
                .done();
        // @formatter:on

        assertEquals(expected, actual);
        assertEquals(2, actual.getRegionInfo().children.size());
    }

    @Test
    public void shouldParseReadChunkTrailer() throws Exception {

        String scriptFragment = "read http:trailer \"checksum\" \"value\"";

        ScriptParserImpl parser = new ScriptParserImpl();
        AstReadConfigNode actual = parser.parseWithStrategy(scriptFragment, READ_CONFIG);

        // @formatter:off
        AstReadConfigNode expected = new AstReadConfigNodeBuilder()
            .setType(CONFIG_TRAILER)
            .setMatcherExactText("name", "checksum")
            .addMatcherExactText("value")
        .done();
        // @formatter:on

        assertEquals(expected, actual);
        assertEquals(2, actual.getRegionInfo().children.size());
    }

     @Test
     public void shouldParseHttpReadRequestWriteResponseScript() throws Exception {
         // @formatter:off
         String script =
             "accept 'http://somehost:8000/path'\n" +
             "accepted\n" +
             "read http:method \"get\"\n" +
             "read http:parameter \".kl\" \"y\"\n" +
             "read http:header \"Upgrade\" \"websocket\"\n" +
             "read [0x82]\n" +
             "read closed\n" +
             "write http:status \"101\" \"Switching Protocols\"\n" +
             "write http:header \"upgrade\" \"websocket\"\n" +
             "write close \n" +
             "close\n" +
             "closed\n";
         // @formatter:on

         ScriptParserImpl parser = new ScriptParserImpl();
         AstScriptNode actual = parser.parseWithStrategy(script, SCRIPT);

         // @formatter:off
         AstScriptNode expected = new AstScriptNodeBuilder()
             .addAcceptStream()
                 .setLocation(new AstLiteralURIValue(URI.create("http://somehost:8000/path")))
             .done()
             .addAcceptedStream()
                 .addReadConfigEvent()
                     .setType(CONFIG_METHOD)
                     .addMatcherExactText("get")
                 .done()
                 .addReadConfigEvent()
                     .setType(CONFIG_PARAMETER)
                     .setMatcherExactText("name", ".kl")
                     .addMatcherExactText("y")
                 .done()
                 .addReadConfigEvent()
                     .setType(CONFIG_HEADER)
                     .setMatcherExactText("name", "Upgrade")
                     .addMatcherExactText("websocket")
                 .done()
                 .addReadEvent()
                     .addExactBytes(new byte[] {(byte) 0x82}, new ExpressionContext())
                 .done()
                 .addReadCloseCommand()
                 .done()
                 .addWriteConfigCommand()
                     .setType(CONFIG_STATUS)
                     .setValue("code", "101")
                     .setValue("reason", "Switching Protocols")
                 .done()
                 .addWriteConfigCommand()
                     .setType(CONFIG_HEADER)
                     .setValue("name", "upgrade")
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
             "connect 'http://somehost:8000/path'\n" +
             "connected\n" +
             "write http:method \"get\"\n" +
             "write http:parameter \".kl\" \"y\"\n" +
             "write http:header \"Upgrade\" \"websocket\"\n" +
             "write close\n" +
             "read http:status \"101\" \"Switching Protocols\"\n" +
             "read http:header \"upgrade\" \"websocket\"\n" +
             "read closed \n" +
             "write [0x82]\n" +
             "close\n" +
             "closed\n";
         // @formatter:on

         ScriptParserImpl parser = new ScriptParserImpl();
         AstScriptNode actual = parser.parseWithStrategy(script, SCRIPT);
         AstValue<URI> location = new AstLiteralURIValue(URI.create("http://somehost:8000/path"));

         // @formatter:off
         AstScriptNode expected = new AstScriptNodeBuilder()
             .addConnectStream()
                 .setLocation(location)
                 .addConnectedEvent()
                 .done()
                 .addWriteConfigCommand()
                     .setType(CONFIG_METHOD)
                     .addValue("get")
                 .done()
                 .addWriteConfigCommand()
                     .setType(CONFIG_PARAMETER)
                     .setValue("name", ".kl")
                     .addValue("y")
                 .done()
                 .addWriteConfigCommand()
                     .setType(CONFIG_HEADER)
                     .setValue("name", "Upgrade")
                     .addValue("websocket")
                 .done()
                 .addWriteCloseCommand()
                 .done()
                 .addReadConfigEvent()
                     .setType(CONFIG_STATUS)
                     .setMatcherExactText("code", "101")
                     .setMatcherExactText("reason", "Switching Protocols")
                 .done()
                 .addReadConfigEvent()
                     .setType(CONFIG_HEADER)
                     .setMatcherExactText("name", "upgrade")
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
             "accept 'http://somehost:8000/path'\n" +
             "accepted\n" +
             "read http:method \"get\"\n" +
             "read closed\n" +
             "write http:status \"200\" \"OK\"\n" +
             "write http:content-length\n" +
             "write \"some content\"\n" +
             "write close \n";
         // @formatter:on

         ScriptParserImpl parser = new ScriptParserImpl();
         AstScriptNode actual = parser.parseWithStrategy(script, SCRIPT);

         // @formatter:off
         AstScriptNode expected = new AstScriptNodeBuilder()
             .addAcceptStream()
                 .setLocation(new AstLiteralURIValue(URI.create("http://somehost:8000/path")))
             .done()
             .addAcceptedStream()
                 .addReadConfigEvent()
                     .setType(CONFIG_METHOD)
                     .addMatcherExactText("get")
                 .done()
                 .addReadCloseCommand()
                 .done()
                 .addWriteConfigCommand()
                     .setType(CONFIG_STATUS)
                     .setValue("code", "200")
                     .setValue("reason", "OK")
                 .done()
                 .addWriteConfigCommand()
                     .setType(CONFIG_CONTENT_LENGTH)
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
