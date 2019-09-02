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

import org.kaazing.k3po.lang.internal.ast.AstReadConfigNode;
import org.kaazing.k3po.lang.internal.ast.AstRejectedNode;
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

public class AstReadConfigNodeBuilder extends AbstractAstRejectableNodeBuilder<AstReadConfigNode, AstReadConfigNode> {

    public AstReadConfigNodeBuilder() {
        this(new AstReadConfigNode());
    }

    private AstReadConfigNodeBuilder(AstReadConfigNode node) {
        super(node, node);
    }

    @Override
    public AstReadConfigNode done() {
        return result;
    }

    public AstReadConfigNodeBuilder setType(StructuredTypeInfo type) {
        node.setType(type);
        return this;
    }

    public AstReadConfigNodeBuilder setMissing(boolean missing) {
        node.setMissing(missing);
        return this;
    }

    public AstReadConfigNodeBuilder setMatcherFixedLengthBytes(String name, int valueLength) {
        node.setMatcher(name, new AstFixedLengthBytesMatcher(valueLength));
        return this;
    }

    public AstReadConfigNodeBuilder setMatcherExactText(String name, String valueExactText) {
        node.setMatcher(name, new AstExactTextMatcher(valueExactText));
        return this;
    }

    public AstReadConfigNodeBuilder setMatcherExactBytes(String name, byte[] valueBytes) {
        node.setMatcher(name, new AstExactBytesMatcher(valueBytes));
        return this;
    }

    public AstReadConfigNodeBuilder setMatcherExpression(String name, ValueExpression valueValueExpression,
        ExpressionContext environment) {
        node.setMatcher(name, new AstExpressionMatcher(valueValueExpression, environment));
        return this;
    }

    public AstReadConfigNodeBuilder setMatcherFixedLengthBytes(String name, int valueLength, String valueCaptureName,
        ExpressionContext environment) {
        node.setMatcher(name, new AstFixedLengthBytesMatcher(valueLength, valueCaptureName, environment));
        return this;
    }

    public AstReadConfigNodeBuilder setMatcherRegex(String name, NamedGroupPattern valuePattern, ExpressionContext environment) {
        node.setMatcher(name, new AstRegexMatcher(valuePattern, environment));
        return this;
    }

    public AstReadConfigNodeBuilder setMatcherVariableLengthBytes(String name, ValueExpression valueLength,
        ExpressionContext environment) {
        node.setMatcher(name, new AstVariableLengthBytesMatcher(valueLength, environment));
        return this;
    }

    public AstReadConfigNodeBuilder setMatcherVariableLengthBytes(String name, ValueExpression valueLength,
        String valueCaptureName, ExpressionContext environment) {
        node.setMatcher(name, new AstVariableLengthBytesMatcher(valueLength, valueCaptureName, environment));
        return this;
    }

    public AstReadConfigNodeBuilder addMatcherFixedLengthBytes(int valueLength) {
        node.addMatcher(new AstFixedLengthBytesMatcher(valueLength));
        return this;
    }

    public AstReadConfigNodeBuilder addMatcherExactText(String valueExactText) {
        node.addMatcher(new AstExactTextMatcher(valueExactText));
        return this;
    }

    public AstReadConfigNodeBuilder addMatcherExactBytes(byte[] valueBytes, ExpressionContext environment) {
        node.addMatcher(new AstExactBytesMatcher(valueBytes));
        return this;
    }

    public AstReadConfigNodeBuilder addMatcherExpression(ValueExpression valueValueExpression, ExpressionContext environment) {
        node.addMatcher(new AstExpressionMatcher(valueValueExpression, environment));
        return this;
    }

    public AstReadConfigNodeBuilder addMatcherFixedLengthBytes(int valueLength, String valueCaptureName,
        ExpressionContext environment) {
        node.addMatcher(new AstFixedLengthBytesMatcher(valueLength, valueCaptureName, environment));
        return this;
    }

    public AstReadConfigNodeBuilder addMatcherRegex(NamedGroupPattern valuePattern, ExpressionContext environment) {
        node.addMatcher(new AstRegexMatcher(valuePattern, environment));
        return this;
    }

    public AstReadConfigNodeBuilder addMatcherVariableLengthBytes(ValueExpression valueLength, ExpressionContext environment) {
        node.addMatcher(new AstVariableLengthBytesMatcher(valueLength, environment));
        return this;
    }

    public AstReadConfigNodeBuilder addMatcherVariableLengthBytes(ValueExpression valueLength, String valueCaptureName,
        ExpressionContext environment) {
        node.addMatcher(new AstVariableLengthBytesMatcher(valueLength, valueCaptureName, environment));
        return this;
    }

