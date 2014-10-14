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

package org.kaazing.robot.driver.behavior.visitor;

import java.net.URI;

import org.junit.Test;
import org.kaazing.robot.lang.ast.AstScriptNode;
import org.kaazing.robot.lang.ast.builder.AstScriptNodeBuilder;

public class InjectHttpStreamsVisitorTest {

    @Test
    public void shouldAllowContentWithChunk() throws Exception {
        // @formatter:off
        AstScriptNode inputScript = new AstScriptNodeBuilder()
            .addAcceptStream()
                .setNextLineInfo(1, 0)
                .setLocation(URI.create("http://localhost:8000/somepath"))
                .addAcceptedStream()
                    .addReadConfigEvent()
                         .setType("method")
                         .setValueExactText("method", "GET")
                         .setNextLineInfo(1, 0)
                    .done()
                    .addReadCloseCommand()
                        .setNextLineInfo(1, 0)
                    .done()
                    .addWriteConfigCommand()
                        .setType("header")
                        .setName("name", "Transfer-Encoding")
                        .addValue("Chunked")
                        .setNextLineInfo(1, 0)
                    .done()
                    .addWriteCommand()
                        .setNextLineInfo(1, 0)
                        .addExactText("Some Content")
                    .done()
                    .addWriteCloseCommand()
                        .setNextLineInfo(1, 0)
                    .done()
                .done()
            .done()
        .done();
        // @formatter:on

        InjectHttpStreamsVisitor injectEvents = new InjectHttpStreamsVisitor();
        inputScript.accept(injectEvents, new InjectHttpStreamsVisitor.State());
    }

    @Test
    public void shouldAllowContentWithContentLength() throws Exception {
        // @formatter:off
        AstScriptNode inputScript = new AstScriptNodeBuilder()
            .addAcceptStream()
                .setNextLineInfo(1, 0)
                .setLocation(URI.create("http://localhost:8000/somepath"))
                .addAcceptedStream()
                    .addReadConfigEvent()
                         .setType("method")
                         .setValueExactText("method", "get")
                         .setNextLineInfo(1, 0)
                    .done()
                    .addReadCloseCommand()
                        .setNextLineInfo(1, 0)
                    .done()
                    .addWriteConfigCommand()
                        .setType("content-length")
                        .setNextLineInfo(1, 0)
                    .done()
                    .addWriteCommand()
                        .setNextLineInfo(1, 0)
                        .addExactText("Some Content")
                    .done()
                    .addWriteCloseCommand()
                        .setNextLineInfo(1, 0)
                    .done()
                .done()
            .done()
        .done();
        // @formatter:on

        InjectHttpStreamsVisitor injectEvents = new InjectHttpStreamsVisitor();
        inputScript.accept(injectEvents, new InjectHttpStreamsVisitor.State());
    }

