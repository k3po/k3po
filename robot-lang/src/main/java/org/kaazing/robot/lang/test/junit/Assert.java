/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
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