    public static class StreamNested<R extends AbstractAstNodeBuilder<? extends AstStreamNode, ?>> extends
            AbstractAstStreamableNodeBuilder<AstReadConfigNode, R> {

        public StreamNested(R builder) {
            super(new AstReadConfigNode(), builder);
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

    public static class RejectedNested<R extends AbstractAstNodeBuilder<? extends AstRejectedNode, ?>>
            extends AbstractAstRejectableNodeBuilder<AstReadConfigNode, R>
    {

        public RejectedNested(R builder)
        {
            super(new AstReadConfigNode(), builder);
        }

        public RejectedNested<R> setType(StructuredTypeInfo type)
        {
            node.setType(type);
            return this;
        }

        public RejectedNested<R> setMissing(boolean missing)
        {
            node.setMissing(missing);
            return this;
        }

        public RejectedNested<R> setMatcherFixedLengthBytes(String name, int valueLength)
        {
            node.setMatcher(name, new AstFixedLengthBytesMatcher(valueLength));
            return this;
        }

        public RejectedNested<R> setMatcherExactText(String name, String valueExactText)
        {
            node.setMatcher(name, new AstExactTextMatcher(valueExactText));
            return this;
        }

        public RejectedNested<R> setMatcherExactBytes(String name, byte[] valueBytes, ExpressionContext environment)
        {
            node.setMatcher(name, new AstExactBytesMatcher(valueBytes));
            return this;
        }

        public RejectedNested<R> setMatcherExpression(String name, ValueExpression valueValueExpression,
                ExpressionContext environment)
        {
            node.setMatcher(name, new AstExpressionMatcher(valueValueExpression, environment));
            return this;
        }

        public RejectedNested<R> setMatcherFixedLengthBytes(String name, int valueLength, String valueCaptureName,
                ExpressionContext environment)
        {
            node.setMatcher(name, new AstFixedLengthBytesMatcher(valueLength, valueCaptureName, environment));
            return this;
        }

        public RejectedNested<R> setMatcherRegex(String name, NamedGroupPattern valuePattern, ExpressionContext environment)
        {
            node.setMatcher(name, new AstRegexMatcher(valuePattern, environment));
            return this;
        }

        public RejectedNested<R> setMatcherVariableLengthBytes(String name, ValueExpression valueLength,
                ExpressionContext environment)
        {
            node.setMatcher(name, new AstVariableLengthBytesMatcher(valueLength, environment));
            return this;
        }

        public RejectedNested<R> setMatcherVariableLengthBytes(String name, ValueExpression valueLength, String valueCaptureName,
                ExpressionContext environment)
        {
            node.setMatcher(name, new AstVariableLengthBytesMatcher(valueLength, valueCaptureName, environment));
            return this;
        }

        public RejectedNested<R> addMatcherFixedLengthBytes(int valueLength)
        {
            node.addMatcher(new AstFixedLengthBytesMatcher(valueLength));
            return this;
        }

        public RejectedNested<R> addMatcherExactText(String valueExactText)
        {
            node.addMatcher(new AstExactTextMatcher(valueExactText));
            return this;
        }

        public RejectedNested<R> addMatcherExactBytes(byte[] valueBytes, ExpressionContext environment)
        {
            node.addMatcher(new AstExactBytesMatcher(valueBytes));
            return this;
        }

        public RejectedNested<R> addMatcherExpression(ValueExpression valueValueExpression, ExpressionContext environment)
        {
            node.addMatcher(new AstExpressionMatcher(valueValueExpression, environment));
            return this;
        }

        public RejectedNested<R> addMatcherFixedLengthBytes(int valueLength, String valueCaptureName, ExpressionContext environment)
        {
            node.addMatcher(new AstFixedLengthBytesMatcher(valueLength, valueCaptureName, environment));
            return this;
        }

        public RejectedNested<R> addMatcherRegex(NamedGroupPattern valuePattern, ExpressionContext environment)
        {
            node.addMatcher(new AstRegexMatcher(valuePattern, environment));
            return this;
        }

        public RejectedNested<R> addMatcherVariableLengthBytes(ValueExpression valueLength, ExpressionContext environment)
        {
            node.addMatcher(new AstVariableLengthBytesMatcher(valueLength, environment));
            return this;
        }

        public RejectedNested<R> addMatcherVariableLengthBytes(ValueExpression valueLength, String valueCaptureName,
                ExpressionContext environment)
        {
            node.addMatcher(new AstVariableLengthBytesMatcher(valueLength, valueCaptureName, environment));
            return this;
        }

        @Override
        public R done()
        {
            AstRejectedNode streamNode = node(result);
            streamNode.getStreamables().add(node);
            return result;
        }
    }

}
