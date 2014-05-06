/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.el.util;

import com.kaazing.el.Function;
import com.kaazing.el.spi.FunctionMapperSpi;

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
