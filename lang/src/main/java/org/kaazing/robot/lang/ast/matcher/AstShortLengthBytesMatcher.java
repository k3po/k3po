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

import static java.lang.String.format;

public class AstShortLengthBytesMatcher extends AstFixedLengthBytesMatcher {

    @Deprecated
    public AstShortLengthBytesMatcher() {
        super(Short.SIZE / Byte.SIZE);
    }

    public AstShortLengthBytesMatcher(String captureName) {
        super(Short.SIZE / Byte.SIZE, captureName);
    }

    @Override
    public <R, P> R accept(Visitor<R, P> visitor, P parameter) throws Exception {
        return visitor.visit(this, parameter);
    }

    @Override
    protected void describe(StringBuilder buf) {
        String captureName = getCaptureName();
        if (captureName == null) {
            buf.append("short");
        }
        else {
            buf.append(format("(short:%s)", captureName));
        }
    }
}
