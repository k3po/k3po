/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.lang.ast.builder;

import com.kaazing.robot.lang.ast.AstStreamNode;
import com.kaazing.robot.lang.ast.AstWriteHttpHeaderNode;
import com.kaazing.robot.lang.ast.value.AstExpressionValue;
import com.kaazing.robot.lang.ast.value.AstLiteralBytesValue;
import com.kaazing.robot.lang.ast.value.AstLiteralTextValue;

import javax.el.ValueExpression;

public class AstWriteHttpHeaderNodeBuilder extends
        AbstractAstStreamableNodeBuilder<AstWriteHttpHeaderNode, AstWriteHttpHeaderNode> {

    private int line;

    public AstWriteHttpHeaderNodeBuilder() {
        this(new AstWriteHttpHeaderNode());
    }

    @Override
    public AstWriteHttpHeaderNodeBuilder setLocationInfo(int line, int column) {
        node.setLocationInfo(line, column);
        internalSetLineInfo(line);
        return this;
    }

    @Override
    public AstWriteHttpHeaderNodeBuilder setNextLineInfo(int linesToSkip, int column) {
        internalSetNextLineInfo(linesToSkip, column);
        return this;
    }

    public AstWriteHttpHeaderNodeBuilder setNameExactBytes(byte[] headerNameExactBytes) {
        node.setName(new AstLiteralBytesValue(headerNameExactBytes));
        return this;
    }

    public AstWriteHttpHeaderNodeBuilder setValueExactBytes(byte[] headerValueExactBytes) {
        node.setValue(new AstLiteralBytesValue(headerValueExactBytes));
        return this;
    }

    public AstWriteHttpHeaderNodeBuilder setNameExactText(String headerNameExactText) {
        node.setName(new AstLiteralTextValue(headerNameExactText));
        return this;
    }

    public AstWriteHttpHeaderNodeBuilder setValueExactText(String headerValueExactText) {
        node.setValue(new AstLiteralTextValue(headerValueExactText));
        return this;
    }

    public AstWriteHttpHeaderNodeBuilder setNameExpression(ValueExpression headerNameValueExpression) {
        node.setName(new AstExpressionValue(headerNameValueExpression));
        return this;
    }

    public AstWriteHttpHeaderNodeBuilder setValueExpression(ValueExpression headerValueValueExpression) {
        node.setValue(new AstExpressionValue(headerValueValueExpression));
        return this;
    }

    @Override
    public AstWriteHttpHeaderNode done() {
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

    private AstWriteHttpHeaderNodeBuilder(AstWriteHttpHeaderNode node) {
        super(node, node);
    }

    public static class StreamNested<R extends AbstractAstNodeBuilder<? extends AstStreamNode, ?>> extends
            AbstractAstStreamableNodeBuilder<AstWriteHttpHeaderNode, R> {

        public StreamNested(R builder) {
            super(new AstWriteHttpHeaderNode(), builder);
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

        public StreamNested<R> setNameExactBytes(byte[] headerNameExactBytes) {
            node.setName(new AstLiteralBytesValue(headerNameExactBytes));
            return this;
        }

        public StreamNested<R> setValueExactBytes(byte[] headerValueExactBytes) {
            node.setValue(new AstLiteralBytesValue(headerValueExactBytes));
            return this;
        }

        public StreamNested<R> setNameExactText(String headerNameExactText) {
            node.setName(new AstLiteralTextValue(headerNameExactText));
            return this;
        }

        public StreamNested<R> setValueExactText(String headerValueExactText) {
            node.setValue(new AstLiteralTextValue(headerValueExactText));
            return this;
        }

        public StreamNested<R> setNameValueExpression(ValueExpression headerNameValueExpression) {
            node.setName(new AstExpressionValue(headerNameValueExpression));
            return this;
        }

        public StreamNested<R> setValueValueExpression(ValueExpression headerValueValueExpression) {
            node.setValue(new AstExpressionValue(headerValueValueExpression));
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
