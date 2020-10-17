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

import org.kaazing.k3po.lang.internal.ast.AstReadAdvisedNode;
import org.kaazing.k3po.lang.internal.ast.AstStreamNode;
import org.kaazing.k3po.lang.internal.ast.matcher.AstExactBytesMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstExactTextMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstExpressionMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstFixedLengthBytesMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstRegexMatcher;
import org.kaazing.k3po.lang.internal.ast.matcher.AstVariableLengthBytesMatcher;
import org.kaazing.k3po.lang.internal.el.ExpressionContext;
import org.kaazing.k3po.lang.internal.regex.NamedGroupPattern;
import org.kaazing.k3po.lang.types.StructuredTypeInfo;

public class AstReadAdvisedNodeBuilder extends AbstractAstRejectableNodeBuilder<AstReadAdvisedNode, AstReadAdvisedNode> {

    public AstReadAdvisedNodeBuilder() {
        this(new AstReadAdvisedNode());
    }

    private AstReadAdvisedNodeBuilder(AstReadAdvisedNode node) {
        super(node, node);
    }

    @Override
    public AstReadAdvisedNode done() {
        return result;
    }

    public AstReadAdvisedNodeBuilder setType(StructuredTypeInfo type) {
        node.setType(type);
        return this;
    }

    public AstReadAdvisedNodeBuilder setMissing(boolean missing) {
        node.setMissing(missing);
        return this;
    }

    public AstReadAdvisedNodeBuilder setMatcherFixedLengthBytes(String name, int valueLength) {
        node.setMatcher(name, new AstFixedLengthBytesMatcher(valueLength));
        return this;
    }

    public AstReadAdvisedNodeBuilder setMatcherExactText(String name, String valueExactText) {
        node.setMatcher(name, new AstExactTextMatcher(valueExactText));
        return this;
    }

    public AstReadAdvisedNodeBuilder setMatcherExactBytes(String name, byte[] valueBytes) {
        node.setMatcher(name, new AstExactBytesMatcher(valueBytes));
        return this;
    }

    public AstReadAdvisedNodeBuilder setMatcherExpression(String name, ValueExpression valueValueExpression,
        ExpressionContext environment) {
        node.setMatcher(name, new AstExpressionMatcher(valueValueExpression, environment));
        return this;
    }

    public AstReadAdvisedNodeBuilder setMatcherFixedLengthBytes(String name, int valueLength, String valueCaptureName,
        ExpressionContext environment) {
        node.setMatcher(name, new AstFixedLengthBytesMatcher(valueLength, valueCaptureName, environment));
        return this;
    }

    public AstReadAdvisedNodeBuilder setMatcherRegex(String name, NamedGroupPattern valuePattern, ExpressionContext environment) {
        node.setMatcher(name, new AstRegexMatcher(valuePattern, environment));
        return this;
    }

    public AstReadAdvisedNodeBuilder setMatcherVariableLengthBytes(String name, ValueExpression valueLength,
        ExpressionContext environment) {
        node.setMatcher(name, new AstVariableLengthBytesMatcher(valueLength, environment));
        return this;
    }

    public AstReadAdvisedNodeBuilder setMatcherVariableLengthBytes(String name, ValueExpression valueLength,
        String valueCaptureName, ExpressionContext environment) {
        node.setMatcher(name, new AstVariableLengthBytesMatcher(valueLength, valueCaptureName, environment));
        return this;
    }

    public AstReadAdvisedNodeBuilder addMatcherFixedLengthBytes(int valueLength) {
        node.addMatcher(new AstFixedLengthBytesMatcher(valueLength));
        return this;
    }

    public AstReadAdvisedNodeBuilder addMatcherExactText(String valueExactText) {
        node.addMatcher(new AstExactTextMatcher(valueExactText));
        return this;
    }

    public AstReadAdvisedNodeBuilder addMatcherExactBytes(byte[] valueBytes, ExpressionContext environment) {
        node.addMatcher(new AstExactBytesMatcher(valueBytes));
        return this;
    }

    public AstReadAdvisedNodeBuilder addMatcherExpression(ValueExpression valueValueExpression, ExpressionContext environment) {
        node.addMatcher(new AstExpressionMatcher(valueValueExpression, environment));
        return this;
    }

    public AstReadAdvisedNodeBuilder addMatcherFixedLengthBytes(int valueLength, String valueCaptureName,
        ExpressionContext environment) {
        node.addMatcher(new AstFixedLengthBytesMatcher(valueLength, valueCaptureName, environment));
        return this;
    }

    public AstReadAdvisedNodeBuilder addMatcherRegex(NamedGroupPattern valuePattern, ExpressionContext environment) {
        node.addMatcher(new AstRegexMatcher(valuePattern, environment));
        return this;
    }

    public AstReadAdvisedNodeBuilder addMatcherVariableLengthBytes(ValueExpression valueLength, ExpressionContext environment) {
        node.addMatcher(new AstVariableLengthBytesMatcher(valueLength, environment));
        return this;
    }

    public AstReadAdvisedNodeBuilder addMatcherVariableLengthBytes(ValueExpression valueLength, String valueCaptureName,
        ExpressionContext environment) {
        node.addMatcher(new AstVariableLengthBytesMatcher(valueLength, valueCaptureName, environment));
        return this;
    }

    public static class StreamNested<R extends AbstractAstNodeBuilder<? extends AstStreamNode, ?>> extends
            AbstractAstStreamableNodeBuilder<AstReadAdvisedNode, R> {

        public StreamNested(R builder) {
            super(new AstReadAdvisedNode(), builder);
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
