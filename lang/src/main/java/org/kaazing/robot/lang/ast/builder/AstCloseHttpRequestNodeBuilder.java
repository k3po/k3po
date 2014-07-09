/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.lang.ast.builder;

import org.kaazing.robot.lang.ast.AstStreamNode;
import org.kaazing.robot.lang.ast.AstCloseHttpRequestNode;

public class AstCloseHttpRequestNodeBuilder extends
        AbstractAstStreamableNodeBuilder<AstCloseHttpRequestNode, AstCloseHttpRequestNode> {

    private int line;

    public AstCloseHttpRequestNodeBuilder() {
        this(new AstCloseHttpRequestNode());
    }

    private AstCloseHttpRequestNodeBuilder(AstCloseHttpRequestNode node) {
        super(node, node);
    }

    @Override
    public AstCloseHttpRequestNodeBuilder setLocationInfo(int line, int column) {
        node.setLocationInfo(line, column);
        internalSetLineInfo(line);
        return this;
    }

    @Override
    public AstCloseHttpRequestNodeBuilder setNextLineInfo(int linesToSkip, int column) {
        internalSetNextLineInfo(linesToSkip, column);
        return this;
    }

    @Override
    public AstCloseHttpRequestNode done() {
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
            AbstractAstStreamableNodeBuilder<AstCloseHttpRequestNode, R> {

        public StreamNested(R builder) {
            super(new AstCloseHttpRequestNode(), builder);
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
