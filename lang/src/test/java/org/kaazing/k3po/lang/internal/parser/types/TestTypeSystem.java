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

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;

import java.net.URI;
import java.util.LinkedHashSet;
import java.util.Set;

import org.kaazing.k3po.lang.types.StructuredTypeInfo;
import org.kaazing.k3po.lang.types.TypeInfo;
import org.kaazing.k3po.lang.types.TypeSystemSpi;

public final class TestTypeSystem implements TypeSystemSpi
{
    private final Set<TypeInfo<?>> acceptOptions;
    private final Set<TypeInfo<?>> connectOptions;
    private final Set<TypeInfo<?>> readOptions;
    private final Set<TypeInfo<?>> writeOptions;
    private final Set<StructuredTypeInfo> readConfigs;
    private final Set<StructuredTypeInfo> writeConfigs;

    public TestTypeSystem()
    {
        TypeInfo<?> transportType = new TypeInfo<>("transport", URI.class);
        TypeInfo<?> stringType = new TypeInfo<>("string", String.class);
        TypeInfo<?> bytesType = new TypeInfo<>("bytes", byte[].class);
        TypeInfo<?> numberType = new TypeInfo<>("number", int.class);
        TypeInfo<?> expressionType = new TypeInfo<>("expression", Object.class);

        Set<TypeInfo<?>> acceptOptions = new LinkedHashSet<>();
        acceptOptions.add(transportType);
        acceptOptions.add(stringType);
        acceptOptions.add(bytesType);
        acceptOptions.add(numberType);
        acceptOptions.add(expressionType);
        this.acceptOptions = acceptOptions;

        Set<TypeInfo<?>> connectOptions = new LinkedHashSet<>();
        connectOptions.add(transportType);
        connectOptions.add(stringType);
        connectOptions.add(bytesType);
        connectOptions.add(numberType);
        connectOptions.add(expressionType);
        this.connectOptions = connectOptions;

        TypeInfo<byte[]> optionType = new TypeInfo<>("option", byte[].class);
        StructuredTypeInfo configType = new StructuredTypeInfo("config", emptyList(), asList(String.class, String.class));

        this.readOptions = singleton(optionType);
        this.writeOptions = singleton(optionType);
        this.readConfigs = singleton(configType);
        this.writeConfigs = singleton(configType);
    }

    @Override
    public String getName()
    {
        return "test";
    }

    @Override
    public Set<TypeInfo<?>> acceptOptions()
    {
        return acceptOptions;
    }

    @Override
    public Set<TypeInfo<?>> connectOptions()
    {
        return connectOptions;
    }

    @Override
    public Set<TypeInfo<?>> readOptions()
    {
        return readOptions;
    }

    @Override
    public Set<TypeInfo<?>> writeOptions()
    {
        return writeOptions;
    }

    @Override
    public Set<StructuredTypeInfo> readConfigs()
    {
        return readConfigs;
    }

    @Override
    public Set<StructuredTypeInfo> writeConfigs()
    {
        return writeConfigs;
    }
}
