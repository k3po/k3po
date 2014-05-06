/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.lang.ast.value;

import static org.kaazing.robot.lang.ast.util.AstUtil.equivalent;

import java.util.Arrays;

public class AstLiteralBytesValue extends AstValue {

    private final byte[] value;

    public AstLiteralBytesValue(byte[] value) {
        if (value == null) {
            throw new NullPointerException("value");
        }
        this.value = value;
    }

    public byte[] getValue() {
        return value;
    }

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) throws Exception {

        return visitor.visit(this, parameter);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || (obj instanceof AstLiteralBytesValue) && equals((AstLiteralBytesValue) obj);
    }

    protected boolean equals(AstLiteralBytesValue that) {
        return equivalent(this.value, that.value);
    }

    @Override
    public String toString() {
        if (value == null || value.length == 0) {
            return "[]";
        }

        StringBuffer buf = new StringBuffer();
        for (byte b : value) {
            buf.append(String.format(" 0x%02x", b));
        }

        buf.setCharAt(0, '[');
        buf.append(']');
        return buf.toString();
    }
}
