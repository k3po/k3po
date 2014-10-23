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

import static java.lang.String.format;
import static org.kaazing.robot.lang.ast.util.AstUtil.equivalent;

import org.kaazing.robot.lang.ast.value.AstValue;

public class AstPropertyNode extends AstNode {

    private String propertyName;
    private AstValue propertyValue;

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) throws Exception {
        return visitor.visit(this, parameter);
    }

    @Override
    public int hashCode() {
        return hashTo();
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || ((obj instanceof AstPropertyNode) && equalTo((AstPropertyNode) obj));
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public AstValue getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(AstValue propertyValue) {
        this.propertyValue = propertyValue;
    }

    protected int hashTo() {
        int hashCode = super.hashTo();

        if (propertyName != null) {
            hashCode <<= 4;
            hashCode ^= propertyName.hashCode();
        }

        if (propertyValue != null) {
            hashCode <<= 4;
            hashCode ^= propertyValue.hashCode();
        }

        return hashCode;
    }

    protected boolean equalTo(AstPropertyNode that) {
        return equivalent(this.propertyName, that.propertyName) && equivalent(this.propertyValue, that.propertyValue);
    }

    @Override
    protected void formatNode(StringBuilder sb) {
        super.formatNode(sb);
        sb.append(format("property %s %s\n", getPropertyName(), getPropertyValue()));
    }

}
