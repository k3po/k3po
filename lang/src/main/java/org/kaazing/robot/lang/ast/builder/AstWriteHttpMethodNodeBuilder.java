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
import org.kaazing.robot.lang.ast.AstWriteHttpMethodNode;
import org.kaazing.robot.lang.ast.value.AstExpressionValue;
import org.kaazing.robot.lang.ast.value.AstLiteralBytesValue;
import org.kaazing.robot.lang.ast.value.AstLiteralTextValue;

import javax.el.ValueExpression;

public class AstWriteHttpMethodNodeBuilder extends
        AbstractAstStreamableNodeBuilder<AstWriteHttpMethodNode, AstWriteHttpMethodNode> {

    private int line;

    public AstWriteHttpMethodNodeBuilder() {
        this(new AstWriteHttpMethodNode());
    }

    @Override
    public AstWriteHttpMethodNodeBuilder setLocationInfo(int line, int column) {
        node.setLocationInfo(line, column);
        internalSetLineInfo(line);
        return this;
    }

    @Override
    public AstWriteHttpMethodNodeBuilder setNextLineInfo(int linesToSkip, int column) {
        internalSetNextLineInfo(linesToSkip, column);
        return this;
    }

    public AstWriteHttpMethodNodeBuilder setExactBytes(byte[] exactBytes) {
        node.setMethod(new AstLiteralBytesValue(exactBytes));
        return this;
    }

    public AstWriteHttpMethodNodeBuilder setExactText(String exactText) {
        node.setMethod(new AstLiteralTextValue(exactText));
        return this;
    }

    public AstWriteHttpMethodNodeBuilder setExpression(ValueExpression value) {
        node.setMethod(new AstExpressionValue(value));
        return this;
    }

    @Override
    public AstWriteHttpMethodNode done() {
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

    private AstWriteHttpMethodNodeBuilder(AstWriteHttpMethodNode node) {
        super(node, node);
    }

    public static class StreamNested<R extends AbstractAstNodeBuilder<? extends AstStreamNode, ?>> extends
            AbstractAstStreamableNodeBuilder<AstWriteHttpMethodNode, R> {

        public StreamNested(R builder) {
            super(new AstWriteHttpMethodNode(), builder);
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
            node.setMethod(new AstLiteralBytesValue(exactBytes));
            return this;
        }

        public StreamNested<R> setExactText(String exactText) {
            node.setMethod(new AstLiteralTextValue(exactText));
            return this;
        }

        public StreamNested<R> setExpression(ValueExpression value) {
            node.setMethod(new AstExpressionValue(value));
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
