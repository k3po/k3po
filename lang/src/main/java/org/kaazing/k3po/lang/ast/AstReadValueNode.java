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

import org.kaazing.k3po.lang.ast.matcher.AstValueMatcher;

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

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) throws Exception {
        return visitor.visit(this, parameter);
    }

    @Override
    protected int hashTo() {
        int hashCode = getClass().hashCode();

        if (matchers != null) {
            hashCode <<= 4;
            hashCode ^= matchers.hashCode();
        }

        return hashCode;
    }

    @Override
    protected boolean equalTo(AstRegion that) {
        return that instanceof AstReadValueNode && equalTo((AstReadValueNode) that);
    }

    protected boolean equalTo(AstReadValueNode that) {
        return equivalent(this.matchers, that.matchers);
    }

    @Override
    protected void describe(StringBuilder buf) {
        super.describe(buf);
        buf.append("read");
        for (AstValueMatcher matcher : matchers) {
            buf.append(" " + matcher);
        }
        buf.append("\n");
    }
}
