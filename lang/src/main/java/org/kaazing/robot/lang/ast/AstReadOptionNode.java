package org.kaazing.robot.lang.ast;

import static java.lang.String.format;

public class AstReadOptionNode extends AstOptionNode {

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
        return (this == obj) || ((obj instanceof AstReadOptionNode) && equalTo((AstReadOptionNode) obj));
    }

    @Override
    protected void formatNode(StringBuilder sb) {
        super.formatNode(sb);
        sb.append(format("read option %s %s\n", getOptionName(), getOptionValue()));
    }
}
