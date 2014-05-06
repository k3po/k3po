/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.el.spi;

import static java.lang.reflect.Modifier.isStatic;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.el.ELException;

import com.kaazing.el.Function;

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
