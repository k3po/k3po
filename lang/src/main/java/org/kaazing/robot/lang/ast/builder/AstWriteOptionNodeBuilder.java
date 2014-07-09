package org.kaazing.robot.lang.ast.builder;

import javax.el.ValueExpression;

import org.kaazing.robot.lang.ast.AstStreamNode;
import org.kaazing.robot.lang.ast.AstWriteOptionNode;
import org.kaazing.robot.lang.ast.value.AstExpressionValue;
import org.kaazing.robot.lang.ast.value.AstLiteralBytesValue;

public class AstWriteOptionNodeBuilder extends AbstractAstStreamableNodeBuilder<AstWriteOptionNode, AstWriteOptionNode> {

    private int line;

    public AstWriteOptionNodeBuilder() {
        this(new AstWriteOptionNode());
    }

    @Override
    public AstWriteOptionNodeBuilder setLocationInfo(int line, int column) {
        node.setLocationInfo(line, column);
        internalSetLineInfo(line);
        return this;
    }

    @Override
    public AstWriteOptionNodeBuilder setNextLineInfo(int linesToSkip, int column) {
        internalSetNextLineInfo(linesToSkip, column);
        return this;
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

    @Override
    protected int line() {
        return line;
    }

    @Override
    protected int line(int line) {
        this.line = line;
        return line;
    }

    private AstWriteOptionNodeBuilder(AstWriteOptionNode node) {
        super(node, node);
    }

    public static class StreamNested<R extends AbstractAstNodeBuilder<? extends AstStreamNode, ?>> extends
            AbstractAstStreamableNodeBuilder<AstWriteOptionNode, R> {

        public StreamNested(R builder) {
            super(new AstWriteOptionNode(), builder);
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