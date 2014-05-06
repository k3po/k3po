/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.lang.ast.builder;

import javax.el.ValueExpression;

import org.kaazing.robot.lang.ast.AstStreamNode;
import org.kaazing.robot.lang.ast.AstReadHttpStatusNode;
import org.kaazing.robot.lang.ast.matcher.AstExactBytesMatcher;
import org.kaazing.robot.lang.ast.matcher.AstExactTextMatcher;
import org.kaazing.robot.lang.ast.matcher.AstExpressionMatcher;
import org.kaazing.robot.lang.ast.matcher.AstFixedLengthBytesMatcher;
import org.kaazing.robot.lang.ast.matcher.AstRegexMatcher;
import org.kaazing.robot.lang.ast.matcher.AstVariableLengthBytesMatcher;
import org.kaazing.robot.lang.regex.NamedGroupPattern;

public class AstReadHttpStatusNodeBuilder extends
        AbstractAstStreamableNodeBuilder<AstReadHttpStatusNode, AstReadHttpStatusNode> {

    private int line;

    public AstReadHttpStatusNodeBuilder() {
        this(new AstReadHttpStatusNode());
    }

    private AstReadHttpStatusNodeBuilder(AstReadHttpStatusNode node) {
        super(node, node);
    }

    @Override
    public AbstractAstNodeBuilder<AstReadHttpStatusNode, AstReadHttpStatusNode> setNextLineInfo(int linesToSkip,
                                                                                                int column) {
        internalSetNextLineInfo(linesToSkip, column);
        return this;
    }

    @Override
    public AbstractAstNodeBuilder<AstReadHttpStatusNode, AstReadHttpStatusNode> setLocationInfo(int line, int column) {
        node.setLocationInfo(line, column);
        internalSetLineInfo(line);
        return this;
    }

    @Override
    public AstReadHttpStatusNode done() {
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

    public AstReadHttpStatusNodeBuilder setCodeExactBytes(byte[] statusCodeBytes) {
        node.setCode(new AstExactBytesMatcher(statusCodeBytes));
        return this;
    }

    public AstReadHttpStatusNodeBuilder setReasonExactBytes(byte[] statusReasonBytes) {
        node.setReason(new AstExactBytesMatcher(statusReasonBytes));
        return this;
    }

    public AstReadHttpStatusNodeBuilder setCodeExactText(String statusCodeExactText) {
        node.setCode(new AstExactTextMatcher(statusCodeExactText));
        return this;
    }

    public AstReadHttpStatusNodeBuilder setReasonExactText(String statusReasonExactText) {
        node.setReason(new AstExactTextMatcher(statusReasonExactText));
        return this;
    }

    public AstReadHttpStatusNodeBuilder setCodeExpression(ValueExpression statusCodeValueExpression) {
        node.setCode(new AstExpressionMatcher(statusCodeValueExpression));
        return this;
    }

    public AstReadHttpStatusNodeBuilder setReasonExpression(ValueExpression statusReasonValueExpression) {
        node.setReason(new AstExpressionMatcher(statusReasonValueExpression));
        return this;
    }

    public AstReadHttpStatusNodeBuilder setCodeFixedLengthBytes(int statusCodeLength) {
        node.setCode(new AstFixedLengthBytesMatcher(statusCodeLength));
        return this;
    }

    public AstReadHttpStatusNodeBuilder setReasonFixedLengthBytes(int statusReasonLength) {
        node.setReason(new AstFixedLengthBytesMatcher(statusReasonLength));
        return this;
    }

    public AstReadHttpStatusNodeBuilder setCodeFixedLengthBytes(int statusCodeLength, String statusCodeName) {
        node.setCode(new AstFixedLengthBytesMatcher(statusCodeLength, statusCodeName));
        return this;
    }

    public AstReadHttpStatusNodeBuilder setReasonFixedLengthBytes(int statusReasonLength, String statusReasonName) {
        node.setReason(new AstFixedLengthBytesMatcher(statusReasonLength, statusReasonName));
        return this;
    }

    public AstReadHttpStatusNodeBuilder setCodeRegex(NamedGroupPattern statusCodePattern) {
        node.setCode(new AstRegexMatcher(statusCodePattern));
        return this;
    }

    public AstReadHttpStatusNodeBuilder setReasonRegex(NamedGroupPattern statusReasonPattern) {
        node.setReason(new AstRegexMatcher(statusReasonPattern));
        return this;
    }

    public AstReadHttpStatusNodeBuilder setCodeVariableLengthBytes(ValueExpression statusCodeLength) {
        node.setCode(new AstVariableLengthBytesMatcher(statusCodeLength));
        return this;
    }

    public AstReadHttpStatusNodeBuilder setReasonVariableLengthBytes(ValueExpression statusReasonLength) {
        node.setReason(new AstVariableLengthBytesMatcher(statusReasonLength));
        return this;
    }

    public static class StreamNested<R extends AbstractAstNodeBuilder<? extends AstStreamNode, ?>> extends
            AbstractAstStreamableNodeBuilder<AstReadHttpStatusNode, R> {

        public StreamNested(R builder) {
            super(new AstReadHttpStatusNode(), builder);
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

        public StreamNested<R> setCodeExactBytes(byte[] statusCodeBytes) {
            node.setCode(new AstExactBytesMatcher(statusCodeBytes));
            return this;
        }

        public StreamNested<R> setReasonExactBytes(byte[] statusReasonBytes) {
            node.setReason(new AstExactBytesMatcher(statusReasonBytes));
            return this;
        }

        public StreamNested<R> setCodeExactText(String statusCodeExactText) {
            node.setCode(new AstExactTextMatcher(statusCodeExactText));
            return this;
        }

        public StreamNested<R> setReasonExactText(String statusReasonExactText) {
            node.setReason(new AstExactTextMatcher(statusReasonExactText));
            return this;
        }

        public StreamNested<R> setCodeExpression(ValueExpression statusCodeValueExpression) {
            node.setCode(new AstExpressionMatcher(statusCodeValueExpression));
            return this;
        }

        public StreamNested<R> setReasonExpression(ValueExpression statusReasonValueExpression) {
            node.setReason(new AstExpressionMatcher(statusReasonValueExpression));
            return this;
        }

        public StreamNested<R> setCodeFixedLengthBytes(int statusCodeLength) {
            node.setCode(new AstFixedLengthBytesMatcher(statusCodeLength));
            return this;
        }

        public StreamNested<R> setReasonFixedLengthBytes(int statusReasonLength) {
            node.setReason(new AstFixedLengthBytesMatcher(statusReasonLength));
            return this;
        }

        public StreamNested<R> setCodeFixedLengthBytes(int statusCodeLength, String statusCodeName) {
            node.setCode(new AstFixedLengthBytesMatcher(statusCodeLength, statusCodeName));
            return this;
        }

        public StreamNested<R> setReasonFixedLengthBytes(int statusReasonLength, String statusReasonName) {
            node.setReason(new AstFixedLengthBytesMatcher(statusReasonLength, statusReasonName));
            return this;
        }

        public StreamNested<R> setCodeRegex(NamedGroupPattern statusCodePattern) {
            node.setCode(new AstRegexMatcher(statusCodePattern));
            return this;
        }

        public StreamNested<R> setReasonRegex(NamedGroupPattern statusReasonPattern) {
            node.setReason(new AstRegexMatcher(statusReasonPattern));
            return this;
        }

        public StreamNested<R> setCodeVariableLengthBytes(ValueExpression statusCodeLength) {
            node.setCode(new AstVariableLengthBytesMatcher(statusCodeLength));
            return this;
        }

        public StreamNested<R> setReasonVariableLengthBytes(ValueExpression statusReasonLength) {
            node.setReason(new AstVariableLengthBytesMatcher(statusReasonLength));
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
