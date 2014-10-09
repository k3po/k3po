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
                    .addReadHttpMethodEvent()
                         .setExactText("GET")
                         .setNextLineInfo(1, 0)
                    .done()
                    .addReadCloseCommand()
                        .setNextLineInfo(1, 0)
                    .done()
                    .addWriteHttpHeaderCommand()
                        .setNextLineInfo(1, 0)
                        .setNameExactText("Transfer-Encoding")
                        .setValueExactText("Chunked")
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
                    .addReadHttpMethodEvent()
                         .setExactText("get")
                         .setNextLineInfo(1, 0)
                    .done()
                    .addReadCloseCommand()
                        .setNextLineInfo(1, 0)
                    .done()
                    .addWriteHttpContentLengthCommand()
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
                    .addReadHttpMethodEvent()
                         .setExactText("GET")
                         .setNextLineInfo(1, 0)
                    .done()
                    .addReadHttpHeaderEvent()
                         .setNameExactText("Upgrade")
                         .setValueExactText("websocket")
                         .setNextLineInfo(1, 0)
                    .done()
                    .addWriteHttpStatusCommand()
                        .setCodeExactText("101")
                        .setReasonExactText("Switching Protocols")
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
                    .addReadHttpMethodEvent()
                         .setExactText("get")
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
    public void shouldNotAllowConnectWriteHttpStatus() throws Exception {
        // @formatter:off
        AstScriptNode inputScript = new AstScriptNodeBuilder()
            .addConnectStream()
                .setNextLineInfo(1, 0)
                .setLocation(URI.create("http://localhost:8000/somepath"))
                .addOpenedEvent()
                    .setNextLineInfo(1, 0)
                .done()
                .addWriteHttpMethodCommand()
                     .setExactText("GET")
                     .setNextLineInfo(1, 0)
                .done()
                .addWriteHttpHeaderCommand()
                     .setNameExactText("Upgrade")
                     .setValueExactText("websocket")
                     .setNextLineInfo(1, 0)
                .done()
                .addWriteCommand()
                    .setNextLineInfo(1, 0)
                    .addExactText("some content")
                .done()
                .addWriteHttpStatusCommand()
                    .setCodeExactText("101")
                    .setReasonExactText("Switching Protocols")
                .done()
                .addWriteCloseCommand()
                    .setNextLineInfo(1, 0)
                .done()
            .done()
        .done();
        // @formatter:on

        InjectHttpStreamsVisitor injectEvents = new InjectHttpStreamsVisitor();
        inputScript.accept(injectEvents, new InjectHttpStreamsVisitor.State());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotAllowConnectReadHttpRequest() throws Exception {
        // @formatter:off
        AstScriptNode inputScript = new AstScriptNodeBuilder()
            .addConnectStream()
                .setNextLineInfo(1, 0)
                .setLocation(URI.create("http://localhost:8000/somepath"))
                .addOpenedEvent()
                    .setNextLineInfo(1, 0)
                .done()
                .addReadHttpMethodEvent()
                     .setExactText("upgrade")
                     .setNextLineInfo(1, 0)
                .done()
                .addReadCloseCommand()
                    .setNextLineInfo(1, 0)
                .done()
                .addWriteHttpStatusCommand()
                    .setCodeExactText("101")
                    .setReasonExactText("Switching Protocols")
                .done()
                .addWriteCloseCommand()
                    .setNextLineInfo(1, 0)
                .done()
            .done()
        .done();
        // @formatter:on

        InjectHttpStreamsVisitor injectEvents = new InjectHttpStreamsVisitor();
        inputScript.accept(injectEvents, new InjectHttpStreamsVisitor.State());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotAllowAcceptReadHttpStatus() throws Exception {
        // @formatter:off
        AstScriptNode inputScript = new AstScriptNodeBuilder()
            .addAcceptStream()
                .setNextLineInfo(1, 0)
                .setLocation(URI.create("http://localhost:8000/somepath"))
                .addAcceptedStream()
                    .addReadHttpMethodEvent()
                         .setExactText("GET")
                         .setNextLineInfo(1, 0)
                    .done()
                    .addReadHttpHeaderEvent()
                         .setNameExactText("Upgrade")
                         .setValueExactText("websocket")
                         .setNextLineInfo(1, 0)
                    .done()
                    .addReadCloseCommand()
                        .setNextLineInfo(1, 0)
                    .done()
                    .addReadHttpStatusEvent()
                        .setCodeExactText("101")
                        .setReasonExactText("Switching Protocols")
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

    @Test(expected = IllegalStateException.class)
    public void shouldNotAllowAcceptWriteHttpRequest() throws Exception {
        // @formatter:off
        AstScriptNode inputScript = new AstScriptNodeBuilder()
            .addAcceptStream()
                .setNextLineInfo(1, 0)
                .setLocation(URI.create("http://localhost:8000/somepath"))
                .addAcceptedStream()
                    .addWriteHttpMethodCommand()
                         .setExactText("UPGRADE")
                         .setNextLineInfo(1, 0)
                    .done()
                    .addWriteHttpContentLengthCommand()
                         .setNextLineInfo(1, 0)
                    .done()
                    .addWriteCommand()
                        .setNextLineInfo(1, 0)
                        .addExactText("some content")
                    .done()
                    .addReadCloseCommand()
                        .setNextLineInfo(1, 0)
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
