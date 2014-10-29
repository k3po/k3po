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

package org.kaazing.robot.lang.test.junit;

import org.junit.ComparisonFailure;
import org.kaazing.robot.lang.ast.AstNode;
import org.kaazing.robot.lang.ast.matcher.AstValueMatcher;

/**
 * Drop in replacement for org.junit.Assert.assertEquals Use this so we can get
 * a ComparisonFailure when asserting't that two AstNode's are the same.
 *
 */
public class Assert extends org.junit.Assert {

    public static void assertEquals(Object expected, Object actual) {
        assertEquals(null, expected, actual);
    }

    public static void assertEquals(String message, Object expected,
            Object actual) {
        if ((expected instanceof AstNode && actual instanceof AstNode)
                || (expected instanceof AstValueMatcher && actual instanceof AstValueMatcher)) {
            try {
                org.junit.Assert.assertEquals(message, expected, actual);
            } catch (AssertionError e) {
                String expectedAsString = expected.toString();
                String actualAsString = actual.toString();
                if (!expectedAsString.equals(actualAsString)) {
                    throw new ComparisonFailure(message == null ? "" : message,
                            expected.toString(), actual.toString());
                } else {
                    // As strings they may be equal but as objects they may not
                    throw e;
                }
            }
        } else {
            org.junit.Assert.assertEquals(message, expected, actual);
        }
    }
}
