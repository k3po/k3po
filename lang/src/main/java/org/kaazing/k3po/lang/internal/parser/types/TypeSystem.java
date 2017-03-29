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
package org.kaazing.k3po.lang.internal.parser.types;

import static java.util.function.UnaryOperator.identity;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.UnaryOperator;

import org.kaazing.k3po.lang.types.StructuredTypeInfo;
import org.kaazing.k3po.lang.types.TypeInfo;
import org.kaazing.k3po.lang.types.TypeSystemSpi;

public final class TypeSystem {

    private final Map<String, Class<?>> acceptOptions;
    private final Map<String, Class<?>> connectOptions;
    private final Map<String, Class<?>> readOptions;
    private final Map<String, Class<?>> writeOptions;
    private final Map<String, StructuredTypeInfo> readConfigs;
    private final Map<String, StructuredTypeInfo> writeConfigs;

    private TypeSystem(Iterable<TypeSystemSpi> typeSystems) {

        Map<String, Class<?>> acceptOptions = new TreeMap<>();
        Map<String, Class<?>> connectOptions = new TreeMap<>();
        Map<String, Class<?>> readOptions = new TreeMap<>();
        Map<String, Class<?>> writeOptions = new TreeMap<>();
        Map<String, StructuredTypeInfo> readConfigs = new TreeMap<>();
        Map<String, StructuredTypeInfo> writeConfigs = new TreeMap<>();

        for (TypeSystemSpi typeSystem : typeSystems) {
            UnaryOperator<String> qualifier = n -> String.format("%s:%s", typeSystem.getName(), n);
            populateOptions(qualifier, acceptOptions, typeSystem.acceptOptions());
            populateOptions(qualifier, connectOptions, typeSystem.connectOptions());
            populateOptions(qualifier, readOptions, typeSystem.readOptions());
            populateOptions(qualifier, writeOptions, typeSystem.writeOptions());
            populateConfigs(qualifier, readConfigs, typeSystem.readConfigs());
            populateConfigs(qualifier, writeConfigs, typeSystem.writeConfigs());
        }

        TypeSystemSpi defaultTypeSystem = new DefaultTypeSystem();
        populateOptions(identity(), acceptOptions, defaultTypeSystem.acceptOptions());
        populateOptions(identity(), connectOptions, defaultTypeSystem.connectOptions());
        populateOptions(identity(), readOptions, defaultTypeSystem.readOptions());
        populateOptions(identity(), writeOptions, defaultTypeSystem.writeOptions());
        populateConfigs(identity(), readConfigs, defaultTypeSystem.readConfigs());
        populateConfigs(identity(), writeConfigs, defaultTypeSystem.writeConfigs());

        this.acceptOptions = acceptOptions;
        this.connectOptions = connectOptions;
        this.readOptions = readOptions;
        this.writeOptions = writeOptions;
        this.readConfigs = readConfigs;
        this.writeConfigs = writeConfigs;
    }

    public Class<?> acceptOption(String optionName) {
        return verifyOption(acceptOptions.get(optionName), optionName);
    }

    public Class<?> connectOption(String optionName) {
        return verifyOption(connectOptions.get(optionName), optionName);
    }

    public Class<?> readOption(String optionName) {
        return verifyOption(readOptions.get(optionName), optionName);
    }

    public Class<?> writeOption(String optionName) {
        return verifyOption(writeOptions.get(optionName), optionName);
    }

    public StructuredTypeInfo readConfig(String configName) {
        return verifyConfig(readConfigs.get(configName), configName);
    }

    public StructuredTypeInfo writeConfig(String configName) {
        return verifyConfig(writeConfigs.get(configName), configName);
    }

    private static <T> T verifyOption(
        T value,
        String optionName)
    {
        if (value == null) {
            throw new IllegalArgumentException("Unrecognized option: " + optionName);
        }
        return value;
    }

    private static <T> T verifyConfig(
        T value,
        String optionName)
    {
        if (value == null) {
            throw new IllegalArgumentException("Unrecognized config: " + optionName);
        }
        return value;
    }

    private static void populateOptions(
        UnaryOperator<String> qualifier,
        Map<String, Class<?>> optionsByName,
        Set<TypeInfo<?>> options)
    {
        for (TypeInfo<?> option : options) {
            String optionName = option.getName();
            String optionQName = qualifier.apply(optionName);
            Class<?> optionType = option.getType();
            optionsByName.put(optionQName, optionType);
        }
    }

    private static void populateConfigs(
        UnaryOperator<String> qualifier,
        Map<String, StructuredTypeInfo> configsByName,
        Set<StructuredTypeInfo> configs)
    {
        for (StructuredTypeInfo config : configs) {
            String configName = config.getName();
            String configQName = qualifier.apply(configName);
            configsByName.put(configQName, config);
        }
    }

    public static final TypeSystem newInstance() {
        final Collection<TypeSystemSpi> services = new LinkedHashSet<>();

        // TODO: move to http transport, discover via service loader
        services.add(new HttpTypeSystem());

        // TODO: move to file transport, discover via service loader
        services.add(new FileTypeSystem());

        for (TypeSystemSpi service : ServiceLoader.load(TypeSystemSpi.class)) {
            services.add(service);
        }

        return new TypeSystem(services);
    }
}
