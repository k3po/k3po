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
package org.kaazing.k3po.driver.internal.behavior.visitor;

import static org.kaazing.k3po.driver.internal.types.HttpTypeSystem.CONFIG_CONTENT_LENGTH;
import static org.kaazing.k3po.driver.internal.types.HttpTypeSystem.CONFIG_HEADER;
import static org.kaazing.k3po.driver.internal.types.HttpTypeSystem.CONFIG_METHOD;
import static org.kaazing.k3po.driver.internal.types.HttpTypeSystem.CONFIG_STATUS;

import java.net.URI;

import org.junit.Test;
import org.kaazing.k3po.lang.internal.ast.AstScriptNode;
import org.kaazing.k3po.lang.internal.ast.builder.AstScriptNodeBuilder;
import org.kaazing.k3po.lang.internal.ast.value.AstLiteralURIValue;

public class ValidateStreamsVisitorTest {

    @Test
    public void shouldAllowContentWithChunk() throws Exception {
        // @formatter:off
        AstScriptNode inputScript = new AstScriptNodeBuilder()
            .addAcceptStream()
                .setLocation(new AstLiteralURIValue(URI.create("http://localhost:8000/somepath")))
                .addAcceptedStream()
                    .addReadConfigEvent()
                         .setType(CONFIG_METHOD)
                         .addMatcherExactText("GET")
                    .done()
                    .addReadCloseCommand()
                    .done()
                    .addWriteConfigCommand()
                        .setType(CONFIG_HEADER)
                        .setValue("name", "Transfer-Encoding")
                        .addValue("Chunked")
                    .done()
                    .addWriteCommand()
                        .addExactText("Some Content")
                    .done()
                    .addWriteCloseCommand()
                    .done()
                .done()
            .done()
        .done();
        // @formatter:on

        ValidateStreamsVisitor injectEvents = new ValidateStreamsVisitor();
        inputScript.accept(injectEvents, new ValidateStreamsVisitor.State());
    }

    @Test
    public void shouldAllowContentWithContentLength() throws Exception {
        // @formatter:off
        AstScriptNode inputScript = new AstScriptNodeBuilder()
            .addAcceptStream()
                .setLocation(new AstLiteralURIValue(URI.create("http://localhost:8000/somepath")))
                .addAcceptedStream()
                    .addReadConfigEvent()
                         .setType(CONFIG_METHOD)
                         .addMatcherExactText("get")
                    .done()
                    .addReadCloseCommand()
                    .done()
                    .addWriteConfigCommand()
                        .setType(CONFIG_CONTENT_LENGTH)
                    .done()
                    .addWriteCommand()
                        .addExactText("Some Content")
                    .done()
                    .addWriteCloseCommand()
                    .done()
                .done()
            .done()
        .done();
        // @formatter:on

        ValidateStreamsVisitor injectEvents = new ValidateStreamsVisitor();
        inputScript.accept(injectEvents, new ValidateStreamsVisitor.State());
    }

