/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.lang.ast;

import static com.kaazing.robot.lang.ast.util.AstUtil.equivalent;

import com.kaazing.robot.lang.ast.matcher.AstValueMatcher;

public class AstReadHttpStatusNode extends AstEventNode {

    private AstValueMatcher code;
    private AstValueMatcher reason;

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
        return (this == obj) || ((obj instanceof AstReadHttpStatusNode) && equals((AstReadHttpStatusNode) obj));
    }

    protected boolean equals(AstReadHttpStatusNode that) {
        return super.equalTo(that) && equivalent(this.code, that.code)
                && equivalent(this.reason, that.reason);
    }

    @Override
    protected void formatNode(StringBuilder sb) {
        super.formatNode(sb);
        sb.append(String.format("read status %s %s\n", code, reason));
    }

    public AstValueMatcher getCode() {
        return code;
    }

    public void setCode(AstValueMatcher code) {
        this.code = code;
    }

    public AstValueMatcher getReason() {
        return reason;
    }

    public void setReason(AstValueMatcher reason) {
        this.reason = reason;
    }
}
