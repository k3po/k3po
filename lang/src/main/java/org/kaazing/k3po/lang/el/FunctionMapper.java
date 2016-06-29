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
package org.kaazing.k3po.lang.el;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.el.ELException;

import org.kaazing.k3po.lang.el.spi.FunctionMapperSpi;

/**
 * Spi for Function in the K3PO Language.
 *
 */
public final class FunctionMapper extends javax.el.FunctionMapper {

    private final Map<String, FunctionMapperSpi> functionMapperSpis;

    private FunctionMapper(Map<String, FunctionMapperSpi> functionMapperSpis) {
        this.functionMapperSpis = functionMapperSpis;
    }

    /**
     * Creates a new Function Mapper.
     * @return returns an instance of the FunctionMapper
     */
    public static FunctionMapper newFunctionMapper() {
        ServiceLoader<FunctionMapperSpi> loader = loadFunctionMapperSpi();

        // load FunctionMapperSpi instances
        ConcurrentMap<String, FunctionMapperSpi> functionMappers = new ConcurrentHashMap<>();
        for (FunctionMapperSpi functionMapperSpi : loader) {
            String prefixName = functionMapperSpi.getPrefixName();
            FunctionMapperSpi oldFunctionMapperSpi = functionMappers.putIfAbsent(prefixName, functionMapperSpi);
            if (oldFunctionMapperSpi != null) {
                throw new ELException(String.format("Duplicate prefix function mapper: %s", prefixName));
            }
        }

        return new FunctionMapper(functionMappers);
    }

    /**
     * Resolves a Function via prefix and local name.
     * @param prefix of the function
     * @param localName of the function
     * @return an instance of a Method
     */
    public Method resolveFunction(String prefix, String localName) {
        FunctionMapperSpi functionMapperSpi = findFunctionMapperSpi(prefix);
        return functionMapperSpi.resolveFunction(localName);
    }

    private static ServiceLoader<FunctionMapperSpi> loadFunctionMapperSpi() {
        Class<FunctionMapperSpi> service = FunctionMapperSpi.class;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return (classLoader != null) ? ServiceLoader.load(service, classLoader) : ServiceLoader.load(service);
    }

    private FunctionMapperSpi findFunctionMapperSpi(String prefix) throws ELException {

        if (prefix == null) {
            throw new NullPointerException("prefix");
        }

        FunctionMapperSpi functionMapper = functionMapperSpis.get(prefix);
        if (functionMapper == null) {
            throw new ELException(String.format("Unable to load prefix '%s': No appropriate function mapper found",
                    prefix));
        }

        return functionMapper;
    }
}
