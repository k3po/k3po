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

package org.kaazing.robot.lang.ast.builder;

import org.kaazing.robot.lang.ast.AstAcceptNode;
import org.kaazing.robot.lang.ast.AstAcceptableNode;
import org.kaazing.robot.lang.ast.AstScriptNode;

public final class AstAcceptableNodeBuilder extends AbstractAstAcceptableNodeBuilder<AstAcceptableNode> {

    private int line;

    public AstAcceptableNodeBuilder() {
        this(new AstAcceptableNode());
    }

    @Override
    public AstAcceptableNodeBuilder setLocationInfo(int line, int column) {
        node.setLocationInfo(line, column);
        internalSetLineInfo(line);
        return this;
    }

    @Override
    public AstAcceptableNodeBuilder setNextLineInfo(int linesToSkip, int column) {
        internalSetNextLineInfo(linesToSkip, column);
        return this;
    }

    public AstAcceptableNodeBuilder setAcceptName(String acceptName) {
        node.setAcceptName(acceptName);
        return this;
    }

    @Override
    public AstOpenedNodeBuilder.StreamNested<AstAcceptableNodeBuilder> addOpenedEvent() {
        return new AstOpenedNodeBuilder.StreamNested<AstAcceptableNodeBuilder>(this);
    }

    @Override
    public AstBoundNodeBuilder.StreamNested<AstAcceptableNodeBuilder> addBoundEvent() {
        return new AstBoundNodeBuilder.StreamNested<AstAcceptableNodeBuilder>(this);
    }

    @Override
    public AstConnectedNodeBuilder.StreamNested<AstAcceptableNodeBuilder> addConnectedEvent() {
        return new AstConnectedNodeBuilder.StreamNested<AstAcceptableNodeBuilder>(this);
    }

    @Override
    public AstReadNodeBuilder.StreamNested<AstAcceptableNodeBuilder> addReadEvent() {
        return new AstReadNodeBuilder.StreamNested<AstAcceptableNodeBuilder>(this);
    }

    @Override
    public AstDisconnectedNodeBuilder.StreamNested<AstAcceptableNodeBuilder> addDisconnectedEvent() {
        return new AstDisconnectedNodeBuilder.StreamNested<AstAcceptableNodeBuilder>(this);
    }

    @Override
    public AstUnboundNodeBuilder.StreamNested<AstAcceptableNodeBuilder> addUnboundEvent() {
        return new AstUnboundNodeBuilder.StreamNested<AstAcceptableNodeBuilder>(this);
    }

    @Override
    public AstClosedNodeBuilder.StreamNested<AstAcceptableNodeBuilder> addClosedEvent() {
        return new AstClosedNodeBuilder.StreamNested<AstAcceptableNodeBuilder>(this);
    }

    @Override
    public AstWriteNodeBuilder.StreamNested<AstAcceptableNodeBuilder> addWriteCommand() {
        return new AstWriteNodeBuilder.StreamNested<AstAcceptableNodeBuilder>(this);
    }

    @Override
    public AstDisconnectNodeBuilder.StreamNested<AstAcceptableNodeBuilder> addDisconnectCommand() {
        return new AstDisconnectNodeBuilder.StreamNested<AstAcceptableNodeBuilder>(this);
    }

    @Override
    public AstUnbindNodeBuilder.StreamNested<AstAcceptableNodeBuilder> addUnbindCommand() {
        return new AstUnbindNodeBuilder.StreamNested<AstAcceptableNodeBuilder>(this);
    }

    @Override
    public AstCloseNodeBuilder.StreamNested<AstAcceptableNodeBuilder> addCloseCommand() {
        return new AstCloseNodeBuilder.StreamNested<AstAcceptableNodeBuilder>(this);
    }

    @Override
    public AstReadAwaitNodeBuilder.StreamNested<AstAcceptableNodeBuilder> addReadAwaitBarrier() {
        return new AstReadAwaitNodeBuilder.StreamNested<AstAcceptableNodeBuilder>(this);
    }

    @Override
    public AstReadNotifyNodeBuilder.StreamNested<AstAcceptableNodeBuilder> addReadNotifyBarrier() {
        return new AstReadNotifyNodeBuilder.StreamNested<AstAcceptableNodeBuilder>(this);
    }

