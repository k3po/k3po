/*
 * Copyright (c) 2014 "Kaazing Corporation," (www.kaazing.com)
 *
 * This file is part of Robot.
 *
 * Robot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.kaazing.robot.lang.ast.builder;

import javax.el.ValueExpression;

import org.kaazing.robot.lang.ast.AstStreamNode;
import org.kaazing.robot.lang.ast.AstReadHttpParameterNode;
import org.kaazing.robot.lang.ast.matcher.AstExactBytesMatcher;
import org.kaazing.robot.lang.ast.matcher.AstExactTextMatcher;
import org.kaazing.robot.lang.ast.matcher.AstExpressionMatcher;
import org.kaazing.robot.lang.ast.matcher.AstFixedLengthBytesMatcher;
import org.kaazing.robot.lang.ast.matcher.AstRegexMatcher;
import org.kaazing.robot.lang.ast.matcher.AstVariableLengthBytesMatcher;
import org.kaazing.robot.lang.ast.value.AstLiteralTextValue;
import org.kaazing.robot.lang.regex.NamedGroupPattern;

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

    public AstReadHttpParameterNodeBuilder addValueExactBytes(byte[] parameterValueBytes) {
        node.addMatcher(new AstExactBytesMatcher(parameterValueBytes));
        return this;
    }

    public AstReadHttpParameterNodeBuilder setKeyExactText(String keyExactText) {
        node.setKey(new AstLiteralTextValue(keyExactText));
        return this;
    }

    public AstReadHttpParameterNodeBuilder addValueExactText(String parameterValueExactText) {
        node.addMatcher(new AstExactTextMatcher(parameterValueExactText));
        return this;
    }

    public AstReadHttpParameterNodeBuilder addValueExpression(ValueExpression parameterValueExpression) {
        node.addMatcher(new AstExpressionMatcher(parameterValueExpression));
        return this;
    }

    public AstReadHttpParameterNodeBuilder addValueFixedLengthBytes(int parameterValueLength) {
        node.addMatcher(new AstFixedLengthBytesMatcher(parameterValueLength));
        return this;
    }

    public AstReadHttpParameterNodeBuilder addValueFixedLengthBytes(int parameterValueLength,
                                                                             String parameterValueName) {
        node.addMatcher(new AstFixedLengthBytesMatcher(parameterValueLength, parameterValueName));
        return this;
    }

    public AstReadHttpParameterNodeBuilder addParamterValueRegex(NamedGroupPattern parameterValuePattern) {
        node.addMatcher(new AstRegexMatcher(parameterValuePattern));
        return this;
    }

    public AstReadHttpParameterNodeBuilder addValueVariableLengthBytes(ValueExpression parameterValueLength) {
        node.addMatcher(new AstVariableLengthBytesMatcher(parameterValueLength));
        return this;
    }

    public AstReadHttpParameterNodeBuilder addValueVariableLengthBytes(ValueExpression parameterValueLength,
                                                                                String parameterValueName) {
        node.addMatcher(new AstVariableLengthBytesMatcher(parameterValueLength, parameterValueName));
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

        public StreamNested<R> addValueExactBytes(byte[] parameterValueBytes) {
            node.addMatcher(new AstExactBytesMatcher(parameterValueBytes));
            return this;
        }

        public StreamNested<R> setKeyExactText(String keyExactText) {
            node.setKey(new AstLiteralTextValue(keyExactText));
            return this;
        }

        public StreamNested<R> addValueExactText(String parameterValueExactText) {
            node.addMatcher(new AstExactTextMatcher(parameterValueExactText));
            return this;
        }

        public StreamNested<R> addValueExpression(ValueExpression parameterValueExpression) {
            node.addMatcher(new AstExpressionMatcher(parameterValueExpression));
            return this;
        }

        public StreamNested<R> addValueFixedLengthBytes(int parameterValueLength) {
            node.addMatcher(new AstFixedLengthBytesMatcher(parameterValueLength));
            return this;
        }

        public StreamNested<R> addValueFixedLengthBytes(int parameterValueLength, String parameterValueName) {
            node.addMatcher(new AstFixedLengthBytesMatcher(parameterValueLength, parameterValueName));
            return this;
        }

        public StreamNested<R> addParamterValueRegex(NamedGroupPattern parameterValuePattern) {
            node.addMatcher(new AstRegexMatcher(parameterValuePattern));
            return this;
        }

        public StreamNested<R> addValueVariableLengthBytes(ValueExpression parameterValueLength) {
            node.addMatcher(new AstVariableLengthBytesMatcher(parameterValueLength));
            return this;
        }

        public StreamNested<R> addValueVariableLengthBytes(ValueExpression parameterValueLength,
                                                                    String parameterValueName) {
            node.addMatcher(new AstVariableLengthBytesMatcher(parameterValueLength, parameterValueName));
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
