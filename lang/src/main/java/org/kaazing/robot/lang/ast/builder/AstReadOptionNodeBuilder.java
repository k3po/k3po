package org.kaazing.robot.lang.ast.builder;

import javax.el.ValueExpression;

import org.kaazing.robot.lang.ast.AstReadOptionNode;
import org.kaazing.robot.lang.ast.AstStreamNode;
import org.kaazing.robot.lang.ast.value.AstExpressionValue;
import org.kaazing.robot.lang.ast.value.AstLiteralBytesValue;

public class AstReadOptionNodeBuilder extends AbstractAstStreamableNodeBuilder<AstReadOptionNode, AstReadOptionNode> {

    private int line;

    public AstReadOptionNodeBuilder() {
        this(new AstReadOptionNode());
    }

    @Override
    public AstReadOptionNodeBuilder setLocationInfo(int line, int column) {
        node.setLocationInfo(line, column);
        internalSetLineInfo(line);
        return this;
    }

    @Override
    public AstReadOptionNodeBuilder setNextLineInfo(int linesToSkip, int column) {
        internalSetNextLineInfo(linesToSkip, column);
        return this;
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

    @Override
    protected int line() {
        return line;
    }

    @Override
    protected int line(int line) {
        this.line = line;
        return line;
    }

    private AstReadOptionNodeBuilder(AstReadOptionNode node) {
        super(node, node);
    }

    public static class StreamNested<R extends AbstractAstNodeBuilder<? extends AstStreamNode, ?>> extends
            AbstractAstStreamableNodeBuilder<AstReadOptionNode, R> {

        public StreamNested(R builder) {
            super(new AstReadOptionNode(), builder);
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