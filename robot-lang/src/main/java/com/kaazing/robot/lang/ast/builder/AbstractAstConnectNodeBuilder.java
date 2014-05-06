/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.lang.ast.builder;

import com.kaazing.robot.lang.ast.AstConnectNode;

public abstract class AbstractAstConnectNodeBuilder<R>
    extends AbstractAstStreamNodeBuilder<AstConnectNode, R> {

    public AbstractAstConnectNodeBuilder(R result) {
        super(new AstConnectNode(), result);
    }

    protected AbstractAstConnectNodeBuilder(AstConnectNode node, R result) {
        super(node, result);
    }
}
