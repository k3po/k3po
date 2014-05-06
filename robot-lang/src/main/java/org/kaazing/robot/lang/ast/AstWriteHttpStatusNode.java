/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.lang.ast;

import static org.kaazing.robot.lang.ast.util.AstUtil.equivalent;

import org.kaazing.robot.lang.ast.value.AstValue;

public class AstWriteHttpStatusNode extends AstCommandNode {

    private AstValue code;
    private AstValue reason;

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) throws Exception {
        return visitor.visit(this, parameter);
    }

    @Override
    public int hashCode() {
        int hashCode = super.hashTo();

        if (code != null) {
            hashCode <<= 4;
            hashCode ^= code.hashCode();
        }
        if (reason != null) {
            hashCode <<= 4;
            hashCode ^= reason.hashCode();
        }

        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || ((obj instanceof AstWriteHttpStatusNode) && equals((AstWriteHttpStatusNode) obj));
    }

    protected boolean equals(AstWriteHttpStatusNode that) {
        return super.equalTo(that) && equivalent(this.reason, that.reason)
                && equivalent(this.code, that.code);
    }

    @Override
    protected void formatNode(StringBuilder sb) {
        super.formatNode(sb);
        sb.append(String.format("write status %s %s\n", code, reason));
    }

    public AstValue getReason() {
        return reason;
    }

    public void setReason(AstValue reason) {
        this.reason = reason;
    }

    public AstValue getCode() {
        return code;
    }

    public void setCode(AstValue code) {
        this.code = code;
    }

}
