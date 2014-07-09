/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.behavior.visitor;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.kaazing.robot.lang.ast.AstScriptNode;
import org.kaazing.robot.lang.ast.builder.AstScriptNodeBuilder;

public class InjectBarriersVisitorTest {

    @Test
    public void shouldInjectBarrierAfterReadBeforeWrite()
        throws Exception {

        AstScriptNode expectedScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .setNextLineInfo(1, 0)
                .addConnectedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addReadEvent()
                    .setNextLineInfo(1, 0)
                    .addExactText("Hello, world")
                    .done()
                .addReadNotifyBarrier()
                    .setBarrierName("~read~write~1")
                    .done()
                .addWriteAwaitBarrier()
                    .setBarrierName("~read~write~1")
                    .done()
                .addWriteCommand()
                    .setNextLineInfo(1, 0)
                    .addExactText("Hello, world")
                    .done()
                .done()
            .done();

        AstScriptNode inputScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
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

        InjectBarriersVisitor injectBarriers = new InjectBarriersVisitor();
        AstScriptNode actualScriptNode = inputScriptNode.accept(injectBarriers, new InjectBarriersVisitor.State());

        assertEquals(expectedScriptNode, actualScriptNode);
    }

    @Test
    public void shouldNotInjectBarrierAfterReadBeforeExplicitReadNotify()
        throws Exception {

        AstScriptNode expectedScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .setNextLineInfo(1, 0)
                .addConnectedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addReadEvent()
                    .setNextLineInfo(1, 0)
                    .addExactText("Hello, world")
                    .done()
                .addReadNotifyBarrier()
                    .setBarrierName("barrier")
                    .done()
                .addWriteCommand()
                    .setNextLineInfo(1, 0)
                    .addExactText("Hello, world")
                    .done()
                .done()
            .done();

        AstScriptNode inputScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .setNextLineInfo(1, 0)
                .addConnectedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addReadEvent()
                    .setNextLineInfo(1, 0)
                    .addExactText("Hello, world")
                    .done()
                .addReadNotifyBarrier()
                    .setBarrierName("barrier")
                    .done()
                .addWriteCommand()
                    .setNextLineInfo(1, 0)
                    .addExactText("Hello, world")
                    .done()
                .done()
            .done();

        InjectBarriersVisitor injectBarriers = new InjectBarriersVisitor();
        AstScriptNode actualScriptNode = inputScriptNode.accept(injectBarriers, new InjectBarriersVisitor.State());

        assertEquals(expectedScriptNode, actualScriptNode);
    }

    @Test
    public void shouldNotInjectBarrierAfterReadBeforeExplicitReadAwait()
        throws Exception {

        AstScriptNode expectedScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .setNextLineInfo(1, 0)
                .addConnectedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addReadEvent()
                    .setNextLineInfo(1, 0)
                    .addExactText("Hello, world")
                    .done()
                .addReadAwaitBarrier()
                    .setBarrierName("barrier")
                    .done()
                .addWriteCommand()
                    .setNextLineInfo(1, 0)
                    .addExactText("Hello, world")
                    .done()
                .done()
            .done();

        AstScriptNode inputScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .setNextLineInfo(1, 0)
                .addConnectedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addReadEvent()
                    .setNextLineInfo(1, 0)
                    .addExactText("Hello, world")
                    .done()
                .addReadAwaitBarrier()
                    .setBarrierName("barrier")
                    .done()
                .addWriteCommand()
                    .setNextLineInfo(1, 0)
                    .addExactText("Hello, world")
                    .done()
                .done()
            .done();

        InjectBarriersVisitor injectBarriers = new InjectBarriersVisitor();
        AstScriptNode actualScriptNode = inputScriptNode.accept(injectBarriers, new InjectBarriersVisitor.State());

        assertEquals(expectedScriptNode, actualScriptNode);
    }

}
