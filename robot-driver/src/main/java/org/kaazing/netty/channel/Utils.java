/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.netty.channel;

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

        Class<? extends Object> targetClass = target.getClass();
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

                        } catch (IllegalArgumentException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();

                        } catch (IllegalAccessException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();

                        } catch (InvocationTargetException e) {
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
