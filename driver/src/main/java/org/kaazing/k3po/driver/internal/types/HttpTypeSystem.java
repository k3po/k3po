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
    private final Set<TypeInfo<?>> acceptOptions;
    private final Set<TypeInfo<?>> connectOptions;
    private final Set<TypeInfo<?>> readOptions;
    private final Set<TypeInfo<?>> writeOptions;
    private final Set<StructuredTypeInfo> readConfigs;
    private final Set<StructuredTypeInfo> writeConfigs;

    public HttpTypeSystem()
    {
        this.acceptOptions = singleton(new TypeInfo<>("transport", URI.class));
        this.connectOptions = singleton(new TypeInfo<>("transport", URI.class));
        this.readOptions = singleton(new TypeInfo<>("chunkExtension", String.class));
        this.writeOptions = singleton(new TypeInfo<>("chunkExtension", String.class));

        StructuredTypeInfo methodType = new StructuredTypeInfo("method", emptyList(), 1);
        StructuredTypeInfo headerType = new StructuredTypeInfo("header", singletonList(new TypeInfo<>("name", String.class)), Integer.MAX_VALUE);
        StructuredTypeInfo parameterType = new StructuredTypeInfo("parameter", singletonList(new TypeInfo<>("name", String.class)), Integer.MAX_VALUE);
        StructuredTypeInfo statusType = new StructuredTypeInfo("status", asList(new TypeInfo<>("code", String.class), new TypeInfo<>("reason", String.class)), 0);
        StructuredTypeInfo versionType = new StructuredTypeInfo("version", emptyList(), 1);
        StructuredTypeInfo trailerType = new StructuredTypeInfo("trailer", singletonList(new TypeInfo<>("name", String.class)), Integer.MAX_VALUE);

        Set<StructuredTypeInfo> readConfigs = new LinkedHashSet<>();
        readConfigs.add(methodType);
        readConfigs.add(headerType);
        readConfigs.add(parameterType);
        readConfigs.add(statusType);
        readConfigs.add(versionType);
        readConfigs.add(trailerType);
        this.readConfigs = readConfigs;

        StructuredTypeInfo requestType = new StructuredTypeInfo("request", emptyList(), 1);
        StructuredTypeInfo hostType = new StructuredTypeInfo("host", emptyList(), 0);
        StructuredTypeInfo contentLengthType = new StructuredTypeInfo("content-length", emptyList(), 0);

        Set<StructuredTypeInfo> writeConfigs = new LinkedHashSet<>();
        writeConfigs.add(requestType);
        writeConfigs.add(methodType);
        writeConfigs.add(headerType);
        writeConfigs.add(parameterType);
        writeConfigs.add(statusType);
        writeConfigs.add(versionType);
        writeConfigs.add(trailerType);
        writeConfigs.add(hostType);
        writeConfigs.add(contentLengthType);
        this.writeConfigs = writeConfigs;
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
}
