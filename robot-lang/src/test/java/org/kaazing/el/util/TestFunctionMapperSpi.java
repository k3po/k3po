/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.el.util;

import org.kaazing.el.Function;
import org.kaazing.el.spi.FunctionMapperSpi;

public class TestFunctionMapperSpi extends FunctionMapperSpi.Reflective {

    public static class Functions {

        @Function(name = "add")
        public static int add(int left, int right) {
            return left + right;
        }
    }

    public TestFunctionMapperSpi() {
        super(Functions.class);
    }

    @Override
    public String getPrefixName() {
        return "test";
    }
}
