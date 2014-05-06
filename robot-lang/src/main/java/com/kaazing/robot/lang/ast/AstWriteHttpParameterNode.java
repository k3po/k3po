/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.lang.ast;

import static com.kaazing.robot.lang.ast.util.AstUtil.equivalent;

import com.kaazing.robot.lang.ast.value.AstValue;

public class AstWriteHttpParameterNode extends AstCommandNode {

    private AstValue key;
    private AstValue value;

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) throws Exception {
        return visitor.visit(this, parameter);
    }

    @Override
    public int hashCode() {
        int hashCode = super.hashTo();

        if (key != null) {
            hashCode <<= 4;
            hashCode ^= key.hashCode();
        }
        if (value != null) {
            hashCode <<= 4;
            hashCode ^= value.hashCode();
        }

        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || ((obj instanceof AstWriteHttpParameterNode) && equals((AstWriteHttpParameterNode) obj));
    }

    protected boolean equals(AstWriteHttpParameterNode that) {
        return super.equalTo(that) && equivalent(this.key, that.key)
                && equivalent(this.value, that.value);
    }

    @Override
    protected void formatNode(StringBuilder sb) {
        super.formatNode(sb);
        sb.append(String.format("write parameter %s %s\n", key, value));
    }

    public AstValue getValue() {
        return value;
    }

    public void setValue(AstValue value) {
        this.value = value;
    }

    public AstValue getKey() {
        return key;
    }

    public void setKey(AstValue key) {
        this.key = key;
    }
}
