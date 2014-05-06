/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.lang.ast.matcher;

import static com.kaazing.robot.lang.ast.util.AstUtil.equivalent;

import com.kaazing.robot.lang.regex.NamedGroupPattern;

public class AstRegexMatcher extends AstValueMatcher {

    private final NamedGroupPattern pattern;
    private String terminator;

    public AstRegexMatcher(NamedGroupPattern pattern) {
        if (pattern == null) {
            throw new NullPointerException("pattern");
        }
        this.pattern = pattern;
    }

    @Deprecated
    public AstRegexMatcher(NamedGroupPattern pattern, String terminator) {
        if (pattern == null) {
            throw new NullPointerException("pattern");
        }
        this.pattern = pattern;
        this.terminator = terminator;
    }

    public NamedGroupPattern getValue() {
        return pattern;
    }

    @Deprecated
    public String getTerminator() {
        return terminator;
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
