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

import static java.util.ServiceLoader.load;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;

import org.kaazing.k3po.lang.types.StructuredTypeInfo;
import org.kaazing.k3po.lang.types.TypeInfo;
import org.kaazing.k3po.lang.types.TypeSystemSpi;

public final class TypeSystem {

    private final Map<String, TypeInfo<?>> acceptOptions;
    private final Map<String, TypeInfo<?>> connectOptions;
    private final Map<String, TypeInfo<?>> readOptions;
    private final Map<String, TypeInfo<?>> writeOptions;
    private final Map<String, StructuredTypeInfo> readConfigs;
    private final Map<String, StructuredTypeInfo> writeConfigs;
    private final Map<String, StructuredTypeInfo> readAdvisories;
    private final Map<String, StructuredTypeInfo> writeAdvisories;

    private TypeSystem(Iterable<TypeSystemSpi> typeSystems) {

        Map<String, TypeInfo<?>> acceptOptions = new TreeMap<>();
        Map<String, TypeInfo<?>> connectOptions = new TreeMap<>();
        Map<String, TypeInfo<?>> readOptions = new TreeMap<>();
        Map<String, TypeInfo<?>> writeOptions = new TreeMap<>();
        Map<String, StructuredTypeInfo> readConfigs = new TreeMap<>();
        Map<String, StructuredTypeInfo> writeConfigs = new TreeMap<>();
        Map<String, StructuredTypeInfo> readAdvisories = new TreeMap<>();
        Map<String, StructuredTypeInfo> writeAdvisories = new TreeMap<>();

        for (TypeSystemSpi typeSystem : typeSystems) {
            Function<TypeInfo<?>, String> namer = t -> String.format("%s:%s", typeSystem.getName(), t.getName());
            Function<StructuredTypeInfo, String> structNamer = t -> String.format("%s:%s", typeSystem.getName(), t.getName());

            populate(namer, acceptOptions, typeSystem.acceptOptions());
            populate(namer, connectOptions, typeSystem.connectOptions());
            populate(namer, readOptions, typeSystem.readOptions());
            populate(namer, writeOptions, typeSystem.writeOptions());
            populate(structNamer, readConfigs, typeSystem.readConfigs());
            populate(structNamer, writeConfigs, typeSystem.writeConfigs());
            populate(structNamer, readAdvisories, typeSystem.readAdvisories());
            populate(structNamer, writeAdvisories, typeSystem.writeAdvisories());
        }

        Function<TypeInfo<?>, String> defaultNamer = t -> t.getName();
        Function<StructuredTypeInfo, String> defaultStructNamer = t -> t.getName();
        TypeSystemSpi defaultTypeSystem = new DefaultTypeSystem();
        populate(defaultNamer, acceptOptions, defaultTypeSystem.acceptOptions());
        populate(defaultNamer, connectOptions, defaultTypeSystem.connectOptions());
        populate(defaultNamer, readOptions, defaultTypeSystem.readOptions());
        populate(defaultNamer, writeOptions, defaultTypeSystem.writeOptions());
        populate(defaultStructNamer, readConfigs, defaultTypeSystem.readConfigs());
        populate(defaultStructNamer, writeConfigs, defaultTypeSystem.writeConfigs());
        populate(defaultStructNamer, readAdvisories, defaultTypeSystem.readAdvisories());
        populate(defaultStructNamer, writeAdvisories, defaultTypeSystem.writeAdvisories());

        this.acceptOptions = acceptOptions;
        this.connectOptions = connectOptions;
        this.readOptions = readOptions;
        this.writeOptions = writeOptions;
        this.readConfigs = readConfigs;
        this.writeConfigs = writeConfigs;
        this.readAdvisories = readAdvisories;
        this.writeAdvisories = writeAdvisories;
    }

    public TypeInfo<?> acceptOption(String optionName) {
        return verifyOption(acceptOptions.get(optionName), optionName);
    }

    public TypeInfo<?> connectOption(String optionName) {
        return verifyOption(connectOptions.get(optionName), optionName);
    }

    public TypeInfo<?> readOption(String optionName) {
        return verifyOption(readOptions.get(optionName), optionName);
    }

    public TypeInfo<?> writeOption(String optionName) {
        return verifyOption(writeOptions.get(optionName), optionName);
    }

    public StructuredTypeInfo readConfig(String configName) {
        return verifyConfig(readConfigs.get(configName), configName);
    }

    public StructuredTypeInfo writeConfig(String configName) {
        return verifyConfig(writeConfigs.get(configName), configName);
    }

    public StructuredTypeInfo readAdvisory(String advisoryName) {
        return verifyAdvisory(readAdvisories.get(advisoryName), advisoryName);
    }

    public StructuredTypeInfo writeAdvisory(String advisoryName) {
        return verifyAdvisory(writeAdvisories.get(advisoryName), advisoryName);
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

    private static <T> T verifyAdvisory(
        T value,
        String advisoryName)
    {
        if (value == null) {
            throw new IllegalArgumentException("Unrecognized advisory: " + advisoryName);
        }
        return value;
    }

    private static <T> void populate(
        Function<T, String> qualifiedNamer,
        Map<String, T> optionsByName,
        Set<T> options)
    {
        for (T option : options) {
            String optionQName = qualifiedNamer.apply(option);
            optionsByName.put(optionQName, option);
        }
    }

    public static final TypeSystem newInstance() {
        return new TypeSystem(load(TypeSystemSpi.class));
    }
}
