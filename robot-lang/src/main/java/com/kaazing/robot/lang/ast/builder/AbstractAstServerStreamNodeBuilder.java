/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.lang.ast.builder;

import com.kaazing.robot.lang.ast.AstBoundNode;
import com.kaazing.robot.lang.ast.AstChildClosedNode;
import com.kaazing.robot.lang.ast.AstChildOpenedNode;
import com.kaazing.robot.lang.ast.AstClosedNode;
import com.kaazing.robot.lang.ast.AstOpenedNode;
import com.kaazing.robot.lang.ast.AstReadAwaitNode;
import com.kaazing.robot.lang.ast.AstReadNotifyNode;
import com.kaazing.robot.lang.ast.AstStreamNode;
import com.kaazing.robot.lang.ast.AstUnboundNode;
import com.kaazing.robot.lang.ast.AstWriteAwaitNode;
import com.kaazing.robot.lang.ast.AstWriteNotifyNode;

public abstract class AbstractAstServerStreamNodeBuilder<T extends AstStreamNode, R> extends AbstractAstNodeBuilder<T, R> {

    public AbstractAstServerStreamNodeBuilder(T node, R result) {
        super(node, result);
    }

    public abstract AbstractAstNodeBuilder<AstOpenedNode, ? extends AbstractAstNodeBuilder<T, ?>> addOpenedEvent();

    public abstract AbstractAstNodeBuilder<AstBoundNode, ? extends AbstractAstNodeBuilder<T, ?>> addBoundEvent();

    public abstract AbstractAstNodeBuilder<AstChildOpenedNode, ? extends AbstractAstNodeBuilder<T, ?>> addChildOpenedEvent();

    public abstract AbstractAstNodeBuilder<AstChildClosedNode, ? extends AbstractAstNodeBuilder<T, ?>> addChildClosedEvent();

    public abstract AbstractAstNodeBuilder<AstUnboundNode, ? extends AbstractAstNodeBuilder<T, ?>> addUnboundEvent();

    public abstract AbstractAstNodeBuilder<AstClosedNode, ? extends AbstractAstNodeBuilder<T, ?>> addClosedEvent();

    public abstract AbstractAstNodeBuilder<AstReadAwaitNode, ? extends AbstractAstNodeBuilder<T, R>> addReadAwaitBarrier();

    public abstract AbstractAstNodeBuilder<AstReadNotifyNode, ? extends AbstractAstNodeBuilder<T, R>> addReadNotifyBarrier();

    public abstract AbstractAstNodeBuilder<AstWriteAwaitNode, ? extends AbstractAstNodeBuilder<T, R>> addWriteAwaitBarrier();

    public abstract AbstractAstNodeBuilder<AstWriteNotifyNode, ? extends AbstractAstNodeBuilder<T, R>> addWriteNotifyBarrier();
}
