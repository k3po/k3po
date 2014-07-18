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

import org.kaazing.robot.lang.ast.matcher.AstValueMatcher;

public class AstReadValueNode extends AstEventNode {

    private List<AstValueMatcher> matchers;

    public List<AstValueMatcher> getMatchers() {
        return matchers;
    }

    public void setMatchers(List<AstValueMatcher> matchers) {
        this.matchers = matchers;
    }

    public void addMatcher(AstValueMatcher matcher) {
        if (matchers == null) {
            matchers = new ArrayList<AstValueMatcher>();
        }
        matchers.add(matcher);
    }

    @Deprecated
    public void setMatcher(AstValueMatcher matcher) {
        if (matchers != null) {
            throw new IllegalStateException("Can not set a matcher when there already is one");
        }
        addMatcher(matcher);
    }

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) throws Exception {
        return visitor.visit(this, parameter);
    }

    @Override
    public int hashCode() {
        int hashCode = super.hashTo();

        if (matchers != null) {
            hashCode <<= 4;
            hashCode ^= matchers.hashCode();
        }

        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || ((obj instanceof AstReadValueNode) && equals((AstReadValueNode) obj));
    }

    protected boolean equals(AstReadValueNode that) {
        return super.equalTo(that) && equivalent(this.matchers, that.matchers);
    }

    @Override
    protected void formatNode(StringBuilder sb) {
        super.formatNode(sb);
        sb.append("read");
        for (AstValueMatcher matcher : matchers) {
            sb.append(" " + matcher);
        }
        sb.append("\n");
    }
}
