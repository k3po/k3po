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

package org.kaazing.k3po.lang.ast.builder;

import javax.el.ValueExpression;

import org.kaazing.k3po.lang.ast.AstStreamNode;
import org.kaazing.k3po.lang.ast.AstWriteOptionNode;
import org.kaazing.k3po.lang.ast.value.AstExpressionValue;
import org.kaazing.k3po.lang.ast.value.AstLiteralBytesValue;

public class AstWriteOptionNodeBuilder extends AbstractAstStreamableNodeBuilder<AstWriteOptionNode, AstWriteOptionNode> {

    public AstWriteOptionNodeBuilder() {
        this(new AstWriteOptionNode());
    }

    public AstWriteOptionNodeBuilder setOptionName(String optionName) {
        node.setOptionName(optionName);
        return this;
    }

    public AstWriteOptionNodeBuilder setOptionValue(byte[] exactBytes) {
        node.setOptionValue(new AstLiteralBytesValue(exactBytes));
        return this;
    }

    public AstWriteOptionNodeBuilder setOptionValue(ValueExpression expression) {
        node.setOptionValue(new AstExpressionValue(expression));
        return this;
    }

    @Override
    public AstWriteOptionNode done() {
        return result;
    }

    private AstWriteOptionNodeBuilder(AstWriteOptionNode node) {
        super(node, node);
    }

    public static class StreamNested<R extends AbstractAstNodeBuilder<? extends AstStreamNode, ?>> extends
            AbstractAstStreamableNodeBuilder<AstWriteOptionNode, R> {

        public StreamNested(R builder) {
            super(new AstWriteOptionNode(), builder);
        }

        public StreamNested<R> setOptionName(String optionName) {
            node.setOptionName(optionName);
            return this;
        }

        public StreamNested<R> setOptionValue(byte[] exactBytes) {
            node.setOptionValue(new AstLiteralBytesValue(exactBytes));
            return this;
        }

        public StreamNested<R> setOptionValue(ValueExpression expression) {
            node.setOptionValue(new AstExpressionValue(expression));
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
