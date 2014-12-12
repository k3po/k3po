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

public class AstAcceptableNode extends AstStreamNode {

    private String acceptName;

    public String getAcceptName() {
        return acceptName;
    }

    public void setAcceptName(String acceptName) {
        this.acceptName = acceptName;
    }

    @Override
    protected int hashTo() {
        int hashCode = super.hashTo();

        if (acceptName != null) {
            hashCode <<= 4;
            hashCode ^= acceptName.hashCode();
        }

        return hashCode;
    }

    @Override
    protected boolean equalTo(AstRegion that) {
        return that instanceof AstAcceptableNode && equalTo((AstAcceptableNode) that);
    }

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) throws Exception {
        return visitor.visit(this, parameter);
    }

    @Override
    protected void describeLine(StringBuilder sb) {
        super.describeLine(sb);

        sb.append("accepted");

        if (acceptName != null) {
            sb.append(" as ");
            sb.append(acceptName);
        }

        sb.append('\n');
    }

    protected boolean equalTo(AstAcceptableNode that) {
        return super.equalTo(that) && equivalent(this.acceptName, that.acceptName);
    }

}
