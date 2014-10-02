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

import static org.junit.Assert.assertEquals;

import java.net.URI;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.kaazing.robot.lang.ast.AstScriptNode;
import org.kaazing.robot.lang.ast.builder.AstScriptNodeBuilder;

public class InjectHttpEventsVisitorTest {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void shouldNotAllowWriteContentWhenNoContentTypeIsSpecified() throws Exception {
        expectedEx.expect(IllegalStateException.class);
        expectedEx.expectMessage("Cannot write content when none of the following has been specified:"
                + "Content-Length, Transfer-Encoding: chunked, Connection: close");
        // @formatter:off
        AstScriptNode inputScript = new AstScriptNodeBuilder()
            .addConnectStream()
                .setNextLineInfo(1, 0)
                .setLocation(URI.create("http://localhost:8000/somepath"))
                .addOpenedEvent()
                    .setNextLineInfo(1, 0)
                .done()
                .addWriteHttpMethodCommand()
                     .setExactText("get")
                     .setNextLineInfo(1, 0)
                .done()
                .addWriteCommand()
                    .setNextLineInfo(1, 0)
                    .addExactText("some content")
                .done()
                .addCloseHttpRequestCommand()
                    .setNextLineInfo(1, 0)
                .done()
                .addCloseHttpResponseCommand()
                    .setNextLineInfo(1, 0)
                .done()
            .done()
        .done();
        // @formatter:on

        InjectHttpEventsVisitor injectEvents = new InjectHttpEventsVisitor();
        inputScript.accept(injectEvents, new InjectHttpEventsVisitor.State());
    }

    @Test
    public void shouldNotAllowDoubleSpecifiedContentTypeContentLength() throws Exception {
        expectedEx.expect(IllegalStateException.class);
        expectedEx.expectMessage("Can not set content-length when content-length has already been set");
        // @formatter:off
        AstScriptNode inputScript = new AstScriptNodeBuilder()
            .addConnectStream()
                .setNextLineInfo(1, 0)
                .setLocation(URI.create("http://localhost:8000/somepath"))
                .addOpenedEvent()
                    .setNextLineInfo(1, 0)
                .done()
                .addWriteHttpMethodCommand()
                     .setExactText("get")
                     .setNextLineInfo(1, 0)
                .done()
                .addWriteHttpContentLengthCommand()
                     .setNextLineInfo(1, 0)
                .done()
                .addWriteHttpContentLengthCommand()
                     .setNextLineInfo(1, 0)
                .done()
                .addWriteCommand()
                    .setNextLineInfo(1, 0)
                    .addExactText("some content")
                .done()
                .addCloseHttpRequestCommand()
                    .setNextLineInfo(1, 0)
                .done()
                .addCloseHttpResponseCommand()
                    .setNextLineInfo(1, 0)
                .done()
            .done()
        .done();
        // @formatter:on

        InjectHttpEventsVisitor injectEvents = new InjectHttpEventsVisitor();
        inputScript.accept(injectEvents, new InjectHttpEventsVisitor.State());
    }

    @Test
    public void shouldNotAllowDoubleSpecifiedContentTypeOneOOne() throws Exception {
        expectedEx.expect(IllegalStateException.class);
        expectedEx.expectMessage("Can not set upgrade to websocket when 101 Upgrade has already been set");
        // @formatter:off
        AstScriptNode inputScript = new AstScriptNodeBuilder()
            .addAcceptStream()
                .setNextLineInfo(1, 0)
                .setLocation(URI.create("http://localhost:8000/somepath"))
                .addAcceptedStream()
                    .addReadHttpMethodEvent()
                         .setExactText("upgrade")
                         .setNextLineInfo(1, 0)
                    .done()
                    .addCloseHttpRequestCommand()
                        .setNextLineInfo(1, 0)
                    .done()
                    .addWriteHttpStatusCommand()
                        .setCodeExactText("101")
                        .setReasonExactText("Switching Protocols")
                    .done()
                    .addWriteHttpStatusCommand()
                        .setCodeExactText("101")
                        .setReasonExactText("Switching Protocols")
                    .done()
                    .addCloseHttpResponseCommand()
                        .setNextLineInfo(1, 0)
                    .done()
                .done()
            .done()
        .done();
        // @formatter:on

        InjectHttpEventsVisitor injectEvents = new InjectHttpEventsVisitor();
        inputScript.accept(injectEvents, new InjectHttpEventsVisitor.State());
    }

