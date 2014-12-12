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

package org.kaazing.k3po.lang.el;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.el.ELException;

import org.kaazing.k3po.lang.el.spi.FunctionMapperSpi;

public final class FunctionMapper extends javax.el.FunctionMapper {

    private final Map<String, FunctionMapperSpi> functionMapperSpis;

    private FunctionMapper(Map<String, FunctionMapperSpi> functionMapperSpis) {
        this.functionMapperSpis = functionMapperSpis;
    }

    public static FunctionMapper newFunctionMapper() {
        ServiceLoader<FunctionMapperSpi> loader = loadFunctionMapperSpi();

        // load FunctionMapperSpi instances
        ConcurrentMap<String, FunctionMapperSpi> functionMappers = new ConcurrentHashMap<String, FunctionMapperSpi>();
        for (FunctionMapperSpi functionMapperSpi : loader) {
            String prefixName = functionMapperSpi.getPrefixName();
            FunctionMapperSpi oldFunctionMapperSpi = functionMappers.putIfAbsent(prefixName, functionMapperSpi);
            if (oldFunctionMapperSpi != null) {
                throw new ELException(String.format("Duplicate prefix function mapper: %s", prefixName));
            }
        }

        return new FunctionMapper(functionMappers);
    }

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
