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

package org.kaazing.k3po.driver.netty.bootstrap;

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