    @Test
    public void shouldNotAllowDoubleSpecifiedContentTypeChunk() throws Exception {
        expectedEx.expect(IllegalStateException.class);
        expectedEx
                .expectMessage("Can not set transfer-encoding: chunked when transfer-encoding: chuncked has already been set");
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
                    .addCloseHttpRequestCommand()
                        .setNextLineInfo(1, 0)
                    .done()
                    .addWriteHttpHeaderCommand()
                        .setNextLineInfo(1, 0)
                        .setNameExactText("Transfer-Encoding")
                        .addValueExactText("Chunked")
                    .done()
                    .addWriteHttpHeaderCommand()
                        .setNextLineInfo(1, 0)
                        .setNameExactText("Transfer-Encoding")
                        .addValueExactText("Chunked")
                    .done()
                    .addCloseHttpResponseCommand()
                        .setNextLineInfo(1, 0)
                    .done()
                .done()
            .done()
        .done();
        // @formatter:on

        InjectHttpEventsVisitor injectEvents = new InjectHttpEventsVisitor();
        inputScript.accept(injectEvents, new InjectHttpEventsVisitor.State());
    }

    @Test
    public void shouldAllowContentWithChunk() throws Exception {
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
                    .addCloseHttpRequestCommand()
                        .setNextLineInfo(1, 0)
                    .done()
                    .addWriteHttpHeaderCommand()
                        .setNextLineInfo(1, 0)
                        .setNameExactText("Transfer-Encoding")
                        .addValueExactText("Chunked")
                    .done()
                    .addWriteCommand()
                        .setNextLineInfo(1, 0)
                        .addExactText("Some Content")
                    .done()
                    .addCloseHttpResponseCommand()
                        .setNextLineInfo(1, 0)
                    .done()
                .done()
            .done()
        .done();
        // @formatter:on

        InjectHttpEventsVisitor injectEvents = new InjectHttpEventsVisitor();
        inputScript.accept(injectEvents, new InjectHttpEventsVisitor.State());
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
                    .addCloseHttpRequestCommand()
                        .setNextLineInfo(1, 0)
                    .done()
                    .addWriteHttpContentLengthCommand()
                        .setNextLineInfo(1, 0)
                    .done()
                    .addWriteCommand()
                        .setNextLineInfo(1, 0)
                        .addExactText("Some Content")
                    .done()
                    .addCloseHttpResponseCommand()
                        .setNextLineInfo(1, 0)
                    .done()
                .done()
            .done()
        .done();
        // @formatter:on

        InjectHttpEventsVisitor injectEvents = new InjectHttpEventsVisitor();
        inputScript.accept(injectEvents, new InjectHttpEventsVisitor.State());
    }

    @Test
    public void shouldAllowTcpAfterRequestResponseWithOneOOne() throws Exception {
        // @formatter:off
        AstScriptNode inputScript = new AstScriptNodeBuilder()
            .addAcceptStream()
                .setNextLineInfo(1, 0)
                .setLocation(URI.create("http://localhost:8000/somepath"))
                .addAcceptedStream()
                    .addReadHttpMethodEvent()
                         .setExactText("upgrade")
                         .setNextLineInfo(1, 0)
                    .done()
                    .addCloseHttpRequestCommand()
                        .setNextLineInfo(1, 0)
                    .done()
                    .addWriteHttpStatusCommand()
                        .setCodeExactText("101")
                        .setReasonExactText("Switching Protocols")
                    .done()
                    .addCloseHttpResponseCommand()
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

        InjectHttpEventsVisitor injectEvents = new InjectHttpEventsVisitor();
        inputScript.accept(injectEvents, new InjectHttpEventsVisitor.State());
    }

