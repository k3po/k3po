/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.lang.ast.builder;

import javax.el.ValueExpression;

import org.kaazing.robot.lang.ast.AstStreamNode;
import org.kaazing.robot.lang.ast.AstWriteValueNode;
import org.kaazing.robot.lang.ast.value.AstExpressionValue;
import org.kaazing.robot.lang.ast.value.AstLiteralBytesValue;
import org.kaazing.robot.lang.ast.value.AstLiteralTextValue;

public class AstWriteNodeBuilder extends AbstractAstStreamableNodeBuilder<AstWriteValueNode, AstWriteValueNode> {

    private int line;

    public AstWriteNodeBuilder() {
        this(new AstWriteValueNode());
    }

    @Override
    public AstWriteNodeBuilder setLocationInfo(int line, int column) {
        node.setLocationInfo(line, column);
        internalSetLineInfo(line);
        return this;
    }

    @Override
    public AstWriteNodeBuilder setNextLineInfo(int linesToSkip, int column) {
        internalSetNextLineInfo(linesToSkip, column);
        return this;
    }

    public AstWriteNodeBuilder addExactBytes(byte[] exactBytes) {
        node.addValue(new AstLiteralBytesValue(exactBytes));
        return this;
    }

    public AstWriteNodeBuilder addExactText(String exactText) {
        node.addValue(new AstLiteralTextValue(exactText));
        return this;
    }

    public AstWriteNodeBuilder addExpression(ValueExpression value) {
        node.addValue(new AstExpressionValue(value));
        return this;
    }

    @Override
    public AstWriteValueNode done() {
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

    private AstWriteNodeBuilder(AstWriteValueNode node) {
        super(node, node);
    }

    public static class StreamNested<R extends AbstractAstNodeBuilder<? extends AstStreamNode, ?>> extends
            AbstractAstStreamableNodeBuilder<AstWriteValueNode, R> {

        public StreamNested(R builder) {
            super(new AstWriteValueNode(), builder);
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

        public StreamNested<R> addExactBytes(byte[] exactBytes) {
            node.addValue(new AstLiteralBytesValue(exactBytes));
            return this;
        }

        public StreamNested<R> addExactText(String exactText) {
            node.addValue(new AstLiteralTextValue(exactText));
            return this;
        }

        public StreamNested<R> addExpression(ValueExpression value) {
            node.addValue(new AstExpressionValue(value));
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
