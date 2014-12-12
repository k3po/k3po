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

package org.kaazing.k3po.driver.behavior.visitor;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.kaazing.k3po.driver.behavior.visitor.InjectFlushVisitor;
import org.kaazing.k3po.lang.ast.AstScriptNode;
import org.kaazing.k3po.lang.ast.builder.AstScriptNodeBuilder;

public class InjectFlushVisitorTest {

    @Test
    public void shouldInjectFlushAfterWriteConfigBeforeFirstReadConfig()
        throws Exception {

        AstScriptNode expectedScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .addConnectedEvent()
                    .done()
                .addWriteConfigCommand()
                    .done()
                .addFlushCommand()
                    .done()
                .addReadConfigEvent()
                    .done()
                .addReadConfigEvent()
                    .done()
                .done()
            .done();

        AstScriptNode inputScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .addConnectedEvent()
                    .done()
                .addWriteConfigCommand()
                    .done()
                .addReadConfigEvent()
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
    public void shouldInjectFlushAfterWriteConfigBeforeFirstReadValue()
        throws Exception {

        AstScriptNode expectedScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .addConnectedEvent()
                    .done()
                .addWriteConfigCommand()
                    .done()
                .addFlushCommand()
                    .done()
                .addReadEvent()
                    .done()
                .addReadEvent()
                    .done()
                .done()
            .done();

        AstScriptNode inputScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .addConnectedEvent()
                    .done()
                .addWriteConfigCommand()
                    .done()
                .addReadEvent()
                    .done()
                .addReadEvent()
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
                .addConnectedEvent()
                    .done()
                .addWriteConfigCommand()
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
                .addWriteConfigCommand()
                    .done()
                .addWriteCommand()
                    .addExactText("Hello, world")
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
                .addConnectedEvent()
                    .done()
                .addWriteCommand()
                    .addExactText("Hello, world")
                    .done()
                .addReadConfigEvent()
                    .done()
                .done()
            .done();

        AstScriptNode inputScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .addConnectedEvent()
                    .done()
                .addWriteCommand()
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
                .addConnectedEvent()
                    .done()
                .addFlushCommand()
                    .done()
                .addReadConfigEvent()
                    .done()
                .done()
            .done();

        AstScriptNode inputScriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .addConnectedEvent()
                    .done()
                .addFlushCommand()
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