    @Test
    public void shouldNotAllowTcpAfterRequestResponseWithoutOneOOne() throws Exception {
        expectedEx.expect(IllegalStateException.class);
        expectedEx.expectMessage("Unexpected http command "
                + "([005:00] write \"some websocket data\") while in http state CLOSED");
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
                    .addCloseHttpRequestCommand()
                        .setNextLineInfo(1, 0)
                    .done()
                    .addCloseHttpResponseCommand()
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

        InjectHttpEventsVisitor injectEvents = new InjectHttpEventsVisitor();
        inputScript.accept(injectEvents, new InjectHttpEventsVisitor.State());
    }

    @Test
    public void shouldInjectEndOfHeadersOnWriteRequestReadResponse() throws Exception {
        // @formatter:off
        AstScriptNode inputScript = new AstScriptNodeBuilder()
            .addConnectStream()
                .setNextLineInfo(1, 0)
                .setLocation(URI.create("http://localhost:8000/somepath"))
                .addWriteHttpMethodCommand()
                     .setExactText("upgrade")
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
                .addCloseHttpResponseCommand()
                    .setNextLineInfo(1, 0)
                    .done()
                .addReadEvent()
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
            .done();
        // @formatter:on

        InjectHttpEventsVisitor injectEvents = new InjectHttpEventsVisitor();
        AstScriptNode actualScriptNode = inputScript.accept(injectEvents, new InjectHttpEventsVisitor.State());

        // @formatter:off
        AstScriptNode expectedScript = new AstScriptNodeBuilder()
            .addConnectStream()
                .setNextLineInfo(1, 0)
                .setLocation(URI.create("http://localhost:8000/somepath"))
                .addWriteHttpMethodCommand()
                     .setExactText("upgrade")
                     .setNextLineInfo(1, 0)
                     .done()
                .addEndOfHeadersCommand()
                    .setNextLineInfo(1, 0)
                    .done()
                .addCloseHttpRequestCommand()
                    .setNextLineInfo(0, 0)
                    .done()
                .addReadHttpStatusEvent()
                    .setCodeExactText("101")
                    .setReasonExactText("Switching Protocols")
                    .setNextLineInfo(1, 0)
                    .done()
                .addEndOfHeadersCommand()
                    .setNextLineInfo(1, 0)
                    .done()
                .addCloseHttpResponseCommand()
                    .setNextLineInfo(0, 0)
                    .done()
                .addReadEvent()
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
            .done();
        // @formatter:on
        assertEquals(expectedScript, actualScriptNode);
    }

    @Test
    public void shouldInjectEndOfHeadersOnWriteRequestReadResponseWithContent() throws Exception {
        // @formatter:off
        AstScriptNode inputScript = new AstScriptNodeBuilder()
            .addConnectStream()
                .setNextLineInfo(1, 0)
                .setLocation(URI.create("http://localhost:8000/somepath"))
                .addWriteHttpMethodCommand()
                     .setExactText("post")
                     .setNextLineInfo(1, 0)
                     .done()
                .addWriteHttpContentLengthCommand()
                    .setNextLineInfo(1, 0)
                    .done()
                .addWriteCommand()
                    .setNextLineInfo(1, 0)
                    .addExactText("some content")
                    .done()
                .addCloseHttpRequestCommand()
                    .setNextLineInfo(1, 0)
                    .done()
                .addReadHttpStatusEvent()
                    .setCodeExactText("200")
                    .setReasonExactText("ok")
                    .setNextLineInfo(1, 0)
                    .done()
                .addCloseHttpResponseCommand()
                    .setNextLineInfo(1, 0)
                    .done()
                .done()
            .done();
        // @formatter:on

        InjectHttpEventsVisitor injectEvents = new InjectHttpEventsVisitor();
        AstScriptNode actualScriptNode = inputScript.accept(injectEvents, new InjectHttpEventsVisitor.State());

        // @formatter:off
        AstScriptNode expectedScript  = new AstScriptNodeBuilder()
            .addConnectStream()
                .setNextLineInfo(1, 0)
                .setLocation(URI.create("http://localhost:8000/somepath"))
                .addWriteHttpMethodCommand()
                     .setExactText("post")
                     .setNextLineInfo(1, 0)
                     .done()
                .addWriteHttpContentLengthCommand()
                    .setNextLineInfo(1, 0)
                    .done()
                .addEndOfHeadersCommand()
                    .setNextLineInfo(1, 0)
                    .done()
                .addWriteCommand()
                    .setNextLineInfo(0, 0)
                    .addExactText("some content")
                    .done()
                .addCloseHttpRequestCommand()
                    .setNextLineInfo(1, 0)
                    .done()
                .addReadHttpStatusEvent()
                    .setCodeExactText("200")
                    .setReasonExactText("ok")
                    .setNextLineInfo(1, 0)
                    .done()
                .addEndOfHeadersCommand()
                    .setNextLineInfo(1, 0)
                    .done()
                .addCloseHttpResponseCommand()
                    .setNextLineInfo(0, 0)
                    .done()
                .addClosedEvent()
                    .setNextLineInfo(0, 0)
                    .done()
                .done()
            .done();
        // @formatter:on
        assertEquals(expectedScript, actualScriptNode);
    }

