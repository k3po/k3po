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
import org.kaazing.k3po.lang.internal.ast.AstScriptNode;
import org.kaazing.k3po.lang.internal.ast.builder.AstScriptNodeBuilder;

public class InjectBarriersVisitorTest {

    @Test
    public void shouldInjectBarrierAfterReadBeforeWrite()
        throws Exception {

        AstScriptNode expectedScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .addConnectedEvent()
                    .done()
                .addReadEvent()
                    .addExactText("Hello, world")
                    .done()
                .addReadNotifyBarrier()
                    .setBarrierName("~read~write~1")
                    .done()
                .addWriteAwaitBarrier()
                    .setBarrierName("~read~write~1")
                    .done()
                .addWriteCommand()
                    .addExactText("Hello, world")
                    .done()
                .done()
            .done();

        AstScriptNode inputScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .addConnectedEvent()
                    .done()
                .addReadEvent()
                    .addExactText("Hello, world")
                    .done()
                .addWriteCommand()
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
                .addConnectedEvent()
                    .done()
                .addReadEvent()
                    .addExactText("Hello, world")
                    .done()
                .addReadNotifyBarrier()
                    .setBarrierName("barrier")
                    .done()
                .addWriteCommand()
                    .addExactText("Hello, world")
                    .done()
                .done()
            .done();

        AstScriptNode inputScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .addConnectedEvent()
                    .done()
                .addReadEvent()
                    .addExactText("Hello, world")
                    .done()
                .addReadNotifyBarrier()
                    .setBarrierName("barrier")
                    .done()
                .addWriteCommand()
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
                .addConnectedEvent()
                    .done()
                .addReadEvent()
                    .addExactText("Hello, world")
                    .done()
                .addReadAwaitBarrier()
                    .setBarrierName("barrier")
                    .done()
                .addWriteCommand()
                    .addExactText("Hello, world")
                    .done()
                .done()
            .done();

        AstScriptNode inputScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .addConnectedEvent()
                    .done()
                .addReadEvent()
                    .addExactText("Hello, world")
                    .done()
                .addReadAwaitBarrier()
                    .setBarrierName("barrier")
                    .done()
                .addWriteCommand()
                    .addExactText("Hello, world")
                    .done()
                .done()
            .done();

        InjectBarriersVisitor injectBarriers = new InjectBarriersVisitor();
        AstScriptNode actualScriptNode = inputScriptNode.accept(injectBarriers, new InjectBarriersVisitor.State());

        assertEquals(expectedScriptNode, actualScriptNode);
    }

}
