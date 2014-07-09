/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.behavior.visitor;

import static org.junit.Assert.assertEquals;

import java.net.URI;

import org.junit.Test;

import org.kaazing.robot.lang.ast.AstScriptNode;
import org.kaazing.robot.lang.ast.builder.AstScriptNodeBuilder;

public class AssociateStreamsVisitorTest {

    @Test
    public void shouldAssociateAcceptedStreamsWithAcceptStream()
        throws Exception {

        AstScriptNode expectedScriptNode = new AstScriptNodeBuilder()
            .addAcceptStream()
                .setNextLineInfo(1, 0)
                .setLocation(URI.create("tcp://localhost:8000"))
                .addAcceptedStream()
                    .setNextLineInfo(1, 0)
                    .addConnectedEvent()
                        .setNextLineInfo(1, 0)
                        .done()
                    .addReadEvent()
                        .setNextLineInfo(1, 0)
                        .addExactText("Hello, world")
                        .done()
                    .addWriteCommand()
                        .setNextLineInfo(1, 0)
                        .addExactText("Hello, world")
                        .done()
                    .done()
                .done()
            .done();

        AstScriptNode inputScriptNode = new AstScriptNodeBuilder()
            .addAcceptStream()
                .setNextLineInfo(1, 0)
                .setLocation(URI.create("tcp://localhost:8000"))
                .done()
            .addAcceptedStream()
                .setNextLineInfo(1, 0)
                .addConnectedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addReadEvent()
                    .setNextLineInfo(1, 0)
                    .addExactText("Hello, world")
                    .done()
                .addWriteCommand()
                    .setNextLineInfo(1, 0)
                    .addExactText("Hello, world")
                    .done()
                .done()
            .done();

        AssociateStreamsVisitor injectBarriers = new AssociateStreamsVisitor();
        AstScriptNode actualScriptNode = inputScriptNode.accept(injectBarriers, new AssociateStreamsVisitor.State());

        assertEquals(expectedScriptNode, actualScriptNode);
    }

}
