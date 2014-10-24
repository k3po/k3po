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

import javax.el.ValueExpression;

import org.kaazing.robot.lang.ast.AstStreamNode;
import org.kaazing.robot.lang.ast.AstWriteValueNode;
import org.kaazing.robot.lang.ast.value.AstExpressionValue;
import org.kaazing.robot.lang.ast.value.AstLiteralBytesValue;
import org.kaazing.robot.lang.ast.value.AstLiteralTextValue;

public class AstWriteNodeBuilder extends AbstractAstStreamableNodeBuilder<AstWriteValueNode, AstWriteValueNode> {

    public AstWriteNodeBuilder() {
        this(new AstWriteValueNode());
    }

    public AstWriteNodeBuilder addExactBytes(byte[] exactBytes) {
        node.addValue(new AstLiteralBytesValue(exactBytes));
        return this;
    }

    public AstWriteNodeBuilder addExactText(String exactText) {
        node.addValue(new AstLiteralTextValue(exactText));
        return this;
    }

    public AstWriteNodeBuilder addExpression(ValueExpression value) {
        node.addValue(new AstExpressionValue(value));
        return this;
    }

    @Override
    public AstWriteValueNode done() {
        return result;
    }

    private AstWriteNodeBuilder(AstWriteValueNode node) {
        super(node, node);
    }

    public static class StreamNested<R extends AbstractAstNodeBuilder<? extends AstStreamNode, ?>> extends
            AbstractAstStreamableNodeBuilder<AstWriteValueNode, R> {

        public StreamNested(R builder) {
            super(new AstWriteValueNode(), builder);
        }

        public StreamNested<R> addExactBytes(byte[] exactBytes) {
            node.addValue(new AstLiteralBytesValue(exactBytes));
            return this;
        }

        public StreamNested<R> addExactText(String exactText) {
            node.addValue(new AstLiteralTextValue(exactText));
            return this;
        }

        public StreamNested<R> addExpression(ValueExpression value) {
            node.addValue(new AstExpressionValue(value));
            return this;
        }

        @Override
        public R done() {
            AstStreamNode streamNode = result.node;
            streamNode.getStreamables().add(node);
            return result;
        }

    }
}
