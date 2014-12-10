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
import org.kaazing.robot.lang.ast.AstWriteConfigNode;
import org.kaazing.robot.lang.ast.value.AstExpressionValue;
import org.kaazing.robot.lang.ast.value.AstLiteralBytesValue;
import org.kaazing.robot.lang.ast.value.AstLiteralTextValue;

public class AstWriteConfigNodeBuilder extends
        AbstractAstStreamableNodeBuilder<AstWriteConfigNode, AstWriteConfigNode> {

    public AstWriteConfigNodeBuilder() {
        this(new AstWriteConfigNode());
    }

    private AstWriteConfigNodeBuilder(AstWriteConfigNode node) {
        super(node, node);
    }

    @Override
    public AstWriteConfigNode done() {
        return result;
    }

    public AstWriteConfigNodeBuilder setType(String type) {
        node.setType(type);
        return this;
    }

    public AstWriteConfigNodeBuilder setName(String name, String value) {
        node.setName(name, new AstLiteralTextValue(value));
        return this;
    }

    public AstWriteConfigNodeBuilder setName(String name, byte[] value) {
        node.setName(name, new AstLiteralBytesValue(value));
        return this;
    }

    public AstWriteConfigNodeBuilder setName(String name, ValueExpression value) {
        node.setName(name, new AstExpressionValue(value));
        return this;
    }

    public AstWriteConfigNodeBuilder setValue(String name, String value) {
        node.setValue(name, new AstLiteralTextValue(value));
        return this;
    }

    public AstWriteConfigNodeBuilder setValue(String name, byte[] value) {
        node.setValue(name, new AstLiteralBytesValue(value));
        return this;
    }

    public AstWriteConfigNodeBuilder setValue(String name, ValueExpression value) {
        node.setValue(name, new AstExpressionValue(value));
        return this;
    }

    public AstWriteConfigNodeBuilder addValue(String value) {
        node.addValue(new AstLiteralTextValue(value));
        return this;
    }

    public AstWriteConfigNodeBuilder addValue(byte[] value) {
        node.addValue(new AstLiteralBytesValue(value));
        return this;
    }

    public AstWriteConfigNodeBuilder addValue(ValueExpression value) {
        node.addValue(new AstExpressionValue(value));
        return this;
    }

    public static class StreamNested<R extends AbstractAstNodeBuilder<? extends AstStreamNode, ?>> extends
            AbstractAstStreamableNodeBuilder<AstWriteConfigNode, R> {

        public StreamNested(R builder) {
            super(new AstWriteConfigNode(), builder);
        }

        public StreamNested<R> setType(String type) {
            node.setType(type);
            return this;
        }

        public StreamNested<R> setName(String name, String value) {
            node.setName(name, new AstLiteralTextValue(value));
            return this;
        }

        public StreamNested<R> setName(String name, byte[] value) {
            node.setName(name, new AstLiteralBytesValue(value));
            return this;
        }

        public StreamNested<R> setName(String name, ValueExpression value) {
            node.setName(name, new AstExpressionValue(value));
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

        public StreamNested<R> setValue(String name, ValueExpression value) {
            node.setValue(name, new AstExpressionValue(value));
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

        public StreamNested<R> addValue(ValueExpression value) {
            node.addValue(new AstExpressionValue(value));
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
