/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.lang.ast.builder;

import org.kaazing.robot.lang.ast.AstStreamNode;
import org.kaazing.robot.lang.ast.AstWriteHttpVersionNode;
import org.kaazing.robot.lang.ast.value.AstExpressionValue;
import org.kaazing.robot.lang.ast.value.AstLiteralBytesValue;
import org.kaazing.robot.lang.ast.value.AstLiteralTextValue;

import javax.el.ValueExpression;

public class AstWriteHttpVersionNodeBuilder extends
        AbstractAstStreamableNodeBuilder<AstWriteHttpVersionNode, AstWriteHttpVersionNode> {

    private int line;

    public AstWriteHttpVersionNodeBuilder() {
        this(new AstWriteHttpVersionNode());
    }

    @Override
    public AstWriteHttpVersionNodeBuilder setLocationInfo(int line, int column) {
        node.setLocationInfo(line, column);
        internalSetLineInfo(line);
        return this;
    }

    @Override
    public AstWriteHttpVersionNodeBuilder setNextLineInfo(int linesToSkip, int column) {
        internalSetNextLineInfo(linesToSkip, column);
        return this;
    }

    public AstWriteHttpVersionNodeBuilder setExactBytes(byte[] exactBytes) {
        node.setVersion(new AstLiteralBytesValue(exactBytes));
        return this;
    }

    public AstWriteHttpVersionNodeBuilder setExactText(String exactText) {
        node.setVersion(new AstLiteralTextValue(exactText));
        return this;
    }

    public AstWriteHttpVersionNodeBuilder setExpression(ValueExpression value) {
        node.setVersion(new AstExpressionValue(value));
        return this;
    }

    @Override
    public AstWriteHttpVersionNode done() {
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

    private AstWriteHttpVersionNodeBuilder(AstWriteHttpVersionNode node) {
        super(node, node);
    }

    public static class StreamNested<R extends AbstractAstNodeBuilder<? extends AstStreamNode, ?>> extends
            AbstractAstStreamableNodeBuilder<AstWriteHttpVersionNode, R> {

        public StreamNested(R builder) {
            super(new AstWriteHttpVersionNode(), builder);
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
            node.setVersion(new AstLiteralBytesValue(exactBytes));
            return this;
        }

        public StreamNested<R> setExactText(String exactText) {
            node.setVersion(new AstLiteralTextValue(exactText));
            return this;
        }

        public StreamNested<R> setExpression(ValueExpression value) {
            node.setVersion(new AstExpressionValue(value));
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
