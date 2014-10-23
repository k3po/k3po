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

import org.kaazing.robot.lang.ast.AstRegion;

public abstract class AstValueMatcher extends AstRegion {

    public abstract <R, P> R accept(Visitor<R, P> visitor, P parameter) throws Exception;

    public interface Visitor<R, P> {

        R visit(AstExpressionMatcher matcher, P parameter) throws Exception;

        R visit(AstFixedLengthBytesMatcher matcher, P parameter) throws Exception;

        R visit(AstRegexMatcher matcher, P parameter) throws Exception;

        R visit(AstExactTextMatcher matcher, P parameter) throws Exception;

        R visit(AstExactBytesMatcher matcher, P parameter) throws Exception;

        R visit(AstVariableLengthBytesMatcher matcher, P parameter) throws Exception;

        R visit(AstByteLengthBytesMatcher matcher, P parameter) throws Exception;

        R visit(AstShortLengthBytesMatcher matcher, P parameter) throws Exception;

        R visit(AstIntLengthBytesMatcher matcher, P parameter) throws Exception;

        R visit(AstLongLengthBytesMatcher matcher, P parameter) throws Exception;

    }
}
