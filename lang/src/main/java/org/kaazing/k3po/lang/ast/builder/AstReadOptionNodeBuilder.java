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

import org.kaazing.k3po.lang.ast.AstReadOptionNode;
import org.kaazing.k3po.lang.ast.AstStreamNode;
import org.kaazing.k3po.lang.ast.value.AstExpressionValue;
import org.kaazing.k3po.lang.ast.value.AstLiteralBytesValue;

public class AstReadOptionNodeBuilder extends AbstractAstStreamableNodeBuilder<AstReadOptionNode, AstReadOptionNode> {

    public AstReadOptionNodeBuilder() {
        this(new AstReadOptionNode());
    }

    public AstReadOptionNodeBuilder setOptionName(String optionName) {
        node.setOptionName(optionName);
        return this;
    }

    public AstReadOptionNodeBuilder setOptionValue(byte[] exactBytes) {
        node.setOptionValue(new AstLiteralBytesValue(exactBytes));
        return this;
    }

    public AstReadOptionNodeBuilder setOptionValue(ValueExpression expression) {
        node.setOptionValue(new AstExpressionValue(expression));
        return this;
    }

    @Override
    public AstReadOptionNode done() {
        return result;
    }

    private AstReadOptionNodeBuilder(AstReadOptionNode node) {
        super(node, node);
    }

    public static class StreamNested<R extends AbstractAstNodeBuilder<? extends AstStreamNode, ?>> extends
            AbstractAstStreamableNodeBuilder<AstReadOptionNode, R> {

        public StreamNested(R builder) {
            super(new AstReadOptionNode(), builder);
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
