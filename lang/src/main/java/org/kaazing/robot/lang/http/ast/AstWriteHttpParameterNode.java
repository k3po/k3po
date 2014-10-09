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

package org.kaazing.robot.lang.http.ast;

import static org.kaazing.robot.lang.ast.util.AstUtil.equivalent;

import org.kaazing.robot.lang.ast.AstCommandNode;
import org.kaazing.robot.lang.ast.value.AstValue;

public class AstWriteHttpParameterNode extends AstCommandNode {

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
        return (this == obj) || ((obj instanceof AstWriteHttpParameterNode) && equals((AstWriteHttpParameterNode) obj));
    }

    protected boolean equals(AstWriteHttpParameterNode that) {
        return super.equalTo(that) && equivalent(this.name, that.name)
                && equivalent(this.value, that.value);
    }

    @Override
    protected void formatNode(StringBuilder sb) {
        super.formatNode(sb);
        sb.append(String.format("write parameter %s %s\n", name, value));
    }

    public AstValue getValue() {
        return value;
    }

    public void setValue(AstValue value) {
        this.value = value;
    }

    public AstValue getName() {
        return name;
    }

    public void setName(AstValue name) {
        this.name = name;
    }
}
