/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.lang.ast.builder;

import com.kaazing.robot.lang.ast.AstEndOfHttpHeadersNode;
import com.kaazing.robot.lang.ast.AstStreamNode;

public class AstEndOfHttpHeadersNodeBuilder extends
        AbstractAstStreamableNodeBuilder<AstEndOfHttpHeadersNode, AstEndOfHttpHeadersNode> {

    private int line;

    public AstEndOfHttpHeadersNodeBuilder() {
        this(new AstEndOfHttpHeadersNode());
    }

    private AstEndOfHttpHeadersNodeBuilder(AstEndOfHttpHeadersNode node) {
        super(node, node);
    }

    @Override
    public AstEndOfHttpHeadersNodeBuilder setLocationInfo(int line, int column) {
        node.setLocationInfo(line, column);
        internalSetLineInfo(line);
        return this;
    }

    @Override
    public AstEndOfHttpHeadersNodeBuilder setNextLineInfo(int linesToSkip, int column) {
        internalSetNextLineInfo(linesToSkip, column);
        return this;
    }

    @Override
    public AstEndOfHttpHeadersNode done() {
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

    public static class StreamNested<R extends AbstractAstNodeBuilder<? extends AstStreamNode, ?>> extends
            AbstractAstStreamableNodeBuilder<AstEndOfHttpHeadersNode, R> {

        public StreamNested(R builder) {
            super(new AstEndOfHttpHeadersNode(), builder);
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
