/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.lang.ast.builder;

import org.kaazing.robot.lang.ast.AstStreamNode;
import org.kaazing.robot.lang.ast.AstWriteHttpMethodNode;
import org.kaazing.robot.lang.ast.value.AstExpressionValue;
import org.kaazing.robot.lang.ast.value.AstLiteralBytesValue;
import org.kaazing.robot.lang.ast.value.AstLiteralTextValue;

import javax.el.ValueExpression;

public class AstWriteHttpMethodNodeBuilder extends
        AbstractAstStreamableNodeBuilder<AstWriteHttpMethodNode, AstWriteHttpMethodNode> {

    private int line;

    public AstWriteHttpMethodNodeBuilder() {
        this(new AstWriteHttpMethodNode());
    }

    @Override
    public AstWriteHttpMethodNodeBuilder setLocationInfo(int line, int column) {
        node.setLocationInfo(line, column);
        internalSetLineInfo(line);
        return this;
    }

    @Override
    public AstWriteHttpMethodNodeBuilder setNextLineInfo(int linesToSkip, int column) {
        internalSetNextLineInfo(linesToSkip, column);
        return this;
    }

    public AstWriteHttpMethodNodeBuilder setExactBytes(byte[] exactBytes) {
        node.setMethod(new AstLiteralBytesValue(exactBytes));
        return this;
    }

    public AstWriteHttpMethodNodeBuilder setExactText(String exactText) {
        node.setMethod(new AstLiteralTextValue(exactText));
        return this;
    }

    public AstWriteHttpMethodNodeBuilder setExpression(ValueExpression value) {
        node.setMethod(new AstExpressionValue(value));
        return this;
    }

    @Override
    public AstWriteHttpMethodNode done() {
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

    private AstWriteHttpMethodNodeBuilder(AstWriteHttpMethodNode node) {
        super(node, node);
    }

    public static class StreamNested<R extends AbstractAstNodeBuilder<? extends AstStreamNode, ?>> extends
            AbstractAstStreamableNodeBuilder<AstWriteHttpMethodNode, R> {

        public StreamNested(R builder) {
            super(new AstWriteHttpMethodNode(), builder);
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

        public StreamNested<R> setExactBytes(byte[] exactBytes) {
            node.setMethod(new AstLiteralBytesValue(exactBytes));
            return this;
        }

        public StreamNested<R> setExactText(String exactText) {
            node.setMethod(new AstLiteralTextValue(exactText));
            return this;
        }

        public StreamNested<R> setExpression(ValueExpression value) {
            node.setMethod(new AstExpressionValue(value));
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
