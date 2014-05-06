/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.lang.ast.builder;

import com.kaazing.robot.lang.ast.AstStreamableNode;

public abstract class AbstractAstStreamableNodeBuilder<N extends AstStreamableNode, R> extends AbstractAstNodeBuilder<N, R> {

    public AbstractAstStreamableNodeBuilder(N node, R result) {
        super(node, result);
    }
}
