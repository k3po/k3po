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

package org.kaazing.robot.lang.http.ast.builder;

import javax.el.ValueExpression;

import org.kaazing.robot.lang.ast.AstStreamNode;
import org.kaazing.robot.lang.ast.builder.AbstractAstNodeBuilder;
import org.kaazing.robot.lang.ast.builder.AbstractAstStreamableNodeBuilder;
import org.kaazing.robot.lang.ast.value.AstExpressionValue;
import org.kaazing.robot.lang.ast.value.AstLiteralBytesValue;
import org.kaazing.robot.lang.ast.value.AstLiteralTextValue;
import org.kaazing.robot.lang.http.ast.AstWriteHttpVersionNode;

public class AstWriteHttpVersionNodeBuilder extends
        AbstractAstStreamableNodeBuilder<AstWriteHttpVersionNode, AstWriteHttpVersionNode> {

    private int line;

    public AstWriteHttpVersionNodeBuilder() {
        this(new AstWriteHttpVersionNode());
    }

    @Override
    public AstWriteHttpVersionNodeBuilder setLocationInfo(int line, int column) {
        node.setLocationInfo(line, column);
        internalSetLineInfo(line);
        return this;
    }

    @Override
    public AstWriteHttpVersionNodeBuilder setNextLineInfo(int linesToSkip, int column) {
        internalSetNextLineInfo(linesToSkip, column);
        return this;
    }

    public AstWriteHttpVersionNodeBuilder setExactBytes(byte[] exactBytes) {
        node.setVersion(new AstLiteralBytesValue(exactBytes));
        return this;
    }

    public AstWriteHttpVersionNodeBuilder setExactText(String exactText) {
        node.setVersion(new AstLiteralTextValue(exactText));
        return this;
    }

    public AstWriteHttpVersionNodeBuilder setExpression(ValueExpression value) {
        node.setVersion(new AstExpressionValue(value));
        return this;
    }

    @Override
    public AstWriteHttpVersionNode done() {
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

    private AstWriteHttpVersionNodeBuilder(AstWriteHttpVersionNode node) {
        super(node, node);
    }

    public static class StreamNested<R extends AbstractAstNodeBuilder<? extends AstStreamNode, ?>> extends
            AbstractAstStreamableNodeBuilder<AstWriteHttpVersionNode, R> {

        public StreamNested(R builder) {
            super(new AstWriteHttpVersionNode(), builder);
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
            node.setVersion(new AstLiteralBytesValue(exactBytes));
            return this;
        }

        public StreamNested<R> setExactText(String exactText) {
            node.setVersion(new AstLiteralTextValue(exactText));
            return this;
        }

        public StreamNested<R> setExpression(ValueExpression value) {
            node.setVersion(new AstExpressionValue(value));
            return this;
        }

        @Override
        public R done() {
            AstStreamNode streamNode = node(result);
            streamNode.getStreamables().add(node);
            return result;
        }

        @Override
        protected int line() {
            return line(result);
        }

        @Override
        protected int line(int line) {
            return line(result, line);
        }
    }
}
