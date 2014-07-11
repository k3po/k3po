/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.lang.ast.builder;

import org.kaazing.robot.lang.ast.AstAcceptableNode;

public abstract class AbstractAstAcceptableNodeBuilder<R>
    extends AbstractAstStreamNodeBuilder<AstAcceptableNode, R> {

    public AbstractAstAcceptableNodeBuilder(R result) {
        super(new AstAcceptableNode(), result);
    }

    protected AbstractAstAcceptableNodeBuilder(AstAcceptableNode node, R result) {
        super(node, result);
    }
}
