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

import java.net.URI;

import org.kaazing.robot.lang.ast.AstAcceptNode;
import org.kaazing.robot.lang.ast.AstScriptNode;

public final class AstAcceptNodeBuilder extends AbstractAstAcceptNodeBuilder<AstAcceptNode> {

    private int line;

    public AstAcceptNodeBuilder() {
        this(new AstAcceptNode());
    }

    @Override
    public AstAcceptNodeBuilder setLocationInfo(int line, int column) {
        node.setLocationInfo(line, column);
        internalSetLineInfo(line);
        return this;
    }

    @Override
    public AstAcceptNodeBuilder setNextLineInfo(int linesToSkip, int column) {
        internalSetNextLineInfo(linesToSkip, column);
        return this;
    }

    public AstAcceptNodeBuilder setLocation(URI location) {
        node.setLocation(location);
        return this;
    }

    public AstAcceptNodeBuilder setAcceptName(String acceptName) {
        node.setAcceptName(acceptName);
        return this;
    }

    @Override
    public AstOpenedNodeBuilder.StreamNested<AstAcceptNodeBuilder> addOpenedEvent() {
        return new AstOpenedNodeBuilder.StreamNested<AstAcceptNodeBuilder>(this);
    }

    @Override
    public AstBoundNodeBuilder.StreamNested<AstAcceptNodeBuilder> addBoundEvent() {
        return new AstBoundNodeBuilder.StreamNested<AstAcceptNodeBuilder>(this);
    }

    @Override
    public AstChildOpenedNodeBuilder.StreamNested<AstAcceptNodeBuilder> addChildOpenedEvent() {
        return new AstChildOpenedNodeBuilder.StreamNested<AstAcceptNodeBuilder>(this);
    }

    @Override
    public AstChildClosedNodeBuilder.StreamNested<AstAcceptNodeBuilder> addChildClosedEvent() {
        return new AstChildClosedNodeBuilder.StreamNested<AstAcceptNodeBuilder>(this);
    }

    @Override
    public AstUnboundNodeBuilder.StreamNested<AstAcceptNodeBuilder> addUnboundEvent() {
        return new AstUnboundNodeBuilder.StreamNested<AstAcceptNodeBuilder>(this);
    }

    @Override
    public AstClosedNodeBuilder.StreamNested<AstAcceptNodeBuilder> addClosedEvent() {
        return new AstClosedNodeBuilder.StreamNested<AstAcceptNodeBuilder>(this);
    }

    @Override
    public AstReadAwaitNodeBuilder.StreamNested<AstAcceptNodeBuilder> addReadAwaitBarrier() {
        return new AstReadAwaitNodeBuilder.StreamNested<AstAcceptNodeBuilder>(this);
    }

    @Override
    public AstReadNotifyNodeBuilder.StreamNested<AstAcceptNodeBuilder> addReadNotifyBarrier() {
        return new AstReadNotifyNodeBuilder.StreamNested<AstAcceptNodeBuilder>(this);
    }

    @Override
    public AstWriteAwaitNodeBuilder.StreamNested<AstAcceptNodeBuilder> addWriteAwaitBarrier() {
        return new AstWriteAwaitNodeBuilder.StreamNested<AstAcceptNodeBuilder>(this);
    }

    @Override
    public AstWriteNotifyNodeBuilder.StreamNested<AstAcceptNodeBuilder> addWriteNotifyBarrier() {
        return new AstWriteNotifyNodeBuilder.StreamNested<AstAcceptNodeBuilder>(this);
    }

    @Override
    public AstAcceptNode done() {
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

    private AstAcceptNodeBuilder(AstAcceptNode node) {
        super(node, node);
    }

    public static final class ScriptNested<R extends AbstractAstNodeBuilder<? extends AstScriptNode, ?>> extends
            AbstractAstAcceptNodeBuilder<R> {

        public ScriptNested(R builder) {
            super(new AstAcceptNode(), builder);
        }

        @Override
        public int line() {
            return result.line();
        }

        @Override
        public int line(int line) {
            return result.line(line);
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

        public ScriptNested<R> setLocation(URI location) {
            node.setLocation(location);
            return this;
        }

        public ScriptNested<R> setAcceptName(String acceptName) {
            node.setAcceptName(acceptName);
            return this;
        }

        public AstAcceptableNodeBuilder.AcceptNested<ScriptNested<R>> addAcceptedStream() {
            return new AstAcceptableNodeBuilder.AcceptNested<ScriptNested<R>>(this);
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
        public AstChildOpenedNodeBuilder.StreamNested<ScriptNested<R>> addChildOpenedEvent() {
            return new AstChildOpenedNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstChildClosedNodeBuilder.StreamNested<ScriptNested<R>> addChildClosedEvent() {
            return new AstChildClosedNodeBuilder.StreamNested<ScriptNested<R>>(this);
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
    }

}