    @Test
    public void shouldInjectEndOfHeadersOnReadRequestWriteResponse() throws Exception {
        // @formatter:off
        AstScriptNode inputScript = new AstScriptNodeBuilder()
            .addAcceptStream()
                .setNextLineInfo(1, 0)
                .setLocation(URI.create("http://localhost:8000/somepath"))
                .done()
            .addAcceptedStream()
                .setNextLineInfo(1, 0)
                .addReadHttpMethodEvent()
                     .setExactText("upgrade")
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
                .addCloseHttpResponseCommand()
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
            .done();
        // @formatter:on

        InjectHttpEventsVisitor injectEvents = new InjectHttpEventsVisitor();
        AstScriptNode actualScriptNode = inputScript.accept(injectEvents, new InjectHttpEventsVisitor.State());

        // @formatter:off
        AstScriptNode expectedScript = new AstScriptNodeBuilder()
            .addAcceptStream()
                .setNextLineInfo(1, 0)
                .setLocation(URI.create("http://localhost:8000/somepath"))
                .done()
            .addAcceptedStream()
                .setNextLineInfo(1, 0)
                .addReadHttpMethodEvent()
                     .setExactText("upgrade")
                     .setNextLineInfo(1, 0)
                .done()
                .addEndOfHeadersCommand()
                    .setNextLineInfo(1, 0)
                .done()
                .addCloseHttpRequestCommand()
                    .setNextLineInfo(0, 0)
                .done()
                .addWriteHttpStatusCommand()
                    .setCodeExactText("101")
                    .setReasonExactText("Switching Protocols")
                    .setNextLineInfo(1, 0)
                .done()
                .addEndOfHeadersCommand()
                    .setNextLineInfo(1, 0)
                .done()
                .addCloseHttpResponseCommand()
                    .setNextLineInfo(0, 0)
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
        .done();
        // @formatter:on
        assertEquals(expectedScript, actualScriptNode);
    }

    @Test
    public void shouldNotAllowWriteRequestAndWriteResponse() throws Exception {
        expectedEx.expect(IllegalStateException.class);
        expectedEx.expectMessage("Unexpected http command (write status \"101\" \"Switching Protocols\") "
                + "while in http state Read Response Headers");
        // @formatter:off
        AstScriptNode inputScript = new AstScriptNodeBuilder()
            .addConnectStream()
                .setNextLineInfo(1, 0)
                .setLocation(URI.create("http://localhost:8000/somepath"))
                .addOpenedEvent()
                    .setNextLineInfo(1, 0)
                .done()
                .addWriteHttpMethodCommand()
                     .setExactText("upgrade")
                     .setNextLineInfo(1, 0)
                .done()
                .addWriteHttpContentLengthCommand()
                     .setNextLineInfo(1, 0)
                .done()
                .addWriteCommand()
                    .setNextLineInfo(1, 0)
                    .addExactText("some content")
                .done()
                .addCloseHttpRequestCommand()
                    .setNextLineInfo(1, 0)
                .done()
                .addWriteHttpStatusCommand()
                    .setCodeExactText("101")
                    .setReasonExactText("Switching Protocols")
                .done()
                .addCloseHttpResponseCommand()
                    .setNextLineInfo(1, 0)
                .done()
            .done()
        .done();
        // @formatter:on

        InjectHttpEventsVisitor injectEvents = new InjectHttpEventsVisitor();
        inputScript.accept(injectEvents, new InjectHttpEventsVisitor.State());
    }

