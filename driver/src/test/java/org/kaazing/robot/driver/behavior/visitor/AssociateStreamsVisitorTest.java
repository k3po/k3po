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

import org.junit.Test;

import org.kaazing.robot.lang.ast.AstScriptNode;
import org.kaazing.robot.lang.ast.builder.AstScriptNodeBuilder;

public class AssociateStreamsVisitorTest {

    @Test
    public void shouldAssociateAcceptedStreamsWithAcceptStream()
        throws Exception {

        AstScriptNode expectedScriptNode = new AstScriptNodeBuilder()
            .addAcceptStream()
                .setLocation(URI.create("tcp://localhost:8000"))
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
                .setLocation(URI.create("tcp://localhost:8000"))
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
