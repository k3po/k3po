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

package org.kaazing.k3po.driver.internal.behavior.visitor;

import java.net.URI;

import org.junit.Test;
import org.kaazing.k3po.lang.internal.ast.AstScriptNode;
import org.kaazing.k3po.lang.internal.ast.builder.AstScriptNodeBuilder;
import org.kaazing.k3po.lang.internal.ast.value.AstLocationLiteral;

public class ValidateStreamsVisitorTest {

    @Test
    public void shouldAllowContentWithChunk() throws Exception {
        // @formatter:off
        AstScriptNode inputScript = new AstScriptNodeBuilder()
            .addAcceptStream()
                .setLocation(new AstLocationLiteral(URI.create("http://localhost:8000/somepath")))
                .addAcceptedStream()
                    .addReadConfigEvent()
                         .setType("method")
                         .setValueExactText("method", "GET")
                    .done()
                    .addReadCloseCommand()
                    .done()
                    .addWriteConfigCommand()
                        .setType("header")
                        .setName("name", "Transfer-Encoding")
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
                .setLocation(new AstLocationLiteral(URI.create("http://localhost:8000/somepath")))
                .addAcceptedStream()
                    .addReadConfigEvent()
                         .setType("method")
                         .setValueExactText("method", "get")
                    .done()
                    .addReadCloseCommand()
                    .done()
                    .addWriteConfigCommand()
                        .setType("content-length")
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
                .setLocation(new AstLocationLiteral(URI.create("http://localhost:8000/somepath")))
                .addAcceptedStream()
                    .addReadConfigEvent()
                         .setType("method")
                         .setValueExactText("method", "GET")
                    .done()
                    .addReadConfigEvent()
                         .setType("header")
                         .setValueExactText("name", "Upgrade")
                         .addMatcherExactText("websocket")
                    .done()
                    .addWriteConfigCommand()
                        .setType("status")
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
                .setLocation(new AstLocationLiteral(URI.create("http://localhost:8000/somepath")))
                .addAcceptedStream()
                    .addReadConfigEvent()
                         .setType("method")
                         .setValueExactText("method", "get")
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

    @Test(expected = IllegalStateException.class)
    public void shouldNotAllowWriteConfigAfterWriteClose() throws Exception {
        // @formatter:off
        AstScriptNode inputScript = new AstScriptNodeBuilder()
            .addConnectStream()
                .setLocation(new AstLocationLiteral(URI.create("http://localhost:8000/somepath")))
                .addOpenedEvent()
                .done()
                .addWriteCloseCommand()
                .done()
                .addWriteConfigCommand()
                     .setType("method")
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

    @Test(expected = IllegalStateException.class)
    public void shouldNotAllowReadConfigAfterReadClose() throws Exception {
        // @formatter:off
        AstScriptNode inputScript = new AstScriptNodeBuilder()
            .addAcceptStream()
                .setLocation(new AstLocationLiteral(URI.create("http://localhost:8000/somepath")))
                .addAcceptedStream()
                    .addReadConfigEvent()
                         .setType("method")
                         .setValueExactText("method", "GET")
                    .done()
                    .addReadConfigEvent()
                         .setType("header")
                         .setValueExactText("name", "Upgrade")
                         .addMatcherExactText("websocket")
                    .done()
                    .addReadCloseCommand()
                    .done()
                    .addReadConfigEvent()
                         .setType("status")
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
            setLocation(new AstLocationLiteral(URI.create("tcp://localhost:8000")))
                .addReadEvent()
                    .addExactText("exact text")
                .done()
            .done()
            .addAcceptStream()
                .setLocation(new AstLocationLiteral(URI.create("tcp://localhost:8000")))
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
