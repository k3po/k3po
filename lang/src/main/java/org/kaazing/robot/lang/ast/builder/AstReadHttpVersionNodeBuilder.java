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
import org.kaazing.robot.lang.ast.AstReadHttpVersionNode;
import org.kaazing.robot.lang.ast.matcher.AstExactBytesMatcher;
import org.kaazing.robot.lang.ast.matcher.AstExactTextMatcher;
import org.kaazing.robot.lang.ast.matcher.AstExpressionMatcher;
import org.kaazing.robot.lang.ast.matcher.AstFixedLengthBytesMatcher;
import org.kaazing.robot.lang.ast.matcher.AstRegexMatcher;
import org.kaazing.robot.lang.ast.matcher.AstVariableLengthBytesMatcher;
import org.kaazing.robot.lang.regex.NamedGroupPattern;

public class AstReadHttpVersionNodeBuilder extends
        AbstractAstStreamableNodeBuilder<AstReadHttpVersionNode, AstReadHttpVersionNode> {

    private int line;

    public AstReadHttpVersionNodeBuilder() {
        this(new AstReadHttpVersionNode());
    }

    private AstReadHttpVersionNodeBuilder(AstReadHttpVersionNode node) {
        super(node, node);
    }

    @Override
    public AbstractAstNodeBuilder<AstReadHttpVersionNode, AstReadHttpVersionNode> setNextLineInfo(int linesToSkip,
                                                                                                  int column) {
        internalSetNextLineInfo(linesToSkip, column);
        return this;
    }

    @Override
    public AbstractAstNodeBuilder<AstReadHttpVersionNode, AstReadHttpVersionNode> setLocationInfo(int line, int column) {
        node.setLocationInfo(line, column);
        internalSetLineInfo(line);
        return this;
    }

    @Override
    public AstReadHttpVersionNode done() {
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

    public AstReadHttpVersionNodeBuilder setExactBytes(byte[] exactBytes) {
        node.setVersion(new AstExactBytesMatcher(exactBytes));
        return this;
    }

    public AstReadHttpVersionNodeBuilder setExactText(String exactText) {
        node.setVersion(new AstExactTextMatcher(exactText));
        return this;
    }

    public AstReadHttpVersionNodeBuilder setExpression(ValueExpression value) {
        node.setVersion(new AstExpressionMatcher(value));
        return this;
    }

    public AstReadHttpVersionNodeBuilder setFixedLengthBytes(int length) {
        node.setVersion(new AstFixedLengthBytesMatcher(length));
        return this;
    }

    public AstReadHttpVersionNodeBuilder setFixedLengthBytes(int length, String captureName) {
        node.setVersion(new AstFixedLengthBytesMatcher(length, captureName));
        return this;
    }

    public AstReadHttpVersionNodeBuilder setRegex(NamedGroupPattern pattern) {
        node.setVersion(new AstRegexMatcher(pattern));
        return this;
    }

    public AstReadHttpVersionNodeBuilder setVariableLengthBytes(ValueExpression length) {
        node.setVersion(new AstVariableLengthBytesMatcher(length));
        return this;
    }

    public AstReadHttpVersionNodeBuilder setVariableLengthBytes(ValueExpression length, String captureName) {
        node.setVersion(new AstVariableLengthBytesMatcher(length, captureName));
        return this;
    }

    public static class StreamNested<R extends AbstractAstNodeBuilder<? extends AstStreamNode, ?>> extends
            AbstractAstStreamableNodeBuilder<AstReadHttpVersionNode, R> {

        public StreamNested(R builder) {
            super(new AstReadHttpVersionNode(), builder);
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
            node.setVersion(new AstExactBytesMatcher(exactBytes));
            return this;
        }

        public StreamNested<R> setExactText(String exactText) {
            node.setVersion(new AstExactTextMatcher(exactText));
            return this;
        }

        public StreamNested<R> setExpression(ValueExpression value) {
            node.setVersion(new AstExpressionMatcher(value));
            return this;
        }

        public StreamNested<R> setFixedLengthBytes(int length) {
            node.setVersion(new AstFixedLengthBytesMatcher(length));
            return this;
        }

        public StreamNested<R> setFixedLengthBytes(int length, String captureName) {
            node.setVersion(new AstFixedLengthBytesMatcher(length, captureName));
            return this;
        }

        public StreamNested<R> setRegex(NamedGroupPattern pattern) {
            node.setVersion(new AstRegexMatcher(pattern));
            return this;
        }

        public StreamNested<R> setVariableLengthBytes(ValueExpression length) {
            node.setVersion(new AstVariableLengthBytesMatcher(length));
            return this;
        }

        public StreamNested<R> setVariableLengthBytes(ValueExpression length, String captureName) {
            node.setVersion(new AstVariableLengthBytesMatcher(length, captureName));
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
