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
package org.kaazing.k3po.lang.internal.ast.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

import javax.el.ValueExpression;

public final class AstUtil {

    public static boolean equivalent(boolean v, boolean v2) {
        return v == v2;
    }

    public static boolean equivalent(byte v, byte v2) {
        return v == v2;
    }

    public static boolean equivalent(char v, char v2) {
        return v == v2;
    }

    public static boolean equivalent(double v, double v2) {
        return v == v2;
    }

    public static boolean equivalent(float v, float v2) {
        return v == v2;
    }

    public static boolean equivalent(int v, int v2) {
        return v == v2;
    }

    public static boolean equivalent(long v, long v2) {
        return v == v2;
    }

    public static boolean equivalent(short v, short v2) {
        return v == v2;
    }

    public static boolean equivalent(ValueExpression e, ValueExpression e2) {
        return e == e2
                || (e != null && e.getExpressionString().equals(e2.getExpressionString()) && e.getExpectedType().equals(
                        e2.getExpectedType()));
    }

    public static boolean equivalent(Pattern p, Pattern p2) {
        return p == p2 || (p != null && p.pattern().equals(p2.pattern()));
    }

    public static boolean equivalent(Object o, Object o2) {
        return o == o2 || (o != null && o.equals(o2));
    }

    public static boolean equivalent(Collection<?> c, Collection<?> c2) {
        if (c != null && c.isEmpty()) {
            c = null;
        }
        if (c2 != null && c2.isEmpty()) {
            c2 = null;
        }
        return c == c2 || (c != null && c.equals(c2));
    }

    public static boolean equivalent(boolean[] a, boolean[] a2) {
        return a == a2 || Arrays.equals(a, a2);
    }

    public static boolean equivalent(byte[] a, byte[] a2) {
        return a == a2 || Arrays.equals(a, a2);
    }

    public static boolean equivalent(char[] a, char[] a2) {
        return a == a2 || Arrays.equals(a, a2);
    }

    public static boolean equivalent(double[] a, double[] a2) {
        return a == a2 || Arrays.equals(a, a2);
    }

    public static boolean equivalent(float[] a, float[] a2) {
        return a == a2 || Arrays.equals(a, a2);
    }

    public static boolean equivalent(int[] a, int[] a2) {
        return a == a2 || Arrays.equals(a, a2);
    }

    public static boolean equivalent(long[] a, long[] a2) {
        return a == a2 || Arrays.equals(a, a2);
    }

    public static boolean equivalent(short[] a, short[] a2) {
        return a == a2 || Arrays.equals(a, a2);
    }

    public static boolean equivalent(Object[] a, Object[] a2) {
        return a == a2 || Arrays.equals(a, a2);
    }

    private AstUtil() {
        // utility class
    }
}