    @Test
    public void shouldNotAllowReadRequestOnConnect() throws Exception {
        expectedEx.expect(IllegalStateException.class);
        expectedEx.expectMessage("Unexpected http event ([003:00] read method \"upgrade\") "
                + "while in http state Write Request Headers");
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
                .addCloseHttpRequestCommand()
                    .setNextLineInfo(1, 0)
                .done()
                .addWriteHttpStatusCommand()
                    .setCodeExactText("101")
                    .setReasonExactText("Switching Protocols")
                .done()
                .addCloseHttpResponseCommand()
                    .setNextLineInfo(1, 0)
                .done()
            .done()
        .done();
        // @formatter:on

        InjectHttpEventsVisitor injectEvents = new InjectHttpEventsVisitor();
        inputScript.accept(injectEvents, new InjectHttpEventsVisitor.State());
    }

    @Test
    public void shouldNotAllowReadRequestAndReadResponse() throws Exception {
        expectedEx.expect(IllegalStateException.class);
        expectedEx.expectMessage("Unexpected http event (read status \"101\" \"Switching Protocols\") "
                + "while in http state Write Response Headers");
        // @formatter:off
        AstScriptNode inputScript = new AstScriptNodeBuilder()
            .addAcceptStream()
                .setNextLineInfo(1, 0)
                .setLocation(URI.create("http://localhost:8000/somepath"))
                .addAcceptedStream()
                    .addReadHttpMethodEvent()
                         .setExactText("upgrade")
                         .setNextLineInfo(1, 0)
                    .done()
                    .addCloseHttpRequestCommand()
                        .setNextLineInfo(1, 0)
                    .done()
                    .addReadHttpStatusEvent()
                        .setCodeExactText("101")
                        .setReasonExactText("Switching Protocols")
                    .done()
                    .addCloseHttpResponseCommand()
                        .setNextLineInfo(1, 0)
                    .done()
                .done()
            .done()
        .done();
        // @formatter:on

        InjectHttpEventsVisitor injectEvents = new InjectHttpEventsVisitor();
        inputScript.accept(injectEvents, new InjectHttpEventsVisitor.State());
    }

    @Test
    public void shouldNotAllowWriteRequestOnAccept() throws Exception {
        expectedEx.expect(IllegalStateException.class);
        expectedEx.expectMessage("Unexpected http command ([002:00] write method \"upgrade\") "
                + "while in http state Read Request Headers");
        // @formatter:off
        AstScriptNode inputScript = new AstScriptNodeBuilder()
            .addAcceptStream()
                .setNextLineInfo(1, 0)
                .setLocation(URI.create("http://localhost:8000/somepath"))
                .addAcceptedStream()
                    .addWriteHttpMethodCommand()
                         .setExactText("upgrade")
                         .setNextLineInfo(1, 0)
                    .done()
                    .addWriteHttpContentLengthCommand()
                         .setNextLineInfo(1, 0)
                    .done()
                    .addWriteCommand()
                        .setNextLineInfo(1, 0)
                        .addExactText("some content")
                    .done()
                    .addCloseHttpRequestCommand()
                        .setNextLineInfo(1, 0)
                    .done()
                    .addCloseHttpResponseCommand()
                        .setNextLineInfo(1, 0)
                    .done()
                .done()
            .done()
        .done();
        // @formatter:on

        InjectHttpEventsVisitor injectEvents = new InjectHttpEventsVisitor();
        inputScript.accept(injectEvents, new InjectHttpEventsVisitor.State());
    }

}
