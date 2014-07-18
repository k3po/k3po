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

import static java.lang.reflect.Modifier.isStatic;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.el.ELException;

import org.kaazing.el.Function;

public abstract class FunctionMapperSpi {

    /**
     * Returns the name of the prefix mapped by functions using this service provider.
     */
    public abstract String getPrefixName();

    /**
     * Returns a {@link Method} instance for the prefixed local name.
     * @param localName the local function name
     */
    public abstract Method resolveFunction(String localName);

    public abstract static class Reflective extends FunctionMapperSpi {
        private final Map<String, Method> functions;

        protected Reflective(Class<?> functions) {
            Map<String, Method> functionsAsMap = new HashMap<String, Method>();
            for (Method method : functions.getMethods()) {
                if (isStatic(method.getModifiers())) {
                    Function annotation = method.getAnnotation(Function.class);
                    if (annotation != null) {
                        String localName = annotation.name();
                        if (localName == null || localName.isEmpty()) {
                            localName = method.getName();
                        }

                        if (functionsAsMap.containsKey(localName)) {
                            throw new ELException(String.format("Duplicate @Function name: %s", localName));
                        }

                        functionsAsMap.put(localName, method);
                    }
                }
            }

            this.functions = functionsAsMap;
        }

        @Override
        public Method resolveFunction(String localName) {
            return functions.get(localName);
        }
    }
}
