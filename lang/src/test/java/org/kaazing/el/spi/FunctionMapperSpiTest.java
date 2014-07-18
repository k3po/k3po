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

package org.kaazing.el.spi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;

import javax.el.ELException;

import org.junit.Test;

import org.kaazing.el.Function;

public class FunctionMapperSpiTest {

    public static class Functions {
        @Function
        public static int add(int left, int right) {
            return left + right;
        }

        @Function(name = "add3")
        public static int add(int left, int middle, int right) {
            return left + middle + right;
        }
    }

    public static class FunctionsOfReality {
        // Since we annotated multiple methods with the same function name,
        // resolving this class should cause a problem.

        @Function(name = "collision")
        public static void unstoppableForce() {
        }

        @Function(name = "collision")
        public static void immovableObject() {
        }
    }

    @Test
    public void shouldResolveAddFunction() throws Exception {

        FunctionMapperSpi mapper = new FunctionMapperSpi.Reflective(Functions.class) {
            @Override
            public String getPrefixName() {
                return "test";
            }
        };

        Method add = mapper.resolveFunction("add");
        assertNotNull(add);

        assertEquals(1 + 2, add.invoke(null, 1, 2));
    }

    @Test
    public void shouldResolveAdd3Function() throws Exception {

        FunctionMapperSpi mapper = new FunctionMapperSpi.Reflective(Functions.class) {
            @Override
            public String getPrefixName() {
                return "test";
            }
        };

        Method add3 = mapper.resolveFunction("add3");
        assertNotNull(add3);

        assertEquals(1 + 2 + 3, add3.invoke(null, 1, 2, 3));
    }

    @Test(expected = ELException.class)
    public void shouldCollideOnDuplicateFunctionName() throws Exception {

        new FunctionMapperSpi.Reflective(FunctionsOfReality.class) {
            @Override
            public String getPrefixName() {
                return "test";
            }
        };

        fail();
    }
}
