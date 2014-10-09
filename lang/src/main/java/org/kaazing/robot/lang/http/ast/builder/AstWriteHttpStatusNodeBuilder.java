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
import org.kaazing.robot.lang.http.ast.AstWriteHttpStatusNode;

public class AstWriteHttpStatusNodeBuilder extends
        AbstractAstStreamableNodeBuilder<AstWriteHttpStatusNode, AstWriteHttpStatusNode> {

    private int line;

    public AstWriteHttpStatusNodeBuilder() {
        this(new AstWriteHttpStatusNode());
    }

    @Override
    public AstWriteHttpStatusNodeBuilder setLocationInfo(int line, int column) {
        node.setLocationInfo(line, column);
        internalSetLineInfo(line);
        return this;
    }

    @Override
    public AstWriteHttpStatusNodeBuilder setNextLineInfo(int linesToSkip, int column) {
        internalSetNextLineInfo(linesToSkip, column);
        return this;
    }

    public AstWriteHttpStatusNodeBuilder setCodeExactBytes(byte[] statusCodeExactBytes) {
        node.setCode(new AstLiteralBytesValue(statusCodeExactBytes));
        return this;
    }

    public AstWriteHttpStatusNodeBuilder setReasonExactBytes(byte[] statusReasonExactBytes) {
        node.setReason(new AstLiteralBytesValue(statusReasonExactBytes));
        return this;
    }

    public AstWriteHttpStatusNodeBuilder setCodeExactText(String statusCodeExactText) {
        node.setCode(new AstLiteralTextValue(statusCodeExactText));
        return this;
    }

    public AstWriteHttpStatusNodeBuilder setReasonExactText(String statusReasonExactText) {
        node.setReason(new AstLiteralTextValue(statusReasonExactText));
        return this;
    }

    public AstWriteHttpStatusNodeBuilder setCodeExpression(ValueExpression statusCodeExpression) {
        node.setCode(new AstExpressionValue(statusCodeExpression));
        return this;
    }

    public AstWriteHttpStatusNodeBuilder setReasonExpression(ValueExpression statusReasonExpression) {
        node.setReason(new AstExpressionValue(statusReasonExpression));
        return this;
    }

    @Override
    public AstWriteHttpStatusNode done() {
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

    private AstWriteHttpStatusNodeBuilder(AstWriteHttpStatusNode node) {
        super(node, node);
    }

    public static class StreamNested<R extends AbstractAstNodeBuilder<? extends AstStreamNode, ?>> extends
            AbstractAstStreamableNodeBuilder<AstWriteHttpStatusNode, R> {

        public StreamNested(R builder) {
            super(new AstWriteHttpStatusNode(), builder);
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

        public StreamNested<R> setCodeExactBytes(byte[] statusCodeExactBytes) {
            node.setCode(new AstLiteralBytesValue(statusCodeExactBytes));
            return this;
        }

        public StreamNested<R> setReasonExactBytes(byte[] statusReasonExactBytes) {
            node.setReason(new AstLiteralBytesValue(statusReasonExactBytes));
            return this;
        }

        public StreamNested<R> setCodeExactText(String statusCodeExactText) {
            node.setCode(new AstLiteralTextValue(statusCodeExactText));
            return this;
        }

        public StreamNested<R> setReasonExactText(String statusReasonExactText) {
            node.setReason(new AstLiteralTextValue(statusReasonExactText));
            return this;
        }

        public StreamNested<R> setCodeExpression(ValueExpression statusCodeExpression) {
            node.setCode(new AstExpressionValue(statusCodeExpression));
            return this;
        }

        public StreamNested<R> setReasonExpression(ValueExpression statusReasonExpression) {
            node.setReason(new AstExpressionValue(statusReasonExpression));
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
