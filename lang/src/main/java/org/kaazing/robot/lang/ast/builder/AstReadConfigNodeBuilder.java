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

import org.kaazing.robot.lang.ast.AstReadConfigNode;
import org.kaazing.robot.lang.ast.AstStreamNode;
import org.kaazing.robot.lang.ast.matcher.AstExactBytesMatcher;
import org.kaazing.robot.lang.ast.matcher.AstExactTextMatcher;
import org.kaazing.robot.lang.ast.matcher.AstExpressionMatcher;
import org.kaazing.robot.lang.ast.matcher.AstFixedLengthBytesMatcher;
import org.kaazing.robot.lang.ast.matcher.AstRegexMatcher;
import org.kaazing.robot.lang.ast.matcher.AstVariableLengthBytesMatcher;
import org.kaazing.robot.lang.ast.value.AstLiteralTextValue;
import org.kaazing.robot.lang.regex.NamedGroupPattern;

public class AstReadConfigNodeBuilder extends
        AbstractAstStreamableNodeBuilder<AstReadConfigNode, AstReadConfigNode> {

    private int line;

    public AstReadConfigNodeBuilder() {
        this(new AstReadConfigNode());
    }

    private AstReadConfigNodeBuilder(AstReadConfigNode node) {
        super(node, node);
    }

    @Override
    public AbstractAstNodeBuilder<AstReadConfigNode, AstReadConfigNode> setNextLineInfo(int linesToSkip,
                                                                                                int column) {
        internalSetNextLineInfo(linesToSkip, column);
        return this;
    }

    @Override
    public AbstractAstNodeBuilder<AstReadConfigNode, AstReadConfigNode> setLocationInfo(int line, int column) {
        node.setLocationInfo(line, column);
        internalSetLineInfo(line);
        return this;
    }

    @Override
    public AstReadConfigNode done() {
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

    public AstReadConfigNodeBuilder setType(String type) {
        node.setType(type);
        return this;
    }

    public AstReadConfigNodeBuilder setValueExactText(String name, String value) {
        node.setValue(name, new AstLiteralTextValue(value));
        return this;
    }

    public AstReadConfigNodeBuilder setMatcherFixedLengthBytes(String name, int valueLength) {
        node.setMatcher(name, new AstFixedLengthBytesMatcher(valueLength));
        return this;
    }

    public AstReadConfigNodeBuilder setMatcherExactText(String name, String valueExactText) {
        node.setMatcher(name, new AstExactTextMatcher(valueExactText));
        return this;
    }

    public AstReadConfigNodeBuilder setMatcherExactBytes(String name, byte[] valueBytes) {
        node.setMatcher(name, new AstExactBytesMatcher(valueBytes));
        return this;
    }

    public AstReadConfigNodeBuilder setMatcherExpression(String name, ValueExpression valueValueExpression) {
        node.setMatcher(name, new AstExpressionMatcher(valueValueExpression));
        return this;
    }

    public AstReadConfigNodeBuilder setMatcherFixedLengthBytes(String name, int valueLength, String valueCaptureName) {
        node.setMatcher(name, new AstFixedLengthBytesMatcher(valueLength, valueCaptureName));
        return this;
    }

    public AstReadConfigNodeBuilder setMatcherRegex(String name, NamedGroupPattern valuePattern) {
        node.setMatcher(name, new AstRegexMatcher(valuePattern));
        return this;
    }

    public AstReadConfigNodeBuilder setMatcherVariableLengthBytes(String name, ValueExpression valueLength) {
        node.setMatcher(name, new AstVariableLengthBytesMatcher(valueLength));
        return this;
    }

    public AstReadConfigNodeBuilder setMatcherVariableLengthBytes(String name, ValueExpression valueLength, String valueCaptureName) {
        node.setMatcher(name, new AstVariableLengthBytesMatcher(valueLength, valueCaptureName));
        return this;
    }

    public AstReadConfigNodeBuilder addMatcherFixedLengthBytes(int valueLength) {
        node.addMatcher(new AstFixedLengthBytesMatcher(valueLength));
        return this;
    }

    public AstReadConfigNodeBuilder addMatcherExactText(String valueExactText) {
        node.addMatcher(new AstExactTextMatcher(valueExactText));
        return this;
    }

    public AstReadConfigNodeBuilder addMatcherExactBytes(byte[] valueBytes) {
        node.addMatcher(new AstExactBytesMatcher(valueBytes));
        return this;
    }

    public AstReadConfigNodeBuilder addMatcherExpression(ValueExpression valueValueExpression) {
        node.addMatcher(new AstExpressionMatcher(valueValueExpression));
        return this;
    }

    public AstReadConfigNodeBuilder addMatcherFixedLengthBytes(int valueLength, String valueCaptureName) {
        node.addMatcher(new AstFixedLengthBytesMatcher(valueLength, valueCaptureName));
        return this;
    }

    public AstReadConfigNodeBuilder addMatcherRegex(NamedGroupPattern valuePattern) {
        node.addMatcher(new AstRegexMatcher(valuePattern));
        return this;
    }

    public AstReadConfigNodeBuilder addMatcherVariableLengthBytes(ValueExpression valueLength) {
        node.addMatcher(new AstVariableLengthBytesMatcher(valueLength));
        return this;
    }

    public AstReadConfigNodeBuilder addMatcherVariableLengthBytes(ValueExpression valueLength, String valueCaptureName) {
        node.addMatcher(new AstVariableLengthBytesMatcher(valueLength, valueCaptureName));
        return this;
    }

    public static class StreamNested<R extends AbstractAstNodeBuilder<? extends AstStreamNode, ?>> extends
            AbstractAstStreamableNodeBuilder<AstReadConfigNode, R> {

        public StreamNested(R builder) {
            super(new AstReadConfigNode(), builder);
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

        public StreamNested<R> setType(String type) {
            node.setType(type);
            return this;
        }

        public StreamNested<R> setValueExactText(String name, String value) {
            node.setValue(name, new AstLiteralTextValue(value));
            return this;
        }

        public StreamNested<R> setMatcherFixedLengthBytes(String name, int valueLength) {
            node.setMatcher(name, new AstFixedLengthBytesMatcher(valueLength));
            return this;
        }

        public StreamNested<R> setMatcherExactText(String name, String valueExactText) {
            node.setMatcher(name, new AstExactTextMatcher(valueExactText));
            return this;
        }

        public StreamNested<R> setMatcherExactBytes(String name, byte[] valueBytes) {
            node.setMatcher(name, new AstExactBytesMatcher(valueBytes));
            return this;
        }

        public StreamNested<R> setMatcherExpression(String name, ValueExpression valueValueExpression) {
            node.setMatcher(name, new AstExpressionMatcher(valueValueExpression));
            return this;
        }

        public StreamNested<R> setMatcherFixedLengthBytes(String name, int valueLength, String valueCaptureName) {
            node.setMatcher(name, new AstFixedLengthBytesMatcher(valueLength, valueCaptureName));
            return this;
        }

        public StreamNested<R> setMatcherRegex(String name, NamedGroupPattern valuePattern) {
            node.setMatcher(name, new AstRegexMatcher(valuePattern));
            return this;
        }

        public StreamNested<R> setMatcherVariableLengthBytes(String name, ValueExpression valueLength) {
            node.setMatcher(name, new AstVariableLengthBytesMatcher(valueLength));
            return this;
        }

        public StreamNested<R> setMatcherVariableLengthBytes(String name, ValueExpression valueLength, String valueCaptureName) {
            node.setMatcher(name, new AstVariableLengthBytesMatcher(valueLength, valueCaptureName));
            return this;
        }

        public StreamNested<R> addMatcherFixedLengthBytes(int valueLength) {
            node.addMatcher(new AstFixedLengthBytesMatcher(valueLength));
            return this;
        }

        public StreamNested<R> addMatcherExactText(String valueExactText) {
            node.addMatcher(new AstExactTextMatcher(valueExactText));
            return this;
        }

        public StreamNested<R> addMatcherExactBytes(byte[] valueBytes) {
            node.addMatcher(new AstExactBytesMatcher(valueBytes));
            return this;
        }

        public StreamNested<R> addMatcherExpression(ValueExpression valueValueExpression) {
            node.addMatcher(new AstExpressionMatcher(valueValueExpression));
            return this;
        }

        public StreamNested<R> addMatcherFixedLengthBytes(int valueLength, String valueCaptureName) {
            node.addMatcher(new AstFixedLengthBytesMatcher(valueLength, valueCaptureName));
            return this;
        }

        public StreamNested<R> addMatcherRegex(NamedGroupPattern valuePattern) {
            node.addMatcher(new AstRegexMatcher(valuePattern));
            return this;
        }

        public StreamNested<R> addMatcherVariableLengthBytes(ValueExpression valueLength) {
            node.addMatcher(new AstVariableLengthBytesMatcher(valueLength));
            return this;
        }

        public StreamNested<R> addMatcherVariableLengthBytes(ValueExpression valueLength, String valueCaptureName) {
            node.addMatcher(new AstVariableLengthBytesMatcher(valueLength, valueCaptureName));
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
