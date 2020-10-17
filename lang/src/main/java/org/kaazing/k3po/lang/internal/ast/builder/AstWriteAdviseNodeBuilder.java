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
import org.kaazing.k3po.lang.internal.ast.AstWriteAdviseNode;
import org.kaazing.k3po.lang.internal.ast.value.AstExpressionValue;
import org.kaazing.k3po.lang.internal.ast.value.AstLiteralBytesValue;
import org.kaazing.k3po.lang.internal.ast.value.AstLiteralTextValue;
import org.kaazing.k3po.lang.internal.el.ExpressionContext;
import org.kaazing.k3po.lang.types.StructuredTypeInfo;

public class AstWriteAdviseNodeBuilder extends AbstractAstStreamableNodeBuilder<AstWriteAdviseNode, AstWriteAdviseNode> {

    public AstWriteAdviseNodeBuilder() {
        this(new AstWriteAdviseNode());
    }

    private AstWriteAdviseNodeBuilder(AstWriteAdviseNode node) {
        super(node, node);
    }

    @Override
    public AstWriteAdviseNode done() {
        return result;
    }

    public AstWriteAdviseNodeBuilder setType(StructuredTypeInfo type) {
        node.setType(type);
        return this;
    }

    public AstWriteAdviseNodeBuilder setValue(String name, String value) {
        node.setValue(name, new AstLiteralTextValue(value));
        return this;
    }

    public AstWriteAdviseNodeBuilder setValue(String name, byte[] value) {
        node.setValue(name, new AstLiteralBytesValue(value));
        return this;
    }

    public AstWriteAdviseNodeBuilder setValue(String name, ValueExpression value, ExpressionContext environment) {
        node.setValue(name, new AstExpressionValue<>(value, environment));
        return this;
    }

    public AstWriteAdviseNodeBuilder addValue(String value) {
        node.addValue(new AstLiteralTextValue(value));
        return this;
    }

    public AstWriteAdviseNodeBuilder addValue(byte[] value) {
        node.addValue(new AstLiteralBytesValue(value));
        return this;
    }

    public AstWriteAdviseNodeBuilder addValue(ValueExpression value, ExpressionContext environment) {
        node.addValue(new AstExpressionValue<>(value, environment));
        return this;
    }

    public static class StreamNested<R extends AbstractAstNodeBuilder<? extends AstStreamNode, ?>> extends
            AbstractAstStreamableNodeBuilder<AstWriteAdviseNode, R> {

        public StreamNested(R builder) {
            super(new AstWriteAdviseNode(), builder);
        }

        public StreamNested<R> setType(StructuredTypeInfo type) {
            node.setType(type);
            return this;
        }

        public StreamNested<R> setValue(String name, String value) {
            node.setValue(name, new AstLiteralTextValue(value));
            return this;
        }

        public StreamNested<R> setValue(String name, byte[] value) {
            node.setValue(name, new AstLiteralBytesValue(value));
            return this;
        }

        public StreamNested<R> setValue(String name, ValueExpression value, ExpressionContext environment) {
            node.setValue(name, new AstExpressionValue<>(value, environment));
            return this;
        }

        public StreamNested<R> addValue(String value) {
            node.addValue(new AstLiteralTextValue(value));
            return this;
        }

        public StreamNested<R> addValue(byte[] valueBytes) {
            node.addValue(new AstLiteralBytesValue(valueBytes));
            return this;
        }

        public StreamNested<R> addValue(ValueExpression value, ExpressionContext environment) {
            node.addValue(new AstExpressionValue<>(value, environment));
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
