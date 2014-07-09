/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.lang.ast.matcher;

import static org.kaazing.robot.lang.ast.util.AstUtil.equivalent;

import org.kaazing.robot.lang.regex.NamedGroupPattern;

public class AstRegexMatcher extends AstValueMatcher {

    private final NamedGroupPattern pattern;

    public AstRegexMatcher(NamedGroupPattern pattern) {
        if (pattern == null) {
            throw new NullPointerException("pattern");
        }
        this.pattern = pattern;
    }

    public NamedGroupPattern getValue() {
        return pattern;
    }

    @Override
    public int hashCode() {
        return pattern.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || (obj instanceof AstRegexMatcher) && equals((AstRegexMatcher) obj);
    }

    protected boolean equals(AstRegexMatcher that) {
        return equivalent(this.pattern, that.pattern);
    }

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) throws Exception {

        return visitor.visit(this, parameter);
    }

    @Override
    public String toString() {
        return pattern.toString();
    }
}
