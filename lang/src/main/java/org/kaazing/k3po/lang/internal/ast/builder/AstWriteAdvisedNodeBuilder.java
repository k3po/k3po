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

import org.kaazing.k3po.lang.internal.ast.AstStreamNode;
import org.kaazing.k3po.lang.internal.ast.AstWriteAdvisedNode;
import org.kaazing.k3po.lang.internal.ast.matcher.AstExactBytesMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstExactTextMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstExpressionMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstFixedLengthBytesMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstRegexMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstVariableLengthBytesMatcher;
import org.kaazing.k3po.lang.internal.el.ExpressionContext;
import org.kaazing.k3po.lang.internal.regex.NamedGroupPattern;
import org.kaazing.k3po.lang.types.StructuredTypeInfo;

public class AstWriteAdvisedNodeBuilder extends AbstractAstRejectableNodeBuilder<AstWriteAdvisedNode, AstWriteAdvisedNode> {

    public AstWriteAdvisedNodeBuilder() {
        this(new AstWriteAdvisedNode());
    }

    private AstWriteAdvisedNodeBuilder(AstWriteAdvisedNode node) {
        super(node, node);
    }

    @Override
    public AstWriteAdvisedNode done() {
        return result;
    }

    public AstWriteAdvisedNodeBuilder setType(StructuredTypeInfo type) {
        node.setType(type);
        return this;
    }

    public AstWriteAdvisedNodeBuilder setMissing(boolean missing) {
        node.setMissing(missing);
        return this;
    }

    public AstWriteAdvisedNodeBuilder setMatcherFixedLengthBytes(String name, int valueLength) {
        node.setMatcher(name, new AstFixedLengthBytesMatcher(valueLength));
        return this;
    }

    public AstWriteAdvisedNodeBuilder setMatcherExactText(String name, String valueExactText) {
        node.setMatcher(name, new AstExactTextMatcher(valueExactText));
        return this;
    }

    public AstWriteAdvisedNodeBuilder setMatcherExactBytes(String name, byte[] valueBytes) {
        node.setMatcher(name, new AstExactBytesMatcher(valueBytes));
        return this;
    }

    public AstWriteAdvisedNodeBuilder setMatcherExpression(String name, ValueExpression valueValueExpression,
        ExpressionContext environment) {
        node.setMatcher(name, new AstExpressionMatcher(valueValueExpression, environment));
        return this;
    }

    public AstWriteAdvisedNodeBuilder setMatcherFixedLengthBytes(String name, int valueLength, String valueCaptureName,
        ExpressionContext environment) {
        node.setMatcher(name, new AstFixedLengthBytesMatcher(valueLength, valueCaptureName, environment));
        return this;
    }

    public AstWriteAdvisedNodeBuilder setMatcherRegex(String name, NamedGroupPattern valuePattern, ExpressionContext environment) {
        node.setMatcher(name, new AstRegexMatcher(valuePattern, environment));
        return this;
    }

    public AstWriteAdvisedNodeBuilder setMatcherVariableLengthBytes(String name, ValueExpression valueLength,
        ExpressionContext environment) {
        node.setMatcher(name, new AstVariableLengthBytesMatcher(valueLength, environment));
        return this;
    }

    public AstWriteAdvisedNodeBuilder setMatcherVariableLengthBytes(String name, ValueExpression valueLength,
        String valueCaptureName, ExpressionContext environment) {
        node.setMatcher(name, new AstVariableLengthBytesMatcher(valueLength, valueCaptureName, environment));
        return this;
    }

    public AstWriteAdvisedNodeBuilder addMatcherFixedLengthBytes(int valueLength) {
        node.addMatcher(new AstFixedLengthBytesMatcher(valueLength));
        return this;
    }

    public AstWriteAdvisedNodeBuilder addMatcherExactText(String valueExactText) {
        node.addMatcher(new AstExactTextMatcher(valueExactText));
        return this;
    }

    public AstWriteAdvisedNodeBuilder addMatcherExactBytes(byte[] valueBytes, ExpressionContext environment) {
        node.addMatcher(new AstExactBytesMatcher(valueBytes));
        return this;
    }

    public AstWriteAdvisedNodeBuilder addMatcherExpression(ValueExpression valueValueExpression, ExpressionContext environment) {
        node.addMatcher(new AstExpressionMatcher(valueValueExpression, environment));
        return this;
    }

    public AstWriteAdvisedNodeBuilder addMatcherFixedLengthBytes(int valueLength, String valueCaptureName,
        ExpressionContext environment) {
        node.addMatcher(new AstFixedLengthBytesMatcher(valueLength, valueCaptureName, environment));
        return this;
    }

    public AstWriteAdvisedNodeBuilder addMatcherRegex(NamedGroupPattern valuePattern, ExpressionContext environment) {
        node.addMatcher(new AstRegexMatcher(valuePattern, environment));
        return this;
    }

