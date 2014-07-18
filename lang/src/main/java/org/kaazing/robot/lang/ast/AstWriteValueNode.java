/*
 * Copyright (c) 2014 "Kaazing Corporation," (www.kaazing.com)
 *
 * This file is part of Robot.
 *
 * Robot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
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
