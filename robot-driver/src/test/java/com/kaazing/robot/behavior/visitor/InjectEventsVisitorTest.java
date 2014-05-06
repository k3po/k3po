/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.behavior.visitor;

import static org.jboss.netty.util.CharsetUtil.UTF_8;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;

import org.junit.Test;

import com.kaazing.robot.behavior.parser.Parser;
import com.kaazing.robot.lang.ast.AstScriptNode;
import com.kaazing.robot.lang.ast.builder.AstScriptNodeBuilder;
import com.kaazing.robot.lang.parser.ScriptParseException;
import com.kaazing.robot.lang.parser.ScriptParser;

public class InjectEventsVisitorTest {

    @Test
    public void shouldNotInjectBeforeOpened()
        throws Exception {

        AstScriptNode expectedScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .setNextLineInfo(1, 0)
                .addOpenedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .done()
            .done();

        AstScriptNode inputScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .setNextLineInfo(1, 0)
                .addOpenedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .done()
            .done();

        InjectEventsVisitor injectEvents = new InjectEventsVisitor();
        AstScriptNode actualScriptNode = inputScriptNode.accept(injectEvents, new InjectEventsVisitor.State());

        assertEquals(expectedScriptNode, actualScriptNode);
    }

    @Test
    public void shouldInjectOpenedBeforeBound()
        throws Exception {

        // @formatter:off
        AstScriptNode expectedScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .setNextLineInfo(1, 0)
                .addOpenedEvent()
                    .setNextLineInfo(0, 0)
                    .done()
                .addBoundEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .done()
            .done();

        AstScriptNode inputScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .setNextLineInfo(1, 0)
                .addBoundEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .done()
            .done();
        // @formatter:on

        InjectEventsVisitor injectEvents = new InjectEventsVisitor();
        AstScriptNode actualScriptNode = inputScriptNode.accept(injectEvents, new InjectEventsVisitor.State());

        assertEquals(expectedScriptNode, actualScriptNode);
    }

    @Test
    public void shouldInjectOpenedAndBoundBeforeConnected()
        throws Exception {

        // @formatter:off
        AstScriptNode expectedScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .setNextLineInfo(1, 0)
                .addOpenedEvent()
                    .setNextLineInfo(0, 0)
                    .done()
                .addBoundEvent()
                    .setNextLineInfo(0, 0)
                    .done()
                .addConnectedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .done()
            .done();

        AstScriptNode inputScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .setNextLineInfo(1, 0)
                .addConnectedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .done()
            .done();
        // @formatter:on

        InjectEventsVisitor injectEvents = new InjectEventsVisitor();
        AstScriptNode actualScriptNode = inputScriptNode.accept(injectEvents, new InjectEventsVisitor.State());

        assertEquals(expectedScriptNode, actualScriptNode);
    }

    @Test
    public void shouldInjectBoundBetweenOpenedBeforeConnected()
        throws Exception {

        // @formatter:off
        AstScriptNode expectedScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .setNextLineInfo(1, 0)
                .addOpenedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addBoundEvent()
                    .setNextLineInfo(0, 0)
                    .done()
                .addConnectedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .done()
            .done();

        AstScriptNode inputScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .setNextLineInfo(1, 0)
                .addOpenedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addConnectedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .done()
            .done();
        // @formatter:on

        InjectEventsVisitor injectEvents = new InjectEventsVisitor();
        AstScriptNode actualScriptNode = inputScriptNode.accept(injectEvents, new InjectEventsVisitor.State());

        assertEquals(expectedScriptNode, actualScriptNode);
    }

    @Test
    public void shouldNotInjectBeforeDisconnected()
        throws Exception {

        // @formatter:off
        AstScriptNode expectedScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .setNextLineInfo(1, 0)
                .addOpenedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addBoundEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addConnectedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addDisconnectedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .done()
            .done();

        AstScriptNode inputScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .setNextLineInfo(1, 0)
                .addOpenedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addBoundEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addConnectedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addDisconnectedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .done()
            .done();
        // @formatter:on

        InjectEventsVisitor injectEvents = new InjectEventsVisitor();
        AstScriptNode actualScriptNode = inputScriptNode.accept(injectEvents, new InjectEventsVisitor.State());

        assertEquals(expectedScriptNode, actualScriptNode);
    }

