/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.lang.ast.builder;

import com.kaazing.robot.lang.ast.AstStreamNode;
import com.kaazing.robot.lang.ast.AstWriteHttpStatusNode;
import com.kaazing.robot.lang.ast.value.AstExpressionValue;
import com.kaazing.robot.lang.ast.value.AstLiteralBytesValue;
import com.kaazing.robot.lang.ast.value.AstLiteralTextValue;

import javax.el.ValueExpression;

public class AstWriteHttpStatusNodeBuilder extends
        AbstractAstStreamableNodeBuilder<AstWriteHttpStatusNode, AstWriteHttpStatusNode> {

    private int line;

    public AstWriteHttpStatusNodeBuilder() {
        this(new AstWriteHttpStatusNode());
    }

    @Override
    public AstWriteHttpStatusNodeBuilder setLocationInfo(int line, int column) {
        node.setLocationInfo(line, column);
        internalSetLineInfo(line);
        return this;
    }

    @Override
    public AstWriteHttpStatusNodeBuilder setNextLineInfo(int linesToSkip, int column) {
        internalSetNextLineInfo(linesToSkip, column);
        return this;
    }

    public AstWriteHttpStatusNodeBuilder setCodeExactBytes(byte[] statusCodeExactBytes) {
        node.setCode(new AstLiteralBytesValue(statusCodeExactBytes));
        return this;
    }

    public AstWriteHttpStatusNodeBuilder setReasonExactBytes(byte[] statusReasonExactBytes) {
        node.setReason(new AstLiteralBytesValue(statusReasonExactBytes));
        return this;
    }

    public AstWriteHttpStatusNodeBuilder setCodeExactText(String statusCodeExactText) {
        node.setCode(new AstLiteralTextValue(statusCodeExactText));
        return this;
    }

    public AstWriteHttpStatusNodeBuilder setReasonExactText(String statusReasonExactText) {
        node.setReason(new AstLiteralTextValue(statusReasonExactText));
        return this;
    }

    public AstWriteHttpStatusNodeBuilder setCodeExpression(ValueExpression statusCodeExpression) {
        node.setCode(new AstExpressionValue(statusCodeExpression));
        return this;
    }

    public AstWriteHttpStatusNodeBuilder setReasonExpression(ValueExpression statusReasonExpression) {
        node.setReason(new AstExpressionValue(statusReasonExpression));
        return this;
    }

    @Override
    public AstWriteHttpStatusNode done() {
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

    private AstWriteHttpStatusNodeBuilder(AstWriteHttpStatusNode node) {
        super(node, node);
    }

    public static class StreamNested<R extends AbstractAstNodeBuilder<? extends AstStreamNode, ?>> extends
            AbstractAstStreamableNodeBuilder<AstWriteHttpStatusNode, R> {

        public StreamNested(R builder) {
            super(new AstWriteHttpStatusNode(), builder);
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

        public StreamNested<R> setCodeExactBytes(byte[] statusCodeExactBytes) {
            node.setCode(new AstLiteralBytesValue(statusCodeExactBytes));
            return this;
        }

        public StreamNested<R> setReasonExactBytes(byte[] statusReasonExactBytes) {
            node.setReason(new AstLiteralBytesValue(statusReasonExactBytes));
            return this;
        }

        public StreamNested<R> setCodeExactText(String statusCodeExactText) {
            node.setCode(new AstLiteralTextValue(statusCodeExactText));
            return this;
        }

        public StreamNested<R> setReasonExactText(String statusReasonExactText) {
            node.setReason(new AstLiteralTextValue(statusReasonExactText));
            return this;
        }

        public StreamNested<R> setCodeExpression(ValueExpression statusCodeExpression) {
            node.setCode(new AstExpressionValue(statusCodeExpression));
            return this;
        }

        public StreamNested<R> setReasonExpression(ValueExpression statusReasonExpression) {
            node.setReason(new AstExpressionValue(statusReasonExpression));
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
