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

package org.kaazing.robot.lang.ast.value;

import static org.kaazing.robot.lang.ast.util.AstUtil.equivalent;

import java.util.Arrays;

import org.kaazing.robot.lang.ast.AstRegion;

public final class AstLiteralBytesValue extends AstValue {

    private final byte[] value;

    public AstLiteralBytesValue(byte[] value) {
        if (value == null) {
            throw new NullPointerException("value");
        }
        this.value = value;
    }

    public byte[] getValue() {
        return value;
    }

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) throws Exception {

        return visitor.visit(this, parameter);
    }

    @Override
    protected int hashTo() {
        return Arrays.hashCode(value);
    }

    @Override
    protected boolean equalTo(AstRegion that) {
        return (that instanceof AstLiteralBytesValue) && equalTo((AstLiteralBytesValue) that);
    }

    protected boolean equalTo(AstLiteralBytesValue that) {
        return equivalent(this.value, that.value);
    }

    @Override
    protected void describe(StringBuilder buf) {
        if (value == null || value.length == 0) {
            buf.append("[]");
        }
        else {
            for (byte b : value) {
                buf.append(String.format(" 0x%02x", b));
            }

            buf.setCharAt(0, '[');
            buf.append(']');
        }
    }
}