    public AstWriteAdvisedNodeBuilder addMatcherVariableLengthBytes(ValueExpression valueLength, ExpressionContext environment) {
        node.addMatcher(new AstVariableLengthBytesMatcher(valueLength, environment));
        return this;
    }

    public AstWriteAdvisedNodeBuilder addMatcherVariableLengthBytes(ValueExpression valueLength, String valueCaptureName,
        ExpressionContext environment) {
        node.addMatcher(new AstVariableLengthBytesMatcher(valueLength, valueCaptureName, environment));
        return this;
    }

    public static class StreamNested<R extends AbstractAstNodeBuilder<? extends AstStreamNode, ?>> extends
            AbstractAstStreamableNodeBuilder<AstWriteAdvisedNode, R> {

        public StreamNested(R builder) {
            super(new AstWriteAdvisedNode(), builder);
        }

        public StreamNested<R> setType(StructuredTypeInfo type) {
            node.setType(type);
            return this;
        }

        public StreamNested<R> setMissing(boolean missing) {
            node.setMissing(missing);
            return this;
        }

        public StreamNested<R> setMatcherFixedLengthBytes(String name, int valueLength) {
            node.setMatcher(name, new AstFixedLengthBytesMatcher(valueLength));
            return this;
        }

        public StreamNested<R> setMatcherExactText(String name, String valueExactText) {
            node.setMatcher(name, new AstExactTextMatcher(valueExactText));
            return this;
        }

        public StreamNested<R> setMatcherExactBytes(String name, byte[] valueBytes, ExpressionContext environment) {
            node.setMatcher(name, new AstExactBytesMatcher(valueBytes));
            return this;
        }

        public StreamNested<R> setMatcherExpression(String name, ValueExpression valueValueExpression,
            ExpressionContext environment) {
            node.setMatcher(name, new AstExpressionMatcher(valueValueExpression, environment));
            return this;
        }

        public StreamNested<R> setMatcherFixedLengthBytes(String name, int valueLength, String valueCaptureName,
            ExpressionContext environment) {
            node.setMatcher(name, new AstFixedLengthBytesMatcher(valueLength, valueCaptureName, environment));
            return this;
        }

        public StreamNested<R> setMatcherRegex(String name, NamedGroupPattern valuePattern, ExpressionContext environment) {
            node.setMatcher(name, new AstRegexMatcher(valuePattern, environment));
            return this;
        }

        public StreamNested<R> setMatcherVariableLengthBytes(String name, ValueExpression valueLength,
            ExpressionContext environment) {
            node.setMatcher(name, new AstVariableLengthBytesMatcher(valueLength, environment));
            return this;
        }

        public StreamNested<R> setMatcherVariableLengthBytes(String name, ValueExpression valueLength, String valueCaptureName,
            ExpressionContext environment) {
            node.setMatcher(name, new AstVariableLengthBytesMatcher(valueLength, valueCaptureName, environment));
            return this;
        }

        public StreamNested<R> addMatcherFixedLengthBytes(int valueLength) {
            node.addMatcher(new AstFixedLengthBytesMatcher(valueLength));
            return this;
        }

        public StreamNested<R> addMatcherExactText(String valueExactText) {
            node.addMatcher(new AstExactTextMatcher(valueExactText));
            return this;
        }

        public StreamNested<R> addMatcherExactBytes(byte[] valueBytes, ExpressionContext environment) {
            node.addMatcher(new AstExactBytesMatcher(valueBytes));
            return this;
        }

        public StreamNested<R> addMatcherExpression(ValueExpression valueValueExpression, ExpressionContext environment) {
            node.addMatcher(new AstExpressionMatcher(valueValueExpression, environment));
            return this;
        }

        public StreamNested<R> addMatcherFixedLengthBytes(int valueLength, String valueCaptureName,
                ExpressionContext environment) {
            node.addMatcher(new AstFixedLengthBytesMatcher(valueLength, valueCaptureName, environment));
            return this;
        }

        public StreamNested<R> addMatcherRegex(NamedGroupPattern valuePattern, ExpressionContext environment) {
            node.addMatcher(new AstRegexMatcher(valuePattern, environment));
            return this;
        }

        public StreamNested<R> addMatcherVariableLengthBytes(ValueExpression valueLength, ExpressionContext environment) {
            node.addMatcher(new AstVariableLengthBytesMatcher(valueLength, environment));
            return this;
        }

        public StreamNested<R> addMatcherVariableLengthBytes(ValueExpression valueLength, String valueCaptureName,
            ExpressionContext environment) {
            node.addMatcher(new AstVariableLengthBytesMatcher(valueLength, valueCaptureName, environment));
            return this;
        }

        @Override
        public R done() {
            AstStreamNode streamNode = node(result);
            streamNode.getStreamables().add(node);
            return result;
        }
    }
}
