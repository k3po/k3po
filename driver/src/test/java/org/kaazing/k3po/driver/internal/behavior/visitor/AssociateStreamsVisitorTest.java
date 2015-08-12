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

import static org.junit.Assert.assertEquals;

import java.net.URI;

import org.junit.Test;
import org.kaazing.k3po.lang.internal.ast.AstScriptNode;
import org.kaazing.k3po.lang.internal.ast.builder.AstScriptNodeBuilder;
import org.kaazing.k3po.lang.internal.ast.value.AstLocationLiteral;

public class AssociateStreamsVisitorTest {

    @Test
    public void shouldAssociateAcceptedStreamsWithAcceptStream()
        throws Exception {

        AstScriptNode expectedScriptNode = new AstScriptNodeBuilder()
            .addAcceptStream()
                .setLocation(new AstLocationLiteral(URI.create("tcp://localhost:8000")))
                .addAcceptedStream()
                    .addConnectedEvent()
                        .done()
                    .addReadEvent()
                        .addExactText("Hello, world")
                        .done()
                    .addWriteCommand()
                        .addExactText("Hello, world")
                        .done()
                    .done()
                .done()
            .done();

        AstScriptNode inputScriptNode = new AstScriptNodeBuilder()
            .addAcceptStream()
                .setLocation(new AstLocationLiteral(URI.create("tcp://localhost:8000")))
                .done()
            .addAcceptedStream()
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

        AssociateStreamsVisitor injectBarriers = new AssociateStreamsVisitor();
        AstScriptNode actualScriptNode = inputScriptNode.accept(injectBarriers, new AssociateStreamsVisitor.State());

        assertEquals(expectedScriptNode, actualScriptNode);
    }

}
