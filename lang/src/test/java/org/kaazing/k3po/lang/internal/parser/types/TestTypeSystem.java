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
    public static final TypeInfo<URI> OPTION_TRANSPORT = new TypeInfo<>("transport", URI.class);
    public static final TypeInfo<String> OPTION_STRING = new TypeInfo<>("string", String.class);
    public static final TypeInfo<byte[]> OPTION_BYTES = new TypeInfo<>("bytes", byte[].class);
    public static final TypeInfo<Integer> OPTION_NUMBER = new TypeInfo<>("number", int.class);
    public static final TypeInfo<Object> OPTION_EXPRESSION = new TypeInfo<>("expression", Object.class);
    public static final StructuredTypeInfo CONFIG_CONFIG = new StructuredTypeInfo("test", "config", emptyList(), Integer.MAX_VALUE);
    public static final StructuredTypeInfo ADVISORY_ADVICE = new StructuredTypeInfo("test", "advice", emptyList(), Integer.MAX_VALUE);

    private final Set<TypeInfo<?>> acceptOptions;
    private final Set<TypeInfo<?>> connectOptions;
    private final Set<TypeInfo<?>> readOptions;
    private final Set<TypeInfo<?>> writeOptions;
    private final Set<StructuredTypeInfo> readConfigs;
    private final Set<StructuredTypeInfo> writeConfigs;
    private final Set<StructuredTypeInfo> readAdvisories;
    private final Set<StructuredTypeInfo> writeAdvisories;

    public TestTypeSystem()
    {
        Set<TypeInfo<?>> acceptOptions = new LinkedHashSet<>();
        acceptOptions.add(OPTION_TRANSPORT);
        acceptOptions.add(OPTION_STRING);
        acceptOptions.add(OPTION_BYTES);
        acceptOptions.add(OPTION_NUMBER);
        acceptOptions.add(OPTION_EXPRESSION);
        this.acceptOptions = acceptOptions;

        Set<TypeInfo<?>> connectOptions = new LinkedHashSet<>();
        connectOptions.add(OPTION_TRANSPORT);
        connectOptions.add(OPTION_STRING);
        connectOptions.add(OPTION_BYTES);
        connectOptions.add(OPTION_NUMBER);
        connectOptions.add(OPTION_EXPRESSION);
        this.connectOptions = connectOptions;

        TypeInfo<byte[]> optionType = new TypeInfo<>("option", byte[].class);
        this.readOptions = singleton(optionType);
        this.writeOptions = singleton(optionType);
        this.readConfigs = singleton(CONFIG_CONFIG);
        this.writeConfigs = singleton(CONFIG_CONFIG);
        this.readAdvisories = singleton(ADVISORY_ADVICE);
        this.writeAdvisories = singleton(ADVISORY_ADVICE);
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

    @Override
    public Set<StructuredTypeInfo> readAdvisories()
    {
        return readAdvisories;
    }

    @Override
    public Set<StructuredTypeInfo> writeAdvisories()
    {
        return writeAdvisories;
    }
}
