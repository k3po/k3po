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

import org.kaazing.robot.lang.ast.AstPropertyNode;
import org.kaazing.robot.lang.ast.AstScriptNode;
import org.kaazing.robot.lang.ast.value.AstExpressionValue;
import org.kaazing.robot.lang.ast.value.AstLiteralBytesValue;
import org.kaazing.robot.lang.ast.value.AstLiteralTextValue;

public class AstPropertyNodeBuilder extends AbstractAstNodeBuilder<AstPropertyNode, AstPropertyNode> {

    private int line;

    public AstPropertyNodeBuilder() {
        this(new AstPropertyNode());
    }

    @Override
    public AstPropertyNodeBuilder setLocationInfo(int line, int column) {
        node.setLocationInfo(line, column);
        internalSetLineInfo(line);
        return this;
    }

    @Override
    public AstPropertyNodeBuilder setNextLineInfo(int linesToSkip, int column) {
        internalSetNextLineInfo(linesToSkip, column);
        return this;
    }

    public AstPropertyNodeBuilder setPropertyName(String propertyName) {
        node.setPropertyName(propertyName);
        return this;
    }

    public AstPropertyNodeBuilder setPropertyValue(String exactText) {
        node.setPropertyValue(new AstLiteralTextValue(exactText));
        return this;
    }

    public AstPropertyNodeBuilder setPropertyValue(byte[] exactBytes) {
        node.setPropertyValue(new AstLiteralBytesValue(exactBytes));
        return this;
    }

    public AstPropertyNodeBuilder setPropertyValue(ValueExpression expression) {
        node.setPropertyValue(new AstExpressionValue(expression));
        return this;
    }

    @Override
    public AstPropertyNode done() {
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

    private AstPropertyNodeBuilder(AstPropertyNode node) {
        super(node, node);
    }

    public static class ScriptNested<R extends AbstractAstNodeBuilder<? extends AstScriptNode, ?>> extends
            AbstractAstNodeBuilder<AstPropertyNode, R> {

        public ScriptNested(R builder) {
            super(new AstPropertyNode(), builder);
        }

        @Override
        public ScriptNested<R> setLocationInfo(int line, int column) {
            node.setLocationInfo(line, column);
            internalSetLineInfo(line);
            return this;
        }

        @Override
        public ScriptNested<R> setNextLineInfo(int linesToSkip, int column) {
            internalSetNextLineInfo(linesToSkip, column);
            return this;
        }

        public ScriptNested<R> setOptionName(String propertyName) {
            node.setPropertyName(propertyName);
            return this;
        }

        public ScriptNested<R> setPropertyValue(String exactText) {
            node.setPropertyValue(new AstLiteralTextValue(exactText));
            return this;
        }

        public ScriptNested<R> setOptionValue(byte[] exactBytes) {
            node.setPropertyValue(new AstLiteralBytesValue(exactBytes));
            return this;
        }

        public ScriptNested<R> setOptionValue(ValueExpression expression) {
            node.setPropertyValue(new AstExpressionValue(expression));
            return this;
        }

        @Override
        public R done() {
            AstScriptNode scriptNode = result.node;
            scriptNode.getProperties().add(node);
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
