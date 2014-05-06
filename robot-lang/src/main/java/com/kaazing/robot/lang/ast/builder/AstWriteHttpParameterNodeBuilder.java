/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.lang.ast.builder;

import com.kaazing.robot.lang.ast.AstStreamNode;
import com.kaazing.robot.lang.ast.AstWriteHttpParameterNode;
import com.kaazing.robot.lang.ast.value.AstExpressionValue;
import com.kaazing.robot.lang.ast.value.AstLiteralBytesValue;
import com.kaazing.robot.lang.ast.value.AstLiteralTextValue;

import javax.el.ValueExpression;

public class AstWriteHttpParameterNodeBuilder extends
        AbstractAstStreamableNodeBuilder<AstWriteHttpParameterNode, AstWriteHttpParameterNode> {

    private int line;

    public AstWriteHttpParameterNodeBuilder() {
        this(new AstWriteHttpParameterNode());
    }

    @Override
    public AstWriteHttpParameterNodeBuilder setLocationInfo(int line, int column) {
        node.setLocationInfo(line, column);
        internalSetLineInfo(line);
        return this;
    }

    @Override
    public AstWriteHttpParameterNodeBuilder setNextLineInfo(int linesToSkip, int column) {
        internalSetNextLineInfo(linesToSkip, column);
        return this;
    }

    public AstWriteHttpParameterNodeBuilder setKeyExactBytes(byte[] keyExactBytes) {
        node.setKey(new AstLiteralBytesValue(keyExactBytes));
        return this;
    }

    public AstWriteHttpParameterNodeBuilder setValueExactBytes(byte[] valueExactBytes) {
        node.setValue(new AstLiteralBytesValue(valueExactBytes));
        return this;
    }

    public AstWriteHttpParameterNodeBuilder setKeyExactText(String keyExactText) {
        node.setKey(new AstLiteralTextValue(keyExactText));
        return this;
    }

    public AstWriteHttpParameterNodeBuilder setValueExactText(String valueExactText) {
        node.setValue(new AstLiteralTextValue(valueExactText));
        return this;
    }

    public AstWriteHttpParameterNodeBuilder setKeyExpression(ValueExpression keyExpression) {
        node.setKey(new AstExpressionValue(keyExpression));
        return this;
    }

    public AstWriteHttpParameterNodeBuilder setValueExpression(ValueExpression valueExpression) {
        node.setValue(new AstExpressionValue(valueExpression));
        return this;
    }

    @Override
    public AstWriteHttpParameterNode done() {
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

    private AstWriteHttpParameterNodeBuilder(AstWriteHttpParameterNode node) {
        super(node, node);
    }

    public static class StreamNested<R extends AbstractAstNodeBuilder<? extends AstStreamNode, ?>> extends
            AbstractAstStreamableNodeBuilder<AstWriteHttpParameterNode, R> {

        public StreamNested(R builder) {
            super(new AstWriteHttpParameterNode(), builder);
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

        public StreamNested<R> setKeyExactBytes(byte[] keyExactBytes) {
            node.setKey(new AstLiteralBytesValue(keyExactBytes));
            return this;
        }

        public StreamNested<R> setValueExactBytes(byte[] valueExactBytes) {
            node.setValue(new AstLiteralBytesValue(valueExactBytes));
            return this;
        }

        public StreamNested<R> setKeyExactText(String keyExactText) {
            node.setKey(new AstLiteralTextValue(keyExactText));
            return this;
        }

        public StreamNested<R> setValueExactText(String valueExactText) {
            node.setValue(new AstLiteralTextValue(valueExactText));
            return this;
        }

        public StreamNested<R> setKeyExpression(ValueExpression keyExpression) {
            node.setKey(new AstExpressionValue(keyExpression));
            return this;
        }

        public StreamNested<R> setValueExpression(ValueExpression valueExpression) {
            node.setValue(new AstExpressionValue(valueExpression));
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
