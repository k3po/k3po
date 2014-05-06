/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.lang.ast.builder;

import com.kaazing.robot.lang.ast.AstBoundNode;
import com.kaazing.robot.lang.ast.AstCloseNode;
import com.kaazing.robot.lang.ast.AstClosedNode;
import com.kaazing.robot.lang.ast.AstConnectedNode;
import com.kaazing.robot.lang.ast.AstDisconnectNode;
import com.kaazing.robot.lang.ast.AstDisconnectedNode;
import com.kaazing.robot.lang.ast.AstOpenedNode;
import com.kaazing.robot.lang.ast.AstReadValueNode;
import com.kaazing.robot.lang.ast.AstScriptNode;
import com.kaazing.robot.lang.ast.AstStreamNode;
import com.kaazing.robot.lang.ast.AstUnbindNode;
import com.kaazing.robot.lang.ast.AstUnboundNode;
import com.kaazing.robot.lang.ast.AstWriteValueNode;

public abstract class AstStreamNodeBuilder<T extends AstStreamNode> extends
        AbstractAstStreamNodeBuilder<T, AstScriptNodeBuilder> {

    public AstStreamNodeBuilder(T node, AstScriptNodeBuilder builder) {
        super(node, builder);
    }

    @Override
    public int line() {
        return result.line();
    }

    @Override
    public int line(int line) {
        return result.line(line);
    }

    public abstract AbstractAstStreamableNodeBuilder<AstOpenedNode, ? extends AstStreamNodeBuilder<T>> addOpenedEvent();

    public abstract AbstractAstStreamableNodeBuilder<AstBoundNode, ? extends AstStreamNodeBuilder<T>> addBoundEvent();

    public abstract AbstractAstStreamableNodeBuilder<AstConnectedNode, ? extends AstStreamNodeBuilder<T>> addConnectedEvent();

    public abstract AbstractAstStreamableNodeBuilder<AstReadValueNode, ? extends AstStreamNodeBuilder<T>> addReadEvent();

    public abstract AbstractAstStreamableNodeBuilder<AstDisconnectedNode, ? extends AstStreamNodeBuilder<T>>
            addDisconnectedEvent();

    public abstract AbstractAstStreamableNodeBuilder<AstUnboundNode, ? extends AstStreamNodeBuilder<T>> addUnboundEvent();

    public abstract AbstractAstStreamableNodeBuilder<AstClosedNode, ? extends AstStreamNodeBuilder<T>> addClosedEvent();

    public abstract AbstractAstStreamableNodeBuilder<AstWriteValueNode, ? extends AstStreamNodeBuilder<T>> addWriteCommand();

    public abstract AbstractAstStreamableNodeBuilder<AstDisconnectNode, ? extends AstStreamNodeBuilder<T>>
            addDisconnectCommand();

    public abstract AbstractAstStreamableNodeBuilder<AstUnbindNode, ? extends AstStreamNodeBuilder<T>> addUnbindCommand();

    public abstract AbstractAstStreamableNodeBuilder<AstCloseNode, ? extends AstStreamNodeBuilder<T>> addCloseCommand();

    @Override
    public final AstScriptNodeBuilder done() {
        AstScriptNode scriptNode = result.node;
        scriptNode.getStreams().add(node);
        return result;
    }
}
