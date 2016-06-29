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
package org.kaazing.k3po.lang.el.spi;

import static java.lang.reflect.Modifier.isStatic;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.el.ELException;

import org.kaazing.k3po.lang.el.Function;

/**
 * Extend this class to make a function available to the K3PO lang.
 *
 */
public abstract class FunctionMapperSpi {

    /**
     * Returns the name of the prefix mapped by functions using this service provider.
     * @return prefixName
     */
    public abstract String getPrefixName();

    /**
     * Returns a {@link Method} instance for the prefixed local name.
     * @param localName the local function name
     * @return the Method / Function
     */
    public abstract Method resolveFunction(String localName);

    /**
     * Reflective FunctionMapper for convenience.
     *
     */
    public abstract static class Reflective extends FunctionMapperSpi {
        private final Map<String, Method> functions;

        protected Reflective(Class<?> functions) {
            Map<String, Method> functionsAsMap = new HashMap<>();
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