    @Test
    public void shouldAllowWriteAfterRequestResponseSwitchingProtocols() throws Exception {
        // @formatter:off
        AstScriptNode inputScript = new AstScriptNodeBuilder()
            .addAcceptStream()
                .setNextLineInfo(1, 0)
                .setLocation(URI.create("http://localhost:8000/somepath"))
                .addAcceptedStream()
                    .addReadConfigEvent()
                         .setType("method")
                         .setValueExactText("method", "GET")
                         .setNextLineInfo(1, 0)
                    .done()
                    .addReadConfigEvent()
                         .setType("header")
                         .setValueExactText("name", "Upgrade")
                         .addMatcherExactText("websocket")
                         .setNextLineInfo(1, 0)
                    .done()
                    .addWriteConfigCommand()
                        .setType("status")
                        .setValue("code", "101")
                        .setValue("reason", "Switching Protocols")
                    .done()
                    .addWriteCommand()
                        .setNextLineInfo(1, 0)
                        .addExactText("some websocket data")
                    .done()
                    .addCloseCommand()
                        .setNextLineInfo(1, 0)
                    .done()
                    .addClosedEvent()
                        .setNextLineInfo(1, 0)
                    .done()
                .done()
            .done()
        .done();
        // @formatter:on

        InjectHttpStreamsVisitor injectEvents = new InjectHttpStreamsVisitor();
        inputScript.accept(injectEvents, new InjectHttpStreamsVisitor.State());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotAllowWriteAfterRequestResponseWithoutSwitchingProtocols() throws Exception {
        // @formatter:off
        AstScriptNode inputScript = new AstScriptNodeBuilder()
            .addAcceptStream()
                .setNextLineInfo(1, 0)
                .setLocation(URI.create("http://localhost:8000/somepath"))
                .addAcceptedStream()
                    .addReadConfigEvent()
                         .setType("method")
                         .setValueExactText("method", "get")
                         .setNextLineInfo(1, 0)
                    .done()
                    .addReadCloseCommand()
                        .setNextLineInfo(1, 0)
                    .done()
                    .addWriteCloseCommand()
                        .setNextLineInfo(1, 0)
                    .done()
                    .addWriteCommand()
                        .setNextLineInfo(1, 0)
                        .addExactText("some websocket data")
                    .done()
                    .addCloseCommand()
                        .setNextLineInfo(1, 0)
                    .done()
                    .addClosedEvent()
                        .setNextLineInfo(1, 0)
                    .done()
                .done()
            .done()
        .done();
        // @formatter:on

        InjectHttpStreamsVisitor injectEvents = new InjectHttpStreamsVisitor();
        inputScript.accept(injectEvents, new InjectHttpStreamsVisitor.State());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotAllowWriteConfigAfterWriteClose() throws Exception {
        // @formatter:off
        AstScriptNode inputScript = new AstScriptNodeBuilder()
            .addConnectStream()
                .setNextLineInfo(1, 0)
                .setLocation(URI.create("http://localhost:8000/somepath"))
                .addOpenedEvent()
                    .setNextLineInfo(1, 0)
                .done()
                .addWriteCloseCommand()
                    .setNextLineInfo(1, 0)
                .done()
                .addWriteConfigCommand()
                     .setType("method")
                     .addValue("upgrade")
                     .setNextLineInfo(1, 0)
                .done()
                .addReadCloseCommand()
                    .setNextLineInfo(1, 0)
                .done()
            .done()
        .done();
        // @formatter:on

        InjectHttpStreamsVisitor injectEvents = new InjectHttpStreamsVisitor();
        inputScript.accept(injectEvents, new InjectHttpStreamsVisitor.State());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotAllowReadConfigAfterReadClose() throws Exception {
        // @formatter:off
        AstScriptNode inputScript = new AstScriptNodeBuilder()
            .addAcceptStream()
                .setNextLineInfo(1, 0)
                .setLocation(URI.create("http://localhost:8000/somepath"))
                .addAcceptedStream()
                    .addReadConfigEvent()
                         .setType("method")
                         .setValueExactText("method", "GET")
                         .setNextLineInfo(1, 0)
                    .done()
                    .addReadConfigEvent()
                         .setType("header")
                         .setValueExactText("name", "Upgrade")
                         .addMatcherExactText("websocket")
                         .setNextLineInfo(1, 0)
                    .done()
                    .addReadCloseCommand()
                        .setNextLineInfo(1, 0)
                    .done()
                    .addReadConfigEvent()
                         .setType("status")
                         .setMatcherExactText("code", "101")
                         .setMatcherExactText("reason", "Switching Protocols")
                    .done()
                    .addWriteCloseCommand()
                        .setNextLineInfo(1, 0)
                    .done()
                .done()
            .done()
        .done();
        // @formatter:on

        InjectHttpStreamsVisitor injectEvents = new InjectHttpStreamsVisitor();
        inputScript.accept(injectEvents, new InjectHttpStreamsVisitor.State());
    }

}
