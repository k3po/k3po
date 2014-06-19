/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.lang.ast.builder;

import javax.el.ValueExpression;

import org.kaazing.robot.lang.ast.AstReadValueNode;
import org.kaazing.robot.lang.ast.AstStreamNode;
import org.kaazing.robot.lang.ast.matcher.AstExactBytesMatcher;
import org.kaazing.robot.lang.ast.matcher.AstExactTextMatcher;
import org.kaazing.robot.lang.ast.matcher.AstExpressionMatcher;
import org.kaazing.robot.lang.ast.matcher.AstFixedLengthBytesMatcher;
import org.kaazing.robot.lang.ast.matcher.AstRegexMatcher;
import org.kaazing.robot.lang.ast.matcher.AstVariableLengthBytesMatcher;
import org.kaazing.robot.lang.regex.NamedGroupPattern;

public class AstReadNodeBuilder extends AbstractAstStreamableNodeBuilder<AstReadValueNode, AstReadValueNode> {

    private int line;

    public AstReadNodeBuilder() {
        this(new AstReadValueNode());
    }

    @Override
    public AstReadNodeBuilder setLocationInfo(int line, int column) {
        node.setLocationInfo(line, column);
        internalSetLineInfo(line);
        return this;
    }

    @Override
    public AstReadNodeBuilder setNextLineInfo(int linesToSkip, int column) {
        internalSetNextLineInfo(linesToSkip, column);
        return this;
    }

    public AstReadNodeBuilder addExactBytes(byte[] exactBytes) {
        node.addMatcher(new AstExactBytesMatcher(exactBytes));
        return this;
    }

    public AstReadNodeBuilder addExactText(String exactText) {
        node.addMatcher(new AstExactTextMatcher(exactText));
        return this;
    }

    public AstReadNodeBuilder addExpression(ValueExpression value) {
        node.addMatcher(new AstExpressionMatcher(value));
        return this;
    }

    public AstReadNodeBuilder addFixedLengthBytes(int length) {
        node.addMatcher(new AstFixedLengthBytesMatcher(length));
        return this;
    }

    public AstReadNodeBuilder addFixedLengthBytes(int length, String captureName) {
        node.addMatcher(new AstFixedLengthBytesMatcher(length, captureName));
        return this;
    }

    public AstReadNodeBuilder addRegex(NamedGroupPattern pattern) {
        node.addMatcher(new AstRegexMatcher(pattern));
        return this;
    }

    @Deprecated
    public AstReadNodeBuilder setRegex(NamedGroupPattern pattern, String terminator) {
        node.addMatcher(new AstRegexMatcher(pattern, terminator));
        return this;
    }

    public AstReadNodeBuilder addVariableLengthBytes(ValueExpression length) {
        node.addMatcher(new AstVariableLengthBytesMatcher(length));
        return this;
    }

    public AstReadNodeBuilder addVariableLengthBytes(ValueExpression length, String captureName) {
        node.addMatcher(new AstVariableLengthBytesMatcher(length, captureName));
        return this;
    }

    @Override
    public AstReadValueNode done() {
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

    private AstReadNodeBuilder(AstReadValueNode node) {
        super(node, node);
    }

    public static class StreamNested<R extends AbstractAstNodeBuilder<? extends AstStreamNode, ?>> extends
            AbstractAstStreamableNodeBuilder<AstReadValueNode, R> {

        public StreamNested(R builder) {
            super(new AstReadValueNode(), builder);
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
            node.addMatcher(new AstExactBytesMatcher(exactBytes));
            return this;
        }

        public StreamNested<R> addExactText(String exactText) {
            node.addMatcher(new AstExactTextMatcher(exactText));
            return this;
        }

        public StreamNested<R> addExpression(ValueExpression value) {
            node.addMatcher(new AstExpressionMatcher(value));
            return this;
        }

        public StreamNested<R> addFixedLengthBytes(int length) {
            node.addMatcher(new AstFixedLengthBytesMatcher(length));
            return this;
        }

        public StreamNested<R> addFixedLengthBytes(int length, String captureName) {
            node.addMatcher(new AstFixedLengthBytesMatcher(length, captureName));
            return this;
        }

        public StreamNested<R> addRegex(NamedGroupPattern pattern) {
            node.addMatcher(new AstRegexMatcher(pattern));
            return this;
        }

        @Deprecated
        public StreamNested<R> setRegex(NamedGroupPattern pattern, String terminator) {
            node.addMatcher(new AstRegexMatcher(pattern, terminator));
            return this;
        }

        public StreamNested<R> addVariableLengthBytes(ValueExpression length) {
            node.addMatcher(new AstVariableLengthBytesMatcher(length));
            return this;
        }

        public StreamNested<R> addVariableLengthBytes(ValueExpression length, String captureName) {
            node.addMatcher(new AstVariableLengthBytesMatcher(length, captureName));
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
