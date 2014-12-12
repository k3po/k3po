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

package org.kaazing.k3po.lang.ast.matcher;

import static java.lang.String.format;
import static org.kaazing.k3po.lang.ast.util.AstUtil.equivalent;

import javax.el.ValueExpression;

import org.kaazing.k3po.lang.ast.AstRegion;

public class AstVariableLengthBytesMatcher extends AstValueMatcher {

    private final ValueExpression length;
    private final String captureName;

    public AstVariableLengthBytesMatcher(ValueExpression length) {
        this(length, null);
    }

    public AstVariableLengthBytesMatcher(ValueExpression length, String captureName) {
        this.length = length;
        this.captureName = captureName;
    }

    public ValueExpression getLength() {
        return length;
    }

    public String getCaptureName() {
        return captureName;
    }

    @Override
    protected int hashTo() {
        int hashCode = getClass().hashCode();

        if (length != null) {
            hashCode <<= 4;
            hashCode ^= length.hashCode();
        }

        if (captureName != null) {
            hashCode <<= 4;
            hashCode ^= captureName.hashCode();
        }

        return hashCode;
    }

    @Override
    protected boolean equalTo(AstRegion that) {
        return that instanceof AstVariableLengthBytesMatcher && equalTo((AstVariableLengthBytesMatcher) that);
    }

    protected boolean equalTo(AstVariableLengthBytesMatcher that) {
        return equivalent(this.length, that.length) && equivalent(this.captureName, that.captureName);
    }

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) throws Exception {
        return visitor.visit(this, parameter);
    }

    @Override
    protected void describe(StringBuilder buf) {
        if (captureName != null) {
            buf.append(format("([0..%s]:%s)", length.getExpressionString(), captureName));
        }
        else {
            buf.append(format("[0..%s]", length.getExpressionString()));
        }
    }
}
