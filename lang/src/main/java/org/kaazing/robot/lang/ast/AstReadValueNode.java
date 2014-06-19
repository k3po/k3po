/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
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
