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

package org.kaazing.robot.websocket.functions;

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
