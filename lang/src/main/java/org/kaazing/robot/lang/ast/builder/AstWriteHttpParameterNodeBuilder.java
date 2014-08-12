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

import org.kaazing.robot.lang.ast.AstStreamNode;
import org.kaazing.robot.lang.ast.AstWriteHttpParameterNode;
import org.kaazing.robot.lang.ast.value.AstExpressionValue;
import org.kaazing.robot.lang.ast.value.AstLiteralBytesValue;
import org.kaazing.robot.lang.ast.value.AstLiteralTextValue;

import javax.el.ValueExpression;

public class AstWriteHttpParameterNodeBuilder extends
        AbstractAstStreamableNodeBuilder<AstWriteHttpParameterNode, AstWriteHttpParameterNode> {

    private int line;

    public AstWriteHttpParameterNodeBuilder() {
        this(new AstWriteHttpParameterNode());
    }

    @Override
    public AstWriteHttpParameterNodeBuilder setLocationInfo(int line, int column) {
        node.setLocationInfo(line, column);
        internalSetLineInfo(line);
        return this;
    }

    @Override
    public AstWriteHttpParameterNodeBuilder setNextLineInfo(int linesToSkip, int column) {
        internalSetNextLineInfo(linesToSkip, column);
        return this;
    }

    public AstWriteHttpParameterNodeBuilder setKeyExactBytes(byte[] keyExactBytes) {
        node.setKey(new AstLiteralBytesValue(keyExactBytes));
        return this;
    }

    public AstWriteHttpParameterNodeBuilder addValueExactBytes(byte[] valueExactBytes) {
        node.addValue(new AstLiteralBytesValue(valueExactBytes));
        return this;
    }

    public AstWriteHttpParameterNodeBuilder setKeyExactText(String keyExactText) {
        node.setKey(new AstLiteralTextValue(keyExactText));
        return this;
    }

    public AstWriteHttpParameterNodeBuilder addValueExactText(String valueExactText) {
        node.addValue(new AstLiteralTextValue(valueExactText));
        return this;
    }

    public AstWriteHttpParameterNodeBuilder setKeyExpression(ValueExpression keyExpression) {
        node.setKey(new AstExpressionValue(keyExpression));
        return this;
    }

    public AstWriteHttpParameterNodeBuilder addValueExpression(ValueExpression valueExpression) {
        node.addValue(new AstExpressionValue(valueExpression));
        return this;
    }

    @Override
    public AstWriteHttpParameterNode done() {
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

    private AstWriteHttpParameterNodeBuilder(AstWriteHttpParameterNode node) {
        super(node, node);
    }

    public static class StreamNested<R extends AbstractAstNodeBuilder<? extends AstStreamNode, ?>> extends
            AbstractAstStreamableNodeBuilder<AstWriteHttpParameterNode, R> {

        public StreamNested(R builder) {
            super(new AstWriteHttpParameterNode(), builder);
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

        public StreamNested<R> setKeyExactBytes(byte[] keyExactBytes) {
            node.setKey(new AstLiteralBytesValue(keyExactBytes));
            return this;
        }

        public StreamNested<R> addValueExactBytes(byte[] valueExactBytes) {
            node.addValue(new AstLiteralBytesValue(valueExactBytes));
            return this;
        }

        public StreamNested<R> setKeyExactText(String keyExactText) {
            node.setKey(new AstLiteralTextValue(keyExactText));
            return this;
        }

        public StreamNested<R> addValueExactText(String valueExactText) {
            node.addValue(new AstLiteralTextValue(valueExactText));
            return this;
        }

        public StreamNested<R> setKeyExpression(ValueExpression keyExpression) {
            node.setKey(new AstExpressionValue(keyExpression));
            return this;
        }

        public StreamNested<R> addValueExpression(ValueExpression valueExpression) {
            node.addValue(new AstExpressionValue(valueExpression));
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
