/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.lang.el;

import org.kaazing.el.Function;
import org.kaazing.el.spi.FunctionMapperSpi;

public class CustomFunctionMapperSpi extends FunctionMapperSpi.Reflective {

    public static class Functions {
        @Function(name = "add2")
        public static int add(int left, int right) {
            return left + left;
        }
    }

    public CustomFunctionMapperSpi() {
        super(Functions.class);
    }

    @Override
    public String getPrefixName() {
        return "custom";
    }
}
