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

import static java.util.Collections.emptySet;

import java.util.LinkedHashSet;
import java.util.Set;

import org.kaazing.k3po.driver.internal.netty.channel.agrona.ChannelReader;
import org.kaazing.k3po.driver.internal.netty.channel.agrona.ChannelWriter;
import org.kaazing.k3po.lang.types.StructuredTypeInfo;
import org.kaazing.k3po.lang.types.TypeInfo;
import org.kaazing.k3po.lang.types.TypeSystemSpi;

public final class AgronaTypeSystem implements TypeSystemSpi
{
    private final Set<TypeInfo<?>> acceptOptions;
    private final Set<TypeInfo<?>> connectOptions;
    private final Set<TypeInfo<?>> readOptions;
    private final Set<TypeInfo<?>> writeOptions;
    private final Set<StructuredTypeInfo> readConfigs;
    private final Set<StructuredTypeInfo> writeConfigs;
    private final Set<StructuredTypeInfo> readAdvisories;
    private final Set<StructuredTypeInfo> writeAdvisories;

    public AgronaTypeSystem()
    {
        TypeInfo<?> readerType = new TypeInfo<>("reader", ChannelReader.class);
        TypeInfo<?> writerType = new TypeInfo<>("writer", ChannelWriter.class);

        Set<TypeInfo<?>> acceptOptions = new LinkedHashSet<>();
        acceptOptions.add(readerType);
        acceptOptions.add(writerType);
        this.acceptOptions = acceptOptions;

        Set<TypeInfo<?>> connectOptions = new LinkedHashSet<>();
        connectOptions.add(readerType);
        connectOptions.add(writerType);
        this.connectOptions = connectOptions;

        this.readOptions = emptySet();
        this.writeOptions = emptySet();
        this.readConfigs = emptySet();
        this.writeConfigs = emptySet();
        this.readAdvisories = emptySet();
        this.writeAdvisories = emptySet();
    }

    @Override
    public String getName()
    {
        return "agrona";
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