    @Test
    public void shouldInjectDisconnectedBeforeUnbound()
        throws Exception {
        // @formatter:off
        AstScriptNode expectedScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .setNextLineInfo(1, 0)
                .addOpenedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addBoundEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addConnectedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addDisconnectedEvent()
                    .setNextLineInfo(0, 0)
                    .done()
                .addUnboundEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .done()
            .done();

        AstScriptNode inputScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .setNextLineInfo(1, 0)
                .addOpenedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addBoundEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addConnectedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addUnboundEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .done()
            .done();
        // @formatter:on

        InjectEventsVisitor injectEvents = new InjectEventsVisitor();
        AstScriptNode actualScriptNode = inputScriptNode.accept(injectEvents, new InjectEventsVisitor.State());

        assertEquals(expectedScriptNode, actualScriptNode);
    }

    @Test
    public void shouldInjectDisconnectedAndUnboundBeforeClosed()
        throws Exception {
        // @formatter:off
        AstScriptNode expectedScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .setNextLineInfo(1, 0)
                .addOpenedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addBoundEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addConnectedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addDisconnectedEvent()
                    .setNextLineInfo(0, 0)
                    .done()
                .addUnboundEvent()
                    .setNextLineInfo(0, 0)
                    .done()
                .addClosedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .done()
            .done();

        AstScriptNode inputScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .setNextLineInfo(1, 0)
                .addOpenedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addBoundEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addConnectedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addClosedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .done()
            .done();
        // @formatter:on

        InjectEventsVisitor injectEvents = new InjectEventsVisitor();
        AstScriptNode actualScriptNode = inputScriptNode.accept(injectEvents, new InjectEventsVisitor.State());

        assertEquals(expectedScriptNode, actualScriptNode);
    }

    @Test
    public void shouldInjectUnboundBetweenDisconnectedAndClosed()
        throws Exception {

        // @formatter:off
        AstScriptNode expectedScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .setNextLineInfo(1, 0)
                .addOpenedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addBoundEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addConnectedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addDisconnectedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addUnboundEvent()
                    .setNextLineInfo(0, 0)
                    .done()
                .addClosedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .done()
            .done();

        AstScriptNode inputScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .setNextLineInfo(1, 0)
                .addOpenedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addBoundEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addConnectedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addDisconnectedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addClosedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .done()
            .done();
        // @formatter:on

        InjectEventsVisitor injectEvents = new InjectEventsVisitor();
        AstScriptNode actualScriptNode = inputScriptNode.accept(injectEvents, new InjectEventsVisitor.State());

        assertEquals(expectedScriptNode, actualScriptNode);
    }

    // These validation tests ensure that the InjectEventsVisitor continues
    // to throw exceptions on malformed (connectivity-wise) streams

    @Test(expected = ScriptParseException.class)
    public void shouldNotParseScriptWithoutConnect()
        throws Exception {

        String script =
            "# tcp.client.connect-then-close\n" +
            "connected\n" +
            "close\n" +
            "closed\n";

        ScriptParser parser = new Parser(com.kaazing.robot.lang.parser.ScriptParserImpl.LATEST_SUPPORTED_FORMAT);
        parser.parse(new ByteArrayInputStream(script.getBytes(UTF_8)));
    }

    @Test(expected = ScriptParseException.class)
    public void shouldNotParseScriptWithUnclosedStream()
        throws Exception {

        String script =
            "# tcp.client.connect-then-close\n" +
            "connect tcp://localhost:7788\n" +
            "connected\n";

        ScriptParser parser = new Parser(com.kaazing.robot.lang.parser.ScriptParserImpl.LATEST_SUPPORTED_FORMAT);
        parser.parse(new ByteArrayInputStream(script.getBytes(UTF_8)));
    }

    @Test(expected = ScriptParseException.class)
    public void shouldNotParseScriptWithMultiUnclosedStreams()
        throws Exception {

        String script =
            "# tcp.client.connect-then-close\n" +
            "connect tcp://localhost:7788\n" +
            "connected\n" +
            "connect tcp://localhost:7789\n" +
            "connected\n" +
            "close\n" +
            "closed\n";

        ScriptParser parser = new Parser(com.kaazing.robot.lang.parser.ScriptParserImpl.LATEST_SUPPORTED_FORMAT);
        parser.parse(new ByteArrayInputStream(script.getBytes(UTF_8)));
    }

    @Test(expected = ScriptParseException.class)
    public void shouldNotParseScriptWithMultiplyClosedStream()
        throws Exception {

        String script =
            "# tcp.client.connect-then-close\n" +
            "connect tcp://localhost:7788\n" +
            "connected\n" +
            "close\n" +
            "closed\n" +
            "close\n" +
            "closed\n";

        ScriptParser parser = new Parser(com.kaazing.robot.lang.parser.ScriptParserImpl.LATEST_SUPPORTED_FORMAT);
        parser.parse(new ByteArrayInputStream(script.getBytes(UTF_8)));
    }
}
