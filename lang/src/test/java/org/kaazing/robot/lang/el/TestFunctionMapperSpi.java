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

package org.kaazing.robot.lang.el;

import org.kaazing.robot.lang.el.Function;
import org.kaazing.robot.lang.el.spi.FunctionMapperSpi;

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
