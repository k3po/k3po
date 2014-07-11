package org.kaazing.robot.lang.ast;

import static java.lang.String.format;

public class AstWriteOptionNode extends AstOptionNode {

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) throws Exception {
        return visitor.visit(this, parameter);
    }

    @Override
    public int hashCode() {
        return hashTo();
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || ((obj instanceof AstWriteOptionNode) && equalTo((AstWriteOptionNode) obj));
    }

    @Override
    protected void formatNode(StringBuilder sb) {
        super.formatNode(sb);
        sb.append(format("write option %s %s\n", getOptionName(), getOptionValue()));
    }
}