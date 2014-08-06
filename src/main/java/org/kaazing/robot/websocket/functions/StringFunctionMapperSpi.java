package org.kaazing.robot.websocket.functions;
/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */


import static org.jboss.netty.util.CharsetUtil.UTF_8;

import org.kaazing.robot.lang.el.Function;
import org.kaazing.robot.lang.el.spi.FunctionMapperSpi;


public class StringFunctionMapperSpi extends FunctionMapperSpi.Reflective {

    public static class Functions {

        @Function
        public static byte[] asBytes(String utf8) {
            return utf8.getBytes(UTF_8);
        }

        @Function
        public static String fromBytes(byte[] bytes) {
            return new String(bytes, UTF_8);
        }

    }

    public StringFunctionMapperSpi() {
        super(Functions.class);
    }

    @Override
    public String getPrefixName() {
        return "string";
    }

}
