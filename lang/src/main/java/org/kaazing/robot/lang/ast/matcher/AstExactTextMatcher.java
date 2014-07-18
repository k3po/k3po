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

package org.kaazing.robot.lang.ast.matcher;

import static org.kaazing.robot.lang.ast.util.AstUtil.equivalent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AstExactTextMatcher extends AstValueMatcher {

    private final String value;

    public AstExactTextMatcher(String value) {
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
        return (this == obj) || (obj instanceof AstExactTextMatcher) && equals((AstExactTextMatcher) obj);
    }

    protected boolean equals(AstExactTextMatcher that) {
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
