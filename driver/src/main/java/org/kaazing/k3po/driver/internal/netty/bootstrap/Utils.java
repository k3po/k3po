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
package org.kaazing.k3po.driver.internal.netty.bootstrap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import javax.annotation.Resource;

final class Utils {

    private Utils() {
    }

    public static <T> void inject(Object target, Class<T> injectableType, T injectableInstance) {
        inject0(target, injectableType, injectableInstance);
    }

    public static void inject0(Object target, Class<?> injectableType, Object injectableInstance) {

        Class<?> targetClass = target.getClass();
        Method[] methods = targetClass.getMethods();
        for (Method method : methods) {
            String methodName = method.getName();
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (methodName.startsWith("set") && methodName.length() > "set".length() && parameterTypes.length == 1) {

                Resource annotation = method.getAnnotation(Resource.class);
                if (annotation != null) {
                    Class<?> resourceType = annotation.type();
                    if (resourceType == Object.class) {
                        resourceType = parameterTypes[0];
                    }

                    if (resourceType == injectableType) {
                        try {
                            method.invoke(target, injectableInstance);

                        } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();

                        }
                    }
                }
            }
        }
    }

    public static void injectAll(Object target, Map<Class<?>, Object> injectables) {

        for (Map.Entry<Class<?>, Object> entry : injectables.entrySet()) {
            Class<?> injectableType = entry.getKey();
            Object injectableInstance = entry.getValue();
            inject0(target, injectableType, injectableInstance);
        }
    }
}
