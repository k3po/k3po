/**
 * Copyright 2007-2015, Kaazing Corporation. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaazing.k3po.lang.internal.test.junit;

import org.junit.ComparisonFailure;
import org.kaazing.k3po.lang.internal.ast.AstNode;
import org.kaazing.k3po.lang.internal.ast.matcher.AstValueMatcher;

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
