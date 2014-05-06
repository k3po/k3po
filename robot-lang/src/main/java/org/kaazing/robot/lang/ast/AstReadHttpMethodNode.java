/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.lang.ast;

import static org.kaazing.robot.lang.ast.util.AstUtil.equivalent;

import org.kaazing.robot.lang.ast.matcher.AstValueMatcher;

public class AstReadHttpMethodNode extends AstEventNode {

    private AstValueMatcher method;

    public AstValueMatcher getMethod() {
        return method;
    }

    public void setMethod(AstValueMatcher method) {
        this.method = method;
    }

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) throws Exception {
        return visitor.visit(this, parameter);
    }

    @Override
    public int hashCode() {
        int hashCode = super.hashTo();

        if (method != null) {
            hashCode <<= 4;
            hashCode ^= method.hashCode();
        }

        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || ((obj instanceof AstReadHttpMethodNode) && equals((AstReadHttpMethodNode) obj));
    }

    protected boolean equals(AstReadHttpMethodNode that) {
        return super.equalTo(that) && equivalent(this.method, that.method);
    }

    @Override
    protected void formatNode(StringBuilder sb) {
        super.formatNode(sb);
        sb.append("read method");
        sb.append(" " + method);
        sb.append("\n");
    }
}
