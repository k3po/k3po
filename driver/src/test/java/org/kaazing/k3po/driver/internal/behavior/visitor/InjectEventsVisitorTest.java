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

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.kaazing.k3po.driver.internal.behavior.parser.Parser;
import org.kaazing.k3po.lang.internal.ast.AstScriptNode;
import org.kaazing.k3po.lang.internal.ast.builder.AstScriptNodeBuilder;
import org.kaazing.k3po.lang.internal.parser.ScriptParseException;
import org.kaazing.k3po.lang.internal.parser.ScriptParser;

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
        parser.parse(script);
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
        parser.parse(script);
    }
}
