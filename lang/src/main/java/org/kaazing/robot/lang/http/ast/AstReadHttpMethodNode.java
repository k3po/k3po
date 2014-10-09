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

import org.kaazing.robot.lang.ast.AstEventNode;
import org.kaazing.robot.lang.ast.matcher.AstValueMatcher;

public class AstReadHttpMethodNode extends AstEventNode {

    private AstValueMatcher method;

    public AstValueMatcher getMethod() {
        return method;
    }

    public void setMethod(AstValueMatcher method) {
        this.method = method;
    }

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) throws Exception {
        return visitor.visit(this, parameter);
    }

    @Override
    public int hashCode() {
        int hashCode = super.hashTo();

        if (method != null) {
            hashCode <<= 4;
            hashCode ^= method.hashCode();
        }

        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || ((obj instanceof AstReadHttpMethodNode) && equals((AstReadHttpMethodNode) obj));
    }

    protected boolean equals(AstReadHttpMethodNode that) {
        return super.equalTo(that) && equivalent(this.method, that.method);
    }

    @Override
    protected void formatNode(StringBuilder sb) {
        super.formatNode(sb);
        sb.append("read method");
        sb.append(" " + method);
        sb.append("\n");
    }
}
