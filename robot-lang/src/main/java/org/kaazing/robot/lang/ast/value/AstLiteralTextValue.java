/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.lang.ast.value;

import static org.kaazing.robot.lang.ast.util.AstUtil.equivalent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AstLiteralTextValue extends AstValue {

    private final String value;

    public AstLiteralTextValue(String value) {
        if (value == null) {
            throw new NullPointerException("value");
        }
        String v = value;

        Pattern p = Pattern.compile("\\r", Pattern.LITERAL);
        Matcher m = p.matcher(value);
        v = m.replaceAll("\r");

        p = Pattern.compile("\\n", Pattern.LITERAL);
        m = p.matcher(v);
        this.value = m.replaceAll("\n");
    }

    public String getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || (obj instanceof AstLiteralTextValue) && equals((AstLiteralTextValue) obj);
    }

    protected boolean equals(AstLiteralTextValue that) {
        return equivalent(this.value, that.value);
    }

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) throws Exception {

        return visitor.visit(this, parameter);
    }

    @Override
    public String toString() {
        return String.format("\"%s\"", value);
    }
}
