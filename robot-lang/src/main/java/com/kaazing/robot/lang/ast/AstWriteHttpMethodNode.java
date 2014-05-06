/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.lang.ast;

import static com.kaazing.robot.lang.ast.util.AstUtil.equivalent;

import com.kaazing.robot.lang.ast.value.AstValue;

public class AstWriteHttpMethodNode extends AstCommandNode {

    private AstValue method;

    public AstValue getMethod() {
        return method;
    }

    public void setMethod(AstValue method) {
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
        return (this == obj) || ((obj instanceof AstWriteHttpMethodNode) && equals((AstWriteHttpMethodNode) obj));
    }

    protected boolean equals(AstWriteHttpMethodNode that) {
        return super.equalTo(that) && equivalent(this.method, that.method);
    }

    @Override
    protected void formatNode(StringBuilder sb) {
        super.formatNode(sb);
        sb.append(String.format("write method %s\n", method));
    }

}
