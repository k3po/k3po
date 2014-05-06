/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.lang.ast.builder;

import org.kaazing.robot.lang.ast.AstAcceptNode;

public abstract class AbstractAstAcceptNodeBuilder<R>
    extends AbstractAstServerStreamNodeBuilder<AstAcceptNode, R> {

    public AbstractAstAcceptNodeBuilder(R result) {
        super(new AstAcceptNode(), result);
    }

    protected AbstractAstAcceptNodeBuilder(AstAcceptNode node, R result) {
        super(node, result);
    }
}
