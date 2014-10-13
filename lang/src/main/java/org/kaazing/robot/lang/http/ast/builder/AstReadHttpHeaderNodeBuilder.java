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

package org.kaazing.robot.lang.http.ast.builder;

import javax.el.ValueExpression;

import org.kaazing.robot.lang.ast.AstStreamNode;
import org.kaazing.robot.lang.ast.builder.AbstractAstNodeBuilder;
import org.kaazing.robot.lang.ast.builder.AbstractAstStreamableNodeBuilder;
import org.kaazing.robot.lang.ast.matcher.AstExactBytesMatcher;
import org.kaazing.robot.lang.ast.matcher.AstExactTextMatcher;
import org.kaazing.robot.lang.ast.matcher.AstExpressionMatcher;
import org.kaazing.robot.lang.ast.matcher.AstFixedLengthBytesMatcher;
import org.kaazing.robot.lang.ast.matcher.AstRegexMatcher;
import org.kaazing.robot.lang.ast.matcher.AstVariableLengthBytesMatcher;
import org.kaazing.robot.lang.ast.value.AstLiteralTextValue;
import org.kaazing.robot.lang.http.ast.AstReadHttpHeaderNode;
import org.kaazing.robot.lang.regex.NamedGroupPattern;

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


    public AstReadHttpHeaderNodeBuilder setNameExactText(String headerNameExactText) {
        node.setName(new AstLiteralTextValue(headerNameExactText));
        return this;
    }

    public AstReadHttpHeaderNodeBuilder setNameFixedLengthBytes(int valueLength) {
        node.addMatcher(new AstFixedLengthBytesMatcher(valueLength));
        return this;
    }

    public AstReadHttpHeaderNodeBuilder addValueExactText(String valueExactText) {
        node.addMatcher(new AstExactTextMatcher(valueExactText));
        return this;
    }

    public AstReadHttpHeaderNodeBuilder addValueExactBytes(byte[] valueBytes) {
        node.addMatcher(new AstExactBytesMatcher(valueBytes));
        return this;
    }

    public AstReadHttpHeaderNodeBuilder addValueExpression(ValueExpression valueValueExpression) {
        node.addMatcher(new AstExpressionMatcher(valueValueExpression));
        return this;
    }

    public AstReadHttpHeaderNodeBuilder addValueFixedLengthBytes(int valueLength, String valueCaptureName) {
        node.addMatcher(new AstFixedLengthBytesMatcher(valueLength, valueCaptureName));
        return this;
    }

    public AstReadHttpHeaderNodeBuilder addValueRegex(NamedGroupPattern valuePattern) {
        node.addMatcher(new AstRegexMatcher(valuePattern));
        return this;
    }

    public AstReadHttpHeaderNodeBuilder addValueVariableLengthBytes(ValueExpression valueLength) {
        node.addMatcher(new AstVariableLengthBytesMatcher(valueLength));
        return this;
    }

    public AstReadHttpHeaderNodeBuilder addValueVariableLengthBytes(ValueExpression valueLength, String valueCaptureName) {
        node.addMatcher(new AstVariableLengthBytesMatcher(valueLength, valueCaptureName));
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

        public StreamNested<R> setNameExactText(String headerNameExactText) {
            node.setName(new AstLiteralTextValue(headerNameExactText));
            return this;
        }

        public StreamNested<R> setNameFixedLengthBytes(int valueLength) {
            node.addMatcher(new AstFixedLengthBytesMatcher(valueLength));
            return this;
        }

        public StreamNested<R> addValueExactBytes(byte[] valueBytes) {
            node.addMatcher(new AstExactBytesMatcher(valueBytes));
            return this;
        }

        public StreamNested<R> addValueExactText(String valueExactText) {
            node.addMatcher(new AstExactTextMatcher(valueExactText));
            return this;
        }

        public StreamNested<R> addValueExpression(ValueExpression valueValueExpression) {
            node.addMatcher(new AstExpressionMatcher(valueValueExpression));
            return this;
        }

        public StreamNested<R> addValueFixedLengthBytes(int valueLength, String valueCaptureName) {
            node.addMatcher(new AstFixedLengthBytesMatcher(valueLength, valueCaptureName));
            return this;
        }

        public StreamNested<R> addValueRegex(NamedGroupPattern valuePattern) {
            node.addMatcher(new AstRegexMatcher(valuePattern));
            return this;
        }

        public StreamNested<R> addValueVariableLengthBytes(ValueExpression valueLength) {
            node.addMatcher(new AstVariableLengthBytesMatcher(valueLength));
            return this;
        }

        public StreamNested<R> addValueVariableLengthBytes(ValueExpression valueLength, String valueCaptureName) {
            node.addMatcher(new AstVariableLengthBytesMatcher(valueLength, valueCaptureName));
            return this;
        }

        @Override
        public R done() {
            AstStreamNode streamNode = node(result);
            streamNode.getStreamables().add(node);
            return result;
        }

        @Override
        protected int line() {
            return line(result);
        }

        @Override
        protected int line(int line) {
            return line(result, line);
        }
    }
}
