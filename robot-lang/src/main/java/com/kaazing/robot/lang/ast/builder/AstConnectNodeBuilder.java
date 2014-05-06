/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.lang.ast.builder;

import java.net.URI;

import com.kaazing.robot.lang.ast.AstConnectNode;
import com.kaazing.robot.lang.ast.AstEndOfHttpHeadersNode;
import com.kaazing.robot.lang.ast.AstScriptNode;

public final class AstConnectNodeBuilder extends AbstractAstConnectNodeBuilder<AstConnectNode> {

    private int line;

    public AstConnectNodeBuilder() {
        this(new AstConnectNode());
    }

    @Override
    public AstConnectNodeBuilder setLocationInfo(int line, int column) {
        node.setLocationInfo(line, column);
        internalSetLineInfo(line);
        return this;
    }

    @Override
    public AstConnectNodeBuilder setNextLineInfo(int linesToSkip, int column) {
        internalSetNextLineInfo(linesToSkip, column);
        return this;
    }

    public AstConnectNodeBuilder setLocation(URI location) {
        node.setLocation(location);
        return this;
    }

    @Override
    public AstOpenedNodeBuilder.StreamNested<AstConnectNodeBuilder> addOpenedEvent() {
        return new AstOpenedNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstBoundNodeBuilder.StreamNested<AstConnectNodeBuilder> addBoundEvent() {
        return new AstBoundNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstConnectedNodeBuilder.StreamNested<AstConnectNodeBuilder> addConnectedEvent() {
        return new AstConnectedNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstReadNodeBuilder.StreamNested<AstConnectNodeBuilder> addReadEvent() {
        return new AstReadNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstDisconnectedNodeBuilder.StreamNested<AstConnectNodeBuilder> addDisconnectedEvent() {
        return new AstDisconnectedNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstUnboundNodeBuilder.StreamNested<AstConnectNodeBuilder> addUnboundEvent() {
        return new AstUnboundNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstClosedNodeBuilder.StreamNested<AstConnectNodeBuilder> addClosedEvent() {
        return new AstClosedNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstWriteNodeBuilder.StreamNested<AstConnectNodeBuilder> addWriteCommand() {
        return new AstWriteNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstDisconnectNodeBuilder.StreamNested<AstConnectNodeBuilder> addDisconnectCommand() {
        return new AstDisconnectNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstUnbindNodeBuilder.StreamNested<AstConnectNodeBuilder> addUnbindCommand() {
        return new AstUnbindNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstCloseNodeBuilder.StreamNested<AstConnectNodeBuilder> addCloseCommand() {
        return new AstCloseNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstReadAwaitNodeBuilder.StreamNested<AstConnectNodeBuilder> addReadAwaitBarrier() {
        return new AstReadAwaitNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstReadNotifyNodeBuilder.StreamNested<AstConnectNodeBuilder> addReadNotifyBarrier() {
        return new AstReadNotifyNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstWriteAwaitNodeBuilder.StreamNested<AstConnectNodeBuilder> addWriteAwaitBarrier() {
        return new AstWriteAwaitNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstWriteNotifyNodeBuilder.StreamNested<AstConnectNodeBuilder> addWriteNotifyBarrier() {
        return new AstWriteNotifyNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    // Http
    @Override
    public AstReadHttpHeaderNodeBuilder.StreamNested<AstConnectNodeBuilder> addReadHttpHeaderEvent() {
        return new AstReadHttpHeaderNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstWriteHttpHeaderNodeBuilder.StreamNested<AstConnectNodeBuilder> addWriteHttpHeaderCommand() {
        return new AstWriteHttpHeaderNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstWriteHttpContentLengthNodeBuilder.StreamNested<AstConnectNodeBuilder> addWriteHttpContentLengthCommand() {
        return new AstWriteHttpContentLengthNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstReadHttpMethodNodeBuilder.StreamNested<AstConnectNodeBuilder> addReadHttpMethodEvent() {
        return new AstReadHttpMethodNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstWriteHttpMethodNodeBuilder.StreamNested<AstConnectNodeBuilder> addWriteHttpMethodCommand() {
        return new AstWriteHttpMethodNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstReadHttpParameterNodeBuilder.StreamNested<AstConnectNodeBuilder> addReadHttpParameterEvent() {
        return new AstReadHttpParameterNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstWriteHttpParameterNodeBuilder.StreamNested<AstConnectNodeBuilder> addWriteHttpParameterCommand() {
        return new AstWriteHttpParameterNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstReadHttpVersionNodeBuilder.StreamNested<AstConnectNodeBuilder> addReadHttpVersionEvent() {
        return new AstReadHttpVersionNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstWriteHttpVersionNodeBuilder.StreamNested<AstConnectNodeBuilder> addWriteHttpVersionCommand() {
        return new AstWriteHttpVersionNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstReadHttpStatusNodeBuilder.StreamNested<AstConnectNodeBuilder> addReadHttpStatusEvent() {
        return new AstReadHttpStatusNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstWriteHttpStatusNodeBuilder.StreamNested<AstConnectNodeBuilder> addWriteHttpStatusCommand() {
        return new AstWriteHttpStatusNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstCloseHttpRequestNodeBuilder.StreamNested<AstConnectNodeBuilder> addCloseHttpRequestCommand() {
        return new AstCloseHttpRequestNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstCloseHttpResponseNodeBuilder.StreamNested<AstConnectNodeBuilder> addCloseHttpResponseCommand() {
        return new AstCloseHttpResponseNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstEndOfHttpHeadersNodeBuilder.StreamNested<AstConnectNodeBuilder> addEndOfHeadersCommand() {
        return new AstEndOfHttpHeadersNodeBuilder.StreamNested<AstConnectNodeBuilder>(this);
    }

    @Override
    public AstConnectNode done() {
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

    private AstConnectNodeBuilder(AstConnectNode node) {
        super(node, node);
    }

    public static final class ScriptNested<R extends AbstractAstNodeBuilder<? extends AstScriptNode, ?>> extends
            AbstractAstConnectNodeBuilder<R> {

        public ScriptNested(R builder) {
            super(new AstConnectNode(), builder);
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

    }

}
