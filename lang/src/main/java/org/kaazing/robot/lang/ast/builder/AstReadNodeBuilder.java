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

    public AstReadNodeBuilder() {
        this(new AstReadValueNode());
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

    private AstReadNodeBuilder(AstReadValueNode node) {
        super(node, node);
    }

    public static class StreamNested<R extends AbstractAstNodeBuilder<? extends AstStreamNode, ?>> extends
            AbstractAstStreamableNodeBuilder<AstReadValueNode, R> {

        public StreamNested(R builder) {
            super(new AstReadValueNode(), builder);
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
    }
}
