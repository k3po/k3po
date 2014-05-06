/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.lang.ast.matcher;

import static com.kaazing.robot.lang.ast.util.AstUtil.equivalent;

public class AstFixedLengthBytesMatcher extends AstValueMatcher {

    private final int length;
    private final String captureName;

    public AstFixedLengthBytesMatcher(int length) {
        this(length, null);
    }

    public AstFixedLengthBytesMatcher(int length, String captureName) {
        this.length = length;
        this.captureName = captureName;
    }

    public int getLength() {
        return length;
    }

    public String getCaptureName() {
        return captureName;
    }

    @Override
    public int hashCode() {
        int hashCode = super.hashCode();

        hashCode <<= 4;
        hashCode ^= length;

        if (captureName != null) {
            hashCode <<= 4;
            hashCode ^= captureName.hashCode();
        }

        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || ((obj instanceof AstFixedLengthBytesMatcher) && equals((AstFixedLengthBytesMatcher) obj));
    }

    protected boolean equals(AstFixedLengthBytesMatcher that) {
        return equivalent(this.length, that.length) && equivalent(this.captureName, that.captureName);
    }

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) throws Exception {

        return visitor.visit(this, parameter);
    }

    @Override
    public String toString() {
        if (captureName != null) {
            return String.format("([0..%d}]:%s)", length, captureName);
        }
        return String.format("[0..%d}]", length);
    }
}