    @Override
    public AstWriteAwaitNodeBuilder.StreamNested<AstAcceptableNodeBuilder> addWriteAwaitBarrier() {
        return new AstWriteAwaitNodeBuilder.StreamNested<AstAcceptableNodeBuilder>(this);
    }

    @Override
    public AstWriteNotifyNodeBuilder.StreamNested<AstAcceptableNodeBuilder> addWriteNotifyBarrier() {
        return new AstWriteNotifyNodeBuilder.StreamNested<AstAcceptableNodeBuilder>(this);
    }

    @Override
    public AstReadOptionNodeBuilder.StreamNested<AstAcceptableNodeBuilder> addReadOption() {
        return new AstReadOptionNodeBuilder.StreamNested<AstAcceptableNodeBuilder>(this);
    }

    @Override
    public AstWriteOptionNodeBuilder.StreamNested<AstAcceptableNodeBuilder> addWriteOption() {
        return new AstWriteOptionNodeBuilder.StreamNested<AstAcceptableNodeBuilder>(this);
    }

    @Override
    public AstAcceptableNode done() {
        return result;
    }

    @Override
    protected int line() {
        return line;
    }

    @Override
    protected int line(int line) {
        this.line = line;
        return line;
    }

    private AstAcceptableNodeBuilder(AstAcceptableNode node) {
        super(node, node);
    }

    @Override
    public AstReadConfigNodeBuilder.StreamNested<AstAcceptableNodeBuilder> addReadConfigEvent() {
        return new AstReadConfigNodeBuilder.StreamNested<AstAcceptableNodeBuilder>(this);
    }

    @Override
    public AstWriteConfigNodeBuilder.StreamNested<AstAcceptableNodeBuilder> addWriteConfigCommand() {
        return new AstWriteConfigNodeBuilder.StreamNested<AstAcceptableNodeBuilder>(this);
    }

    @Override
    public AstFlushNodeBuilder.StreamNested<AstAcceptableNodeBuilder> addFlushCommand() {
        return new AstFlushNodeBuilder.StreamNested<AstAcceptableNodeBuilder>(this);
    }

    @Override
    public AstReadClosedNodeBuilder.StreamNested<AstAcceptableNodeBuilder> addReadCloseCommand() {
        return new AstReadClosedNodeBuilder.StreamNested<AstAcceptableNodeBuilder>(this);
    }

    @Override
    public AstWriteCloseNodeBuilder.StreamNested<AstAcceptableNodeBuilder> addWriteCloseCommand() {
        return new AstWriteCloseNodeBuilder.StreamNested<AstAcceptableNodeBuilder>(this);
    }

