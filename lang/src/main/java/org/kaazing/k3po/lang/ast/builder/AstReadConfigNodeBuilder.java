/*
 * Copyright 2014, Kaazing Corporation. All rights reserved.
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

package org.kaazing.k3po.lang.ast.builder;

import javax.el.ValueExpression;

import org.kaazing.k3po.lang.ast.AstReadConfigNode;
import org.kaazing.k3po.lang.ast.AstStreamNode;
import org.kaazing.k3po.lang.ast.matcher.AstExactBytesMatcher;
import org.kaazing.k3po.lang.ast.matcher.AstExactTextMatcher;
import org.kaazing.k3po.lang.ast.matcher.AstExpressionMatcher;
import org.kaazing.k3po.lang.ast.matcher.AstFixedLengthBytesMatcher;
import org.kaazing.k3po.lang.ast.matcher.AstRegexMatcher;
import org.kaazing.k3po.lang.ast.matcher.AstVariableLengthBytesMatcher;
import org.kaazing.k3po.lang.ast.value.AstLiteralTextValue;
import org.kaazing.k3po.lang.regex.NamedGroupPattern;

public class AstReadConfigNodeBuilder extends AbstractAstStreamableNodeBuilder<AstReadConfigNode, AstReadConfigNode> {

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

    public AstReadConfigNodeBuilder setType(String type) {
        node.setType(type);
        return this;
    }

    public AstReadConfigNodeBuilder setValueExactText(String name, String value) {
        node.setValue(name, new AstLiteralTextValue(value));
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

    public AstReadConfigNodeBuilder setMatcherExpression(String name, ValueExpression valueValueExpression) {
        node.setMatcher(name, new AstExpressionMatcher(valueValueExpression));
        return this;
    }

    public AstReadConfigNodeBuilder setMatcherFixedLengthBytes(String name, int valueLength, String valueCaptureName) {
        node.setMatcher(name, new AstFixedLengthBytesMatcher(valueLength, valueCaptureName));
        return this;
    }

    public AstReadConfigNodeBuilder setMatcherRegex(String name, NamedGroupPattern valuePattern) {
        node.setMatcher(name, new AstRegexMatcher(valuePattern));
        return this;
    }

    public AstReadConfigNodeBuilder setMatcherVariableLengthBytes(String name, ValueExpression valueLength) {
        node.setMatcher(name, new AstVariableLengthBytesMatcher(valueLength));
        return this;
    }

    public AstReadConfigNodeBuilder setMatcherVariableLengthBytes(String name, ValueExpression valueLength,
        String valueCaptureName) {
        node.setMatcher(name, new AstVariableLengthBytesMatcher(valueLength, valueCaptureName));
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

    public AstReadConfigNodeBuilder addMatcherExactBytes(byte[] valueBytes) {
        node.addMatcher(new AstExactBytesMatcher(valueBytes));
        return this;
    }

    public AstReadConfigNodeBuilder addMatcherExpression(ValueExpression valueValueExpression) {
        node.addMatcher(new AstExpressionMatcher(valueValueExpression));
        return this;
    }

    public AstReadConfigNodeBuilder addMatcherFixedLengthBytes(int valueLength, String valueCaptureName) {
        node.addMatcher(new AstFixedLengthBytesMatcher(valueLength, valueCaptureName));
        return this;
    }

    public AstReadConfigNodeBuilder addMatcherRegex(NamedGroupPattern valuePattern) {
        node.addMatcher(new AstRegexMatcher(valuePattern));
        return this;
    }

    public AstReadConfigNodeBuilder addMatcherVariableLengthBytes(ValueExpression valueLength) {
        node.addMatcher(new AstVariableLengthBytesMatcher(valueLength));
        return this;
    }

    public AstReadConfigNodeBuilder addMatcherVariableLengthBytes(ValueExpression valueLength, String valueCaptureName) {
        node.addMatcher(new AstVariableLengthBytesMatcher(valueLength, valueCaptureName));
        return this;
    }

    public static class StreamNested<R extends AbstractAstNodeBuilder<? extends AstStreamNode, ?>> extends
            AbstractAstStreamableNodeBuilder<AstReadConfigNode, R> {

        public StreamNested(R builder) {
            super(new AstReadConfigNode(), builder);
        }

        public StreamNested<R> setType(String type) {
            node.setType(type);
            return this;
        }

        public StreamNested<R> setValueExactText(String name, String value) {
            node.setValue(name, new AstLiteralTextValue(value));
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

        public StreamNested<R> setMatcherExactBytes(String name, byte[] valueBytes) {
            node.setMatcher(name, new AstExactBytesMatcher(valueBytes));
            return this;
        }

        public StreamNested<R> setMatcherExpression(String name, ValueExpression valueValueExpression) {
            node.setMatcher(name, new AstExpressionMatcher(valueValueExpression));
            return this;
        }

        public StreamNested<R> setMatcherFixedLengthBytes(String name, int valueLength, String valueCaptureName) {
            node.setMatcher(name, new AstFixedLengthBytesMatcher(valueLength, valueCaptureName));
            return this;
        }

        public StreamNested<R> setMatcherRegex(String name, NamedGroupPattern valuePattern) {
            node.setMatcher(name, new AstRegexMatcher(valuePattern));
            return this;
        }

        public StreamNested<R> setMatcherVariableLengthBytes(String name, ValueExpression valueLength) {
            node.setMatcher(name, new AstVariableLengthBytesMatcher(valueLength));
            return this;
        }

        public StreamNested<R> setMatcherVariableLengthBytes(String name, ValueExpression valueLength, String valueCaptureName) {
            node.setMatcher(name, new AstVariableLengthBytesMatcher(valueLength, valueCaptureName));
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

        public StreamNested<R> addMatcherExactBytes(byte[] valueBytes) {
            node.addMatcher(new AstExactBytesMatcher(valueBytes));
            return this;
        }

        public StreamNested<R> addMatcherExpression(ValueExpression valueValueExpression) {
            node.addMatcher(new AstExpressionMatcher(valueValueExpression));
            return this;
        }

        public StreamNested<R> addMatcherFixedLengthBytes(int valueLength, String valueCaptureName) {
            node.addMatcher(new AstFixedLengthBytesMatcher(valueLength, valueCaptureName));
            return this;
        }

        public StreamNested<R> addMatcherRegex(NamedGroupPattern valuePattern) {
            node.addMatcher(new AstRegexMatcher(valuePattern));
            return this;
        }

        public StreamNested<R> addMatcherVariableLengthBytes(ValueExpression valueLength) {
            node.addMatcher(new AstVariableLengthBytesMatcher(valueLength));
            return this;
        }

        public StreamNested<R> addMatcherVariableLengthBytes(ValueExpression valueLength, String valueCaptureName) {
            node.addMatcher(new AstVariableLengthBytesMatcher(valueLength, valueCaptureName));
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
