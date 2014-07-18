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

    // Http
    @Override
    public AstReadHttpHeaderNodeBuilder.StreamNested<AstAcceptableNodeBuilder> addReadHttpHeaderEvent() {
        return new AstReadHttpHeaderNodeBuilder.StreamNested<AstAcceptableNodeBuilder>(this);
    }

    @Override
    public AstWriteHttpHeaderNodeBuilder.StreamNested<AstAcceptableNodeBuilder> addWriteHttpHeaderCommand() {
        return new AstWriteHttpHeaderNodeBuilder.StreamNested<AstAcceptableNodeBuilder>(this);
    }

    @Override
    public AstWriteHttpContentLengthNodeBuilder.StreamNested<AstAcceptableNodeBuilder> addWriteHttpContentLengthCommand() {
        return new AstWriteHttpContentLengthNodeBuilder.StreamNested<AstAcceptableNodeBuilder>(this);
    }

    @Override
    public AstReadHttpMethodNodeBuilder.StreamNested<AstAcceptableNodeBuilder> addReadHttpMethodEvent() {
        return new AstReadHttpMethodNodeBuilder.StreamNested<AstAcceptableNodeBuilder>(this);
    }

    @Override
    public AstWriteHttpMethodNodeBuilder.StreamNested<AstAcceptableNodeBuilder> addWriteHttpMethodCommand() {
        return new AstWriteHttpMethodNodeBuilder.StreamNested<AstAcceptableNodeBuilder>(this);
    }

    @Override
    public AstReadHttpParameterNodeBuilder.StreamNested<AstAcceptableNodeBuilder> addReadHttpParameterEvent() {
        return new AstReadHttpParameterNodeBuilder.StreamNested<AstAcceptableNodeBuilder>(this);
    }

    @Override
    public AstWriteHttpParameterNodeBuilder.StreamNested<AstAcceptableNodeBuilder> addWriteHttpParameterCommand() {
        return new AstWriteHttpParameterNodeBuilder.StreamNested<AstAcceptableNodeBuilder>(this);
    }

    @Override
    public AstReadHttpVersionNodeBuilder.StreamNested<AstAcceptableNodeBuilder> addReadHttpVersionEvent() {
        return new AstReadHttpVersionNodeBuilder.StreamNested<AstAcceptableNodeBuilder>(this);
    }

    @Override
    public AstWriteHttpVersionNodeBuilder.StreamNested<AstAcceptableNodeBuilder> addWriteHttpVersionCommand() {
        return new AstWriteHttpVersionNodeBuilder.StreamNested<AstAcceptableNodeBuilder>(this);
    }

    @Override
    public AstReadHttpStatusNodeBuilder.StreamNested<AstAcceptableNodeBuilder> addReadHttpStatusEvent() {
        return new AstReadHttpStatusNodeBuilder.StreamNested<AstAcceptableNodeBuilder>(this);
    }

    @Override
    public AstWriteHttpStatusNodeBuilder.StreamNested<AstAcceptableNodeBuilder> addWriteHttpStatusCommand() {
        return new AstWriteHttpStatusNodeBuilder.StreamNested<AstAcceptableNodeBuilder>(this);
    }

    @Override
    public AstCloseHttpRequestNodeBuilder.StreamNested<AstAcceptableNodeBuilder> addCloseHttpRequestCommand() {
        return new AstCloseHttpRequestNodeBuilder.StreamNested<AstAcceptableNodeBuilder>(this);
    }

    @Override
    public AstCloseHttpResponseNodeBuilder.StreamNested<AstAcceptableNodeBuilder> addCloseHttpResponseCommand() {
        return new AstCloseHttpResponseNodeBuilder.StreamNested<AstAcceptableNodeBuilder>(this);
    }

    @Override
    public AstEndOfHttpHeadersNodeBuilder.StreamNested<AstAcceptableNodeBuilder> addEndOfHeadersCommand() {
        return new AstEndOfHttpHeadersNodeBuilder.StreamNested<AstAcceptableNodeBuilder>(this);
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

        // HTTP
        @Override
        public AstReadHttpHeaderNodeBuilder.StreamNested<ScriptNested<R>> addReadHttpHeaderEvent() {
            return new AstReadHttpHeaderNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstWriteHttpHeaderNodeBuilder.StreamNested<ScriptNested<R>> addWriteHttpHeaderCommand() {
            return new AstWriteHttpHeaderNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstWriteHttpContentLengthNodeBuilder.StreamNested<ScriptNested<R>> addWriteHttpContentLengthCommand() {
            return new AstWriteHttpContentLengthNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstReadHttpMethodNodeBuilder.StreamNested<ScriptNested<R>> addReadHttpMethodEvent() {
            return new AstReadHttpMethodNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstWriteHttpMethodNodeBuilder.StreamNested<ScriptNested<R>> addWriteHttpMethodCommand() {
            return new AstWriteHttpMethodNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstReadHttpParameterNodeBuilder.StreamNested<ScriptNested<R>> addReadHttpParameterEvent() {
            return new AstReadHttpParameterNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstWriteHttpParameterNodeBuilder.StreamNested<ScriptNested<R>> addWriteHttpParameterCommand() {
            return new AstWriteHttpParameterNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstReadHttpVersionNodeBuilder.StreamNested<ScriptNested<R>> addReadHttpVersionEvent() {
            return new AstReadHttpVersionNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstWriteHttpVersionNodeBuilder.StreamNested<ScriptNested<R>> addWriteHttpVersionCommand() {
            return new AstWriteHttpVersionNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstReadHttpStatusNodeBuilder.StreamNested<ScriptNested<R>> addReadHttpStatusEvent() {
            return new AstReadHttpStatusNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstWriteHttpStatusNodeBuilder.StreamNested<ScriptNested<R>> addWriteHttpStatusCommand() {
            return new AstWriteHttpStatusNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstCloseHttpRequestNodeBuilder.StreamNested<ScriptNested<R>> addCloseHttpRequestCommand() {
            return new AstCloseHttpRequestNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstCloseHttpResponseNodeBuilder.StreamNested<ScriptNested<R>> addCloseHttpResponseCommand() {
            return new AstCloseHttpResponseNodeBuilder.StreamNested<ScriptNested<R>>(this);
        }

        @Override
        public AstEndOfHttpHeadersNodeBuilder.StreamNested<ScriptNested<R>> addEndOfHeadersCommand() {
            return new AstEndOfHttpHeadersNodeBuilder.StreamNested<ScriptNested<R>>(this);
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

        // Http
        @Override
        public AstReadHttpHeaderNodeBuilder.StreamNested<AcceptNested<R>> addReadHttpHeaderEvent() {
            return new AstReadHttpHeaderNodeBuilder.StreamNested<AcceptNested<R>>(this);
        }

        @Override
        public AstWriteHttpHeaderNodeBuilder.StreamNested<AcceptNested<R>> addWriteHttpHeaderCommand() {
            return new AstWriteHttpHeaderNodeBuilder.StreamNested<AcceptNested<R>>(this);
        }

        @Override
        public AstWriteHttpContentLengthNodeBuilder.StreamNested<AcceptNested<R>> addWriteHttpContentLengthCommand() {
            return new AstWriteHttpContentLengthNodeBuilder.StreamNested<AcceptNested<R>>(this);
        }

        @Override
        public AstReadHttpMethodNodeBuilder.StreamNested<AcceptNested<R>> addReadHttpMethodEvent() {
            return new AstReadHttpMethodNodeBuilder.StreamNested<AcceptNested<R>>(this);
        }

        @Override
        public AstWriteHttpMethodNodeBuilder.StreamNested<AcceptNested<R>> addWriteHttpMethodCommand() {
            return new AstWriteHttpMethodNodeBuilder.StreamNested<AcceptNested<R>>(this);
        }

        @Override
        public AstReadHttpParameterNodeBuilder.StreamNested<AcceptNested<R>> addReadHttpParameterEvent() {
            return new AstReadHttpParameterNodeBuilder.StreamNested<AcceptNested<R>>(this);
        }

        @Override
        public AstWriteHttpParameterNodeBuilder.StreamNested<AcceptNested<R>> addWriteHttpParameterCommand() {
            return new AstWriteHttpParameterNodeBuilder.StreamNested<AcceptNested<R>>(this);
        }

        @Override
        public AstReadHttpVersionNodeBuilder.StreamNested<AcceptNested<R>> addReadHttpVersionEvent() {
            return new AstReadHttpVersionNodeBuilder.StreamNested<AcceptNested<R>>(this);
        }

        @Override
        public AstWriteHttpVersionNodeBuilder.StreamNested<AcceptNested<R>> addWriteHttpVersionCommand() {
            return new AstWriteHttpVersionNodeBuilder.StreamNested<AcceptNested<R>>(this);
        }

        @Override
        public AstReadHttpStatusNodeBuilder.StreamNested<AcceptNested<R>> addReadHttpStatusEvent() {
            return new AstReadHttpStatusNodeBuilder.StreamNested<AcceptNested<R>>(this);
        }

        @Override
        public AstWriteHttpStatusNodeBuilder.StreamNested<AcceptNested<R>> addWriteHttpStatusCommand() {
            return new AstWriteHttpStatusNodeBuilder.StreamNested<AcceptNested<R>>(this);
        }

        @Override
        public AstCloseHttpRequestNodeBuilder.StreamNested<AcceptNested<R>> addCloseHttpRequestCommand() {
            return new AstCloseHttpRequestNodeBuilder.StreamNested<AcceptNested<R>>(this);
        }

        @Override
        public AstCloseHttpResponseNodeBuilder.StreamNested<AcceptNested<R>> addCloseHttpResponseCommand() {
            return new AstCloseHttpResponseNodeBuilder.StreamNested<AcceptNested<R>>(this);
        }

        @Override
        public AstEndOfHttpHeadersNodeBuilder.StreamNested<AcceptNested<R>> addEndOfHeadersCommand() {
            return new AstEndOfHttpHeadersNodeBuilder.StreamNested<AcceptNested<R>>(this);
        }
    }
}
