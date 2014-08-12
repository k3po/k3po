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
import org.kaazing.robot.lang.ast.AstWriteHttpHeaderNode;
import org.kaazing.robot.lang.ast.value.AstExpressionValue;
import org.kaazing.robot.lang.ast.value.AstLiteralBytesValue;
import org.kaazing.robot.lang.ast.value.AstLiteralTextValue;

import javax.el.ValueExpression;

public class AstWriteHttpHeaderNodeBuilder extends
        AbstractAstStreamableNodeBuilder<AstWriteHttpHeaderNode, AstWriteHttpHeaderNode> {

    private int line;

    public AstWriteHttpHeaderNodeBuilder() {
        this(new AstWriteHttpHeaderNode());
    }

    @Override
    public AstWriteHttpHeaderNodeBuilder setLocationInfo(int line, int column) {
        node.setLocationInfo(line, column);
        internalSetLineInfo(line);
        return this;
    }

    @Override
    public AstWriteHttpHeaderNodeBuilder setNextLineInfo(int linesToSkip, int column) {
        internalSetNextLineInfo(linesToSkip, column);
        return this;
    }

    public AstWriteHttpHeaderNodeBuilder setNameExactBytes(byte[] headerNameExactBytes) {
        node.setName(new AstLiteralBytesValue(headerNameExactBytes));
        return this;
    }

    public AstWriteHttpHeaderNodeBuilder addValueExactBytes(byte[] headerValueExactBytes) {
        node.addValue(new AstLiteralBytesValue(headerValueExactBytes));
        return this;
    }

    public AstWriteHttpHeaderNodeBuilder setNameExactText(String headerNameExactText) {
        node.setName(new AstLiteralTextValue(headerNameExactText));
        return this;
    }

    public AstWriteHttpHeaderNodeBuilder addValueExactText(String headerValueExactText) {
        node.addValue(new AstLiteralTextValue(headerValueExactText));
        return this;
    }

    public AstWriteHttpHeaderNodeBuilder setNameExpression(ValueExpression headerNameValueExpression) {
        node.setName(new AstExpressionValue(headerNameValueExpression));
        return this;
    }

    public AstWriteHttpHeaderNodeBuilder addValueExpression(ValueExpression headerValueValueExpression) {
        node.addValue(new AstExpressionValue(headerValueValueExpression));
        return this;
    }

    @Override
    public AstWriteHttpHeaderNode done() {
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

    private AstWriteHttpHeaderNodeBuilder(AstWriteHttpHeaderNode node) {
        super(node, node);
    }

    public static class StreamNested<R extends AbstractAstNodeBuilder<? extends AstStreamNode, ?>> extends
            AbstractAstStreamableNodeBuilder<AstWriteHttpHeaderNode, R> {

        public StreamNested(R builder) {
            super(new AstWriteHttpHeaderNode(), builder);
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

        public StreamNested<R> setNameExactBytes(byte[] headerNameExactBytes) {
            node.setName(new AstLiteralBytesValue(headerNameExactBytes));
            return this;
        }

        public StreamNested<R> addValueExactBytes(byte[] headerValueExactBytes) {
            node.addValue(new AstLiteralBytesValue(headerValueExactBytes));
            return this;
        }

        public StreamNested<R> setNameExactText(String headerNameExactText) {
            node.setName(new AstLiteralTextValue(headerNameExactText));
            return this;
        }

        public StreamNested<R> addValueExactText(String headerValueExactText) {
            node.addValue(new AstLiteralTextValue(headerValueExactText));
            return this;
        }

        public StreamNested<R> setNameValueExpression(ValueExpression headerNameValueExpression) {
            node.setName(new AstExpressionValue(headerNameValueExpression));
            return this;
        }

        public StreamNested<R> addValueValueExpression(ValueExpression headerValueValueExpression) {
            node.addValue(new AstExpressionValue(headerValueValueExpression));
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
