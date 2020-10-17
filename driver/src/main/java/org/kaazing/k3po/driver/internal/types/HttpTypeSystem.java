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
package org.kaazing.k3po.driver.internal.types;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;

import java.net.URI;
import java.util.LinkedHashSet;
import java.util.Set;

import org.kaazing.k3po.lang.types.StructuredTypeInfo;
import org.kaazing.k3po.lang.types.TypeInfo;
import org.kaazing.k3po.lang.types.TypeSystemSpi;

public final class HttpTypeSystem implements TypeSystemSpi
{
    public static final TypeInfo<URI> OPTION_TRANSPORT = new TypeInfo<>("transport", URI.class);
    public static final TypeInfo<String> OPTION_CHUNK_EXT = new TypeInfo<>("chunkExtension", String.class);

    public static final StructuredTypeInfo CONFIG_METHOD = new StructuredTypeInfo("http", "method", emptyList(), 1);
    public static final StructuredTypeInfo CONFIG_HEADER = new StructuredTypeInfo("http", "header", singletonList(new TypeInfo<>("name", String.class)), Integer.MAX_VALUE);
    public static final StructuredTypeInfo CONFIG_PARAMETER = new StructuredTypeInfo("http", "parameter", singletonList(new TypeInfo<>("name", String.class)), Integer.MAX_VALUE);
    public static final StructuredTypeInfo CONFIG_STATUS = new StructuredTypeInfo("http", "status", asList(new TypeInfo<>("code", String.class), new TypeInfo<>("reason", String.class)), 0);
    public static final StructuredTypeInfo CONFIG_VERSION = new StructuredTypeInfo("http", "version", emptyList(), 1);
    public static final StructuredTypeInfo CONFIG_TRAILER = new StructuredTypeInfo("http", "trailer", singletonList(new TypeInfo<>("name", String.class)), Integer.MAX_VALUE);
    public static final StructuredTypeInfo CONFIG_REQUEST = new StructuredTypeInfo("http", "request", emptyList(), 1);
    public static final StructuredTypeInfo CONFIG_HOST = new StructuredTypeInfo("http", "host", emptyList(), 0);
    public static final StructuredTypeInfo CONFIG_CONTENT_LENGTH = new StructuredTypeInfo("http", "content-length", emptyList(), 0);

    private final Set<TypeInfo<?>> acceptOptions;
    private final Set<TypeInfo<?>> connectOptions;
    private final Set<TypeInfo<?>> readOptions;
    private final Set<TypeInfo<?>> writeOptions;
    private final Set<StructuredTypeInfo> readConfigs;
    private final Set<StructuredTypeInfo> writeConfigs;
    private final Set<StructuredTypeInfo> readAdvisories;
    private final Set<StructuredTypeInfo> writeAdvisories;

    public HttpTypeSystem()
    {
        this.acceptOptions = singleton(OPTION_TRANSPORT);
        this.connectOptions = singleton(OPTION_TRANSPORT);
        this.readOptions = singleton(OPTION_CHUNK_EXT);
        this.writeOptions = singleton(OPTION_CHUNK_EXT);

        Set<StructuredTypeInfo> readConfigs = new LinkedHashSet<>();
        readConfigs.add(CONFIG_METHOD);
        readConfigs.add(CONFIG_HEADER);
        readConfigs.add(CONFIG_PARAMETER);
        readConfigs.add(CONFIG_STATUS);
        readConfigs.add(CONFIG_VERSION);
        readConfigs.add(CONFIG_TRAILER);
        this.readConfigs = readConfigs;

        Set<StructuredTypeInfo> writeConfigs = new LinkedHashSet<>();
        writeConfigs.add(CONFIG_REQUEST);
        writeConfigs.add(CONFIG_METHOD);
        writeConfigs.add(CONFIG_HEADER);
        writeConfigs.add(CONFIG_PARAMETER);
        writeConfigs.add(CONFIG_STATUS);
        writeConfigs.add(CONFIG_VERSION);
        writeConfigs.add(CONFIG_TRAILER);
        writeConfigs.add(CONFIG_HOST);
        writeConfigs.add(CONFIG_CONTENT_LENGTH);
        this.writeConfigs = writeConfigs;

        this.readAdvisories = emptySet();
        this.writeAdvisories = emptySet();
    }

    @Override
    public String getName()
    {
        return "http";
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
