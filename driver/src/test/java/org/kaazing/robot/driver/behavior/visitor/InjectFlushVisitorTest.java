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

import org.junit.Test;

import org.kaazing.robot.lang.ast.AstScriptNode;
import org.kaazing.robot.lang.ast.builder.AstScriptNodeBuilder;

public class InjectFlushVisitorTest {

    @Test
    public void shouldInjectFlushAfterWriteConfigBeforeFirstReadConfig()
        throws Exception {

        AstScriptNode expectedScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .setNextLineInfo(1, 0)
                .addConnectedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addWriteConfigCommand()
                    .setNextLineInfo(1, 0)
                    .done()
                .addFlushCommand()
                    .done()
                .addReadConfigEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addReadConfigEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .done()
            .done();

        AstScriptNode inputScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .setNextLineInfo(1, 0)
                .addConnectedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addWriteConfigCommand()
                    .setNextLineInfo(1, 0)
                    .done()
                .addReadConfigEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addReadConfigEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .done()
            .done();

        InjectFlushVisitor injectFlush = new InjectFlushVisitor();
        AstScriptNode actualScriptNode = inputScriptNode.accept(injectFlush, new InjectFlushVisitor.State());

        assertEquals(expectedScriptNode, actualScriptNode);
    }

    @Test
    public void shouldInjectFlushAfterWriteConfigBeforeFirstReadValue()
        throws Exception {

        AstScriptNode expectedScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .setNextLineInfo(1, 0)
                .addConnectedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addWriteConfigCommand()
                    .setNextLineInfo(1, 0)
                    .done()
                .addFlushCommand()
                    .done()
                .addReadEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addReadEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .done()
            .done();

        AstScriptNode inputScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .setNextLineInfo(1, 0)
                .addConnectedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addWriteConfigCommand()
                    .setNextLineInfo(1, 0)
                    .done()
                .addReadEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addReadEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .done()
            .done();

        InjectFlushVisitor injectFlush = new InjectFlushVisitor();
        AstScriptNode actualScriptNode = inputScriptNode.accept(injectFlush, new InjectFlushVisitor.State());

        assertEquals(expectedScriptNode, actualScriptNode);
    }

    @Test
    public void shouldNotInjectFlushAfterWriteConfigBeforeWrite()
        throws Exception {

        AstScriptNode expectedScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .setNextLineInfo(1, 0)
                .addConnectedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addWriteConfigCommand()
                    .setNextLineInfo(1, 0)
                    .done()
                .addWriteCommand()
                    .addExactText("Hello, world")
                    .setNextLineInfo(1, 0)
                    .done()
                .done()
            .done();

        AstScriptNode inputScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .setNextLineInfo(1, 0)
                .addConnectedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addWriteConfigCommand()
                    .setNextLineInfo(1, 0)
                    .done()
                .addWriteCommand()
                    .addExactText("Hello, world")
                    .setNextLineInfo(1, 0)
                    .done()
                .done()
            .done();

        InjectFlushVisitor injectFlush = new InjectFlushVisitor();
        AstScriptNode actualScriptNode = inputScriptNode.accept(injectFlush, new InjectFlushVisitor.State());

        assertEquals(expectedScriptNode, actualScriptNode);
    }

    @Test
    public void shouldNotInjectFlushAfterWriteBeforeReadConfig()
        throws Exception {

        AstScriptNode expectedScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .setNextLineInfo(1, 0)
                .addConnectedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addWriteCommand()
                    .setNextLineInfo(1, 0)
                    .addExactText("Hello, world")
                    .done()
                .addReadConfigEvent()
                    .done()
                .done()
            .done();

        AstScriptNode inputScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .setNextLineInfo(1, 0)
                .addConnectedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addWriteCommand()
                    .setNextLineInfo(1, 0)
                    .addExactText("Hello, world")
                    .done()
                .addReadConfigEvent()
                    .done()
                .done()
            .done();

        InjectFlushVisitor injectFlush = new InjectFlushVisitor();
        AstScriptNode actualScriptNode = inputScriptNode.accept(injectFlush, new InjectFlushVisitor.State());

        assertEquals(expectedScriptNode, actualScriptNode);
    }

    @Test
    public void shouldNotInjectFlushAfterExplicitFlushBeforeReadConfig()
        throws Exception {

        AstScriptNode expectedScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .setNextLineInfo(1, 0)
                .addConnectedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addFlushCommand()
                    .setNextLineInfo(1, 0)
                    .done()
                .addReadConfigEvent()
                    .done()
                .done()
            .done();

        AstScriptNode inputScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .setNextLineInfo(1, 0)
                .addConnectedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addFlushCommand()
                    .setNextLineInfo(1, 0)
                    .done()
                .addReadConfigEvent()
                    .done()
                .done()
            .done();

        InjectFlushVisitor injectFlush = new InjectFlushVisitor();
        AstScriptNode actualScriptNode = inputScriptNode.accept(injectFlush, new InjectFlushVisitor.State());

        assertEquals(expectedScriptNode, actualScriptNode);
    }
}
