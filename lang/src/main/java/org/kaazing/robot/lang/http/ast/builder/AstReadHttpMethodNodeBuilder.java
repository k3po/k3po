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
import org.kaazing.robot.lang.http.ast.AstReadHttpMethodNode;
import org.kaazing.robot.lang.regex.NamedGroupPattern;

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
