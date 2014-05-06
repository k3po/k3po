/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.lang.ast.builder;

import com.kaazing.robot.lang.ast.AstBoundNode;
import com.kaazing.robot.lang.ast.AstCloseHttpRequestNode;
import com.kaazing.robot.lang.ast.AstCloseHttpResponseNode;
import com.kaazing.robot.lang.ast.AstCloseNode;
import com.kaazing.robot.lang.ast.AstClosedNode;
import com.kaazing.robot.lang.ast.AstConnectedNode;
import com.kaazing.robot.lang.ast.AstDisconnectNode;
import com.kaazing.robot.lang.ast.AstDisconnectedNode;
import com.kaazing.robot.lang.ast.AstEndOfHttpHeadersNode;
import com.kaazing.robot.lang.ast.AstOpenedNode;
import com.kaazing.robot.lang.ast.AstReadAwaitNode;
import com.kaazing.robot.lang.ast.AstReadHttpHeaderNode;
import com.kaazing.robot.lang.ast.AstReadHttpMethodNode;
import com.kaazing.robot.lang.ast.AstReadHttpParameterNode;
import com.kaazing.robot.lang.ast.AstReadHttpStatusNode;
import com.kaazing.robot.lang.ast.AstReadHttpVersionNode;
import com.kaazing.robot.lang.ast.AstReadNotifyNode;
import com.kaazing.robot.lang.ast.AstReadValueNode;
import com.kaazing.robot.lang.ast.AstStreamNode;
import com.kaazing.robot.lang.ast.AstUnbindNode;
import com.kaazing.robot.lang.ast.AstUnboundNode;
import com.kaazing.robot.lang.ast.AstWriteAwaitNode;
import com.kaazing.robot.lang.ast.AstWriteHttpContentLengthNode;
import com.kaazing.robot.lang.ast.AstWriteHttpHeaderNode;
import com.kaazing.robot.lang.ast.AstWriteHttpMethodNode;
import com.kaazing.robot.lang.ast.AstWriteHttpParameterNode;
import com.kaazing.robot.lang.ast.AstWriteHttpStatusNode;
import com.kaazing.robot.lang.ast.AstWriteHttpVersionNode;
import com.kaazing.robot.lang.ast.AstWriteNotifyNode;
import com.kaazing.robot.lang.ast.AstWriteValueNode;

public abstract class AbstractAstStreamNodeBuilder<T extends AstStreamNode, R> extends AbstractAstNodeBuilder<T, R> {

    public AbstractAstStreamNodeBuilder(T node, R result) {
        super(node, result);
    }

    public abstract AbstractAstStreamableNodeBuilder<AstOpenedNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
            addOpenedEvent();

    public abstract AbstractAstStreamableNodeBuilder<AstBoundNode, ? extends AbstractAstStreamNodeBuilder<T, R>> addBoundEvent();

    public abstract AbstractAstStreamableNodeBuilder<AstConnectedNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
            addConnectedEvent();

    public abstract AbstractAstStreamableNodeBuilder<AstReadValueNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
            addReadEvent();

    public abstract AbstractAstStreamableNodeBuilder<AstDisconnectedNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
            addDisconnectedEvent();

    public abstract AbstractAstStreamableNodeBuilder<AstUnboundNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
            addUnboundEvent();

    public abstract AbstractAstStreamableNodeBuilder<AstClosedNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
            addClosedEvent();

    public abstract AbstractAstStreamableNodeBuilder<AstWriteValueNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
            addWriteCommand();

    public abstract AbstractAstStreamableNodeBuilder<AstDisconnectNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
            addDisconnectCommand();

    public abstract AbstractAstStreamableNodeBuilder<AstUnbindNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
            addUnbindCommand();

    public abstract AbstractAstStreamableNodeBuilder<AstCloseNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
            addCloseCommand();

    public abstract AbstractAstStreamableNodeBuilder<AstReadAwaitNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
            addReadAwaitBarrier();

    public abstract AbstractAstStreamableNodeBuilder<AstReadNotifyNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
            addReadNotifyBarrier();

    public abstract AbstractAstStreamableNodeBuilder<AstWriteAwaitNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
            addWriteAwaitBarrier();

    public abstract AbstractAstStreamableNodeBuilder<AstWriteNotifyNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
            addWriteNotifyBarrier();

    // http
    public abstract AbstractAstStreamableNodeBuilder<AstReadHttpHeaderNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
        addReadHttpHeaderEvent();

    public abstract AbstractAstStreamableNodeBuilder<AstWriteHttpHeaderNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
        addWriteHttpHeaderCommand();

    public abstract AbstractAstStreamableNodeBuilder<AstWriteHttpContentLengthNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
        addWriteHttpContentLengthCommand();

    public abstract AbstractAstStreamableNodeBuilder<AstReadHttpMethodNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
        addReadHttpMethodEvent();

    public abstract AbstractAstStreamableNodeBuilder<AstWriteHttpMethodNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
        addWriteHttpMethodCommand();

    public abstract AbstractAstStreamableNodeBuilder<AstReadHttpParameterNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
        addReadHttpParameterEvent();

    public abstract AbstractAstStreamableNodeBuilder<AstWriteHttpParameterNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
        addWriteHttpParameterCommand();

    public abstract AbstractAstStreamableNodeBuilder<AstReadHttpVersionNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
        addReadHttpVersionEvent();

    public abstract AbstractAstStreamableNodeBuilder<AstWriteHttpVersionNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
        addWriteHttpVersionCommand();

    public abstract AbstractAstStreamableNodeBuilder<AstReadHttpStatusNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
        addReadHttpStatusEvent();

    public abstract AbstractAstStreamableNodeBuilder<AstWriteHttpStatusNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
        addWriteHttpStatusCommand();

    public abstract AbstractAstStreamableNodeBuilder<AstCloseHttpRequestNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
        addCloseHttpRequestCommand();

    public abstract AbstractAstStreamableNodeBuilder<AstCloseHttpResponseNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
        addCloseHttpResponseCommand();

    public abstract AbstractAstStreamableNodeBuilder<AstEndOfHttpHeadersNode, ? extends AbstractAstStreamNodeBuilder<T, R>>
        addEndOfHeadersCommand();
}