    public static final class ScriptNested<R extends AbstractAstNodeBuilder<? extends AstScriptNode, ?>> extends
            AbstractAstAcceptableNodeBuilder<R> {

        public ScriptNested(R builder) {
            super(builder);
        }

        @Override
        public ScriptNested<R> setLocationInfo(int line, int column) {
            node.setLocationInfo(line, column);
            internalSetLineInfo(line);
            return this;
        }

        @Override
        public ScriptNested<R> setNextLineInfo(int linesToSkip, int column) {
            internalSetNextLineInfo(linesToSkip, column);
            return this;
        }

        public ScriptNested<R> setAcceptName(String acceptName) {
            node.setAcceptName(acceptName);
            return this;
        }

        @Override
        public AstOpenedNodeBuilder.StreamNested<ScriptNested<R>> addOpenedEvent() {
            return new AstOpenedNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstBoundNodeBuilder.StreamNested<ScriptNested<R>> addBoundEvent() {
            return new AstBoundNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstConnectedNodeBuilder.StreamNested<ScriptNested<R>> addConnectedEvent() {
            return new AstConnectedNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstReadNodeBuilder.StreamNested<ScriptNested<R>> addReadEvent() {
            return new AstReadNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstDisconnectedNodeBuilder.StreamNested<ScriptNested<R>> addDisconnectedEvent() {
            return new AstDisconnectedNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstUnboundNodeBuilder.StreamNested<ScriptNested<R>> addUnboundEvent() {
            return new AstUnboundNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstClosedNodeBuilder.StreamNested<ScriptNested<R>> addClosedEvent() {
            return new AstClosedNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstWriteNodeBuilder.StreamNested<ScriptNested<R>> addWriteCommand() {
            return new AstWriteNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstDisconnectNodeBuilder.StreamNested<ScriptNested<R>> addDisconnectCommand() {
            return new AstDisconnectNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstUnbindNodeBuilder.StreamNested<ScriptNested<R>> addUnbindCommand() {
            return new AstUnbindNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstCloseNodeBuilder.StreamNested<ScriptNested<R>> addCloseCommand() {
            return new AstCloseNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstReadAwaitNodeBuilder.StreamNested<ScriptNested<R>> addReadAwaitBarrier() {
            return new AstReadAwaitNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstReadNotifyNodeBuilder.StreamNested<ScriptNested<R>> addReadNotifyBarrier() {
            return new AstReadNotifyNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstWriteAwaitNodeBuilder.StreamNested<ScriptNested<R>> addWriteAwaitBarrier() {
            return new AstWriteAwaitNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstWriteNotifyNodeBuilder.StreamNested<ScriptNested<R>> addWriteNotifyBarrier() {
            return new AstWriteNotifyNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public R done() {
            AstScriptNode scriptNode = result.node;
            scriptNode.getStreams().add(node);
            return result;
        }

        @Override
        protected int line() {
            return result.line();
        }

        @Override
        protected int line(int line) {
            return result.line(line);
        }

        @Override
        public AstReadConfigNodeBuilder.StreamNested<ScriptNested<R>> addReadConfigEvent() {
            return new AstReadConfigNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstWriteConfigNodeBuilder.StreamNested<ScriptNested<R>> addWriteConfigCommand() {
            return new AstWriteConfigNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstFlushNodeBuilder.StreamNested<ScriptNested<R>> addFlushCommand() {
            return new AstFlushNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstReadClosedNodeBuilder.StreamNested<ScriptNested<R>> addReadCloseCommand() {
            return new AstReadClosedNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstWriteCloseNodeBuilder.StreamNested<ScriptNested<R>> addWriteCloseCommand() {
            return new AstWriteCloseNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstReadOptionNodeBuilder.StreamNested<ScriptNested<R>> addReadOption() {
            return new AstReadOptionNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstWriteOptionNodeBuilder.StreamNested<ScriptNested<R>> addWriteOption() {
            return new AstWriteOptionNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }
    }

    public static final class AcceptNested<R extends AbstractAstNodeBuilder<? extends AstAcceptNode, ?>> extends
            AbstractAstAcceptableNodeBuilder<R> {

        public AcceptNested(R builder) {
            super(builder);
        }

        @Override
        public AcceptNested<R> setLocationInfo(int line, int column) {
            node.setLocationInfo(line, column);
            internalSetLineInfo(line);
            return this;
        }

        @Override
        public AcceptNested<R> setNextLineInfo(int linesToSkip, int column) {
            internalSetNextLineInfo(linesToSkip, column);
            return this;
        }

        public AcceptNested<R> setAcceptName(String acceptName) {
            node.setAcceptName(acceptName);
            return this;
        }

        @Override
        public AstOpenedNodeBuilder.StreamNested<AcceptNested<R>> addOpenedEvent() {
            return new AstOpenedNodeBuilder.StreamNested<AcceptNested<R>>(this);
        }

        @Override
        public AstBoundNodeBuilder.StreamNested<AcceptNested<R>> addBoundEvent() {
            return new AstBoundNodeBuilder.StreamNested<AcceptNested<R>>(this);
        }

        @Override
        public AstConnectedNodeBuilder.StreamNested<AcceptNested<R>> addConnectedEvent() {
            return new AstConnectedNodeBuilder.StreamNested<AcceptNested<R>>(this);
        }

        @Override
        public AstReadNodeBuilder.StreamNested<AcceptNested<R>> addReadEvent() {
            return new AstReadNodeBuilder.StreamNested<AcceptNested<R>>(this);
        }

        @Override
        public AstDisconnectedNodeBuilder.StreamNested<AcceptNested<R>> addDisconnectedEvent() {
            return new AstDisconnectedNodeBuilder.StreamNested<AcceptNested<R>>(this);
        }

        @Override
        public AstUnboundNodeBuilder.StreamNested<AcceptNested<R>> addUnboundEvent() {
            return new AstUnboundNodeBuilder.StreamNested<AcceptNested<R>>(this);
        }

        @Override
        public AstClosedNodeBuilder.StreamNested<AcceptNested<R>> addClosedEvent() {
            return new AstClosedNodeBuilder.StreamNested<AcceptNested<R>>(this);
        }

        @Override
        public AstWriteNodeBuilder.StreamNested<AcceptNested<R>> addWriteCommand() {
            return new AstWriteNodeBuilder.StreamNested<AcceptNested<R>>(this);
        }

        @Override
        public AstDisconnectNodeBuilder.StreamNested<AcceptNested<R>> addDisconnectCommand() {
            return new AstDisconnectNodeBuilder.StreamNested<AcceptNested<R>>(this);
        }

        @Override
        public AstUnbindNodeBuilder.StreamNested<AcceptNested<R>> addUnbindCommand() {
            return new AstUnbindNodeBuilder.StreamNested<AcceptNested<R>>(this);
        }

        @Override
        public AstCloseNodeBuilder.StreamNested<AcceptNested<R>> addCloseCommand() {
            return new AstCloseNodeBuilder.StreamNested<AcceptNested<R>>(this);
        }

        @Override
        public AstReadAwaitNodeBuilder.StreamNested<AcceptNested<R>> addReadAwaitBarrier() {
            return new AstReadAwaitNodeBuilder.StreamNested<AcceptNested<R>>(this);
        }

        @Override
        public AstReadNotifyNodeBuilder.StreamNested<AcceptNested<R>> addReadNotifyBarrier() {
            return new AstReadNotifyNodeBuilder.StreamNested<AcceptNested<R>>(this);
        }

        @Override
        public AstWriteAwaitNodeBuilder.StreamNested<AcceptNested<R>> addWriteAwaitBarrier() {
            return new AstWriteAwaitNodeBuilder.StreamNested<AcceptNested<R>>(this);
        }

        @Override
        public AstWriteNotifyNodeBuilder.StreamNested<AcceptNested<R>> addWriteNotifyBarrier() {
            return new AstWriteNotifyNodeBuilder.StreamNested<AcceptNested<R>>(this);
        }

        @Override
        public R done() {
            AstAcceptNode acceptNode = result.node;
            acceptNode.getAcceptables().add(node);
            return result;
        }

        @Override
        protected int line() {
            return result.line();
        }

        @Override
        protected int line(int line) {
            return result.line(line);
        }

        @Override
        public AstReadOptionNodeBuilder.StreamNested<AcceptNested<R>> addReadOption() {
            return new AstReadOptionNodeBuilder.StreamNested<AcceptNested<R>>(this);
        }

        @Override
        public AstWriteOptionNodeBuilder.StreamNested<AcceptNested<R>> addWriteOption() {
            return new AstWriteOptionNodeBuilder.StreamNested<AcceptNested<R>>(this);
        }

        @Override
        public AstReadConfigNodeBuilder.StreamNested<AcceptNested<R>> addReadConfigEvent() {
            return new AstReadConfigNodeBuilder.StreamNested<AcceptNested<R>>(this);
        }

        @Override
        public AstWriteConfigNodeBuilder.StreamNested<AcceptNested<R>> addWriteConfigCommand() {
            return new AstWriteConfigNodeBuilder.StreamNested<AcceptNested<R>>(this);
        }

        @Override
        public AstFlushNodeBuilder.StreamNested<AcceptNested<R>> addFlushCommand() {
            return new AstFlushNodeBuilder.StreamNested<AcceptNested<R>>(this);
        }

        @Override
        public AstReadClosedNodeBuilder.StreamNested<AcceptNested<R>> addReadCloseCommand() {
            return new AstReadClosedNodeBuilder.StreamNested<AcceptNested<R>>(this);
        }

        @Override
        public AstWriteCloseNodeBuilder.StreamNested<AcceptNested<R>> addWriteCloseCommand() {
            return new AstWriteCloseNodeBuilder.StreamNested<AcceptNested<R>>(this);
        }
    }
}
