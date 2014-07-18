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

public class AstFixedLengthBytesMatcher extends AstValueMatcher {

    private final int length;
    private final String captureName;

    public AstFixedLengthBytesMatcher(int length) {
        this(length, null);
    }

    public AstFixedLengthBytesMatcher(int length, String captureName) {
        this.length = length;
        this.captureName = captureName;
    }

    public int getLength() {
        return length;
    }

    public String getCaptureName() {
        return captureName;
    }

    @Override
    public int hashCode() {
        int hashCode = super.hashCode();

        hashCode <<= 4;
        hashCode ^= length;

        if (captureName != null) {
            hashCode <<= 4;
            hashCode ^= captureName.hashCode();
        }

        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || ((obj instanceof AstFixedLengthBytesMatcher) && equals((AstFixedLengthBytesMatcher) obj));
    }

    protected boolean equals(AstFixedLengthBytesMatcher that) {
        return equivalent(this.length, that.length) && equivalent(this.captureName, that.captureName);
    }

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) throws Exception {

        return visitor.visit(this, parameter);
    }

    @Override
    public String toString() {
        if (captureName != null) {
            return String.format("([0..%d}]:%s)", length, captureName);
        }
        return String.format("[0..%d}]", length);
    }
}
