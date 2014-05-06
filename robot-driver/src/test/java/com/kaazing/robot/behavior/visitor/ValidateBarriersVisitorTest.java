/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.behavior.visitor;

import org.junit.Test;

import com.kaazing.robot.lang.ast.AstScriptNode;
import com.kaazing.robot.lang.ast.builder.AstScriptNodeBuilder;

public class ValidateBarriersVisitorTest {

    @Test
    public void shouldNotFailNotifyWithoutMatchingAwait()
        throws Exception {

        AstScriptNode inputScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .setNextLineInfo(1, 0)
                .addOpenedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addBoundEvent()
                    .done()
                .addConnectedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addReadNotifyBarrier()
                    .setNextLineInfo(1, 0)
                    .setBarrierName("BARRIER")
                    .done()
                .addDisconnectedEvent()
                    .done()
                .addUnboundEvent()
                    .done()
                .addClosedEvent()
                    .done()
                .done()
            .done();

        ValidateBarriersVisitor validateBarriers = new ValidateBarriersVisitor();
        inputScriptNode.accept(validateBarriers, new ValidateBarriersVisitor.State());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailAwaitWithoutMatchingNotify()
        throws Exception {

        AstScriptNode inputScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .setNextLineInfo(1, 0)
                .addOpenedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addBoundEvent()
                    .done()
                .addConnectedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addReadAwaitBarrier()
                    .setNextLineInfo(1, 0)
                    .setBarrierName("BARRIER")
                    .done()
                .addDisconnectedEvent()
                    .done()
                .addUnboundEvent()
                    .done()
                .addClosedEvent()
                    .done()
                .done()
            .done();

        ValidateBarriersVisitor validateBarriers = new ValidateBarriersVisitor();
        inputScriptNode.accept(validateBarriers, new ValidateBarriersVisitor.State());
    }
}
