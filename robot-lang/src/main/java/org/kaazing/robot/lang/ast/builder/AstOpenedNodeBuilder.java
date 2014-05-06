/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.lang.ast.builder;

import org.kaazing.robot.lang.ast.AstOpenedNode;
import org.kaazing.robot.lang.ast.AstStreamNode;

public class AstOpenedNodeBuilder extends AbstractAstStreamableNodeBuilder<AstOpenedNode, AstOpenedNode> {

    private int line;

    public AstOpenedNodeBuilder() {
        this(new AstOpenedNode());
    }

    @Override
    public AstOpenedNodeBuilder setLocationInfo(int line, int column) {
        node.setLocationInfo(line, column);
        internalSetLineInfo(line);
        return this;
    }

    @Override
    public AstOpenedNodeBuilder setNextLineInfo(int linesToSkip, int column) {
        internalSetNextLineInfo(linesToSkip, column);
        return this;
    }

    @Override
    public AstOpenedNode done() {
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

    private AstOpenedNodeBuilder(AstOpenedNode node) {
        super(node, node);
    }

    public static class StreamNested<R extends AbstractAstNodeBuilder<? extends AstStreamNode, ?>> extends
            AbstractAstStreamableNodeBuilder<AstOpenedNode, R> {

        public StreamNested(R builder) {
            super(new AstOpenedNode(), builder);
        }

        @Override
        public StreamNested<R> setLocationInfo(int line, int column) {
            node.setLocationInfo(line, column);
            internalSetLineInfo(line);
            return this;
        }

        @Override
        public StreamNested<R> setNextLineInfo(int linesToSkip, int column) {
            internalSetNextLineInfo(linesToSkip, column);
            return this;
        }

        @Override
        public R done() {
            AstStreamNode streamNode = result.node;
            streamNode.getStreamables().add(node);
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
