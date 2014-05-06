/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.lang.ast;

import static org.kaazing.robot.lang.ast.util.AstUtil.equivalent;

import org.kaazing.robot.lang.ast.value.AstValue;

public class AstWriteHttpHeaderNode extends AstCommandNode {

    private AstValue name;
    private AstValue value;

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) throws Exception {
        return visitor.visit(this, parameter);
    }

    @Override
    public int hashCode() {
        int hashCode = super.hashTo();

        if (name != null) {
            hashCode <<= 4;
            hashCode ^= name.hashCode();
        }
        if (value != null) {
            hashCode <<= 4;
            hashCode ^= value.hashCode();
        }

        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || ((obj instanceof AstWriteHttpHeaderNode) && equals((AstWriteHttpHeaderNode) obj));
    }

    protected boolean equals(AstWriteHttpHeaderNode that) {
        return super.equalTo(that) && equivalent(this.name, that.name)
                && equivalent(this.value, that.value);
    }

    @Override
    protected void formatNode(StringBuilder sb) {
        super.formatNode(sb);
        sb.append(String.format("write header %s %s\n", name, value));
    }

    public AstValue getName() {
        return name;
    }

    public void setName(AstValue name) {
        this.name = name;
    }

    public AstValue getValue() {
        return value;
    }

    public void setValue(AstValue value) {
        this.value = value;
    }

}
