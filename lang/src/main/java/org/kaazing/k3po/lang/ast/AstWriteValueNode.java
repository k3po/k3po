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

package org.kaazing.k3po.lang.ast;

import static org.kaazing.k3po.lang.ast.util.AstUtil.equivalent;

import java.util.ArrayList;
import java.util.List;

import org.kaazing.k3po.lang.ast.value.AstValue;

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
    protected int hashTo() {
        int hashCode = getClass().hashCode();

        if (values != null) {
            hashCode <<= 4;
            hashCode ^= values.hashCode();
        }

        return hashCode;
    }

    @Override
    protected boolean equalTo(AstRegion that) {
        return that instanceof AstWriteValueNode && equalTo((AstWriteValueNode) that);
    }

    protected boolean equalTo(AstWriteValueNode that) {
        return equivalent(this.values, that.values);
    }

    @Override
    protected void describe(StringBuilder buf) {
        super.describe(buf);
        buf.append("write");
        for (AstValue val : values) {
            buf.append(" " + val);
        }
        buf.append("\n");
    }
}
