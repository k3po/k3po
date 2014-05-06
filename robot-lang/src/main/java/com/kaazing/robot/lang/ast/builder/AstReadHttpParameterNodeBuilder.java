/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.lang.ast.builder;

import javax.el.ValueExpression;

import com.kaazing.robot.lang.ast.AstStreamNode;
import com.kaazing.robot.lang.ast.AstReadHttpParameterNode;
import com.kaazing.robot.lang.ast.matcher.AstExactBytesMatcher;
import com.kaazing.robot.lang.ast.matcher.AstExactTextMatcher;
import com.kaazing.robot.lang.ast.matcher.AstExpressionMatcher;
import com.kaazing.robot.lang.ast.matcher.AstFixedLengthBytesMatcher;
import com.kaazing.robot.lang.ast.matcher.AstRegexMatcher;
import com.kaazing.robot.lang.ast.matcher.AstVariableLengthBytesMatcher;
import com.kaazing.robot.lang.ast.value.AstLiteralTextValue;
import com.kaazing.robot.lang.regex.NamedGroupPattern;

public class AstReadHttpParameterNodeBuilder extends
        AbstractAstStreamableNodeBuilder<AstReadHttpParameterNode, AstReadHttpParameterNode> {

    private int line;

    public AstReadHttpParameterNodeBuilder() {
        this(new AstReadHttpParameterNode());
    }

    private AstReadHttpParameterNodeBuilder(AstReadHttpParameterNode node) {
        super(node, node);
    }

    @Override
    public AbstractAstNodeBuilder<AstReadHttpParameterNode, AstReadHttpParameterNode> setNextLineInfo(int linesToSkip,
                                                                                                      int column) {
        internalSetNextLineInfo(linesToSkip, column);
        return this;
    }

    @Override
    public AbstractAstNodeBuilder<AstReadHttpParameterNode, AstReadHttpParameterNode> setLocationInfo(int line,
                                                                                                      int column) {
        node.setLocationInfo(line, column);
        internalSetLineInfo(line);
        return this;
    }

    @Override
    public AstReadHttpParameterNode done() {
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

    public AstReadHttpParameterNodeBuilder setValueExactBytes(byte[] parameterValueBytes) {
        node.setValue(new AstExactBytesMatcher(parameterValueBytes));
        return this;
    }

    public AstReadHttpParameterNodeBuilder setKeyExactText(String keyExactText) {
        node.setKey(new AstLiteralTextValue(keyExactText));
        return this;
    }

    public AstReadHttpParameterNodeBuilder setValueExactText(String parameterValueExactText) {
        node.setValue(new AstExactTextMatcher(parameterValueExactText));
        return this;
    }

    public AstReadHttpParameterNodeBuilder setValueExpression(ValueExpression parameterValueExpression) {
        node.setValue(new AstExpressionMatcher(parameterValueExpression));
        return this;
    }

    public AstReadHttpParameterNodeBuilder setValueFixedLengthBytes(int parameterValueLength) {
        node.setValue(new AstFixedLengthBytesMatcher(parameterValueLength));
        return this;
    }

    public AstReadHttpParameterNodeBuilder setValueFixedLengthBytes(int parameterValueLength,
                                                                             String parameterValueName) {
        node.setValue(new AstFixedLengthBytesMatcher(parameterValueLength, parameterValueName));
        return this;
    }

    public AstReadHttpParameterNodeBuilder setParamterValueRegex(NamedGroupPattern parameterValuePattern) {
        node.setValue(new AstRegexMatcher(parameterValuePattern));
        return this;
    }

    public AstReadHttpParameterNodeBuilder setValueVariableLengthBytes(ValueExpression parameterValueLength) {
        node.setValue(new AstVariableLengthBytesMatcher(parameterValueLength));
        return this;
    }

    public AstReadHttpParameterNodeBuilder setValueVariableLengthBytes(ValueExpression parameterValueLength,
                                                                                String parameterValueName) {
        node.setValue(new AstVariableLengthBytesMatcher(parameterValueLength, parameterValueName));
        return this;
    }

    public static class StreamNested<R extends AbstractAstNodeBuilder<? extends AstStreamNode, ?>> extends
            AbstractAstStreamableNodeBuilder<AstReadHttpParameterNode, R> {

        public StreamNested(R builder) {
            super(new AstReadHttpParameterNode(), builder);
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

        public StreamNested<R> setValueExactBytes(byte[] parameterValueBytes) {
            node.setValue(new AstExactBytesMatcher(parameterValueBytes));
            return this;
        }

        public StreamNested<R> setKeyExactText(String keyExactText) {
            node.setKey(new AstLiteralTextValue(keyExactText));
            return this;
        }

        public StreamNested<R> setValueExactText(String parameterValueExactText) {
            node.setValue(new AstExactTextMatcher(parameterValueExactText));
            return this;
        }

        public StreamNested<R> setValueExpression(ValueExpression parameterValueExpression) {
            node.setValue(new AstExpressionMatcher(parameterValueExpression));
            return this;
        }

        public StreamNested<R> setValueFixedLengthBytes(int parameterValueLength) {
            node.setValue(new AstFixedLengthBytesMatcher(parameterValueLength));
            return this;
        }

        public StreamNested<R> setValueFixedLengthBytes(int parameterValueLength, String parameterValueName) {
            node.setValue(new AstFixedLengthBytesMatcher(parameterValueLength, parameterValueName));
            return this;
        }

        public StreamNested<R> setParamterValueRegex(NamedGroupPattern parameterValuePattern) {
            node.setValue(new AstRegexMatcher(parameterValuePattern));
            return this;
        }

        public StreamNested<R> setValueVariableLengthBytes(ValueExpression parameterValueLength) {
            node.setValue(new AstVariableLengthBytesMatcher(parameterValueLength));
            return this;
        }

        public StreamNested<R> setValueVariableLengthBytes(ValueExpression parameterValueLength,
                                                                    String parameterValueName) {
            node.setValue(new AstVariableLengthBytesMatcher(parameterValueLength, parameterValueName));
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
