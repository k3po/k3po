/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.lang.ast.builder;

import org.kaazing.robot.lang.ast.AstNode;

public abstract class AbstractAstNodeBuilder<N extends AstNode, R> {

    protected final N node;
    protected final R result;

    protected AbstractAstNodeBuilder(N node, R result) {
        this.node = node;
        this.result = result;
    }

    public abstract AbstractAstNodeBuilder<N, R> setNextLineInfo(int linesToSkip, int column);

    public abstract AbstractAstNodeBuilder<N, R> setLocationInfo(int line, int column);

    public abstract R done();

    protected abstract int line();

    protected abstract int line(int line);

    protected void internalSetLineInfo(int line) {
        line(line);
    }

    protected void internalSetNextLineInfo(int linesToSkip, int column) {
        setLocationInfo(line() + linesToSkip, column);
    }
}
