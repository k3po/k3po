/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.lang.ast;

import static com.kaazing.robot.lang.ast.util.AstUtil.equivalent;

public class AstAcceptableNode extends AstStreamNode {

    private String acceptName;

    public String getAcceptName() {
        return acceptName;
    }

    public void setAcceptName(String acceptName) {
        this.acceptName = acceptName;
    }

    @Override
    public int hashCode() {
        int hashCode = super.hashTo();

        if (acceptName != null) {
            hashCode <<= 4;
            hashCode ^= acceptName.hashCode();
        }

        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || ((obj instanceof AstAcceptableNode) && equals((AstAcceptableNode) obj));
    }

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) throws Exception {
        return visitor.visit(this, parameter);
    }

    @Override
    protected void formatNodeLine(StringBuilder sb) {
        super.formatNodeLine(sb);

        sb.append("accepted");

        if (acceptName != null) {
            sb.append(" as ");
            sb.append(acceptName);
        }

        sb.append('\n');
    }

    protected boolean equals(AstAcceptableNode that) {
        return super.equalTo(that) && equivalent(this.acceptName, that.acceptName);
    }

}
