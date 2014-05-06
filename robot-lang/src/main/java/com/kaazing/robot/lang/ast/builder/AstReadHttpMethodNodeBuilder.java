/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.lang.ast.builder;

import javax.el.ValueExpression;

import com.kaazing.robot.lang.ast.AstStreamNode;
import com.kaazing.robot.lang.ast.AstReadHttpMethodNode;
import com.kaazing.robot.lang.ast.matcher.AstExactBytesMatcher;
import com.kaazing.robot.lang.ast.matcher.AstExactTextMatcher;
import com.kaazing.robot.lang.ast.matcher.AstExpressionMatcher;
import com.kaazing.robot.lang.ast.matcher.AstFixedLengthBytesMatcher;
import com.kaazing.robot.lang.ast.matcher.AstRegexMatcher;
import com.kaazing.robot.lang.ast.matcher.AstVariableLengthBytesMatcher;
import com.kaazing.robot.lang.regex.NamedGroupPattern;

public class AstReadHttpMethodNodeBuilder extends
        AbstractAstStreamableNodeBuilder<AstReadHttpMethodNode, AstReadHttpMethodNode> {

    private int line;

    public AstReadHttpMethodNodeBuilder() {
        this(new AstReadHttpMethodNode());
    }

    private AstReadHttpMethodNodeBuilder(AstReadHttpMethodNode node) {
        super(node, node);
    }

    @Override
    public AbstractAstNodeBuilder<AstReadHttpMethodNode, AstReadHttpMethodNode> setNextLineInfo(int linesToSkip,
                                                                                                int column) {
        internalSetNextLineInfo(linesToSkip, column);
        return this;
    }

    @Override
    public AbstractAstNodeBuilder<AstReadHttpMethodNode, AstReadHttpMethodNode> setLocationInfo(int line, int column) {
        node.setLocationInfo(line, column);
        internalSetLineInfo(line);
        return this;
    }

    @Override
    public AstReadHttpMethodNode done() {
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

    public AstReadHttpMethodNodeBuilder setExactBytes(byte[] exactBytes) {
        node.setMethod(new AstExactBytesMatcher(exactBytes));
        return this;
    }

    public AstReadHttpMethodNodeBuilder setExactText(String exactText) {
        node.setMethod(new AstExactTextMatcher(exactText));
        return this;
    }

    public AstReadHttpMethodNodeBuilder setExpression(ValueExpression value) {
        node.setMethod(new AstExpressionMatcher(value));
        return this;
    }

    public AstReadHttpMethodNodeBuilder setFixedLengthBytes(int length) {
        node.setMethod(new AstFixedLengthBytesMatcher(length));
        return this;
    }

    public AstReadHttpMethodNodeBuilder setFixedLengthBytes(int length, String captureName) {
        node.setMethod(new AstFixedLengthBytesMatcher(length, captureName));
        return this;
    }

    public AstReadHttpMethodNodeBuilder setRegex(NamedGroupPattern pattern) {
        node.setMethod(new AstRegexMatcher(pattern));
        return this;
    }

    public AstReadHttpMethodNodeBuilder setVariableLengthBytes(ValueExpression length) {
        node.setMethod(new AstVariableLengthBytesMatcher(length));
        return this;
    }

    public AstReadHttpMethodNodeBuilder setVariableLengthBytes(ValueExpression length, String captureName) {
        node.setMethod(new AstVariableLengthBytesMatcher(length, captureName));
        return this;
    }

    public static class StreamNested<R extends AbstractAstNodeBuilder<? extends AstStreamNode, ?>> extends
            AbstractAstStreamableNodeBuilder<AstReadHttpMethodNode, R> {

        public StreamNested(R builder) {
            super(new AstReadHttpMethodNode(), builder);
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
            node.setMethod(new AstExactBytesMatcher(exactBytes));
            return this;
        }

        public StreamNested<R> setExactText(String exactText) {
            node.setMethod(new AstExactTextMatcher(exactText));
            return this;
        }

        public StreamNested<R> setExpression(ValueExpression value) {
            node.setMethod(new AstExpressionMatcher(value));
            return this;
        }

        public StreamNested<R> setFixedLengthBytes(int length) {
            node.setMethod(new AstFixedLengthBytesMatcher(length));
            return this;
        }

        public StreamNested<R> setFixedLengthBytes(int length, String captureName) {
            node.setMethod(new AstFixedLengthBytesMatcher(length, captureName));
            return this;
        }

        public StreamNested<R> setRegex(NamedGroupPattern pattern) {
            node.setMethod(new AstRegexMatcher(pattern));
            return this;
        }

        public StreamNested<R> setVariableLengthBytes(ValueExpression length) {
            node.setMethod(new AstVariableLengthBytesMatcher(length));
            return this;
        }

        public StreamNested<R> setVariableLengthBytes(ValueExpression length, String captureName) {
            node.setMethod(new AstVariableLengthBytesMatcher(length, captureName));
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