    @Test
    public void shouldAllowWriteAfterRequestResponseSwitchingProtocols() throws Exception {
        // @formatter:off
        AstScriptNode inputScript = new AstScriptNodeBuilder()
            .addAcceptStream()
                .setLocation(new AstLiteralURIValue(URI.create("http://localhost:8000/somepath")))
                .addAcceptedStream()
                    .addReadConfigEvent()
                         .setType(CONFIG_METHOD)
                         .addMatcherExactText("GET")
                    .done()
                    .addReadConfigEvent()
                         .setType(CONFIG_HEADER)
                         .setMatcherExactText("name", "Upgrade")
                         .addMatcherExactText("websocket")
                    .done()
                    .addWriteConfigCommand()
                        .setType(CONFIG_STATUS)
                        .setValue("code", "101")
                        .setValue("reason", "Switching Protocols")
                    .done()
                    .addWriteCommand()
                        .addExactText("some websocket data")
                    .done()
                    .addCloseCommand()
                    .done()
                    .addClosedEvent()
                    .done()
                .done()
            .done()
        .done();
        // @formatter:on

        ValidateStreamsVisitor injectEvents = new ValidateStreamsVisitor();
        inputScript.accept(injectEvents, new ValidateStreamsVisitor.State());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotAllowWriteAfterRequestResponseWithoutSwitchingProtocols() throws Exception {
        // @formatter:off
        AstScriptNode inputScript = new AstScriptNodeBuilder()
            .addAcceptStream()
                .setLocation(new AstLiteralURIValue(URI.create("http://localhost:8000/somepath")))
                .addAcceptedStream()
                    .addReadConfigEvent()
                         .setType(CONFIG_METHOD)
                         .addMatcherExactText("get")
                    .done()
                    .addReadCloseCommand()
                    .done()
                    .addWriteCloseCommand()
                    .done()
                    .addWriteCommand()
                        .addExactText("some websocket data")
                    .done()
                    .addCloseCommand()
                    .done()
                    .addClosedEvent()
                    .done()
                .done()
            .done()
        .done();
        // @formatter:on

        ValidateStreamsVisitor injectEvents = new ValidateStreamsVisitor();
        inputScript.accept(injectEvents, new ValidateStreamsVisitor.State());
    }

    @Test
    public void shouldAllowWriteConfigAfterWriteClose() throws Exception {
        // @formatter:off
        AstScriptNode inputScript = new AstScriptNodeBuilder()
            .addConnectStream()
                .setLocation(new AstLiteralURIValue(URI.create("http://localhost:8000/somepath")))
                .addOpenedEvent()
                .done()
                .addWriteCloseCommand()
                .done()
                .addWriteConfigCommand()
                     .setType(CONFIG_METHOD)
                     .addValue("upgrade")
                .done()
                .addReadCloseCommand()
                .done()
            .done()
        .done();
        // @formatter:on

        ValidateStreamsVisitor injectEvents = new ValidateStreamsVisitor();
        inputScript.accept(injectEvents, new ValidateStreamsVisitor.State());
    }

    @Test
    public void shouldAllowReadConfigAfterReadClose() throws Exception {
        // @formatter:off
        AstScriptNode inputScript = new AstScriptNodeBuilder()
            .addAcceptStream()
                .setLocation(new AstLiteralURIValue(URI.create("http://localhost:8000/somepath")))
                .addAcceptedStream()
                    .addReadConfigEvent()
                         .setType(CONFIG_METHOD)
                         .addMatcherExactText("GET")
                    .done()
                    .addReadConfigEvent()
                         .setType(CONFIG_HEADER)
                         .setMatcherExactText("name", "Upgrade")
                         .addMatcherExactText("websocket")
                    .done()
                    .addReadCloseCommand()
                    .done()
                    .addReadConfigEvent()
                         .setType(CONFIG_STATUS)
                         .setMatcherExactText("code", "101")
                         .setMatcherExactText("reason", "Switching Protocols")
                    .done()
                    .addWriteCloseCommand()
                    .done()
                .done()
            .done()
        .done();
        // @formatter:on

        ValidateStreamsVisitor injectEvents = new ValidateStreamsVisitor();
        inputScript.accept(injectEvents, new ValidateStreamsVisitor.State());
    }

    @Test
    public void shouldNotThrowErrorsOnNonHttpStreams() throws Exception {
        // @formatter:off
        AstScriptNode inputScript = new AstScriptNodeBuilder()
        .addConnectStream().
            setLocation(new AstLiteralURIValue(URI.create("tcp://localhost:8000")))
                .addReadEvent()
                    .addExactText("exact text")
                .done()
            .done()
            .addAcceptStream()
                .setLocation(new AstLiteralURIValue(URI.create("tcp://localhost:8000")))
                .addAcceptedStream()
                    .addReadEvent()
                        .addExactText("exact text")
                    .done()
                .done()
            .done()
        .done();
        // @formatter:on

        ValidateStreamsVisitor injectEvents = new ValidateStreamsVisitor();
        inputScript.accept(injectEvents, new ValidateStreamsVisitor.State());
    }

}
