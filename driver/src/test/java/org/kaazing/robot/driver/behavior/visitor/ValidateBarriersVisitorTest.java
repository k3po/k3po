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

import org.junit.Test;
import org.kaazing.robot.lang.ast.AstScriptNode;
import org.kaazing.robot.lang.ast.builder.AstScriptNodeBuilder;
import org.kaazing.robot.lang.parser.ScriptParseException;

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
