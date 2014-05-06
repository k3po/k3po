/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.lang.ast.matcher;

import static com.kaazing.robot.lang.ast.util.AstUtil.equivalent;

import javax.el.ValueExpression;

public class AstVariableLengthBytesMatcher extends AstValueMatcher {

    private final ValueExpression length;
    private final String captureName;

    public AstVariableLengthBytesMatcher(ValueExpression length) {
        this(length, null);
    }

    public AstVariableLengthBytesMatcher(ValueExpression length, String captureName) {
        this.length = length;
        this.captureName = captureName;
    }

    public ValueExpression getLength() {
        return length;
    }

    public String getCaptureName() {
        return captureName;
    }

    @Override
    public int hashCode() {
        int hashCode = super.hashCode();

        if (length != null) {
            hashCode <<= 4;
            hashCode ^= length.hashCode();
        }

        if (captureName != null) {
            hashCode <<= 4;
            hashCode ^= captureName.hashCode();
        }

        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || ((obj instanceof AstVariableLengthBytesMatcher) && equals((AstVariableLengthBytesMatcher) obj));
    }

    protected boolean equals(AstVariableLengthBytesMatcher that) {
        return equivalent(this.length, that.length) && equivalent(this.captureName, that.captureName);
    }

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) throws Exception {
        return visitor.visit(this, parameter);
    }

    @Override
    public String toString() {
        if (captureName != null) {
            return String.format("([0..%s]:%s)", length.getExpressionString(), captureName);
        }

        return String.format("[0..%s]", length.getExpressionString());
    }
}
