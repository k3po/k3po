/*
 * Copyright 2014, Kaazing Corporation. All rights reserved.
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

import org.junit.Test;
import org.kaazing.k3po.lang.internal.ast.AstScriptNode;
import org.kaazing.k3po.lang.internal.ast.builder.AstScriptNodeBuilder;
import org.kaazing.k3po.lang.internal.parser.ScriptParseException;

public class ValidateBarriersVisitorTest {

    @Test
    public void shouldNotFailNotifyWithoutMatchingAwait()
        throws Exception {

        AstScriptNode inputScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .addOpenedEvent()
                    .done()
                .addBoundEvent()
                    .done()
                .addConnectedEvent()
                    .done()
                .addReadNotifyBarrier()
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

    @Test(expected = ScriptParseException.class)
    public void shouldFailAwaitWithoutMatchingNotify()
        throws Exception {

        AstScriptNode inputScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .addOpenedEvent()
                    .done()
                .addBoundEvent()
                    .done()
                .addConnectedEvent()
                    .done()
                .addReadAwaitBarrier()
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
