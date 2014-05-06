/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.lang.ast;

import static org.kaazing.robot.lang.ast.util.AstUtil.equivalent;

import java.util.ArrayList;
import java.util.List;

import org.kaazing.robot.lang.ast.value.AstValue;

public class AstWriteValueNode extends AstCommandNode {

    private List<AstValue> values;

    public List<AstValue> getValues() {
        return values;
    }

    public void setValues(List<AstValue> values) {
        this.values = values;
    }

    public void addValue(AstValue value) {
        if (values == null) {
            values = new ArrayList<AstValue>();
        }
        values.add(value);
    }

    @Deprecated
    public void setValue(AstValue value) {
        if (values != null) {
            throw new IllegalStateException("Can not setValue when there are already values");
        }
        addValue(value);
    }

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) throws Exception {
        return visitor.visit(this, parameter);
    }

    @Override
    public int hashCode() {
        int hashCode = super.hashTo();

        if (values != null) {
            hashCode <<= 4;
            hashCode ^= values.hashCode();
        }

        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || ((obj instanceof AstWriteValueNode) && equals((AstWriteValueNode) obj));
    }

    protected boolean equals(AstWriteValueNode that) {
        return super.equalTo(that) && equivalent(this.values, that.values);
    }

    @Override
    protected void formatNode(StringBuilder sb) {
        super.formatNode(sb);
        sb.append("write");
        for (AstValue val : values) {
            sb.append(" " + val);
        }
        sb.append("\n");
    }
}
