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

package org.kaazing.k3po.driver.behavior.visitor;

import static org.jboss.netty.util.CharsetUtil.UTF_8;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;

import org.junit.Test;
import org.kaazing.k3po.driver.behavior.parser.Parser;
import org.kaazing.k3po.driver.behavior.visitor.InjectEventsVisitor;
import org.kaazing.k3po.lang.ast.AstScriptNode;
import org.kaazing.k3po.lang.ast.builder.AstScriptNodeBuilder;
import org.kaazing.k3po.lang.parser.ScriptParseException;
import org.kaazing.k3po.lang.parser.ScriptParser;

public class InjectEventsVisitorTest {

    @Test
    public void shouldNotInjectBeforeOpened()
        throws Exception {

        AstScriptNode expectedScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .addOpenedEvent()
                    .done()
                .done()
            .done();

        AstScriptNode inputScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .addOpenedEvent()
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
                .addOpenedEvent()
                    .done()
                .addBoundEvent()
                    .done()
                .done()
            .done();

        AstScriptNode inputScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .addBoundEvent()
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
                .addOpenedEvent()
                    .done()
                .addBoundEvent()
                    .done()
                .addConnectedEvent()
                    .done()
                .done()
            .done();

        AstScriptNode inputScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .addConnectedEvent()
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
                .addOpenedEvent()
                    .done()
                .addBoundEvent()
                    .done()
                .addConnectedEvent()
                    .done()
                .done()
            .done();

        AstScriptNode inputScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .addOpenedEvent()
                    .done()
                .addConnectedEvent()
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
                .addOpenedEvent()
                    .done()
                .addBoundEvent()
                    .done()
                .addConnectedEvent()
                    .done()
                .addDisconnectedEvent()
                    .done()
                .done()
            .done();

        AstScriptNode inputScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .addOpenedEvent()
                    .done()
                .addBoundEvent()
                    .done()
                .addConnectedEvent()
                    .done()
                .addDisconnectedEvent()
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
                .addOpenedEvent()
                    .done()
                .addBoundEvent()
                    .done()
                .addConnectedEvent()
                    .done()
                .addDisconnectedEvent()
                    .done()
                .addUnboundEvent()
                    .done()
                .done()
            .done();

        AstScriptNode inputScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .addOpenedEvent()
                    .done()
                .addBoundEvent()
                    .done()
                .addConnectedEvent()
                    .done()
                .addUnboundEvent()
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
                .addOpenedEvent()
                    .done()
                .addBoundEvent()
                    .done()
                .addConnectedEvent()
                    .done()
                .addDisconnectedEvent()
                    .done()
                .addUnboundEvent()
                    .done()
                .addClosedEvent()
                    .done()
                .done()
            .done();

        AstScriptNode inputScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .addOpenedEvent()
                    .done()
                .addBoundEvent()
                    .done()
                .addConnectedEvent()
                    .done()
                .addClosedEvent()
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
                .addOpenedEvent()
                    .done()
                .addBoundEvent()
                    .done()
                .addConnectedEvent()
                    .done()
                .addDisconnectedEvent()
                    .done()
                .addUnboundEvent()
                    .done()
                .addClosedEvent()
                    .done()
                .done()
            .done();

        AstScriptNode inputScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .addOpenedEvent()
                    .done()
                .addBoundEvent()
                    .done()
                .addConnectedEvent()
                    .done()
                .addDisconnectedEvent()
                    .done()
                .addClosedEvent()
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

        ScriptParser parser = new Parser();
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

        ScriptParser parser = new Parser();
        parser.parse(new ByteArrayInputStream(script.getBytes(UTF_8)));
    }
}
