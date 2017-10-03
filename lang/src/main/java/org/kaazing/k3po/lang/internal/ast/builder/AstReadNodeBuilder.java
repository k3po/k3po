/**
 * Copyright 2007-2015, Kaazing Corporation. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaazing.k3po.lang.internal.ast.builder;

import javax.el.ValueExpression;

import org.kaazing.k3po.lang.internal.ast.AstReadValueNode;
import org.kaazing.k3po.lang.internal.ast.AstStreamNode;
import org.kaazing.k3po.lang.internal.ast.matcher.AstExactBytesMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstExactTextMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstExpressionMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstFixedLengthBytesMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstNumberMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstRegexMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstVariableLengthBytesMatcher;
import org.kaazing.k3po.lang.internal.el.ExpressionContext;
import org.kaazing.k3po.lang.internal.regex.NamedGroupPattern;

public class AstReadNodeBuilder extends AbstractAstStreamableNodeBuilder<AstReadValueNode, AstReadValueNode> {

    public AstReadNodeBuilder() {
        this(new AstReadValueNode());
    }

    public AstReadNodeBuilder addExactBytes(byte[] exactBytes, ExpressionContext environment) {
        node.addMatcher(new AstExactBytesMatcher(exactBytes));
        return this;
    }

    public AstReadNodeBuilder addExactText(String exactText) {
        node.addMatcher(new AstExactTextMatcher(exactText));
        return this;
    }

    public AstReadNodeBuilder addNumber(Number number) {
        node.addMatcher(new AstNumberMatcher(number));
        return this;
    }

    public AstReadNodeBuilder addExpression(ValueExpression value, ExpressionContext environment) {
        node.addMatcher(new AstExpressionMatcher(value, environment));
        return this;
    }

    public AstReadNodeBuilder addFixedLengthBytes(int length) {
        node.addMatcher(new AstFixedLengthBytesMatcher(length));
        return this;
    }

    public AstReadNodeBuilder addFixedLengthBytes(int length, String captureName, ExpressionContext environment) {
        node.addMatcher(new AstFixedLengthBytesMatcher(length, captureName, environment));
        return this;
    }

    public AstReadNodeBuilder addRegex(NamedGroupPattern pattern, ExpressionContext environment) {
        node.addMatcher(new AstRegexMatcher(pattern, environment));
        return this;
    }

    public AstReadNodeBuilder addVariableLengthBytes(ValueExpression length, ExpressionContext environment) {
        node.addMatcher(new AstVariableLengthBytesMatcher(length, environment));
        return this;
    }

    public AstReadNodeBuilder addVariableLengthBytes(ValueExpression length, String captureName, ExpressionContext environment) {
        node.addMatcher(new AstVariableLengthBytesMatcher(length, captureName, environment));
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

        public StreamNested<R> addExactBytes(byte[] exactBytes, ExpressionContext environment) {
            node.addMatcher(new AstExactBytesMatcher(exactBytes));
            return this;
        }

        public StreamNested<R> addExactText(String exactText) {
            node.addMatcher(new AstExactTextMatcher(exactText));
            return this;
        }

        public StreamNested<R> addExpression(ValueExpression value, ExpressionContext environment) {
            node.addMatcher(new AstExpressionMatcher(value, environment));
            return this;
        }

        public StreamNested<R> addFixedLengthBytes(int length) {
            node.addMatcher(new AstFixedLengthBytesMatcher(length));
            return this;
        }

        public StreamNested<R> addFixedLengthBytes(int length, String captureName, ExpressionContext environment) {
            node.addMatcher(new AstFixedLengthBytesMatcher(length, captureName, environment));
            return this;
        }

        public StreamNested<R> addRegex(NamedGroupPattern pattern, ExpressionContext environment) {
            node.addMatcher(new AstRegexMatcher(pattern, environment));
            return this;
        }

        public StreamNested<R> addVariableLengthBytes(ValueExpression length, ExpressionContext environment) {
            node.addMatcher(new AstVariableLengthBytesMatcher(length, environment));
            return this;
        }

        public StreamNested<R> addVariableLengthBytes(ValueExpression length, String captureName, ExpressionContext environment)
        {
            node.addMatcher(new AstVariableLengthBytesMatcher(length, captureName, environment));
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
