/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.lang.ast.builder;

import javax.el.ValueExpression;

import com.kaazing.robot.lang.ast.AstReadHttpHeaderNode;
import com.kaazing.robot.lang.ast.AstStreamNode;
import com.kaazing.robot.lang.ast.matcher.AstExactBytesMatcher;
import com.kaazing.robot.lang.ast.matcher.AstExactTextMatcher;
import com.kaazing.robot.lang.ast.matcher.AstExpressionMatcher;
import com.kaazing.robot.lang.ast.matcher.AstFixedLengthBytesMatcher;
import com.kaazing.robot.lang.ast.matcher.AstRegexMatcher;
import com.kaazing.robot.lang.ast.matcher.AstVariableLengthBytesMatcher;
import com.kaazing.robot.lang.ast.value.AstLiteralTextValue;
import com.kaazing.robot.lang.regex.NamedGroupPattern;

public class AstReadHttpHeaderNodeBuilder extends
        AbstractAstStreamableNodeBuilder<AstReadHttpHeaderNode, AstReadHttpHeaderNode> {

    private int line;

    public AstReadHttpHeaderNodeBuilder() {
        this(new AstReadHttpHeaderNode());
    }

    private AstReadHttpHeaderNodeBuilder(AstReadHttpHeaderNode node) {
        super(node, node);
    }

    @Override
    public AbstractAstNodeBuilder<AstReadHttpHeaderNode, AstReadHttpHeaderNode> setNextLineInfo(int linesToSkip,
                                                                                                int column) {
        internalSetNextLineInfo(linesToSkip, column);
        return this;
    }

    @Override
    public AbstractAstNodeBuilder<AstReadHttpHeaderNode, AstReadHttpHeaderNode> setLocationInfo(int line, int column) {
        node.setLocationInfo(line, column);
        internalSetLineInfo(line);
        return this;
    }

    @Override
    public AstReadHttpHeaderNode done() {
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


    public AstReadHttpHeaderNodeBuilder setValueExactBytes(byte[] valueBytes) {
        node.setValue(new AstExactBytesMatcher(valueBytes));
        return this;
    }

    public AstReadHttpHeaderNodeBuilder setNameExactText(String headerNameExactText) {
        node.setName(new AstLiteralTextValue(headerNameExactText));
        return this;
    }

    public AstReadHttpHeaderNodeBuilder setValueExactText(String valueExactText) {
        node.setValue(new AstExactTextMatcher(valueExactText));
        return this;
    }

    public AstReadHttpHeaderNodeBuilder setValueExpression(ValueExpression valueValueExpression) {
        node.setValue(new AstExpressionMatcher(valueValueExpression));
        return this;
    }

    public AstReadHttpHeaderNodeBuilder setNameFixedLengthBytes(int valueLength) {
        node.setValue(new AstFixedLengthBytesMatcher(valueLength));
        return this;
    }

    public AstReadHttpHeaderNodeBuilder setValueFixedLengthBytes(int valueLength, String valueCaptureName) {
        node.setValue(new AstFixedLengthBytesMatcher(valueLength, valueCaptureName));
        return this;
    }

    public AstReadHttpHeaderNodeBuilder setValueRegex(NamedGroupPattern valuePattern) {
        node.setValue(new AstRegexMatcher(valuePattern));
        return this;
    }

    public AstReadHttpHeaderNodeBuilder setValueVariableLengthBytes(ValueExpression valueLength) {
        node.setValue(new AstVariableLengthBytesMatcher(valueLength));
        return this;
    }

    public AstReadHttpHeaderNodeBuilder setValueVariableLengthBytes(ValueExpression valueLength, String valueCaptureName) {
        node.setValue(new AstVariableLengthBytesMatcher(valueLength, valueCaptureName));
        return this;
    }

    public static class StreamNested<R extends AbstractAstNodeBuilder<? extends AstStreamNode, ?>> extends
            AbstractAstStreamableNodeBuilder<AstReadHttpHeaderNode, R> {

        public StreamNested(R builder) {
            super(new AstReadHttpHeaderNode(), builder);
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

        public StreamNested<R> setValueExactBytes(byte[] valueBytes) {
            node.setValue(new AstExactBytesMatcher(valueBytes));
            return this;
        }

        public StreamNested<R> setNameExactText(String headerNameExactText) {
            node.setName(new AstLiteralTextValue(headerNameExactText));
            return this;
        }

        public StreamNested<R> setValueExactText(String valueExactText) {
            node.setValue(new AstExactTextMatcher(valueExactText));
            return this;
        }

        public StreamNested<R> setValueExpression(ValueExpression valueValueExpression) {
            node.setValue(new AstExpressionMatcher(valueValueExpression));
            return this;
        }

        public StreamNested<R> setNameFixedLengthBytes(int valueLength) {
            node.setValue(new AstFixedLengthBytesMatcher(valueLength));
            return this;
        }

        public StreamNested<R> setValueFixedLengthBytes(int valueLength, String valueCaptureName) {
            node.setValue(new AstFixedLengthBytesMatcher(valueLength, valueCaptureName));
            return this;
        }

        public StreamNested<R> setValueRegex(NamedGroupPattern valuePattern) {
            node.setValue(new AstRegexMatcher(valuePattern));
            return this;
        }

        public StreamNested<R> setValueVariableLengthBytes(ValueExpression valueLength) {
            node.setValue(new AstVariableLengthBytesMatcher(valueLength));
            return this;
        }

        public StreamNested<R> setValueVariableLengthBytes(ValueExpression valueLength, String valueCaptureName) {
            node.setValue(new AstVariableLengthBytesMatcher(valueLength, valueCaptureName));
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
